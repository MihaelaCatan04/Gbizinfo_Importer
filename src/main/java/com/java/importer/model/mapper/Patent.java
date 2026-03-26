package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class Patent {
    @JsonProperty("patent_type")
    private String patentType;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("application_date")
    private String applicationDate;

    @JsonProperty("classifications")
    private List<Classifications> classifications;

    @JsonProperty("title")
    private String title;

    @JsonProperty("url")
    private String url;

    public String patentMergeKey() {
        return mergeKeyOrNull(normText(this.patentType), normText(this.registrationNumber), normDate(this.applicationDate), normText(this.title), normText(this.url));
    }
}