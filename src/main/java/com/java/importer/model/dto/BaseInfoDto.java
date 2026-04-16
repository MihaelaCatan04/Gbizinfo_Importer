package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseInfoDto {
    private String corporateNumber;
    private String mergeKey;
    private String averageContinuousServiceYearsType;
    private Double averageContinuousServiceYearsMale;
    private Double averageContinuousServiceYearsFemale;
    private Double averageContinuousServiceYears;
    private Double averageAge;
    private Double monthAveragePredeterminedOvertimeHours;
}