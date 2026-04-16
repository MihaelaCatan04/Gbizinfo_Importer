package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CertificationDto {
    private String corporateNumber;
    private String mergeKey;
    private LocalDate dateOfApproval;
    private String title;
    private String target;
    private String governmentDepartments;
    private String category;
}