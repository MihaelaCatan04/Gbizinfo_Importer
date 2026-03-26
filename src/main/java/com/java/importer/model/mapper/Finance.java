package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class Finance {
    @JsonProperty("accounting_standards")
    private String accountingStandards;

    @JsonProperty("fiscal_year_cover_page")
    private String fiscalYearCoverPage;

    @JsonProperty("management_index")
    private List<ManagementIndex> managementIndex;

    @JsonProperty("major_shareholders")
    private List<MajorShareholders> majorShareholders;

    public String financeMergeKey() {
        return mergeKeyOrNull(normText(this.accountingStandards), normText(this.fiscalYearCoverPage));
    }
}