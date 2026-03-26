package com.java.importer.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.importer.client.WarehouseClient;
import com.java.importer.mapper.CompanyEntryMapper;
import com.java.importer.mapper.ExportJobMapper;
import com.java.importer.service.CompanyPreparer;
import com.java.importer.util.DTOUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ExportWorker {
    private final ExportJobMapper exportJobMapper;
    private final CompanyPreparer companyPreparer;
    private final WarehouseClient warehouseClient;
    private final CompanyEntryMapper companyEntryMapper;
    @Value("${exporter.batch.size}")
    int batchSize;

    public ExportWorker(ExportJobMapper exportJobMapper, CompanyPreparer companyPreparer, WarehouseClient warehouseClient, CompanyEntryMapper companyEntryMapper, ObjectMapper objectMapper) {
        this.exportJobMapper = exportJobMapper;
        this.companyPreparer = companyPreparer;
        this.warehouseClient = warehouseClient;
        this.companyEntryMapper = companyEntryMapper;
    }

    public void run(String runId, String instanceId) {
        while (true) {
            List<String> batch = claimBatch(runId, instanceId);
            if (batch.isEmpty()) {
                log.info("No more companies to process, instance {} done", instanceId);
                break;
            }
            processBatch(runId, batch);
        }
    }

    private List<String> claimBatch(String runId, String instanceId) {
        List<String> batch = exportJobMapper.claimBatch(runId, instanceId, batchSize);
        log.info("Claimed batch of {} companies", batch.size());
        return batch;
    }

    private void processBatch(String runId, List<String> batch) {
        DTOUtil dtoUtil = new DTOUtil();
        List<String> successful = new ArrayList<>();

        for (String corporateNumber : batch) {
            if (processCompany(runId, corporateNumber, dtoUtil)) {
                successful.add(corporateNumber);
            }
        }

        finalizeBatch(runId, dtoUtil, successful);
    }

    private boolean processCompany(String runId, String corporateNumber, DTOUtil dtoUtil) {
        try {
            mapInfo(runId, corporateNumber, dtoUtil);
            return true;
        } catch (Exception e) {
            log.error("Failed to parse {}, marking failed", corporateNumber, e);
            exportJobMapper.markFailed(runId, corporateNumber);
            return false;
        }
    }

    private void finalizeBatch(String runId, DTOUtil dtoUtil, List<String> successful) {
        postInfo(dtoUtil);
        successful.forEach(cn -> exportJobMapper.markDone(runId, cn));
    }


    private void mapInfo(String runId, String corporateNumber, DTOUtil dtoUtil)
            throws Exception {
        String raw = companyEntryMapper.findRawByCorporateNumber(corporateNumber);
        companyPreparer.mapInto(runId, corporateNumber, raw, dtoUtil);
    }

    private void postInfo(DTOUtil dtoUtil) {
        if (!dtoUtil.getCompanyList().isEmpty())
            warehouseClient.postCompanies(dtoUtil.getCompanyList());
        if (!dtoUtil.getPatentList().isEmpty())
            warehouseClient.postPatents(dtoUtil.getPatentList());
        if (!dtoUtil.getClassificationList().isEmpty())
            warehouseClient.postClassifications(dtoUtil.getClassificationList());
        if (!dtoUtil.getFinanceList().isEmpty())
            warehouseClient.postFinances(dtoUtil.getFinanceList());
        if (!dtoUtil.getMajorShareholderList().isEmpty())
            warehouseClient.postMajorShareholders(dtoUtil.getMajorShareholderList());
        if (!dtoUtil.getManagementIndexList().isEmpty())
            warehouseClient.postManagementIndexes(dtoUtil.getManagementIndexList());
        if (!dtoUtil.getCommendationList().isEmpty())
            warehouseClient.postCommendations(dtoUtil.getCommendationList());
        if (!dtoUtil.getCertificationList().isEmpty())
            warehouseClient.postCertifications(dtoUtil.getCertificationList());
        if (!dtoUtil.getSubsidyList().isEmpty())
            warehouseClient.postSubsidies(dtoUtil.getSubsidyList());
        if (!dtoUtil.getProcurementList().isEmpty())
            warehouseClient.postProcurements(dtoUtil.getProcurementList());
        if (!dtoUtil.getItemInfoList().isEmpty())
            warehouseClient.postItemInfos(dtoUtil.getItemInfoList());
        if (!dtoUtil.getBaseInfoList().isEmpty())
            warehouseClient.postBaseInfos(dtoUtil.getBaseInfoList());
        if (!dtoUtil.getWomenActivityInfoList().isEmpty())
            warehouseClient.postWomenActivities(dtoUtil.getWomenActivityInfoList());
        if (!dtoUtil.getCompatibilityOfChildcareAndWorkList().isEmpty())
            warehouseClient.postCompatibilities(dtoUtil.getCompatibilityOfChildcareAndWorkList());
        if (!dtoUtil.getWorkplaceInfoList().isEmpty())
            warehouseClient.postWorkplaceInfos(dtoUtil.getWorkplaceInfoList());
    }
}
