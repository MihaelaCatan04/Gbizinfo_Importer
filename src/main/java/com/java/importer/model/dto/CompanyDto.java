package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompanyDto {
    private String runId;
    private String corporateNumber;
    private String name;
    private String kana;
    private String nameEn;
    private String postalCode;
    private String location;
    private String process;
    private String aggregatedYear;
    private String status;
    private String closeDate;
    private String closeCause;
    private String kind;
    private String representativeName;
    private Long capitalStock;
    private Integer employeeNumber;
    private Integer companySizeMale;
    private Integer companySizeFemale;
    private String businessSummary;
    private String companyUrl;
    private Integer foundingYear;
    private String dateOfEstablishment;
    private String qualificationGrade;
    private String updateDate;
    private String workplaceInfoMergeKey;
}