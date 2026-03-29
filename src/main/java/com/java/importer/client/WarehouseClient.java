package com.java.importer.client;

import com.java.importer.model.dto.*;
import com.java.importer.model.export.EntityType;
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

    public void postEntities(EntityType type, List<?> entities) {
        post(type.getEndpoint(), entities);
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