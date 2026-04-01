package com.java.importer.model.dto;

import com.java.importer.model.util.PipelinePhase;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class PipelineControlDto {
    private String pipelineName;
    private PipelinePhase phase;
    private boolean exportRequested;
    private String ownerNode;
    private OffsetDateTime leaseUntil;
    private OffsetDateTime updatedAt;
}
