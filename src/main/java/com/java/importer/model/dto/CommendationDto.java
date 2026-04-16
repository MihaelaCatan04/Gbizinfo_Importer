package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CommendationDto {
    private String corporateNumber;
    private String mergeKey;
    private LocalDate dateOfCommendation;
    private String title;
    private String target;
    private String category;
    private String governmentDepartments;
    private String note;
}