package com.java.importer.service;

import com.java.importer.mapper.DataMapper;
import com.java.importer.mapper.PrepMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
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

    @Value("${apiToken}")
    private String apiToken;
    @Value("${app.importer.url}")
    private String url;
    @Value("${local-path}")
    private String localPath;
    @Value("${node.id}")
    private String nodeId;

    public DataImporter(PrepMapper prepMapper, DataMapper dataMapper, TransactionTemplate transactionTemplate, PipelineControlService pipelineControlService) {
        this.prepMapper = prepMapper;
        this.dataMapper = dataMapper;
        this.transactionTemplate = transactionTemplate;
        this.pipelineControlService = pipelineControlService;
    }

    public void importDataRemote(LocalDate transactionDate) throws Exception {
        executeRemoteImport(transactionDate);
    }

    private void executeRemoteImport(LocalDate transactionDate) throws Exception {
        HttpURLConnection http = openConnection();
        sendRequestParams(http);
        try (ZipInputStream zip = new ZipInputStream(http.getInputStream(), StandardCharsets.UTF_8)) {
            processZipInputStream(zip, transactionDate);
        } finally {
            http.disconnect();
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
                if (checkAlreadyExists(name, transactionDate)) {
                    return;
                }
                try (var inputStream = Files.newInputStream(file)) {
                    dataMapper.copy(inputStream, transactionDate);
                } catch (IOException e) {
                    log.error("Exception ", e);
                }
                setCheckpoint(name, transactionDate);

            });
        } catch (Exception e) {
            log.error("Failed on {}, rolling back", name, e);
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
            return stream.filter(path -> path.getFileName().toString().endsWith(".json")).sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
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

    private void processZipEntry(ZipInputStream zip, String name, LocalDate transactionDate) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                if (!pipelineControlService.renewImportLease(nodeId)) {
                    throw new IllegalStateException("Import lease lost for node " + nodeId);
                }
                if (checkAlreadyExists(name, transactionDate)) {
                    return;
                }
                dataMapper.copy(zip, transactionDate);
                setCheckpoint(name, transactionDate);
            });
        } catch (Exception e) {
            log.error("Failed on {}, rolling back", name, e);
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
}