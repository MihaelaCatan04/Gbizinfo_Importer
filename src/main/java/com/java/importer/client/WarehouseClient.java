package com.java.importer.client;

import com.java.importer.model.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class WarehouseClient {

    private final RestClient restClient;

    public WarehouseClient(@Value("${warehouse.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public void postCompanies(List<CompanyDto> list) {
        post("/companies", list);
    }

    public void postPatents(List<PatentDto> list) {
        post("/patents", list);
    }

    public void postClassifications(List<ClassificationDto> list) {
        post("/classifications", list);
    }

    public void postFinances(List<FinanceDto> list) {
        post("/finances", list);
    }

    public void postMajorShareholders(List<MajorShareholderDto> list) {
        post("/major-shareholders", list);
    }

    public void postManagementIndexes(List<ManagementIndexDto> list) {
        post("/management-indexes", list);
    }

    public void postCommendations(List<CommendationDto> list) {
        post("/commendations", list);
    }

    public void postCertifications(List<CertificationDto> list) {
        post("/certifications", list);
    }

    public void postSubsidies(List<SubsidyDto> list) {
        post("/subsidies", list);
    }

    public void postProcurements(List<ProcurementDto> list) {
        post("/procurements", list);
    }

    public void postItemInfos(List<ItemInfoDto> list) {
        post("/item-infos", list);
    }

    public void postBaseInfos(List<BaseInfoDto> list) {
        post("/base-infos", list);
    }

    public void postWomenActivities(List<WomenActivityInfoDto> list) {
        post("/women-activities", list);
    }

    public void postCompatibilities(List<CompatibilityOfChildcareAndWorkDto> list) {
        post("/compatibilities", list);
    }

    public void postWorkplaceInfos(List<WorkplaceInfoDto> list) {
        post("/workplace-infos", list);
    }

    public void completeEntity(String runId, String entity) {
        post("/run/" + runId + "/complete/" + entity, null);
    }

    public void completeRun(String runId) {
        post("/run/" + runId + "/complete", null);
    }

    private void post(String path, Object body) {
        var request = restClient.post().uri(path);
        if (body != null) {
            request.body(body);
        }
        request.retrieve().toBodilessEntity();
    }
}