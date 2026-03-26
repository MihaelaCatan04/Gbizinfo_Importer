package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class Classifications {
    @JsonProperty("コード値")
    private String codeValue;

    @JsonProperty("コード名")
    private String codeName;

    @JsonProperty("日本語")
    private String japanese;

    public String classificationMergeKey() {
        return mergeKeyOrNull(normText(this.codeValue), normText(this.codeName), normText(this.japanese));
    }
}
