package com.java.importer.model.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CompanySnapshot<T> {
    private String corporateNumber;
    private List<T> entities;
}