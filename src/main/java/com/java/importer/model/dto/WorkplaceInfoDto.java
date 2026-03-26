package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkplaceInfoDto {
    private String runId;
    private String corporateNumber;
    private String mergeKey;
    private String baseInfoMergeKey;
    private String womenActivityMergeKey;
    private String compatibilityMergeKey;
}