package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CompanyEntryMapper {
    String findRawByCorporateNumber(@Param("corporateNumber") String corporateNumber);
}
