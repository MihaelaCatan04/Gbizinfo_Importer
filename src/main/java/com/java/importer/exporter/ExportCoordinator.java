package com.java.importer.exporter;

import com.java.importer.mapper.ExportJobMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Log4j2
@Service
public class ExportCoordinator {

    private final ExportJobMapper exportJobMapper;
    private final ExportWorker exportWorker;

    @Value("${node.id}")
    private String instanceId;

    @Value("${exporter.thread.count:4}")
    private int threadCount;

    @Value("${exporter.max-attempts:3}")
    private int maxAttempts;

    public ExportCoordinator(ExportJobMapper exportJobMapper, ExportWorker exportWorker) {
        this.exportJobMapper = exportJobMapper;
        this.exportWorker = exportWorker;
    }

    @Transactional
    public void initExportJobs() {
        exportJobMapper.createAllPendingJobs();
        log.info("Export jobs seeded (idempotent).");
    }

    public void run() {
        log.info("Node {} starting export ({} threads).", instanceId, threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Callable<Void>> tasks = createTasks();
            List<Future<Void>> futures = executeTasks(executor, tasks);
            waitForCompletion(futures);
            log.info("Export run complete.");
        } catch (InterruptedException e) {
            handleInterruptedException(e);
        } catch (ExecutionException e) {
            handleExecutionException(e);
        } finally {
            shutdownExecutor(executor);
        }
    }

    private List<Callable<Void>> createTasks() {
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final String workerId = instanceId + "-worker-" + i;
            tasks.add(() -> {
                exportWorker.run(workerId, maxAttempts);
                return null;
            });
        }
        return tasks;
    }

    private List<Future<Void>> executeTasks(ExecutorService executor, List<Callable<Void>> tasks) throws InterruptedException {
        return executor.invokeAll(tasks);
    }

    private void waitForCompletion(List<Future<Void>> futures) throws InterruptedException, ExecutionException {
        for (Future<Void> future : futures) {
            future.get();
        }
    }

    private void handleInterruptedException(InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Export coordinator interrupted", e);
    }

    private void handleExecutionException(ExecutionException e) {
        throw new IllegalStateException("Export worker failed", e.getCause());
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
    }

}