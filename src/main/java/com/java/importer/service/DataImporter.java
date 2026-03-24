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
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j2
@Service
public class DataImporter {
    private static final String PARAMS_TEMPLATE = "apiToken=%s&downfile=Hojinjoho&meta=META&downenc=UTF-8&isZip=on&downtype=zip";

    private final PrepMapper prepMapper;
    private final DataMapper dataMapper;
    private final TransactionTemplate transactionTemplate;

    @Value("${apiToken}")
    private String apiToken;
    @Value("${app.importer.url}")
    private String url;

    public DataImporter(PrepMapper prepMapper, DataMapper dataMapper, TransactionTemplate transactionTemplate) {
        this.prepMapper = prepMapper;
        this.dataMapper = dataMapper;
        this.transactionTemplate = transactionTemplate;
    }

    public void importDataRemote() throws Exception {
        LocalDate transactionDate = LocalDate.now();
        prepMapper.clearDataTables(); // -> TODO delete this
        executeRemoteImport(transactionDate);
        //TODO Before clear data, do export
        //prepMapper.clearDataTables();

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

    private void processZipInputStream(ZipInputStream zip, LocalDate transactionDate) throws IOException {
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            String name = entry.getName();

            if (entry.isDirectory() || !name.toLowerCase().endsWith(".json")) {
                log.warn("Skipping non-JSON entry: {}", name);
                zip.closeEntry();
                continue;
            }

            processEntry(zip, name, transactionDate);
            zip.closeEntry();
        }
    }

    private void processEntry(ZipInputStream zip, String name, LocalDate transactionDate) {
        try {
            transactionTemplate.executeWithoutResult(status -> processOrSkip(zip, name, transactionDate));
        } catch (Exception e) {
            log.error("Failed on {}, rolling back", name, e);
        }
    }

    private void processOrSkip(ZipInputStream zip, String name, LocalDate transactionDate) {
        if (prepMapper.getEntryCount(name, transactionDate) > 0) {
            log.info("Entry {} already processed -> skipping.", name);
            return;
        }

        dataMapper.copy(zip);
        prepMapper.setCheckpoint(name, transactionDate);
        log.info("Committed {}", name);
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