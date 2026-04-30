package com.java.importer.service;

import com.java.importer.mapper.DataMapper;
import com.java.importer.mapper.ImportCheckpointMapper;
import com.java.importer.mapper.ImportFailureMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class DataImporter {

    private final ImportCheckpointMapper checkpointMapper;
    private final ImportFailureMapper failureMapper;
    private final DataMapper dataMapper;
    private final TransactionTemplate transactionTemplate;
    private final PipelineControlService pipelineControlService;

    @Value("${node.id}")
    private String nodeId;

    @Value("${importer.max-attempts:3}")
    private int maxAttempts;

    public DataImporter(ImportCheckpointMapper checkpointMapper, ImportFailureMapper failureMapper, DataMapper dataMapper, TransactionTemplate transactionTemplate, PipelineControlService pipelineControlService) {
        this.checkpointMapper = checkpointMapper;
        this.failureMapper = failureMapper;
        this.dataMapper = dataMapper;
        this.transactionTemplate = transactionTemplate;
        this.pipelineControlService = pipelineControlService;
    }

    public void importFromLocalFolder(LocalDate transactionDate, String folder) throws Exception {
        List<Path> files = listJsonFiles(validateFolder(folder));

        if (files.isEmpty()) {
            log.warn("No JSON files found in {}", folder);
            return;
        }

        for (Path file : files) {
            processFile(file, transactionDate);
        }
    }

    private void processFile(Path file, LocalDate transactionDate) {
        String name = file.getFileName().toString();

        try {
            if (!pipelineControlService.renewImportLease(nodeId)) {
                throw new LeaseRevokedException("Lost import lease for node " + nodeId);
            }

            if (shouldSkip(name, transactionDate)) {
                return;
            }

            transactionTemplate.executeWithoutResult(status -> {
                try (var inputStream = Files.newInputStream(file)) {
                    dataMapper.copy(inputStream, transactionDate);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                markSuccess(name, transactionDate);
            });

        } catch (LeaseRevokedException e) {
            throw e;
        } catch (Exception e) {
            handleFailure(name, transactionDate, e);
        }
    }

    private boolean shouldSkip(String name, LocalDate date) {
        if (checkpointMapper.getEntryCount(name, date) > 0) {
            log.info("Skipping already-processed file: {}", name);
            return true;
        }

        Integer attempts = failureMapper.getFailureCount(name, date);
        int failureCount = attempts != null ? attempts : 0;

        if (failureCount >= maxAttempts) {
            log.warn("File {} has exceeded max attempts ({}), skipping permanently", name, failureCount);
            return true;
        }

        return false;
    }

    private void markSuccess(String name, LocalDate date) {
        checkpointMapper.setCheckpoint(name, date);
        failureMapper.deleteFailure(name, date);
        log.info("Imported successfully: {}", name);
    }

    private void handleFailure(String name, LocalDate date, Exception e) {
        String error = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        try {
            failureMapper.recordFailure(name, date, error);
            int attempts = failureMapper.getFailureCount(name, date);
            log.error("Import failed for {} (attempt {})", name, attempts, e);
        } catch (Exception ex) {
            log.error("Could not record failure for {}", name, ex);
        }
    }

    private List<Path> listJsonFiles(Path folder) throws Exception {
        try (var stream = Files.list(folder)) {
            return stream.filter(p -> p.toString().endsWith(".json")).sorted(Comparator.comparing(p -> p.getFileName().toString())).toList();
        }
    }

    private Path validateFolder(String folder) {
        Path path = Path.of(folder);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Invalid folder: " + folder);
        }
        return path;
    }

    public static class LeaseRevokedException extends RuntimeException {
        public LeaseRevokedException(String message) {
            super(message);
        }
    }
}