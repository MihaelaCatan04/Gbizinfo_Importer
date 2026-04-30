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

    private static final String PIPELINE_NAME = "MAIN";

    private final PipelineControlMapper mapper;

    @Value("${pipeline.import.lease.seconds:120}")
    private int leaseSeconds;

    public PipelineControlService(PipelineControlMapper mapper) {
        this.mapper = mapper;
    }

    public void ensurePipelineExists() {
        mapper.insertInitial(PIPELINE_NAME);
    }

    public boolean tryStartImport(String nodeId) {
        ensurePipelineExists();

        int updated = mapper.tryStartImport(PIPELINE_NAME, nodeId, leaseSeconds);

        if (updated == 1) {
            log.info("Node {} acquired IMPORTING phase", nodeId);
            return true;
        }

        log.debug("Node {} could not acquire IMPORTING phase", nodeId);
        return false;
    }

    public void markImportSuccess(String nodeId, LocalDate date) {
        mapper.markImportSuccess(PIPELINE_NAME, nodeId, date);
    }

    public void markImportFailure(String nodeId) {
        mapper.markImportFailure(PIPELINE_NAME, nodeId);
    }

    public boolean renewImportLease(String nodeId) {
        return mapper.renewImportLease(PIPELINE_NAME, nodeId, leaseSeconds) == 1;
    }

    public Optional<LocalDate> getExportDate() {
        return Optional.ofNullable(mapper.getExportDate(PIPELINE_NAME));
    }

    public void finishExportIfComplete(LocalDate date) {
        mapper.finishExportIfComplete(PIPELINE_NAME, date);
    }
}