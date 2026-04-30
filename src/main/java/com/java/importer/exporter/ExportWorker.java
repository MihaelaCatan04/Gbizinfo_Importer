package com.java.importer.exporter;

import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.CompanyEntryMapper;
import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.model.export.BatchPayloadCollector;
import com.java.importer.service.CompanyPreparer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class ExportWorker {

    private final ExportJobMapper exportJobMapper;
    private final CompanyPreparer companyPreparer;
    private final WarehouseClient warehouseClient;
    private final CompanyEntryMapper companyEntryMapper;

    @Value("${exporter.batch.size}")
    private int batchSize;

    @Value("${pipeline.export.lease.seconds:300}")
    private int exportLeaseSeconds;

    public ExportWorker(ExportJobMapper exportJobMapper, CompanyPreparer companyPreparer, WarehouseClient warehouseClient, CompanyEntryMapper companyEntryMapper) {
        this.exportJobMapper = exportJobMapper;
        this.companyPreparer = companyPreparer;
        this.warehouseClient = warehouseClient;
        this.companyEntryMapper = companyEntryMapper;
    }

    public void run(String workerId, LocalDate checkpointDate, int maxAttempts) {
        while (true) {
            List<String> batch = claimBatch(workerId, checkpointDate, maxAttempts);
            if (batch.isEmpty()) {
                log.info("Worker {} — no more companies to process", workerId);
                break;
            }
            processBatch(workerId, batch, checkpointDate);
        }
    }

    private List<String> claimBatch(String workerId, LocalDate checkpointDate, int maxAttempts) {
        List<String> batch = exportJobMapper.claimBatch(workerId, batchSize, checkpointDate, exportLeaseSeconds, maxAttempts);
        log.info("Worker {} claimed batch of {}", workerId, batch.size());
        return batch;
    }

    private void processBatch(String workerId, List<String> batch, LocalDate checkpointDate) {
        long startTime = System.nanoTime();

        BatchPayloadCollector collector = new BatchPayloadCollector();
        List<String> successful = new ArrayList<>();

        for (String corporateNumber : batch) {
            if (processCompany(workerId, corporateNumber, checkpointDate, collector)) {
                successful.add(corporateNumber);
            }
        }

        finalizeBatch(workerId, collector, successful, checkpointDate);

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        log.info("Worker {} — batch done: {}/{} successful in {} ms", workerId, successful.size(), batch.size(), durationMs);
    }

    private boolean processCompany(String workerId, String corporateNumber, LocalDate checkpointDate, BatchPayloadCollector collector) {
        try {
            String raw = companyEntryMapper.findRawByCorporateNumber(checkpointDate, corporateNumber);
            companyPreparer.mapInto(corporateNumber, raw, collector);
            return true;
        } catch (Exception e) {
            log.error("Worker {} — failed to parse {}, marking failed", workerId, corporateNumber, e);
            String error = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            exportJobMapper.markJobFailed(checkpointDate, corporateNumber, workerId, error);
            return false;
        }
    }

    private void finalizeBatch(String workerId, BatchPayloadCollector collector, List<String> successful, LocalDate checkpointDate) {
        if (successful.isEmpty()) {
            return;
        }

        try {
            collector.postFlat(successful, warehouseClient);
            collector.postNested(warehouseClient);
        } catch (Exception e) {
            log.error("Worker {} — warehouse post failed, batch will be retried via lease expiry", workerId, e);
            return;
        }

        successful.forEach(cn -> exportJobMapper.markJobDone(checkpointDate, cn, workerId));
        log.info("Worker {} — marked {} jobs done", workerId, successful.size());
    }
}