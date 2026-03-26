package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GbizCompany {

    @JsonProperty("corporate_number")
    private String corporateNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("kana")
    private String kana;

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("location")
    private String location;

    @JsonProperty("process")
    private String process;

    @JsonProperty("aggregated_year")
    private String aggregatedYear;

    @JsonProperty("status")
    private String status;

    @JsonProperty("close_date")
    private String closeDate;

    @JsonProperty("close_cause")
    private String closeCause;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("representative_name")
    private String representativeName;

    @JsonProperty("capital_stock")
    private Long capitalStock;

    @JsonProperty("employee_number")
    private Integer employeeNumber;

    @JsonProperty("company_size_male")
    private Integer companySizeMale;

    @JsonProperty("company_size_female")
    private Integer companySizeFemale;

    @JsonProperty("business_summary")
    private String businessSummary;

    @JsonProperty("company_url")
    private String companyUrl;

    @JsonProperty("founding_year")
    private Integer foundingYear;

    @JsonProperty("date_of_establishment")
    private String dateOfEstablishment;

    @JsonProperty("qualification_grade")
    private String qualificationGrade;

    @JsonProperty("update_date")
    private String updateDate;

    @JsonProperty("industry")
    private List<String> industry;

    @JsonProperty("business_items")
    private List<String> businessItems;

    @JsonProperty("patent")
    private List<Patent> patent;

    @JsonProperty("certification")
    private List<Certification> certification;

    @JsonProperty("subsidy")
    private List<Subsidy> subsidy;

    @JsonProperty("workplace_info")
    private WorkplaceInfo workplaceInfo;

    @JsonProperty("commendation")
    private List<Commendation> commendation;

    @JsonProperty("procurement")
    private List<Procurement> procurement;

    @JsonProperty("finance")
    private List<Finance> finance;
}