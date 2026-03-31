package com.java.importer.model.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MajorShareholderBatchRequest {
    private List<FinanceMajorShareholderSnapshot> finances;
}
