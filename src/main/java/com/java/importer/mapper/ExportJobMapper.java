package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExportJobMapper {
    void populate(LocalDate checkpointDate);

    List<String> claimBatch(@Param("instanceId") String instanceId, @Param("batchSize") int batchSize, @Param("checkpointDate") LocalDate checkpointDate, @Param("leaseSeconds") int leaseSeconds, @Param("maxAttempts") int maxAttempts);

    void markDone(@Param("checkpointDate") LocalDate checkpointDate, @Param("corporateNumber") String corporateNumber, @Param("instanceId") String instanceId);

    void markFailed(@Param("checkpointDate") LocalDate checkpointDate, @Param("corporateNumber") String corporateNumber, @Param("instanceId") String instanceId);
}