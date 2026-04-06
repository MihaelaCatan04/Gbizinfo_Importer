package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProcurementDto {
    private String corporateNumber;
    private String mergeKey;
    private OffsetDateTime dateOfOrder;
    private String title;
    private Long amount;
    private String governmentDepartments;
    private String note;
}