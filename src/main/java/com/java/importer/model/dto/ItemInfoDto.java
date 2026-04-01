package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemInfoDto {
    private String corporateNumber;
    private String mergeKey;
    private String value;
    private Boolean isIndustry;
}