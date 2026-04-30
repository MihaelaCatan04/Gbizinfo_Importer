package com.java.importer.exporter;

import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.service.PipelineControlService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Log4j2
@Service
public class ExportCoordinator {

    private final ExportJobMapper exportJobMapper;
    private final ExportWorker exportWorker;
    private final PipelineControlService pipelineControlService;

    @Value("${node.id}")
    private String instanceId;

    @Value("${exporter.thread.count:4}")
    private int threadCount;

    @Value("${exporter.max-attempts:3}")
    private int maxAttempts;

    public ExportCoordinator(ExportJobMapper exportJobMapper, ExportWorker exportWorker, PipelineControlService pipelineControlService) {
        this.exportJobMapper = exportJobMapper;
        this.exportWorker = exportWorker;
        this.pipelineControlService = pipelineControlService;
    }

    @Transactional
    public void initExportJobs(LocalDate transactionDate, String nodeId) {
        pipelineControlService.markImportSuccess(nodeId, transactionDate);
        exportJobMapper.createJobsForDate(transactionDate);
        log.info("Phase → EXPORTING, jobs seeded for {}", transactionDate);
    }

    public void run(LocalDate transactionDate) {
        log.info("Node {} starting export run ({} threads) for {}", instanceId, threadCount, transactionDate);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                final String workerId = instanceId + "-worker-" + i;
                tasks.add(() -> {
                    exportWorker.run(workerId, transactionDate, maxAttempts);
                    return null;
                });
            }

            List<Future<Void>> futures = executor.invokeAll(tasks);

            for (Future<Void> future : futures) {
                future.get();
            }

            log.info("Export run complete for {}", transactionDate);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Export coordinator interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Export worker failed", e.getCause());
        } finally {
            executor.shutdown();
        }
    }
}