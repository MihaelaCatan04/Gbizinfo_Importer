package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.InputStream;
import java.time.LocalDate;

@Mapper
public interface DataMapper {
    int copy(@Param("inputStream") InputStream inputStream,
             @Param("dateInserted") LocalDate dateInserted);
}