package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WorkplaceInfo {
    @JsonProperty("base_infos")
    private BaseInfos baseInfos;

    @JsonProperty("women_activity_infos")
    private WomenActivityInfos womenActivityInfos;

    @JsonProperty("compatibility_of_childcare_and_work")
    private CompatibilityOfChildcareAndWork compatibilityOfChildcareAndWork;
}