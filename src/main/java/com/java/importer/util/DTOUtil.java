package com.java.importer.util;

import com.java.importer.model.dto.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DTOUtil {
    List<BaseInfoDto> baseInfoList = new ArrayList<>();
    List<CertificationDto> certificationList = new ArrayList<>();
    List<ClassificationDto> classificationList = new ArrayList<>();
    List<CommendationDto> commendationList = new ArrayList<>();
    List<CompatibilityOfChildcareAndWorkDto> compatibilityOfChildcareAndWorkList = new ArrayList<>();
    List<FinanceDto> financeList = new ArrayList<>();
    List<ItemInfoDto> itemInfoList = new ArrayList<>();
    List<MajorShareholderDto> majorShareholderList = new ArrayList<>();
    List<ManagementIndexDto> managementIndexList = new ArrayList<>();
    List<PatentDto> patentList = new ArrayList<>();
    List<ProcurementDto> procurementList = new ArrayList<>();
    List<SubsidyDto> subsidyList = new ArrayList<>();
    List<WomenActivityInfoDto> womenActivityInfoList = new ArrayList<>();
    List<WorkplaceInfoDto> workplaceInfoList = new ArrayList<>();
    List<CompanyDto> companyList = new ArrayList<>();
}
