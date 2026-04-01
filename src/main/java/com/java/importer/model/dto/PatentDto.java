package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatentDto {
    private String corporateNumber;
    private String mergeKey;
    private String patentType;
    private String registrationNumber;
    private String applicationDate;
    private String title;
    private String url;
}