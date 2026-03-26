package com.java.importer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.importer.model.dto.*;
import com.java.importer.model.mapper.*;
import com.java.importer.util.DTOUtil;
import org.springframework.stereotype.Component;

import static com.java.importer.util.HashUtil.*;

@Component
public class CompanyPreparer {

    private final ObjectMapper objectMapper;

    public CompanyPreparer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private CompanyDto mapCompany(GbizCompany company, String runId) {
        String workplaceInfoMergeKey = null;

        if (company.getWorkplaceInfo() != null) {
            workplaceInfoMergeKey = workplaceInfoMergeKey(company.getWorkplaceInfo());
        }

        return new CompanyDto(
                runId,
                company.getCorporateNumber(),
                company.getName(),
                company.getKana(),
                company.getNameEn(),
                company.getPostalCode(),
                company.getLocation(),
                company.getProcess(),
                company.getAggregatedYear(),
                company.getStatus(),
                company.getCloseDate(),
                company.getCloseCause(),
                company.getKind(),
                company.getRepresentativeName(),
                company.getCapitalStock(),
                company.getEmployeeNumber(),
                company.getCompanySizeMale(),
                company.getCompanySizeFemale(),
                company.getBusinessSummary(),
                company.getCompanyUrl(),
                company.getFoundingYear(),
                company.getDateOfEstablishment(),
                company.getQualificationGrade(),
                company.getUpdateDate(),
                workplaceInfoMergeKey
        );
    }

    private void mapPatents(GbizCompany company, String runId,
                            String corporateNumber, DTOUtil dtoUtil) {
        if (company.getPatent() == null) return;

        for (Patent patent : company.getPatent()) {
            String patentMergeKey = patent.patentMergeKey();
            if (patentMergeKey == null) continue;

            PatentDto dto = new PatentDto(
                    runId,
                    corporateNumber,
                    patentMergeKey,
                    patent.getPatentType(),
                    patent.getRegistrationNumber(),
                    patent.getApplicationDate(),
                    patent.getTitle(),
                    patent.getUrl()
            );
            dtoUtil.getPatentList().add(dto);

            if (patent.getClassifications() == null) continue;

            for (Classifications cls : patent.getClassifications()) {
                String classificationMergeKey = cls.classificationMergeKey();
                if (classificationMergeKey == null) continue;

                ClassificationDto clsDto = new ClassificationDto(
                        runId,
                        patentMergeKey,
                        classificationMergeKey,
                        cls.getCodeValue(),
                        cls.getCodeName(),
                        cls.getJapanese()
                );
                dtoUtil.getClassificationList().add(clsDto);
            }
        }
    }

    private void mapFinances(GbizCompany company, String runId,
                             String corporateNumber, DTOUtil dtoUtil) {
        if (company.getFinance() == null) return;

        for (Finance finance : company.getFinance()) {
            String financeMergeKey = finance.financeMergeKey();
            if (financeMergeKey == null) continue;

            FinanceDto dto = new FinanceDto(
                    runId,
                    corporateNumber,
                    financeMergeKey,
                    finance.getAccountingStandards(),
                    finance.getFiscalYearCoverPage()
            );
            dtoUtil.getFinanceList().add(dto);

            if (finance.getMajorShareholders() != null) {
                for (MajorShareholders ms : finance.getMajorShareholders()) {
                    String shareholderMergeKey = ms.shareholderMergeKey();
                    if (shareholderMergeKey == null) continue;

                    MajorShareholderDto msDto = new MajorShareholderDto(
                            runId,
                            financeMergeKey,
                            shareholderMergeKey,
                            ms.getNameMajorShareholders(),
                            ms.getShareholdingRatio()
                    );
                    dtoUtil.getMajorShareholderList().add(msDto);
                }
            }

            if (finance.getManagementIndex() != null) {
                for (ManagementIndex mi : finance.getManagementIndex()) {
                    String managementIndexMergeKey = mi.managementIndexMergeKey();
                    if (managementIndexMergeKey == null) continue;

                    ManagementIndexDto miDto = new ManagementIndexDto(
                            runId,
                            financeMergeKey,
                            managementIndexMergeKey,
                            mi.getPeriod(),
                            mi.getNetSalesSummaryOfBusinessResults(),
                            mi.getNetSalesSummaryOfBusinessResultsUnitRef(),
                            mi.getOperatingRevenue1SummaryOfBusinessResults(),
                            mi.getOperatingRevenue1SummaryOfBusinessResultsUnitRef(),
                            mi.getOperatingRevenue2SummaryOfBusinessResults(),
                            mi.getOperatingRevenue2SummaryOfBusinessResultsUnitRef(),
                            mi.getGrossOperatingRevenueSummaryOfBusinessResults(),
                            mi.getGrossOperatingRevenueSummaryOfBusinessResultsUnitRef(),
                            mi.getOrdinaryIncomeSummaryOfBusinessResults(),
                            mi.getOrdinaryIncomeSummaryOfBusinessResultsUnitRef(),
                            mi.getNetPremiumsWrittenSummaryOfBusinessResultIns(),
                            mi.getNetPremiumsWrittenSummaryOfBusinessResultsInsUnitRef(),
                            mi.getOrdinaryIncomeLossSummaryOfBusinessResults(),
                            mi.getOrdinaryIncomeLossSummaryOfBusinessResultsUnitRef(),
                            mi.getNetIncomeLossSummaryOfBusinessResults(),
                            mi.getNetIncomeLossSummaryOfBusinessResultsUnitRef(),
                            mi.getCapitalStockSummaryOfBusinessResults(),
                            mi.getCapitalStockSummaryOfBusinessResultsUnitRef(),
                            mi.getNetAssetsSummaryOfBusinessResults(),
                            mi.getNetAssetsSummaryOfBusinessResultsUnitRef(),
                            mi.getTotalAssetsSummaryOfBusinessResults(),
                            mi.getTotalAssetsSummaryOfBusinessResultsUnitRef(),
                            mi.getNumberOfEmployees(),
                            mi.getNumberOfEmployeesUnitRef()
                    );
                    dtoUtil.getManagementIndexList().add(miDto);
                }
            }
        }
    }

    private void mapCommendations(GbizCompany company, String runId,
                                  String corporateNumber, DTOUtil dtoUtil) {
        if (company.getCommendation() == null) return;

        for (Commendation c : company.getCommendation()) {
            String commendationMergeKey = c.commendationMergeKey();
            if (commendationMergeKey == null) continue;

            CommendationDto dto = new CommendationDto(
                    runId,
                    corporateNumber,
                    commendationMergeKey,
                    c.getDateOfCommendation(),
                    c.getTitle(),
                    c.getTarget(),
                    c.getCategory(),
                    c.getGovernmentDepartments(),
                    c.getNote()
            );
            dtoUtil.getCommendationList().add(dto);
        }
    }

    private void mapCertifications(GbizCompany company, String runId,
                                   String corporateNumber, DTOUtil dtoUtil) {
        if (company.getCertification() == null) return;

        for (Certification c : company.getCertification()) {
            String certificationMergeKey = c.certificationMergeKey();
            if (certificationMergeKey == null) continue;

            CertificationDto dto = new CertificationDto(
                    runId,
                    corporateNumber,
                    certificationMergeKey,
                    c.getDateOfApproval(),
                    c.getTitle(),
                    c.getTarget(),
                    c.getGovernmentDepartments(),
                    c.getCategory()
            );
            dtoUtil.getCertificationList().add(dto);
        }
    }

    private void mapSubsidies(GbizCompany company, String runId,
                              String corporateNumber, DTOUtil dtoUtil) {
        if (company.getSubsidy() == null) return;

        for (Subsidy s : company.getSubsidy()) {
            String subsidyMergeKey = s.subsidyMergeKey();
            if (subsidyMergeKey == null) continue;

            SubsidyDto dto = new SubsidyDto(
                    runId,
                    corporateNumber,
                    subsidyMergeKey,
                    s.getDateOfApproval(),
                    s.getTitle(),
                    s.getAmount(),
                    s.getTarget(),
                    s.getGovernmentDepartments()
            );
            dtoUtil.getSubsidyList().add(dto);
        }
    }

    private void mapProcurements(GbizCompany company, String runId,
                                 String corporateNumber, DTOUtil dtoUtil) {
        if (company.getProcurement() == null) return;

        for (Procurement p : company.getProcurement()) {
            String procurementMergeKey = p.procurementMergeKey();
            if (procurementMergeKey == null) continue;

            ProcurementDto dto = new ProcurementDto(
                    runId,
                    corporateNumber,
                    procurementMergeKey,
                    p.getDateOfOrder(),
                    p.getTitle(),
                    p.getAmount(),
                    p.getGovernmentDepartments(),
                    p.getNote()
            );
            dtoUtil.getProcurementList().add(dto);
        }
    }

    private void mapWorkplaceInfo(GbizCompany company, String runId,
                                  String corporateNumber, DTOUtil dtoUtil) {
        WorkplaceInfo wi = company.getWorkplaceInfo();
        if (wi == null) return;

        String baseInfoMergeKey = null;
        String womenActivityMergeKey = null;
        String compatibilityMergeKey = null;

        if (wi.getBaseInfos() != null) {
            BaseInfos bi = wi.getBaseInfos();
            baseInfoMergeKey = bi.baseInfoMergeKey();

            if (baseInfoMergeKey != null) {
                BaseInfoDto dto = new BaseInfoDto(
                        runId,
                        corporateNumber,
                        baseInfoMergeKey,
                        bi.getAverageContinuousServiceYearsType(),
                        bi.getAverageContinuousServiceYearsMale(),
                        bi.getAverageContinuousServiceYearsFemale(),
                        bi.getAverageContinuousServiceYears(),
                        bi.getAverageAge(),
                        bi.getMonthAveragePredeterminedOvertimeHours()
                );
                dtoUtil.getBaseInfoList().add(dto);
            }
        }

        if (wi.getWomenActivityInfos() != null) {
            WomenActivityInfos wa = wi.getWomenActivityInfos();
            womenActivityMergeKey = wa.womenActivityMergeKey();

            if (womenActivityMergeKey != null) {
                WomenActivityInfoDto dto = new WomenActivityInfoDto(
                        runId,
                        corporateNumber,
                        womenActivityMergeKey,
                        wa.getFemaleWorkersProportionType(),
                        wa.getFemaleWorkersProportion(),
                        wa.getFemaleShareOfManager(),
                        wa.getGenderTotalOfManager(),
                        wa.getFemaleShareOfOfficers(),
                        wa.getGenderTotalOfOfficers()
                );
                dtoUtil.getWomenActivityInfoList().add(dto);
            }
        }

        if (wi.getCompatibilityOfChildcareAndWork() != null) {
            CompatibilityOfChildcareAndWork cc = wi.getCompatibilityOfChildcareAndWork();
            compatibilityMergeKey = cc.compatChildcareMergeKey();

            if (compatibilityMergeKey != null) {
                CompatibilityOfChildcareAndWorkDto dto = new CompatibilityOfChildcareAndWorkDto(
                        runId,
                        corporateNumber,
                        compatibilityMergeKey,
                        cc.getNumberOfPaternityLeave(),
                        cc.getNumberOfMaternityLeave(),
                        cc.getPaternityLeaveAcquisitionNum(),
                        cc.getMaternityLeaveAcquisitionNum()
                );
                dtoUtil.getCompatibilityOfChildcareAndWorkList().add(dto);
            }
        }

        String workplaceInfoMergeKey = workplaceInfoMergeKey(wi);
        if (workplaceInfoMergeKey != null) {
            WorkplaceInfoDto dto = new WorkplaceInfoDto(
                    runId,
                    corporateNumber,
                    workplaceInfoMergeKey,
                    baseInfoMergeKey,
                    womenActivityMergeKey,
                    compatibilityMergeKey
            );
            dtoUtil.getWorkplaceInfoList().add(dto);
        }
    }

    private String workplaceInfoMergeKey(WorkplaceInfo wi) {
        String base = wi.getBaseInfos() != null
                ? wi.getBaseInfos().baseInfoMergeKey() : null;
        String women = wi.getWomenActivityInfos() != null
                ? wi.getWomenActivityInfos().womenActivityMergeKey() : null;
        String compat = wi.getCompatibilityOfChildcareAndWork() != null
                ? wi.getCompatibilityOfChildcareAndWork().compatChildcareMergeKey() : null;

        return mergeKeyOrNull(base, women, compat);
    }

    private void mapItemInfos(GbizCompany company, String runId,
                              String corporateNumber, DTOUtil dtoUtil) {
        if (company.getIndustry() != null) {
            for (String value : company.getIndustry()) {
                if (value == null || value.isBlank()) continue;

                ItemInfoDto dto = buildItemInfoDto(runId, corporateNumber, value, true);
                if (dto != null) {
                    dtoUtil.getItemInfoList().add(dto);
                }
            }
        }

        if (company.getBusinessItems() != null) {
            for (String value : company.getBusinessItems()) {
                if (value == null || value.isBlank()) continue;

                ItemInfoDto dto = buildItemInfoDto(runId, corporateNumber, value, false);
                if (dto != null) {
                    dtoUtil.getItemInfoList().add(dto);
                }
            }
        }
    }

    private ItemInfoDto buildItemInfoDto(String runId, String corporateNumber,
                                         String value, boolean isIndustry) {
        String mergeKey = mergeKeyOrNull(normText(value), normBool(isIndustry));
        if (mergeKey == null) return null;

        return new ItemInfoDto(
                runId,
                corporateNumber,
                mergeKey,
                value,
                isIndustry
        );
    }

    public void mapInto(String runId, String corporateNumber,
                        String raw, DTOUtil dtoUtil) throws Exception {
        GbizCompany company = objectMapper.readValue(raw, GbizCompany.class);

        dtoUtil.getCompanyList().add(mapCompany(company, runId));
        mapPatents(company, runId, corporateNumber, dtoUtil);
        mapFinances(company, runId, corporateNumber, dtoUtil);
        mapCommendations(company, runId, corporateNumber, dtoUtil);
        mapCertifications(company, runId, corporateNumber, dtoUtil);
        mapSubsidies(company, runId, corporateNumber, dtoUtil);
        mapProcurements(company, runId, corporateNumber, dtoUtil);
        mapWorkplaceInfo(company, runId, corporateNumber, dtoUtil);
        mapItemInfos(company, runId, corporateNumber, dtoUtil);
    }
}