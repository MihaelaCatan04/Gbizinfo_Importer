package com.java.importer.model.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicBatchRequest<T> {
    private List<CompanySnapshot<T>> companies;
}
