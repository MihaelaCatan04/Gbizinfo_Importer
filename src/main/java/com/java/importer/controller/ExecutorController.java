package com.java.importer.controller;

import com.java.importer.model.request.TriggerRequest;
import com.java.importer.service.ExecutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/executor")
@RequiredArgsConstructor
@Tag(name = "Executor", description = "Endpoint to trigger executor")
public class ExecutorController {

    private final ExecutorService executorService;

    @PostMapping("/trigger")
    @Operation(summary = "Trigger executor to read data from local folder and export it to Companyhouse")
    public ResponseEntity<String> trigger(@RequestBody TriggerRequest request) {
        try {
            executorService.runImporter(request.folder());
            return ResponseEntity.ok("Import triggered for: " + request.folder());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Import failed: " + e.getMessage());
        }
    }
}
