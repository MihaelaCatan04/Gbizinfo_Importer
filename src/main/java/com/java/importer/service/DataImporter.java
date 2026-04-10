package com.java.importer.service;

import com.java.importer.mapper.DataMapper;
import com.java.importer.mapper.ImportFailureMapper;
import com.java.importer.mapper.PrepMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j2
@Service
public class DataImporter {
    private static final String PARAMS_TEMPLATE = "apiToken=%s&downfile=Hojinjoho&meta=META&downenc=UTF-8&isZip=on&downtype=zip";

    private final PrepMapper prepMapper;
    private final DataMapper dataMapper;
    private final TransactionTemplate transactionTemplate;
    private final PipelineControlService pipelineControlService;
    private final ImportFailureMapper importFailureMapper;

    @Value("${apiToken}")
    private String apiToken;
    @Value("${app.importer.url}")
    private String url;
    @Value("${local-path}")
    private String localPath;
    @Value("${node.id}")
    private String nodeId;
    @Value("${importer.max-attempts}")
    private int MAX_ATTEMPTS;

    public DataImporter(PrepMapper prepMapper, DataMapper dataMapper, TransactionTemplate transactionTemplate, PipelineControlService pipelineControlService, ImportFailureMapper importFailureMapper) {
        this.prepMapper = prepMapper;
        this.dataMapper = dataMapper;
        this.transactionTemplate = transactionTemplate;
        this.pipelineControlService = pipelineControlService;
        this.importFailureMapper = importFailureMapper;
    }

    public void importDataRemote(LocalDate transactionDate) throws Exception {
        executeRemoteImport(transactionDate);
    }

    private void executeRemoteImport(LocalDate transactionDate) throws Exception {
        HttpURLConnection http = openConnection();
        try {
            sendRequestParams(http);
            validateResponse(http);
            processResponse(http, transactionDate);
        } finally {
            http.disconnect();
        }
    }

    private void processResponse(HttpURLConnection http, LocalDate transactionDate) throws IOException {
        try (InputStream in = http.getInputStream();
             ZipInputStream zip = new ZipInputStream(in, StandardCharsets.UTF_8)) {
            processZipInputStream(zip, transactionDate);
        }
    }


    public void importFromLocalFolder(LocalDate transactionDate) throws Exception {
        Path folder = validateLocalFolder();

        List<Path> files = listJsonFiles(folder);
        if (files.isEmpty()) {
            log.warn("No .json files found in {}", folder);
            return;
        }

        for (Path file : files) {
            processLocalFile(file, transactionDate);
        }
    }

    private void processLocalFile(Path file, LocalDate transactionDate) {
        String name = file.getFileName().toString();

        try {
            transactionTemplate.executeWithoutResult(status -> {
                if (!pipelineControlService.renewImportLease(nodeId)) {
                    throw new IllegalStateException("Import lease lost for node " + nodeId);
                }
                if (shouldSkipEntry(name, transactionDate)) {
                    return;
                }
                try (var inputStream = Files.newInputStream(file)) {
                    dataMapper.copy(inputStream, transactionDate);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                handleEntrySuccess(name, transactionDate);
            });
        } catch (Exception e) {
            handleEntryFailure(name, transactionDate, e);
        }
    }

    private void processZipEntry(ZipInputStream zip, String name, LocalDate transactionDate) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                if (!pipelineControlService.renewImportLease(nodeId)) {
                    throw new IllegalStateException("Import lease lost for node " + nodeId);
                }
                if (shouldSkipEntry(name, transactionDate)) {
                    return;
                }
                try {
                    dataMapper.copy(zip, transactionDate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                handleEntrySuccess(name, transactionDate);
            });
        } catch (Exception e) {
            handleEntryFailure(name, transactionDate, e);
        }
    }

    private Boolean checkAlreadyExists(String name, LocalDate transactionDate) {
        if (prepMapper.getEntryCount(name, transactionDate) > 0) {
            log.info("Entry {} already processed -> skipping.", name);
            return true;
        }
        return false;
    }

    private void setCheckpoint(String name, LocalDate transactionDate) {
        prepMapper.setCheckpoint(name, transactionDate);
        log.info("Committed {}", name);
    }

    private List<Path> listJsonFiles(Path folder) throws IOException {
        try (var stream = Files.list(folder)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }
    }

    private Path validateLocalFolder() {
        Path folder = Path.of(localPath);
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("local-path is not a directory: " + folder);
        }
        return folder;
    }

    private void processZipInputStream(ZipInputStream zip, LocalDate transactionDate) throws IOException {
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            String name = entry.getName();
            try {
                if (entry.isDirectory() || !name.toLowerCase().endsWith(".json")) {
                    log.warn("Skipping non-JSON entry: {}", name);
                    continue;
                }
                processZipEntry(zip, name, transactionDate);
            } finally {
                zip.closeEntry();
            }
        }
    }

    private HttpURLConnection openConnection() throws Exception {
        HttpURLConnection http = (HttpURLConnection) new URI(url).toURL().openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setConnectTimeout(10_000);
        http.setReadTimeout(300_000);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return http;
    }

    private void sendRequestParams(HttpURLConnection http) throws IOException {
        http.getOutputStream().write(String.format(PARAMS_TEMPLATE, apiToken).getBytes(StandardCharsets.UTF_8));
    }

    private void validateResponse(HttpURLConnection http) throws IOException {
        int status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            return;
        }
        String body = "";
        if (http.getErrorStream() != null) {
            body = new String(http.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        }
        throw new IOException("Unexpected HTTP status " + status + ": " + body);
    }

    private boolean shouldSkipEntry(String name, LocalDate transactionDate) {
        if (checkAlreadyExists(name, transactionDate)) {
            return true;
        }
        int attempts = importFailureMapper.getFailureCount(name, transactionDate);
        if (attempts >= MAX_ATTEMPTS) {
            log.warn("Entry {} has failed {} times, skipping permanently", name, attempts);
            return true;
        }
        return false;
    }

    private void handleEntryFailure(String name, LocalDate transactionDate, Exception e) {
        String error = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        importFailureMapper.recordFailure(name, transactionDate, error);
        int attempts = importFailureMapper.getFailureCount(name, transactionDate);
        log.error("Failed on {} (attempt {}/{}), rolling back", name, attempts, MAX_ATTEMPTS, e);
    }

    private void handleEntrySuccess(String name, LocalDate transactionDate) {
        setCheckpoint(name, transactionDate);
        importFailureMapper.deleteFailure(name, transactionDate);
    }
}