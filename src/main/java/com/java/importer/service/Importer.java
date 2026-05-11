package com.java.importer.service;

import com.java.importer.exporter.ExportCoordinator;
import com.java.importer.mapper.FileMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Importer {

    private final SingleFileProcessor singleFileProcessor;
    private final ExportCoordinator exportCoordinator;
    private final FileMapper fileMapper;

    @PostConstruct
    public void onStartup() {
        processAllFiles();
    }

    public void processAllFiles() {
        importPhase();
        waitForOtherNodes();
        exportPhase();
    }

    private void importPhase() {
        int processed = 0;
        Long fileId;

        while ((fileId = safelyProcessFile()) != null) {
            processed++;
            log.info("Imported file id={} ({} total this run)", fileId, processed);
        }

        log.info("Import phase done. Processed {} files this run.", processed);
    }

    private Long safelyProcessFile() {
        try {
            return singleFileProcessor.processFile();
        } catch (Exception e) {
            log.error("File processing failed, skipping to next", e);
            return null;
        }
    }

    private void waitForOtherNodes() {
        while (hasUnprocessedFiles()) {
            log.info("Waiting for other nodes to finish importing...");
            sleepSafely();
        }
    }

    private boolean hasUnprocessedFiles() {
        return fileMapper.countUnprocessed() > 0;
    }

    private void sleepSafely() {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for import completion", e);
        }
    }

    private void exportPhase() {
        exportCoordinator.initExportJobs();
        exportCoordinator.run();
    }
}