package com.java.importer.service;

import com.java.importer.exporter.ExportCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class JobSchedulerService {

    private final DataImporter importer;
    private final ExportCoordinator exportCoordinator;
    private final DatabasePreparer databasePreparer;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Value("${data.source}")
    private String dataSource;

    public JobSchedulerService(
            DataImporter importer,
            ExportCoordinator exportCoordinator,
            DatabasePreparer databasePreparer
    ) {
        this.importer = importer;
        this.exportCoordinator = exportCoordinator;
        this.databasePreparer = databasePreparer;
    }

    @Scheduled(fixedDelayString = "${jobs.import.delay}")
    public void runImporter() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Importer skipped because another job is already running.");
            return;
        }

        try {
            if ("local".equals(dataSource)) {
                importer.importFromLocalFolder();
            } else if ("remote".equals(dataSource)) {
                importer.importDataRemote();
            } else {
                log.error("Invalid data source: {}", dataSource);
            }
        } catch (Exception e) {
            log.error("Failed to import data", e);
        } finally {
            running.set(false);
        }
    }

    @Scheduled(cron = "${jobs.export.cron}")
    public void runExporter() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Exporter skipped because another job is already running.");
            return;
        }

        try {
            exportCoordinator.run();
            databasePreparer.prepareDatabase();
            log.info("Data exported and database prepared");
        } catch (Exception e) {
            log.error("Failed to export/reset data", e);
        } finally {
            running.set(false);
        }
    }
}