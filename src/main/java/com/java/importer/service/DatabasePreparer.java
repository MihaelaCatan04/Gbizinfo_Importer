package com.java.importer.service;

import com.java.importer.mapper.PrepMapper;
import org.springframework.stereotype.Service;

@Service
public class DatabasePreparer {
    private final PrepMapper prepMapper;

    public DatabasePreparer(PrepMapper prepMapper) {
        this.prepMapper = prepMapper;
    }

    public void prepareDatabase() {
        prepMapper.clearDataTables();
    }

}
