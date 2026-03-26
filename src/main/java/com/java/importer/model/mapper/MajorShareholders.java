package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class MajorShareholders {
    @JsonProperty("name_major_shareholders")
    private String nameMajorShareholders;

    @JsonProperty("shareholding_ratio")
    private Double shareholdingRatio;

    public String shareholderMergeKey() {
        return mergeKeyOrNull(normText(this.nameMajorShareholders), normNumber(this.shareholdingRatio));
    }
}
