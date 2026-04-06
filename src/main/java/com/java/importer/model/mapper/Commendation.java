package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class Commendation {
    @JsonProperty("date_of_commendation")
    private LocalDate dateOfCommendation;

    @JsonProperty("title")
    private String title;

    @JsonProperty("target")
    private String target;

    @JsonProperty("category")
    private String category;

    @JsonProperty("government_departments")
    private String governmentDepartments;

    @JsonProperty("note")
    private String note;

    public String commendationMergeKey() {
        return mergeKeyOrNull(normDate(String.valueOf(this.dateOfCommendation)), normText(this.title), normText(this.target), normText(this.category), normText(this.governmentDepartments), normText(this.note));
    }
}