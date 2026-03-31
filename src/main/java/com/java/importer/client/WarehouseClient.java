package com.java.importer.client;

import com.java.importer.model.export.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WarehouseClient {

    private final RestClient restClient;

    public WarehouseClient(@Value("${warehouse.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public void postBatch(EntityType type, TopicBatchRequest<?> request) {
        restClient.post()
                .uri(type.getEndpoint())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void postClassificationBatch(ClassificationBatchRequest request) {
        restClient.post()
                .uri(EntityType.CLASSIFICATION.getEndpoint())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void postMajorShareholderBatch(MajorShareholderBatchRequest request) {
        restClient.post()
                .uri(EntityType.MAJOR_SHAREHOLDER.getEndpoint())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void postManagementIndexBatch(ManagementIndexBatchRequest request) {
        restClient.post()
                .uri(EntityType.MANAGEMENT_INDEX.getEndpoint())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}