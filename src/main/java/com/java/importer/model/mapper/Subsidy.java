package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class Subsidy {
    @JsonProperty("date_of_approval")
    private String dateOfApproval;

    @JsonProperty("title")
    private String title;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("target")
    private String target;

    @JsonProperty("government_departments")
    private String governmentDepartments;

    public String subsidyMergeKey() {
        return mergeKeyOrNull(normDate(this.dateOfApproval), normText(this.title), normText(this.amount), normText(this.target), normText(this.governmentDepartments));
    }
}