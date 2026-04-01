package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommendationDto {
    private String corporateNumber;
    private String mergeKey;
    private String dateOfCommendation;
    private String title;
    private String target;
    private String category;
    private String governmentDepartments;
    private String note;
}