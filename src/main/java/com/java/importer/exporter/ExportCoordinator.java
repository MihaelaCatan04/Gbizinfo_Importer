package com.java.importer.exporter;

import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.ExportJobMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class ExportCoordinator {
    private static final List<String> ENTITIES = List.of(
            "patents", "classifications", "commendations", "certifications",
            "finances", "subsidies", "procurements", "item-infos", "workplace-infos"
    );
    private final ExportJobMapper exportJobMapper;
    private final ExportWorker exportWorker;
    private final WarehouseClient warehouseClient;
    @Value("${instance.id}")
    String instanceId;

    public ExportCoordinator(ExportJobMapper exportJobMapper, ExportWorker exportWorker, WarehouseClient warehouseClient) {
        this.exportJobMapper = exportJobMapper;
        this.exportWorker = exportWorker;
        this.warehouseClient = warehouseClient;
    }

    public void run() {
        String runId = UUID.randomUUID().toString();

        log.info("Starting export run {}", runId);
        exportJobMapper.populate(runId);

        exportWorker.run(runId, instanceId);

        log.info("All companies processed, triggering completion");

        completeEntities(warehouseClient, runId);

        log.info("Export run {} complete", runId);
    }


    private void completeEntities(WarehouseClient warehouseClient, String runId) {
        ENTITIES.forEach(entity -> warehouseClient.completeEntity(runId, entity));
        warehouseClient.completeRun(runId);
    }
}