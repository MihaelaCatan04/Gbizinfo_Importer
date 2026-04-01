package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WomenActivityInfoDto {
    private String corporateNumber;
    private String mergeKey;
    private String femaleWorkersProportionType;
    private Double femaleWorkersProportion;
    private Integer femaleShareOfManager;
    private Integer genderTotalOfManager;
    private Integer femaleShareOfOfficers;
    private Integer genderTotalOfOfficers;
}