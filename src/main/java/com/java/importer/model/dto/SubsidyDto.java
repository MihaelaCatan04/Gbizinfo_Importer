package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubsidyDto {
    private String runId;
    private String corporateNumber;
    private String mergeKey;
    private String dateOfApproval;
    private String title;
    private String amount;
    private String target;
    private String governmentDepartments;
}