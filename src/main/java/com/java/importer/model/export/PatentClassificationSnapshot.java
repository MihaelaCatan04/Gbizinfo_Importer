package com.java.importer.model.export;

import com.java.importer.model.dto.ClassificationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PatentClassificationSnapshot {
    private String corporateNumber;
    private String patentMergeKey;
    private List<ClassificationDto> classifications;
}