package com.java.importer.model.export;

import com.java.importer.model.dto.ManagementIndexDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FinanceManagementIndexSnapshot {
    private String financeMergeKey;
    private List<ManagementIndexDto> managementIndexes;
}
