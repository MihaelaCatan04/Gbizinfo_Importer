package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProcurementDto {
    private String runId;
    private String corporateNumber;
    private String mergeKey;
    private String dateOfOrder;
    private String title;
    private Long amount;
    private String governmentDepartments;
    private String note;
}