package com.java.importer.service;

import com.java.importer.mapper.PipelineControlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class PipelineControlService {

    private static final String PIPELINE = "MAIN";

    private final PipelineControlMapper mapper;

    @Value("${pipeline.import.lease.seconds:120}")
    private int leaseSeconds;

    public PipelineControlService(PipelineControlMapper mapper) {
        this.mapper = mapper;
    }

    public boolean tryStartImport(String nodeId) {
        mapper.insertInitial(PIPELINE);
        boolean acquired = mapper.tryStartImport(PIPELINE, nodeId, leaseSeconds) == 1;
        if (acquired) log.info("Node {} acquired IMPORTING phase", nodeId);
        else log.debug("Node {} could not acquire IMPORTING phase", nodeId);
        return acquired;
    }

    /**
     * Returns false if the lease was stolen — caller must abort immediately.
     */
    public boolean renewImportLease(String nodeId) {
        return mapper.renewImportLease(PIPELINE, nodeId, leaseSeconds) == 1;
    }

    public void markImportSuccess(String nodeId, LocalDate date) {
        mapper.markImportSuccess(PIPELINE, nodeId, date);
    }

    public void markImportFailure(String nodeId) {
        mapper.markImportFailure(PIPELINE, nodeId);
    }

    public Optional<LocalDate> getExportDate() {
        return Optional.ofNullable(mapper.getExportDate(PIPELINE));
    }

    public void finishExportIfComplete(LocalDate date) {
        int updated = mapper.finishExportIfComplete(PIPELINE, date);
        if (updated == 1) log.info("Pipeline returned to IDLE after export of {}", date);
    }
}