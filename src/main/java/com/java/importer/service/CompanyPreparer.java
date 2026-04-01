package com.java.importer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.importer.model.dto.*;
import com.java.importer.model.export.BatchPayloadCollector;
import com.java.importer.model.mapper.BaseInfos;
import com.java.importer.model.mapper.CompatibilityOfChildcareAndWork;
import com.java.importer.model.mapper.Finance;
import com.java.importer.model.mapper.GbizCompany;
import com.java.importer.model.mapper.Patent;
import com.java.importer.model.mapper.WorkplaceInfo;
import com.java.importer.model.mapper.WomenActivityInfos;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

import static com.java.importer.util.HashUtil.mergeKeyOrNull;
import static com.java.importer.util.HashUtil.normBool;
import static com.java.importer.util.HashUtil.normText;

@Component
public final class CompanyPreparer {

    private final ObjectMapper objectMapper;

    public CompanyPreparer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void mapInto(String corporateNumber,
                        String raw,
                        BatchPayloadCollector collector) throws Exception {
        GbizCompany company = objectMapper.readValue(raw, GbizCompany.class);

        collector.addCompany(corporateNumber, mapCompany(company));
        mapPatents(company, corporateNumber, collector);
        mapFinances(company, corporateNumber, collector);
        mapCommendations(company, corporateNumber, collector);
        mapCertifications(company, corporateNumber, collector);
        mapSubsidies(company, corporateNumber, collector);
        mapProcurements(company, corporateNumber, collector);
        mapWorkplaceInfo(company, corporateNumber, collector);
        mapItemInfos(company, corporateNumber, collector);
    }

    private CompanyDto mapCompany(GbizCompany company) {
        String workplaceInfoMergeKey = null;

        if (company.getWorkplaceInfo() != null) {
            workplaceInfoMergeKey = workplaceInfoMergeKey(company.getWorkplaceInfo());
        }

        return new CompanyDto(
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

    private void mapPatents(GbizCompany company,
                            String corporateNumber,
                            BatchPayloadCollector collector) {

        forEachIfPresent(company.getPatent(), patent -> {
            String patentMergeKey = patent.patentMergeKey();
            if (patentMergeKey == null) return;

            collector.registerPatentKey(patentMergeKey);

            collector.addPatent(corporateNumber, new PatentDto(
                    corporateNumber,
                    patentMergeKey,
                    patent.getPatentType(),
                    patent.getRegistrationNumber(),
                    patent.getApplicationDate(),
                    patent.getTitle(),
                    patent.getUrl()
            ));

            mapClassifications(patent, patentMergeKey, collector);
        });
    }

    private void mapClassifications(Patent patent,
                                    String patentMergeKey,
                                    BatchPayloadCollector collector) {

        forEachIfPresent(patent.getClassifications(), cls -> {
            String mergeKey = cls.classificationMergeKey();
            if (mergeKey == null) return;

            collector.addClassification(patentMergeKey, new ClassificationDto(
                    patentMergeKey,
                    mergeKey,
                    cls.getCodeValue(),
                    cls.getCodeName(),
                    cls.getJapanese()
            ));
        });
    }

    private void mapFinances(GbizCompany company,
                             String corporateNumber,
                             BatchPayloadCollector collector) {

        forEachIfPresent(company.getFinance(), finance -> {
            String financeMergeKey = finance.financeMergeKey();
            if (financeMergeKey == null) return;

            collector.addFinance(corporateNumber, new FinanceDto(
                    corporateNumber,
                    financeMergeKey,
                    finance.getAccountingStandards(),
                    finance.getFiscalYearCoverPage()
            ));

            collector.registerFinanceKeyForShareholders(financeMergeKey);
            collector.registerFinanceKeyForManagementIndexes(financeMergeKey);

            mapMajorShareholders(finance, financeMergeKey, collector);
            mapManagementIndices(finance, financeMergeKey, collector);
        });
    }

    private void mapMajorShareholders(Finance finance,
                                      String financeMergeKey,
                                      BatchPayloadCollector collector) {

        forEachIfPresent(finance.getMajorShareholders(), ms -> {
            String mergeKey = ms.shareholderMergeKey();
            if (mergeKey == null) return;

            collector.addMajorShareholder(financeMergeKey, new MajorShareholderDto(
                    financeMergeKey,
                    mergeKey,
                    ms.getNameMajorShareholders(),
                    ms.getShareholdingRatio()
            ));
        });
    }

    private void mapManagementIndices(Finance finance,
                                      String financeMergeKey,
                                      BatchPayloadCollector collector) {

        forEachIfPresent(finance.getManagementIndex(), mi -> {
            String mergeKey = mi.managementIndexMergeKey();
            if (mergeKey == null) return;

            collector.addManagementIndex(financeMergeKey, new ManagementIndexDto(
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

    private void mapCommendations(GbizCompany company,
                                  String corporateNumber,
                                  BatchPayloadCollector collector) {

        forEachIfPresent(company.getCommendation(), c -> {
            String mergeKey = c.commendationMergeKey();
            if (mergeKey == null) return;

            collector.addCommendation(corporateNumber, new CommendationDto(
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

    private void mapCertifications(GbizCompany company,
                                   String corporateNumber,
                                   BatchPayloadCollector collector) {

        forEachIfPresent(company.getCertification(), c -> {
            String mergeKey = c.certificationMergeKey();
            if (mergeKey == null) return;

            collector.addCertification(corporateNumber, new CertificationDto(
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

    private void mapSubsidies(GbizCompany company,
                              String corporateNumber,
                              BatchPayloadCollector collector) {

        forEachIfPresent(company.getSubsidy(), s -> {
            String mergeKey = s.subsidyMergeKey();
            if (mergeKey == null) return;

            collector.addSubsidy(corporateNumber, new SubsidyDto(
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

    private void mapProcurements(GbizCompany company,
                                 String corporateNumber,
                                 BatchPayloadCollector collector) {

        forEachIfPresent(company.getProcurement(), p -> {
            String mergeKey = p.procurementMergeKey();
            if (mergeKey == null) return;

            collector.addProcurement(corporateNumber, new ProcurementDto(
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

    private void mapWorkplaceInfo(GbizCompany company,
                                  String corporateNumber,
                                  BatchPayloadCollector collector) {
        WorkplaceInfo wi = company.getWorkplaceInfo();
        if (wi == null) return;

        String baseInfoMergeKey = mapBaseInfo(wi, corporateNumber, collector);
        String womenActivityMergeKey = mapWomenActivity(wi, corporateNumber, collector);
        String compatibilityMergeKey = mapCompatibility(wi, corporateNumber, collector);

        String workplaceInfoMergeKey = mergeKeyOrNull(
                baseInfoMergeKey, womenActivityMergeKey, compatibilityMergeKey);

        if (workplaceInfoMergeKey != null) {
            collector.addWorkplaceInfo(corporateNumber, new WorkplaceInfoDto(
                    corporateNumber,
                    workplaceInfoMergeKey,
                    baseInfoMergeKey,
                    womenActivityMergeKey,
                    compatibilityMergeKey
            ));
        }
    }

    private String mapBaseInfo(WorkplaceInfo wi,
                               String corporateNumber,
                               BatchPayloadCollector collector) {
        if (wi.getBaseInfos() == null) return null;

        BaseInfos bi = wi.getBaseInfos();
        String mergeKey = bi.baseInfoMergeKey();
        if (mergeKey == null) return null;

        collector.addBaseInfo(corporateNumber, new BaseInfoDto(
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

    private String mapWomenActivity(WorkplaceInfo wi,
                                    String corporateNumber,
                                    BatchPayloadCollector collector) {
        if (wi.getWomenActivityInfos() == null) return null;

        WomenActivityInfos wa = wi.getWomenActivityInfos();
        String mergeKey = wa.womenActivityMergeKey();
        if (mergeKey == null) return null;

        collector.addWomenActivity(corporateNumber, new WomenActivityInfoDto(
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

    private String mapCompatibility(WorkplaceInfo wi,
                                    String corporateNumber,
                                    BatchPayloadCollector collector) {
        if (wi.getCompatibilityOfChildcareAndWork() == null) return null;

        CompatibilityOfChildcareAndWork cc = wi.getCompatibilityOfChildcareAndWork();
        String mergeKey = cc.compatChildcareMergeKey();
        if (mergeKey == null) return null;

        collector.addCompatibility(corporateNumber, new CompatibilityOfChildcareAndWorkDto(
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
                ? wi.getBaseInfos().baseInfoMergeKey()
                : null;
        String women = wi.getWomenActivityInfos() != null
                ? wi.getWomenActivityInfos().womenActivityMergeKey()
                : null;
        String compat = wi.getCompatibilityOfChildcareAndWork() != null
                ? wi.getCompatibilityOfChildcareAndWork().compatChildcareMergeKey()
                : null;

        return mergeKeyOrNull(base, women, compat);
    }

    private void mapItemInfos(GbizCompany company,
                              String corporateNumber,
                              BatchPayloadCollector collector) {

        mapItemInfoValues(company.getIndustry(), corporateNumber, true, collector);
        mapItemInfoValues(company.getBusinessItems(), corporateNumber, false, collector);
    }

    private void mapItemInfoValues(Collection<String> values,
                                   String corporateNumber,
                                   boolean isIndustry,
                                   BatchPayloadCollector collector) {

        forEachIfPresent(values, value -> {
            if (value == null || value.isBlank()) return;

            ItemInfoDto dto = buildItemInfoDto(corporateNumber, value, isIndustry);
            if (dto != null) {
                collector.addItemInfo(corporateNumber, dto);
            }
        });
    }

    private ItemInfoDto buildItemInfoDto(String corporateNumber,
                                         String value,
                                         boolean isIndustry) {
        String mergeKey = mergeKeyOrNull(normText(value), normBool(isIndustry));
        if (mergeKey == null) return null;

        return new ItemInfoDto(
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