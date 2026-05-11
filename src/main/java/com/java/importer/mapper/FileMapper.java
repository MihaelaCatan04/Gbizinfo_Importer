package com.java.importer.mapper;

import com.java.importer.model.importer.ProcessedFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

    ProcessedFile claimNextFile();

    void markFileDone(long id);

    int countUnprocessed();
}