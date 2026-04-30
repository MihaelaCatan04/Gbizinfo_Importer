package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExportJobMapper {
    void createJobsForDate(@Param("transactionDate") LocalDate transactionDate);

    List<String> claimBatch(@Param("workerId") String workerId, @Param("batchSize") int batchSize, @Param("transactionDate") LocalDate transactionDate, @Param("leaseSeconds") int leaseSeconds, @Param("maxAttempts") int maxAttempts);

    int markJobDone(@Param("transactionDate") LocalDate transactionDate, @Param("corporateNumber") String corporateNumber, @Param("workerId") String workerId);

    int markJobFailed(@Param("transactionDate") LocalDate transactionDate, @Param("corporateNumber") String corporateNumber, @Param("workerId") String workerId, @Param("error") String error);
}