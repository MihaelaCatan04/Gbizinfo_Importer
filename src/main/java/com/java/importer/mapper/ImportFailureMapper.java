package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ImportFailureMapper {

    void recordFailure(@Param("entryName") String entryName, @Param("transactionDate") LocalDate transactionDate, @Param("lastError") String lastError);

    int getFailureCount(@Param("entryName") String entryName, @Param("transactionDate") LocalDate transactionDate);

    void deleteFailure(@Param("entryName") String entryName, @Param("transactionDate") LocalDate transactionDate);
}