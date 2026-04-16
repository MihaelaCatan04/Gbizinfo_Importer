package com.java.importer.model.export;

import com.java.importer.model.dto.MajorShareholderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FinanceMajorShareholderSnapshot {
    private String corporateNumber;
    private String financeMergeKey;
    private List<MajorShareholderDto> shareholders;
}
