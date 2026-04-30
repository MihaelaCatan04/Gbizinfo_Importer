package com.java.importer.client;

import com.java.importer.model.export.EntityType;
import com.java.importer.model.export.NestedBatchRequest;
import com.java.importer.model.export.TopicBatchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WarehouseClient {

    private final RestClient restClient;

    public WarehouseClient(@Value("${warehouse.base-url}") String baseUrl, @Value("${exporter.security.username}") String username, @Value("${exporter.security.password}") String password) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).defaultHeaders(headers -> {
            headers.setBasicAuth(username, password);
            headers.set("Content-Type", "application/json");
        }).build();
    }

    public void postBatch(EntityType type, TopicBatchRequest<?> request) {
        restClient.post().uri(type.getEndpoint()).body(request).retrieve().toBodilessEntity();
    }

    public void postNestedBatch(EntityType type, NestedBatchRequest<?> request) {
        restClient.post().uri(type.getEndpoint()).body(request).retrieve().toBodilessEntity();
    }
}