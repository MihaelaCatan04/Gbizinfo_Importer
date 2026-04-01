package com.java.importer.exporter;

import com.java.importer.mapper.ExportJobMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Log4j2
@Service
public class ExportCoordinator {

    private final ExportJobMapper exportJobMapper;
    private final ExportWorker exportWorker;

    @Value("${node.id}")
    String instanceId;

    @Value("${exporter.thread.count:4}")
    int threadCount;

    public ExportCoordinator(ExportJobMapper exportJobMapper, ExportWorker exportWorker) {
        this.exportJobMapper = exportJobMapper;
        this.exportWorker = exportWorker;
    }

    public void run(LocalDate transactionDate) {
        exportJobMapper.populate(transactionDate);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                final int workerIndex = i;
                tasks.add(() -> {
                    exportWorker.run(instanceId + "-worker-" + workerIndex, transactionDate);
                    return null;
                });
            }

            List<Future<Void>> futures = executor.invokeAll(tasks);

            for (Future<Void> future : futures) {
                future.get();
            }

            log.info("Export run complete");
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