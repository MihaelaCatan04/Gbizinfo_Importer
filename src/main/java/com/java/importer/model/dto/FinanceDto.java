package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FinanceDto {
    private String corporateNumber;
    private String mergeKey;
    private String accountingStandards;
    private String fiscalYearCoverPage;
}