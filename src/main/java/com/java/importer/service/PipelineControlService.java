package com.java.importer.service;

import com.java.importer.mapper.PipelineControlMapper;
import com.java.importer.model.dto.PipelineControlDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PipelineControlService {

    private static final String PIPELINE_NAME = "MAIN";

    private final PipelineControlMapper pipelineControlMapper;

    @Value("${pipeline.import.lease.seconds:120}")
    private int importLeaseSeconds;

    @Value("${pipeline.export.lease.seconds:300}")
    private int exportLeaseSeconds;

    public PipelineControlService(PipelineControlMapper pipelineControlMapper) {
        this.pipelineControlMapper = pipelineControlMapper;
    }

    public PipelineControlDto getState() {
        return pipelineControlMapper.findMain(PIPELINE_NAME);
    }

    public boolean tryStartImport(String nodeId) {
        int updated = pipelineControlMapper.tryStartImport(
                PIPELINE_NAME,
                nodeId,
                importLeaseSeconds
        );

        boolean success = updated == 1;
        if (success) {
            log.info("Node {} acquired IMPORTING phase", nodeId);
        } else {
            log.warn("Node {} could not acquire IMPORTING phase", nodeId);
        }
        return success;
    }

    public void requestExport() {
        pipelineControlMapper.requestExport(PIPELINE_NAME);
        log.info("Export requested for pipeline {}", PIPELINE_NAME);
    }

    public boolean tryStartExport(String nodeId) {
        int updated = pipelineControlMapper.tryStartExport(
                PIPELINE_NAME,
                nodeId,
                exportLeaseSeconds
        );

        boolean success = updated == 1;
        if (success) {
            log.info("Node {} acquired EXPORTING phase", nodeId);
        } else {
            log.warn("Node {} could not acquire EXPORTING phase", nodeId);
        }
        return success;
    }

    public boolean finishImport(String nodeId) {
        int updated = pipelineControlMapper.finishImport(PIPELINE_NAME, nodeId);
        boolean success = updated == 1;

        if (success) {
            log.info("Node {} released IMPORTING phase", nodeId);
        } else {
            log.warn("Node {} could not release IMPORTING phase", nodeId);
        }

        return success;
    }

    public void finishExport(String nodeId) {
        int updated = pipelineControlMapper.finishExport(PIPELINE_NAME, nodeId);
        boolean success = updated == 1;

        if (success) {
            log.info("Node {} released EXPORTING phase", nodeId);
        } else {
            log.warn("Node {} could not release EXPORTING phase", nodeId);
        }
    }

    public boolean renewImportLease(String nodeId) {
        int updated = pipelineControlMapper.renewImportLease(
                PIPELINE_NAME,
                nodeId,
                importLeaseSeconds
        );
        return updated == 1;
    }

    public boolean renewExportLease(String nodeId) {
        int updated = pipelineControlMapper.renewExportLease(
                PIPELINE_NAME,
                nodeId,
                exportLeaseSeconds
        );
        return updated == 1;
    }

    public boolean isExportRequested() {
        PipelineControlDto state = pipelineControlMapper.findMain(PIPELINE_NAME);
        return state != null && state.isExportRequested();
    }
}