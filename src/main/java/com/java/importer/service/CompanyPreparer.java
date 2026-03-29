package com.java.importer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.importer.model.dto.*;
import com.java.importer.model.export.EntityCollector;
import com.java.importer.model.export.EntityType;
import com.java.importer.model.mapper.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

import static com.java.importer.util.HashUtil.*;

@Component
public final class CompanyPreparer {

    private final ObjectMapper objectMapper;

    public CompanyPreparer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void mapInto(String runId, String corporateNumber,
                        String raw, EntityCollector collector) throws Exception {
        GbizCompany company = objectMapper.readValue(raw, GbizCompany.class);

        collector.add(EntityType.COMPANY, mapCompany(company, runId));
        mapPatents(company, runId, corporateNumber, collector);
        mapFinances(company, runId, corporateNumber, collector);
        mapCommendations(company, runId, corporateNumber, collector);
        mapCertifications(company, runId, corporateNumber, collector);
        mapSubsidies(company, runId, corporateNumber, collector);
        mapProcurements(company, runId, corporateNumber, collector);
        mapWorkplaceInfo(company, runId, corporateNumber, collector);
        mapItemInfos(company, runId, corporateNumber, collector);
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
                            String corporateNumber, EntityCollector collector) {

        forEachIfPresent(company.getPatent(), patent -> {
            String patentMergeKey = patent.patentMergeKey();
            if (patentMergeKey == null) return;

            collector.add(EntityType.PATENT, new PatentDto(
                    runId,
                    corporateNumber,
                    patentMergeKey,
                    patent.getPatentType(),
                    patent.getRegistrationNumber(),
                    patent.getApplicationDate(),
                    patent.getTitle(),
                    patent.getUrl()
            ));

            mapClassifications(patent, runId, patentMergeKey, collector);
        });
    }

    private void mapClassifications(Patent patent, String runId,
                                    String patentMergeKey, EntityCollector collector) {

        forEachIfPresent(patent.getClassifications(), cls -> {
            String mergeKey = cls.classificationMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.CLASSIFICATION, new ClassificationDto(
                    runId,
                    patentMergeKey,
                    mergeKey,
                    cls.getCodeValue(),
                    cls.getCodeName(),
                    cls.getJapanese()
            ));
        });
    }

    private void mapFinances(GbizCompany company, String runId,
                             String corporateNumber, EntityCollector collector) {

        forEachIfPresent(company.getFinance(), finance -> {
            String financeMergeKey = finance.financeMergeKey();
            if (financeMergeKey == null) return;

            collector.add(EntityType.FINANCE, new FinanceDto(
                    runId,
                    corporateNumber,
                    financeMergeKey,
                    finance.getAccountingStandards(),
                    finance.getFiscalYearCoverPage()
            ));

            mapMajorShareholders(finance, runId, financeMergeKey, collector);
            mapManagementIndices(finance, runId, financeMergeKey, collector);
        });
    }

    private void mapMajorShareholders(Finance finance, String runId,
                                      String financeMergeKey, EntityCollector collector) {

        forEachIfPresent(finance.getMajorShareholders(), ms -> {
            String mergeKey = ms.shareholderMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.MAJOR_SHAREHOLDER, new MajorShareholderDto(
                    runId,
                    financeMergeKey,
                    mergeKey,
                    ms.getNameMajorShareholders(),
                    ms.getShareholdingRatio()
            ));
        });
    }

    private void mapManagementIndices(Finance finance, String runId,
                                      String financeMergeKey, EntityCollector collector) {

        forEachIfPresent(finance.getManagementIndex(), mi -> {
            String mergeKey = mi.managementIndexMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.MANAGEMENT_INDEX, new ManagementIndexDto(
                    runId,
                    financeMergeKey,
                    mergeKey,
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
            ));
        });
    }

    private void mapCommendations(GbizCompany company, String runId,
                                  String corporateNumber, EntityCollector collector) {

        forEachIfPresent(company.getCommendation(), c -> {
            String mergeKey = c.commendationMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.COMMENDATION, new CommendationDto(
                    runId,
                    corporateNumber,
                    mergeKey,
                    c.getDateOfCommendation(),
                    c.getTitle(),
                    c.getTarget(),
                    c.getCategory(),
                    c.getGovernmentDepartments(),
                    c.getNote()
            ));
        });
    }

    private void mapCertifications(GbizCompany company, String runId,
                                   String corporateNumber, EntityCollector collector) {

        forEachIfPresent(company.getCertification(), c -> {
            String mergeKey = c.certificationMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.CERTIFICATION, new CertificationDto(
                    runId,
                    corporateNumber,
                    mergeKey,
                    c.getDateOfApproval(),
                    c.getTitle(),
                    c.getTarget(),
                    c.getGovernmentDepartments(),
                    c.getCategory()
            ));
        });
    }

    private void mapSubsidies(GbizCompany company, String runId,
                              String corporateNumber, EntityCollector collector) {

        forEachIfPresent(company.getSubsidy(), s -> {
            String mergeKey = s.subsidyMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.SUBSIDY, new SubsidyDto(
                    runId,
                    corporateNumber,
                    mergeKey,
                    s.getDateOfApproval(),
                    s.getTitle(),
                    s.getAmount(),
                    s.getTarget(),
                    s.getGovernmentDepartments()
            ));
        });
    }

    private void mapProcurements(GbizCompany company, String runId,
                                 String corporateNumber, EntityCollector collector) {

        forEachIfPresent(company.getProcurement(), p -> {
            String mergeKey = p.procurementMergeKey();
            if (mergeKey == null) return;

            collector.add(EntityType.PROCUREMENT, new ProcurementDto(
                    runId,
                    corporateNumber,
                    mergeKey,
                    p.getDateOfOrder(),
                    p.getTitle(),
                    p.getAmount(),
                    p.getGovernmentDepartments(),
                    p.getNote()
            ));
        });
    }

    private void mapWorkplaceInfo(GbizCompany company, String runId,
                                  String corporateNumber, EntityCollector collector) {
        WorkplaceInfo wi = company.getWorkplaceInfo();
        if (wi == null) return;

        String baseInfoMergeKey = mapBaseInfo(wi, runId, corporateNumber, collector);
        String womenActivityMergeKey = mapWomenActivity(wi, runId, corporateNumber, collector);
        String compatibilityMergeKey = mapCompatibility(wi, runId, corporateNumber, collector);

        String workplaceInfoMergeKey = mergeKeyOrNull(
                baseInfoMergeKey, womenActivityMergeKey, compatibilityMergeKey);

        if (workplaceInfoMergeKey != null) {
            collector.add(EntityType.WORKPLACE_INFO, new WorkplaceInfoDto(
                    runId,
                    corporateNumber,
                    workplaceInfoMergeKey,
                    baseInfoMergeKey,
                    womenActivityMergeKey,
                    compatibilityMergeKey
            ));
        }
    }

    private String mapBaseInfo(WorkplaceInfo wi, String runId,
                               String corporateNumber, EntityCollector collector) {
        if (wi.getBaseInfos() == null) return null;

        BaseInfos bi = wi.getBaseInfos();
        String mergeKey = bi.baseInfoMergeKey();
        if (mergeKey == null) return null;

        collector.add(EntityType.BASE_INFO, new BaseInfoDto(
                runId,
                corporateNumber,
                mergeKey,
                bi.getAverageContinuousServiceYearsType(),
                bi.getAverageContinuousServiceYearsMale(),
                bi.getAverageContinuousServiceYearsFemale(),
                bi.getAverageContinuousServiceYears(),
                bi.getAverageAge(),
                bi.getMonthAveragePredeterminedOvertimeHours()
        ));
        return mergeKey;
    }

    private String mapWomenActivity(WorkplaceInfo wi, String runId,
                                    String corporateNumber, EntityCollector collector) {
        if (wi.getWomenActivityInfos() == null) return null;

        WomenActivityInfos wa = wi.getWomenActivityInfos();
        String mergeKey = wa.womenActivityMergeKey();
        if (mergeKey == null) return null;

        collector.add(EntityType.WOMEN_ACTIVITY, new WomenActivityInfoDto(
                runId,
                corporateNumber,
                mergeKey,
                wa.getFemaleWorkersProportionType(),
                wa.getFemaleWorkersProportion(),
                wa.getFemaleShareOfManager(),
                wa.getGenderTotalOfManager(),
                wa.getFemaleShareOfOfficers(),
                wa.getGenderTotalOfOfficers()
        ));
        return mergeKey;
    }

    private String mapCompatibility(WorkplaceInfo wi, String runId,
                                    String corporateNumber, EntityCollector collector) {
        if (wi.getCompatibilityOfChildcareAndWork() == null) return null;

        CompatibilityOfChildcareAndWork cc = wi.getCompatibilityOfChildcareAndWork();
        String mergeKey = cc.compatChildcareMergeKey();
        if (mergeKey == null) return null;

        collector.add(EntityType.COMPATIBILITY, new CompatibilityOfChildcareAndWorkDto(
                runId,
                corporateNumber,
                mergeKey,
                cc.getNumberOfPaternityLeave(),
                cc.getNumberOfMaternityLeave(),
                cc.getPaternityLeaveAcquisitionNum(),
                cc.getMaternityLeaveAcquisitionNum()
        ));
        return mergeKey;
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
                              String corporateNumber, EntityCollector collector) {

        mapItemInfoValues(company.getIndustry(), runId, corporateNumber, true, collector);
        mapItemInfoValues(company.getBusinessItems(), runId, corporateNumber, false, collector);
    }

    private void mapItemInfoValues(Collection<String> values, String runId,
                                   String corporateNumber, boolean isIndustry,
                                   EntityCollector collector) {

        forEachIfPresent(values, value -> {
            if (value == null || value.isBlank()) return;

            ItemInfoDto dto = buildItemInfoDto(runId, corporateNumber, value, isIndustry);
            if (dto != null) {
                collector.add(EntityType.ITEM_INFO, dto);
            }
        });
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

    private <T> void forEachIfPresent(Collection<T> collection, Consumer<T> action) {
        if (collection == null) return;
        collection.forEach(action);
    }
}