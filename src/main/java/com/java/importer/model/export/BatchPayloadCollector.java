package com.java.importer.model.export;

import com.java.importer.model.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchPayloadCollector {

    public final Map<String, List<ClassificationDto>> classificationsByPatent = new HashMap<>();
    public final List<String> patentKeys = new ArrayList<>();
    private final Map<String, List<CompanyDto>> companies = new HashMap<>();
    private final Map<String, List<PatentDto>> patents = new HashMap<>();
    private final Map<String, List<FinanceDto>> finances = new HashMap<>();
    private final Map<String, List<MajorShareholderDto>> shareholdersByFinance = new HashMap<>();
    private final List<String> financeKeysForShareholders = new ArrayList<>();
    private final Map<String, List<ManagementIndexDto>> managementIndexesByFinance = new HashMap<>();
    private final List<String> financeKeysForManagementIndexes = new ArrayList<>();
    private final Map<String, List<CommendationDto>> commendations = new HashMap<>();
    private final Map<String, List<CertificationDto>> certifications = new HashMap<>();
    private final Map<String, List<SubsidyDto>> subsidies = new HashMap<>();
    private final Map<String, List<ProcurementDto>> procurements = new HashMap<>();
    private final Map<String, List<ItemInfoDto>> itemInfos = new HashMap<>();
    private final Map<String, List<BaseInfoDto>> baseInfos = new HashMap<>();
    private final Map<String, List<WomenActivityInfoDto>> womenActivities = new HashMap<>();
    private final Map<String, List<CompatibilityOfChildcareAndWorkDto>> compatibilities = new HashMap<>();
    private final Map<String, List<WorkplaceInfoDto>> workplaceInfos = new HashMap<>();

    private final Map<String, String> corporateNumberByPatentKey = new HashMap<>();
    private final Map<String, String> corporateNumberByFinanceKey = new HashMap<>();

    private static <T> TopicBatchRequest<T> build(List<String> corporateNumbers, Map<String, List<T>> source) {
        List<CompanySnapshot<T>> companies = new ArrayList<>();

        for (String corporateNumber : corporateNumbers) {
            companies.add(new CompanySnapshot<>(corporateNumber, source.getOrDefault(corporateNumber, List.of())));
        }

        return new TopicBatchRequest<>(companies);
    }

    public void addCompany(String corporateNumber, CompanyDto dto) {
        companies.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addPatent(String corporateNumber, PatentDto dto) {
        patents.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addClassification(String patentMergeKey, ClassificationDto dto) {
        classificationsByPatent.computeIfAbsent(patentMergeKey, k -> new ArrayList<>()).add(dto);
    }

    public void addFinance(String corporateNumber, FinanceDto dto) {
        finances.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addMajorShareholder(String financeMergeKey, MajorShareholderDto dto) {
        shareholdersByFinance.computeIfAbsent(financeMergeKey, k -> new ArrayList<>()).add(dto);
    }

    public void addManagementIndex(String financeMergeKey, ManagementIndexDto dto) {
        managementIndexesByFinance.computeIfAbsent(financeMergeKey, k -> new ArrayList<>()).add(dto);
    }

    public void addCommendation(String corporateNumber, CommendationDto dto) {
        commendations.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addCertification(String corporateNumber, CertificationDto dto) {
        certifications.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addSubsidy(String corporateNumber, SubsidyDto dto) {
        subsidies.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addProcurement(String corporateNumber, ProcurementDto dto) {
        procurements.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addItemInfo(String corporateNumber, ItemInfoDto dto) {
        itemInfos.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addBaseInfo(String corporateNumber, BaseInfoDto dto) {
        baseInfos.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addWomenActivity(String corporateNumber, WomenActivityInfoDto dto) {
        womenActivities.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addCompatibility(String corporateNumber, CompatibilityOfChildcareAndWorkDto dto) {
        compatibilities.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public void addWorkplaceInfo(String corporateNumber, WorkplaceInfoDto dto) {
        workplaceInfos.computeIfAbsent(corporateNumber, k -> new ArrayList<>()).add(dto);
    }

    public TopicBatchRequest<CompanyDto> companyRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, companies);
    }

    public TopicBatchRequest<PatentDto> patentRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, patents);
    }

    public TopicBatchRequest<FinanceDto> financeRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, finances);
    }

    public MajorShareholderBatchRequest majorShareholderRequest() {
        List<FinanceMajorShareholderSnapshot> financeSnapshots = new ArrayList<>();

        for (String financeMergeKey : financeKeysForShareholders) {
            String corporateNumber = corporateNumberByFinanceKey.get(financeMergeKey);
            financeSnapshots.add(new FinanceMajorShareholderSnapshot(
                    corporateNumber,
                    financeMergeKey,
                    shareholdersByFinance.getOrDefault(financeMergeKey, List.of())
            ));
        }

        return new MajorShareholderBatchRequest(financeSnapshots);
    }

    public ManagementIndexBatchRequest managementIndexRequest() {
        List<FinanceManagementIndexSnapshot> financeSnapshots = new ArrayList<>();

        for (String financeMergeKey : financeKeysForManagementIndexes) {
            String corporateNumber = corporateNumberByFinanceKey.get(financeMergeKey);
            financeSnapshots.add(new FinanceManagementIndexSnapshot(
                    corporateNumber,
                    financeMergeKey,
                    managementIndexesByFinance.getOrDefault(financeMergeKey, List.of())
            ));
        }

        return new ManagementIndexBatchRequest(financeSnapshots);
    }

    public TopicBatchRequest<CommendationDto> commendationRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, commendations);
    }

    public TopicBatchRequest<CertificationDto> certificationRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, certifications);
    }

    public TopicBatchRequest<SubsidyDto> subsidyRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, subsidies);
    }

    public TopicBatchRequest<ProcurementDto> procurementRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, procurements);
    }

    public TopicBatchRequest<ItemInfoDto> itemInfoRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, itemInfos);
    }

    public TopicBatchRequest<BaseInfoDto> baseInfoRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, baseInfos);
    }

    public TopicBatchRequest<WomenActivityInfoDto> womenActivityRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, womenActivities);
    }

    public TopicBatchRequest<CompatibilityOfChildcareAndWorkDto> compatibilityRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, compatibilities);
    }

    public TopicBatchRequest<WorkplaceInfoDto> workplaceInfoRequest(List<String> corporateNumbers) {
        return build(corporateNumbers, workplaceInfos);
    }

    public void registerPatentKey(String patentMergeKey, String corporateNumber) {
        if (!patentKeys.contains(patentMergeKey)) {
            patentKeys.add(patentMergeKey);
            corporateNumberByPatentKey.put(patentMergeKey, corporateNumber);
        }
    }

    public void registerFinanceKeyForShareholders(String financeMergeKey, String corporateNumber) {
        if (!financeKeysForShareholders.contains(financeMergeKey)) {
            financeKeysForShareholders.add(financeMergeKey);
            corporateNumberByFinanceKey.put(financeMergeKey, corporateNumber);
        }
    }

    public void registerFinanceKeyForManagementIndexes(String financeMergeKey, String corporateNumber) {
        if (!financeKeysForManagementIndexes.contains(financeMergeKey)) {
            financeKeysForManagementIndexes.add(financeMergeKey);
            corporateNumberByFinanceKey.putIfAbsent(financeMergeKey, corporateNumber);
        }
    }

    public ClassificationBatchRequest classificationRequest() {
        List<PatentClassificationSnapshot> patentSnapshots = new ArrayList<>();

        for (String patentMergeKey : patentKeys) {
            String corporateNumber = corporateNumberByPatentKey.get(patentMergeKey);
            patentSnapshots.add(new PatentClassificationSnapshot(
                    corporateNumber,
                    patentMergeKey,
                    classificationsByPatent.getOrDefault(patentMergeKey, List.of())
            ));
        }

        return new ClassificationBatchRequest(patentSnapshots);
    }
}