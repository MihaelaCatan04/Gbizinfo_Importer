package com.java.importer.model.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class ExportJob {
    private LocalDate checkpointDate;
    private String corporateNumber;
}