package com.java.importer.mapper;

import com.java.importer.model.export.ExportJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExportJobMapper {

    void createAllPendingJobs();

    List<ExportJob> claimBatch(@Param("workerId") String workerId, @Param("batchSize") int batchSize, @Param("leaseSeconds") int leaseSeconds, @Param("maxAttempts") int maxAttempts);

    void markJobDone(@Param("checkpointDate") LocalDate checkpointDate, @Param("corporateNumber") String corporateNumber, @Param("workerId") String workerId);

    void markJobFailed(@Param("checkpointDate") LocalDate checkpointDate, @Param("corporateNumber") String corporateNumber, @Param("workerId") String workerId, @Param("error") String error);
}