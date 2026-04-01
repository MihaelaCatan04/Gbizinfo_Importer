package com.java.importer.service;

import com.java.importer.exporter.ExportCoordinator;
import com.java.importer.model.source.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class JobSchedulerService {

    private final DataImporter importer;
    private final ExportCoordinator exportCoordinator;
    private final PipelineControlService pipelineControlService;

    @Value("${data.source}")
    private DataSource dataSource;

    @Value("${node.id}")
    private String nodeId;

    private LocalDate transactionDate;

    public JobSchedulerService(
            DataImporter importer,
            ExportCoordinator exportCoordinator,
            PipelineControlService pipelineControlService
    ) {
        this.importer = importer;
        this.exportCoordinator = exportCoordinator;
        this.pipelineControlService = pipelineControlService;
    }

    @Scheduled(fixedDelayString = "${jobs.import.delay}")
    public void runImporterTick() {
        if (!pipelineControlService.tryStartImport(nodeId)) {
            return;
        }

        try {
            transactionDate = LocalDate.now();
            if (DataSource.LOCAL.equals(dataSource)) {
                importer.importFromLocalFolder(transactionDate);
            } else if (DataSource.REMOTE.equals(dataSource)) {
                importer.importDataRemote(transactionDate);
            } else {
                log.error("Invalid data source: {}", dataSource);
            }
        } catch (Exception e) {
            log.error("Failed to import data", e);
        } finally {
            pipelineControlService.finishImport(nodeId);
        }
    }

    @Scheduled(cron = "${jobs.export.cron}")
    public void requestExportTick() {
        pipelineControlService.requestExport();
    }

    @Scheduled(fixedDelayString = "${jobs.orchestrator.delay}")
    public void runOrchestratorTick() {
        if (!pipelineControlService.isExportRequested()) {
            return;
        }

        if (!pipelineControlService.tryStartExport(nodeId)) {
            return;
        }

        try {
            exportCoordinator.run(transactionDate);
            log.info("Data exported by node {}", nodeId);
        } catch (Exception e) {
            log.error("Failed to export data", e);
        } finally {
            pipelineControlService.finishExport(nodeId);
        }
    }
}