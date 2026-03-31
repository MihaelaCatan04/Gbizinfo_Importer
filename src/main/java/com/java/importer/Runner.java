package com.java.importer;

import com.java.importer.exporter.ExportCoordinator;
import com.java.importer.service.DataImporter;
import com.java.importer.service.DatabasePreparer;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final DataImporter importer;
    private final ExportCoordinator exportCoordinator;
    private final DatabasePreparer databasePreparer;

    public Runner(DataImporter importer, ExportCoordinator exportCoordinator, DatabasePreparer databasePreparer) {
        this.importer = importer;
        this.exportCoordinator = exportCoordinator;
        this.databasePreparer = databasePreparer;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        databasePreparer.prepareDatabase();
        importer.importFromLocalFolder();
        exportCoordinator.run();
        databasePreparer.prepareDatabase();
    }
}