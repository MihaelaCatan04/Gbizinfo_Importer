package com.java.importer.controller;

import com.java.importer.service.Importer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/executor")
@RequiredArgsConstructor
@Tag(name = "DataReceiver", description = "Endpoint to trigger executor")
public class DataReceiverController {

    private final Importer importer;

    @PostMapping("/trigger")
    @Operation(summary = "Trigger import + export pipeline")
    public ResponseEntity<String> trigger() {
        try {
            importer.processAllFiles();
            return ResponseEntity.ok("Pipeline complete.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Pipeline failed: " + e.getMessage());
        }
    }
}