package com.java.importer;

import com.java.importer.service.DataImporter;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final DataImporter importer;

    public Runner(DataImporter importer) {
        this.importer = importer;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        importer.importDataRemote();
    }
}