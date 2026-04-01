package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExportJobMapper {
    void populate();

    List<String> claimBatch(@Param("instanceId") String instanceId, @Param("batchSize") int batchSize, @Param("checkpointDate") LocalDate checkpointDate);

    void markDone(@Param("corporateNumber") String corporateNumber);

    void markFailed(@Param("corporateNumber") String corporateNumber);
}
