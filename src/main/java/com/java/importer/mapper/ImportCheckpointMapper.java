package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ImportCheckpointMapper {
    int getEntryCount(@Param("entryName") String entryName, @Param("date") LocalDate date);

    void setCheckpoint(@Param("entryName") String entryName, @Param("date") LocalDate date);
}