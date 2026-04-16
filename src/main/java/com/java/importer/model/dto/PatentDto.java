package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PatentDto {
    private String corporateNumber;
    private String mergeKey;
    private String patentType;
    private String registrationNumber;
    private LocalDate applicationDate;
    private String title;
    private String url;
}