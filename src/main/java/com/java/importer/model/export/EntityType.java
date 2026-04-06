package com.java.importer.model.export;

import com.java.importer.model.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntityType {
    BASE_INFO("/baseinfos", BaseInfoDto.class),
    CERTIFICATION("/certifications", CertificationDto.class),
    CLASSIFICATION("/classifications", ClassificationDto.class),
    COMMENDATION("/commendations", CommendationDto.class),
    COMPANY("/companies", CompanyDto.class),
    COMPATIBILITY("/compatibilities", CompatibilityOfChildcareAndWorkDto.class),
    FINANCE("/finances", FinanceDto.class),
    ITEM_INFO("/iteminfos", ItemInfoDto.class),
    MAJOR_SHAREHOLDER("/majorshareholders", MajorShareholderDto.class),
    MANAGEMENT_INDEX("/managementindexes", ManagementIndexDto.class),
    PATENT("/patents", PatentDto.class),
    PROCUREMENT("/procurements", ProcurementDto.class),
    SUBSIDY("/subsidies", SubsidyDto.class),
    WOMEN_ACTIVITY("/womenactivities", WomenActivityInfoDto.class),
    WORKPLACE_INFO("/workplaceinfos", WorkplaceInfoDto.class);

    private final String endpoint;
    private final Class<?> dtoClass;
}
