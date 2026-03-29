package com.java.importer.model.export;

import com.java.importer.model.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntityType {
    BASE_INFO("/base-infos", BaseInfoDto.class),
    CERTIFICATION("/certifications", CertificationDto.class),
    CLASSIFICATION("/classifications", ClassificationDto.class),
    COMMENDATION("/commendations", CommendationDto.class),
    COMPANY("/companies", CompanyDto.class),
    COMPATIBILITY("/compatibilities", CompatibilityOfChildcareAndWorkDto.class),
    FINANCE("/finances", FinanceDto.class),
    ITEM_INFO("/item-infos", ItemInfoDto.class),
    MAJOR_SHAREHOLDER("/major-shareholders", MajorShareholderDto.class),
    MANAGEMENT_INDEX("/management-indexes", ManagementIndexDto.class),
    PATENT("/patents", PatentDto.class),
    PROCUREMENT("/procurements", ProcurementDto.class),
    SUBSIDY("/subsidies", SubsidyDto.class),
    WOMEN_ACTIVITY("/women-activities", WomenActivityInfoDto.class),
    WORKPLACE_INFO("/workplace-infos", WorkplaceInfoDto.class);

    private final String endpoint;
    private final Class<?> dtoClass;
}
