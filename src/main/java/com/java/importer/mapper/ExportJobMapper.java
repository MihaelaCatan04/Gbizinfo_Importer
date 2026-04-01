package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExportJobMapper {
    void populate(LocalDate checkpointDate);

    List<String> claimBatch(String instanceId, int batchSize, LocalDate checkpointDate);

    void markDone(LocalDate checkpointDate, String corporateNumber);

    void markFailed(LocalDate checkpointDate, String corporateNumber);
}
