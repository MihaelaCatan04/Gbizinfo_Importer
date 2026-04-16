package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface CompanyEntryMapper {
    String findRawByCorporateNumber(@Param("checkpointDate") LocalDate checkpointDate, @Param("corporateNumber") String corporateNumber);
}
