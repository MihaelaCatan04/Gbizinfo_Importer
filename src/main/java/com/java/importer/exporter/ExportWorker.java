package com.java.importer.exporter;

import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.CompanyEntryMapper;
import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.model.export.EntityCollector;
import com.java.importer.service.CompanyPreparer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ExportWorker {
    private final ExportJobMapper exportJobMapper;
    private final CompanyPreparer companyPreparer;
    private final WarehouseClient warehouseClient;
    private final CompanyEntryMapper companyEntryMapper;
    @Value("${exporter.batch.size}")
    int batchSize;

    public ExportWorker(ExportJobMapper exportJobMapper, CompanyPreparer companyPreparer, WarehouseClient warehouseClient, CompanyEntryMapper companyEntryMapper) {
        this.exportJobMapper = exportJobMapper;
        this.companyPreparer = companyPreparer;
        this.warehouseClient = warehouseClient;
        this.companyEntryMapper = companyEntryMapper;
    }

    public void run(String runId, String instanceId) {
        while (true) {
            List<String> batch = claimBatch(runId, instanceId);
            if (batch.isEmpty()) {
                log.info("No more companies to process, instance {} done", instanceId);
                break;
            }
            processBatch(runId, batch);
        }
    }

    private List<String> claimBatch(String runId, String instanceId) {
        List<String> batch = exportJobMapper.claimBatch(runId, instanceId, batchSize);
        log.info("Claimed batch of {} companies", batch.size());
        return batch;
    }

    private void processBatch(String runId, List<String> batch) {
        EntityCollector entityCollector = new EntityCollector();
        List<String> successful = new ArrayList<>();

        for (String corporateNumber : batch) {
            if (processCompany(runId, corporateNumber, entityCollector)) {
                successful.add(corporateNumber);
            }
        }

        finalizeBatch(runId, entityCollector, successful);
    }

    private boolean processCompany(String runId, String corporateNumber, EntityCollector entityCollector) {
        try {
            mapInfo(runId, corporateNumber, entityCollector);
            return true;
        } catch (Exception e) {
            log.error("Failed to parse {}, marking failed", corporateNumber, e);
            exportJobMapper.markFailed(runId, corporateNumber);
            return false;
        }
    }

    private void finalizeBatch(String runId, EntityCollector entityCollector, List<String> successful) {
        postInfo(entityCollector);
        successful.forEach(cn -> exportJobMapper.markDone(runId, cn));
    }


    private void mapInfo(String runId, String corporateNumber, EntityCollector entityCollector) throws Exception {
        String raw = companyEntryMapper.findRawByCorporateNumber(corporateNumber);
        companyPreparer.mapInto(runId, corporateNumber, raw, entityCollector);
    }

    private void postInfo(EntityCollector collector) {
        collector.forEachNonEmpty(warehouseClient::postEntities);
    }
}
