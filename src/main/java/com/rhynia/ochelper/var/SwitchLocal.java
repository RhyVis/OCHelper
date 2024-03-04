package com.rhynia.ochelper.var;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SwitchLocal {
    private final String pre;
    private final String alt;

    @JsonCreator
    public SwitchLocal(
            @JsonProperty("pre") String pre,
            @JsonProperty("alt") String alt) {
        this.pre = pre;
        this.alt = alt;
    }
}
