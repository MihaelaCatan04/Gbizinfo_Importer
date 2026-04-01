package com.java.importer.exporter;

import com.java.importer.mapper.ExportJobMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Log4j2
@Service
public class ExportCoordinator {

    private final ExportJobMapper exportJobMapper;
    private final ExportWorker exportWorker;

    @Value("${node.id}")
    String instanceId;

    public ExportCoordinator(ExportJobMapper exportJobMapper, ExportWorker exportWorker) {
        this.exportJobMapper = exportJobMapper;
        this.exportWorker = exportWorker;
    }

    public void run(LocalDate transactionDate) {
        exportJobMapper.populate();
        exportWorker.run(instanceId, transactionDate);

        log.info("Export run complete");
    }
}