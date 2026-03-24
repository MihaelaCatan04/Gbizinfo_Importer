package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.zip.ZipInputStream;

@Mapper
public interface DataMapper {
    void copy(ZipInputStream zipInputStream);
}
