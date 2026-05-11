package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ImportFailureMapper {
    Integer getFailureCount(@Param("entryName") String entryName, @Param("date") LocalDate date);

    void recordFailure(@Param("entryName") String entryName, @Param("date") LocalDate date, @Param("error") String error);

    void deleteFailure(@Param("entryName") String entryName, @Param("date") LocalDate date);
}