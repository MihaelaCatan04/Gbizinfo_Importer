package com.java.importer.exporter;

import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.CompanyEntryMapper;
import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.model.export.BatchPayloadCollector;
import com.java.importer.model.export.ExportJob;
import com.java.importer.service.CompanyPreparer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public void run(String workerId, int maxAttempts) {
        while (true) {
            List<ExportJob> batch = claimBatch(workerId, maxAttempts);
            if (batch.isEmpty()) {
                log.info("Worker {} — no more companies to process", workerId);
                break;
            }
            processBatch(workerId, batch);
        }
    }

    private List<ExportJob> claimBatch(String workerId, int maxAttempts) {
        List<ExportJob> batch = exportJobMapper.claimBatch(workerId, batchSize, exportLeaseSeconds, maxAttempts);
        log.info("Worker {} claimed batch of {}", workerId, batch.size());
        return batch;
    }

    private void processBatch(String workerId, List<ExportJob> batch) {
        long startTime = System.nanoTime();
        BatchPayloadCollector collector = new BatchPayloadCollector();
        List<ExportJob> successful = new ArrayList<>();

        for (ExportJob job : batch) {
            if (processCompany(workerId, job, collector)) {
                successful.add(job);
            }
        }

        finalizeBatch(workerId, collector, successful);

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        log.info("Worker {} — batch done: {}/{} successful in {} ms", workerId, successful.size(), batch.size(), durationMs);
    }

    private boolean processCompany(String workerId, ExportJob job, BatchPayloadCollector collector) {
        try {
            String raw = companyEntryMapper.findRawByCorporateNumber(job.getCheckpointDate(), job.getCorporateNumber());
            companyPreparer.mapInto(job.getCorporateNumber(), raw, collector);
            return true;
        } catch (Exception e) {
            log.error("Worker {} — failed to parse {}, marking failed", workerId, job.getCorporateNumber(), e);
            String error = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            exportJobMapper.markJobFailed(job.getCheckpointDate(), job.getCorporateNumber(), workerId, error);
            return false;
        }
    }

    private void finalizeBatch(String workerId, BatchPayloadCollector collector, List<ExportJob> successful) {
        if (successful.isEmpty()) {
            return;
        }

        try {
            List<String> corporateNumbers = successful.stream().map(ExportJob::getCorporateNumber).toList();
            collector.postFlat(corporateNumbers, warehouseClient);
            collector.postNested(warehouseClient);
        } catch (Exception e) {
            log.error("Worker {} — warehouse post failed, " + "batch retried via lease expiry", workerId, e);
            return;
        }

        for (ExportJob job : successful) {
            exportJobMapper.markJobDone(job.getCheckpointDate(), job.getCorporateNumber(), workerId);
        }
        log.info("Worker {} — marked {} jobs done", workerId, successful.size());
    }
}