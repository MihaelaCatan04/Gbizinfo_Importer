package com.java.importer.exporter;

import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.CompanyEntryMapper;
import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.model.export.BatchPayloadCollector;
import com.java.importer.service.CompanyPreparer;
import com.java.importer.service.PipelineControlService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ExportWorker {

    private final ExportJobMapper exportJobMapper;
    private final CompanyPreparer companyPreparer;
    private final WarehouseClient warehouseClient;
    private final CompanyEntryMapper companyEntryMapper;
    private final PipelineControlService pipelineControlService;

    @Value("${exporter.batch.size}")
    int batchSize;

    @Value("${node.id}")
    String nodeId;

    @Value("${pipeline.export.lease.seconds:300}")
    int exportLeaseSeconds;

    @Value("${exporter.max-attempts:3}")
    int maxAttempts;

    public ExportWorker(ExportJobMapper exportJobMapper, CompanyPreparer companyPreparer, WarehouseClient warehouseClient, CompanyEntryMapper companyEntryMapper, PipelineControlService pipelineControlService) {
        this.exportJobMapper = exportJobMapper;
        this.companyPreparer = companyPreparer;
        this.warehouseClient = warehouseClient;
        this.companyEntryMapper = companyEntryMapper;
        this.pipelineControlService = pipelineControlService;
    }

    public void run(String instanceId, LocalDate checkpointDate) {
        while (true) {
            List<String> batch = claimBatch(instanceId, checkpointDate);
            if (batch.isEmpty()) {
                log.info("No more companies to process, instance {} done", instanceId);
                break;
            }
            processBatch(instanceId, batch, checkpointDate);
        }
    }

    private List<String> claimBatch(String instanceId, LocalDate checkpointDate) {
        List<String> batch = exportJobMapper.claimBatch(instanceId, batchSize, checkpointDate, exportLeaseSeconds, maxAttempts);
        log.info("Claimed batch of {} companies", batch.size());
        if (!pipelineControlService.renewExportLease(nodeId)) {
            throw new IllegalStateException("Export lease lost for node " + nodeId);
        }
        return batch;
    }

    private void processBatch(String instanceId, List<String> batch, LocalDate checkpointDate) {
        BatchPayloadCollector collector = new BatchPayloadCollector();
        List<String> successful = new ArrayList<>();
        for (String corporateNumber : batch) {
            if (!pipelineControlService.renewExportLease(nodeId)) {
                throw new IllegalStateException("Export lease lost during batch processing for node " + nodeId);
            }
            if (processCompany(instanceId, corporateNumber, checkpointDate, collector)) {
                successful.add(corporateNumber);
            }
        }
        finalizeBatch(instanceId, collector, successful, checkpointDate);
    }

    private boolean processCompany(String instanceId, String corporateNumber, LocalDate checkpointDate, BatchPayloadCollector collector) {
        try {
            String raw = companyEntryMapper.findRawByCorporateNumber(checkpointDate, corporateNumber);
            companyPreparer.mapInto(corporateNumber, raw, collector);
            return true;
        } catch (Exception e) {
            log.error("Failed to parse {}, marking failed", corporateNumber, e);
            exportJobMapper.markFailed(checkpointDate, corporateNumber, instanceId);
            return false;
        }
    }

    private void finalizeBatch(String instanceId, BatchPayloadCollector collector, List<String> successful, LocalDate checkpointDate) {
        try {
            collector.postFlat(successful, warehouseClient);
            collector.postNested(warehouseClient);
        } catch (Exception e) {
            log.error("Warehouse post failed for batch, will be retried", e);
            return;
        }
        successful.forEach(cn -> exportJobMapper.markDone(checkpointDate, cn, instanceId));
    }
}