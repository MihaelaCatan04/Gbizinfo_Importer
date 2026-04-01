package com.java.importer.exporter;

import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.CompanyEntryMapper;
import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.model.export.BatchPayloadCollector;
import com.java.importer.model.export.EntityType;
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
            processBatch(batch);
        }
    }

    private List<String> claimBatch(String instanceId, LocalDate checkpointDate) {
        List<String> batch = exportJobMapper.claimBatch(instanceId, batchSize, checkpointDate);
        log.info("Claimed batch of {} companies", batch.size());
        if (!pipelineControlService.renewExportLease(nodeId)) {
            throw new IllegalStateException("Export lease lost for node " + nodeId);
        }
        return batch;
    }

    private void processBatch(List<String> batch) {
        BatchPayloadCollector collector = new BatchPayloadCollector();
        List<String> successful = new ArrayList<>();

        for (String corporateNumber : batch) {
            if (processCompany(corporateNumber, collector)) {
                successful.add(corporateNumber);
            }
        }

        finalizeBatch(collector, successful);
    }

    private boolean processCompany(String corporateNumber, BatchPayloadCollector collector) {
        try {
            mapInfo(corporateNumber, collector);
            return true;
        } catch (Exception e) {
            log.error("Failed to parse {}, marking failed", corporateNumber, e);
            exportJobMapper.markFailed(corporateNumber);
            return false;
        }
    }

    private void finalizeBatch(BatchPayloadCollector collector, List<String> successful) {
        postInfo(collector, successful);
        successful.forEach(exportJobMapper::markDone);
    }

    private void mapInfo(String corporateNumber, BatchPayloadCollector collector) throws Exception {
        String raw = companyEntryMapper.findRawByCorporateNumber(corporateNumber);
        companyPreparer.mapInto(corporateNumber, raw, collector);
    }

    private void postInfo(BatchPayloadCollector collector, List<String> successful) {
        warehouseClient.postBatch(EntityType.COMPANY, collector.companyRequest(successful));
        warehouseClient.postBatch(EntityType.PATENT, collector.patentRequest(successful));
        warehouseClient.postClassificationBatch(collector.classificationRequest());
        warehouseClient.postBatch(EntityType.FINANCE, collector.financeRequest(successful));
        warehouseClient.postMajorShareholderBatch(collector.majorShareholderRequest());
        warehouseClient.postManagementIndexBatch(collector.managementIndexRequest());
        warehouseClient.postBatch(EntityType.COMMENDATION, collector.commendationRequest(successful));
        warehouseClient.postBatch(EntityType.CERTIFICATION, collector.certificationRequest(successful));
        warehouseClient.postBatch(EntityType.SUBSIDY, collector.subsidyRequest(successful));
        warehouseClient.postBatch(EntityType.PROCUREMENT, collector.procurementRequest(successful));
        warehouseClient.postBatch(EntityType.ITEM_INFO, collector.itemInfoRequest(successful));
        warehouseClient.postBatch(EntityType.BASE_INFO, collector.baseInfoRequest(successful));
        warehouseClient.postBatch(EntityType.WOMEN_ACTIVITY, collector.womenActivityRequest(successful));
        warehouseClient.postBatch(EntityType.COMPATIBILITY, collector.compatibilityRequest(successful));
        warehouseClient.postBatch(EntityType.WORKPLACE_INFO, collector.workplaceInfoRequest(successful));
    }
}