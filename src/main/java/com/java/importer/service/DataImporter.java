package com.java.importer.service;

import com.java.importer.mapper.DataMapper;
import com.java.importer.mapper.ImportFailureMapper;
import com.java.importer.mapper.PrepMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
public class DataImporter {

    private final PrepMapper prepMapper;
    private final DataMapper dataMapper;
    private final TransactionTemplate transactionTemplate;
    private final PipelineControlService pipelineControlService;
    private final ImportFailureMapper importFailureMapper;

    @Value("${node.id}")
    private String nodeId;

    @Value("${importer.max-attempts}")
    private int maxAttempts;

    public DataImporter(PrepMapper prepMapper, DataMapper dataMapper, TransactionTemplate transactionTemplate, PipelineControlService pipelineControlService, ImportFailureMapper importFailureMapper) {
        this.prepMapper = prepMapper;
        this.dataMapper = dataMapper;
        this.transactionTemplate = transactionTemplate;
        this.pipelineControlService = pipelineControlService;
        this.importFailureMapper = importFailureMapper;
    }

    public void importFromLocalFolder(LocalDate transactionDate, String folder) throws Exception {

        Path folderPath = validateLocalFolder(folder);

        List<Path> files = listJsonFiles(folderPath);

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
                throw new IllegalStateException("Lost import lease for node " + nodeId);
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

        } catch (Exception e) {
            handleFailure(name, transactionDate, e);
        }
    }

    private boolean shouldSkip(String name, LocalDate date) {

        if (prepMapper.getEntryCount(name, date) > 0) {
            log.info("Skipping already processed file {}", name);
            return true;
        }

        int attempts = importFailureMapper.getFailureCount(name, date);

        if (attempts >= maxAttempts) {
            log.warn("File {} exceeded max attempts ({}), skipping permanently", name, attempts);
            return true;
        }

        return false;
    }

    private void markSuccess(String name, LocalDate date) {
        prepMapper.setCheckpoint(name, date);
        importFailureMapper.deleteFailure(name, date);
        log.info("Processed successfully: {}", name);
    }

    private void handleFailure(String name, LocalDate date, Exception e) {

        String error = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();

        try {
            importFailureMapper.recordFailure(name, date, error);

            int attempts = importFailureMapper.getFailureCount(name, date);

            log.error("Failed {} (attempt {})", name, attempts, e);

        } catch (Exception ex) {
            log.error("Could not record failure for {}", name, ex);
        }
    }

    private List<Path> listJsonFiles(Path folder) throws Exception {
        try (var stream = Files.list(folder)) {
            return stream.filter(p -> p.toString().endsWith(".json")).sorted(Comparator.comparing(p -> p.getFileName().toString())).toList();
        }
    }

    private Path validateLocalFolder(String folder) {
        Path path = Path.of(folder);

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Invalid folder: " + folder);
        }

        return path;
    }
}