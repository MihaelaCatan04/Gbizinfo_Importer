package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClassificationDto {
    private String patentMergeKey;
    private String mergeKey;
    private String codeValue;
    private String codeName;
    private String japanese;
}