
package com.java.importer.exporter;

import com.java.importer.mapper.ExportJobMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@Service
public class ExportCoordinator {

    private final ExportJobMapper exportJobMapper;
    private final ExportWorker exportWorker;

    @Value("${instance.id}")
    String instanceId;

    public ExportCoordinator(ExportJobMapper exportJobMapper, ExportWorker exportWorker) {
        this.exportJobMapper = exportJobMapper;
        this.exportWorker = exportWorker;
    }

    public void run() {
        String runId = UUID.randomUUID().toString();

        log.info("Starting export run {}", runId);
        exportJobMapper.populate(runId);

        exportWorker.run(runId, instanceId);

        log.info("Export run {} complete", runId);
    }
}