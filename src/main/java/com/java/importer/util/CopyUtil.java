package com.java.importer.util;

public record CopyUtil() {
    public static final String COPY_METHOD = "com.java.importer.mapper.DataMapper.copy";
    public static final String COPY_JSON_ENTRY = "COPY company_entry(entry) FROM STDIN WITH (FORMAT csv)";

}
