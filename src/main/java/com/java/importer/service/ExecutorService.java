package com.java.importer.service;

import com.java.importer.exporter.ExportCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@Slf4j
public class ExecutorService {

    private final DataImporter importer;
    private final ExportCoordinator exportCoordinator;
    private final PipelineControlService pipelineControlService;

    @Value("${node.id}")
    private String nodeId;

    @Value("${jobs.export.not-before}")
    private LocalTime exportNotBefore;

    public ExecutorService(DataImporter importer, ExportCoordinator exportCoordinator, PipelineControlService pipelineControlService) {
        this.importer = importer;
        this.exportCoordinator = exportCoordinator;
        this.pipelineControlService = pipelineControlService;
    }

    public void runImporter(String folder) {
        LocalDate transactionDate = LocalDate.now();

        if (!pipelineControlService.tryStartImport(nodeId)) {
            log.warn("Node {} could not acquire import lease, skipping", nodeId);
            return;
        }

        try {
            importer.importFromLocalFolder(transactionDate, folder);

            pipelineControlService.markImportSuccess(nodeId, transactionDate);

            runExporter();

        } catch (Exception e) {
            log.error("Import failed for date {}", transactionDate, e);
            pipelineControlService.markImportFailure(nodeId);
        }
    }

    public void runExporter() {
        Optional<LocalDate> dateOpt = pipelineControlService.getExportDate();

        if (dateOpt.isEmpty()) {
            return;
        }

        LocalDate transactionDate = dateOpt.get();

        if (LocalTime.now().isBefore(exportNotBefore)) {
            log.info("Export blocked by time window for {}", transactionDate);
            return;
        }

        try {
            exportCoordinator.run(transactionDate);
        } catch (Exception e) {
            log.error("Export failed for {}", transactionDate, e);
        }

        pipelineControlService.finishExportIfComplete(transactionDate);
    }
}