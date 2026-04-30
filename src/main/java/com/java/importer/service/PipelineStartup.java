package com.java.importer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PipelineStartup {

    private final ExecutorService executorService;

    public PipelineStartup(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("App started — resuming export if in progress");
        executorService.runExporter();
    }
}