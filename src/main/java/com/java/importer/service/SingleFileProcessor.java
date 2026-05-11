package com.java.importer.service;

import com.java.importer.mapper.FileMapper;
import com.java.importer.model.importer.ProcessedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class SingleFileProcessor {

    private final FileMapper fileMapper;
    private final DataImporter dataImporter;

    @Transactional
    public Long processFile() {
        ProcessedFile file = fileMapper.claimNextFile();
        if (file == null) {
            return null;
        }

        Path filePath = Path.of(file.getPath());
        dataImporter.processFile(filePath, file.getFileDate());
        fileMapper.markFileDone(file.getId());

        return file.getId();
    }
}