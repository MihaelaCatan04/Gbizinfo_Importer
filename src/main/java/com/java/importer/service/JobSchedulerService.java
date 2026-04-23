package com.java.importer.service;

import com.java.importer.exporter.ExportCoordinator;
import com.java.importer.model.source.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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

    @Value("${jobs.export.not-before}")
    private LocalTime exportNotBefore;

    public JobSchedulerService(DataImporter importer, ExportCoordinator exportCoordinator, PipelineControlService pipelineControlService) {
        this.importer = importer;
        this.exportCoordinator = exportCoordinator;
        this.pipelineControlService = pipelineControlService;
    }

    @Scheduled(fixedDelayString = "${jobs.import.delay}")
    public void runImporterTick() {
        if (!pipelineControlService.tryStartImport(nodeId)) {
            return;
        }
        LocalDate transactionDate = LocalDate.now();
        try {
            if (DataSource.LOCAL.equals(dataSource)) {
                importer.importFromLocalFolder(transactionDate);
            } else if (DataSource.REMOTE.equals(dataSource)) {
                importer.importDataRemote(transactionDate);
            } else {
                log.error("Invalid data source: {}", dataSource);
                pipelineControlService.markImportFailure(nodeId);
                return;
            }
            pipelineControlService.markImportSuccess(nodeId, transactionDate);
        } catch (Exception e) {
            log.error("Failed to import data for date {}", transactionDate, e);
            pipelineControlService.markImportFailure(nodeId);
        }
    }

    @Scheduled(fixedDelayString = "${jobs.orchestrator.delay}")
    public void runOrchestratorTick() {
        if (LocalTime.now().isBefore(exportNotBefore)) {
            return;
        }

        Optional<LocalDate> dateOpt = pipelineControlService.getExportDate();
        if (dateOpt.isEmpty()) {
            return;
        }

        LocalDate transactionDate = dateOpt.get();
        try {
            exportCoordinator.run(transactionDate);
        } catch (Exception e) {
            log.error("Export batch failed on node {} for date {}", nodeId, transactionDate, e);
        }

        pipelineControlService.finishExportIfComplete(transactionDate);
    }
}