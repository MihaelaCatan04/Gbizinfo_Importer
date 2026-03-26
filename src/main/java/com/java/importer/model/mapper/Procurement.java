package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class Procurement {
    @JsonProperty("date_of_order")
    private String dateOfOrder;

    @JsonProperty("title")
    private String title;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("government_departments")
    private String governmentDepartments;

    @JsonProperty("note")
    private String note;

    public String procurementMergeKey() {
        return mergeKeyOrNull(normTimestamp(this.dateOfOrder), normText(this.title), normLong(this.amount), normText(this.governmentDepartments), normText(this.note));
    }
}