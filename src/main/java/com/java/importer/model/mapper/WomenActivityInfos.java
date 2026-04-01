package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.java.importer.util.HashUtil.*;

@Getter
@Setter
public class WomenActivityInfos {
    @JsonProperty("female_workers_proportion_type")
    private String femaleWorkersProportionType;

    @JsonProperty("female_workers_proportion")
    private Double femaleWorkersProportion;

    @JsonProperty("female_share_of_manager")
    private Integer femaleShareOfManager;

    @JsonProperty("gender_total_of_manager")
    private Integer genderTotalOfManager;

    @JsonProperty("female_share_of_officers")
    private Integer femaleShareOfOfficers;

    @JsonProperty("gender_total_of_officers")
    private Integer genderTotalOfOfficers;

    public String womenActivityMergeKey() {
        return mergeKeyOrNull(normText(this.femaleWorkersProportionType), normNumber(this.femaleWorkersProportion), normNumber(this.femaleShareOfManager), normNumber(this.genderTotalOfManager), normNumber(this.femaleShareOfOfficers), normNumber(this.genderTotalOfOfficers));
    }
}
