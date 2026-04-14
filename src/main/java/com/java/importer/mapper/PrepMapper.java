package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PrepMapper {
    void clearDataTables();

    int getEntryCount(@Param("name") String name, @Param("transaction_date") LocalDate transactionDate);

    int setCheckpoint(@Param("name") String name, @Param("transaction_date") LocalDate transactionDate);
}
