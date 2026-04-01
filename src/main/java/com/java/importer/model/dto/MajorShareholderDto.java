package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MajorShareholderDto {
    private String financeMergeKey;
    private String mergeKey;
    private String nameMajorShareholders;
    private Double shareholdingRatio;
}