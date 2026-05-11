package com.java.importer.model.importer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class ProcessedFile {
    private Long id;
    private String path;
    private LocalDate fileDate;
}