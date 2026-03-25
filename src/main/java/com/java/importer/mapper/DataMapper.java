package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.io.InputStream;

@Mapper
public interface DataMapper {
    void copy(InputStream inputStream);
}
