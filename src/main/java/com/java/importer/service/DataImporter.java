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

@Slf4j
@Service
public class DataImporter {

    private final ImportCheckpointMapper checkpointMapper;
    private final ImportFailureMapper failureMapper;
    private final DataMapper dataMapper;
    private final TransactionTemplate transactionTemplate;

    @Value("${importer.max-attempts:3}")
    private int maxAttempts;

    public DataImporter(ImportCheckpointMapper checkpointMapper, ImportFailureMapper failureMapper, DataMapper dataMapper, TransactionTemplate transactionTemplate) {
        this.checkpointMapper = checkpointMapper;
        this.failureMapper = failureMapper;
        this.dataMapper = dataMapper;
        this.transactionTemplate = transactionTemplate;
    }

    public void processFile(Path file, LocalDate transactionDate) {
        String name = file.getFileName().toString();

        try {
            if (shouldSkip(name, transactionDate)) {
                return;
            }
            transactionTemplate.executeWithoutResult(status -> processTransaction(file, name, transactionDate));
        } catch (Exception e) {
            handleFailure(name, transactionDate, e);
        }
    }

    private void processTransaction(Path file, String name, LocalDate transactionDate) {
        try (var inputStream = Files.newInputStream(file)) {
            dataMapper.copy(inputStream, transactionDate);
            markSuccess(name, transactionDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}