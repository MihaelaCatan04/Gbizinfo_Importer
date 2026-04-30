package com.java.importer.service;

import com.java.importer.exporter.ExportCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Service
public class ExecutorService {

    private final DataImporter importer;
    private final ExportCoordinator exportCoordinator;
    private final PipelineControlService pipelineControlService;

    @Value("${node.id}")
    private String nodeId;

    @Value("${jobs.export.not-before:00:00}")
    private LocalTime exportNotBefore;

    public ExecutorService(DataImporter importer, ExportCoordinator exportCoordinator, PipelineControlService pipelineControlService) {
        this.importer = importer;
        this.exportCoordinator = exportCoordinator;
        this.pipelineControlService = pipelineControlService;
    }

    @Async
    public void runImporter(String folder) {
        LocalDate transactionDate = LocalDate.now();

        if (!pipelineControlService.tryStartImport(nodeId)) {
            log.warn("Node {} could not acquire import lease", nodeId);
            runExporter();
            return;
        }

        try {
            importer.importFromLocalFolder(transactionDate, folder);
            exportCoordinator.initExportJobs(transactionDate, nodeId);
            log.info("Import complete for {}, export jobs seeded", transactionDate);
        } catch (Exception e) {
            log.error("Import failed for {}", transactionDate, e);
            pipelineControlService.markImportFailure(nodeId);
            throw new RuntimeException("Import failed", e);
        }

        runExporter();
    }

    @Async
    public void runExporter() {
        if (LocalTime.now().isBefore(exportNotBefore)) {
            log.info("Export blocked by time window (not before {})", exportNotBefore);
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
            log.error("Export run failed for {}", transactionDate, e);
        }

        pipelineControlService.finishExportIfComplete(transactionDate);
    }
}