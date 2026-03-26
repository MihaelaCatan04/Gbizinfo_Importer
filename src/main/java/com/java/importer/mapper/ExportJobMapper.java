package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExportJobMapper {
    void populate(@Param("runId") String runId);

    List<String> claimBatch(@Param("runId") String runId, @Param("instanceId") String instanceId, @Param("batchSize") int batchSize);

    void markDone(@Param("runId") String runId, @Param("corporateNumber") String corporateNumber);

    void markFailed(@Param("runId") String runId, @Param("corporateNumber") String corporateNumber);
}
