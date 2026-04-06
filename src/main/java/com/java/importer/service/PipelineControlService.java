package com.java.importer.service;

import com.java.importer.mapper.PipelineControlMapper;
import com.java.importer.model.dto.PipelineControlDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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

    public boolean tryStartImport(String nodeId) {
        int updated = pipelineControlMapper.tryStartImport(PIPELINE_NAME, nodeId, importLeaseSeconds);
        boolean success = updated == 1;

        if (success) {
            log.info("Node {} acquired IMPORTING phase", nodeId);
        } else {
            log.debug("Node {} could not acquire IMPORTING phase", nodeId);
        }
        return success;
    }

    public void markImportSuccess(String nodeId, LocalDate transactionDate) {
        int updated = pipelineControlMapper.markImportSuccess(PIPELINE_NAME, nodeId, transactionDate);
        if (updated == 1) {
            log.info("Node {} completed import, export requested for date {}", nodeId, transactionDate);
        } else {
            log.warn("Node {} could not mark import success for date {}", nodeId, transactionDate);
        }
    }

    public void markImportFailure(String nodeId) {
        int updated = pipelineControlMapper.markImportFailure(PIPELINE_NAME, nodeId);
        if (updated == 1) {
            log.info("Node {} released IMPORTING phase after failure", nodeId);
        } else {
            log.warn("Node {} could not release IMPORTING phase after failure", nodeId);
        }
    }

    public Optional<LocalDate> tryStartExport(String nodeId) {
        int updated = pipelineControlMapper.tryStartExport(PIPELINE_NAME, nodeId, exportLeaseSeconds);
        if (updated != 1) {
            return Optional.empty();
        }

        PipelineControlDto state = pipelineControlMapper.findMain(PIPELINE_NAME);
        if (state == null || !state.isExportRequested() || state.getTransactionDate() == null) {
            log.warn("Node {} acquired EXPORTING but no transaction_date was found", nodeId);
            return Optional.empty();
        }

        log.info("Node {} acquired EXPORTING phase for transaction date {}", nodeId, state.getTransactionDate());
        return Optional.of(state.getTransactionDate());
    }

    public void finishExport(String nodeId) {
        int updated = pipelineControlMapper.finishExport(PIPELINE_NAME, nodeId);
        if (updated == 1) {
            log.info("Node {} released EXPORTING phase", nodeId);
        } else {
            log.warn("Node {} could not release EXPORTING phase", nodeId);
        }
    }

    public void markExportFailure(String nodeId) {
        int updated = pipelineControlMapper.markExportFailure(PIPELINE_NAME, nodeId);
        if (updated == 1) {
            log.info("Node {} released EXPORTING phase after failure", nodeId);
        } else {
            log.warn("Node {} could not release EXPORTING phase after failure", nodeId);
        }
    }

    public boolean renewImportLease(String nodeId) {
        int updated = pipelineControlMapper.renewImportLease(PIPELINE_NAME, nodeId, importLeaseSeconds);
        return updated == 1;
    }

    public boolean renewExportLease(String nodeId) {
        int updated = pipelineControlMapper.renewExportLease(PIPELINE_NAME, nodeId, exportLeaseSeconds);
        return updated == 1;
    }
}