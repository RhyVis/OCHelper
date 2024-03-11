package com.rhynia.ochelper.var.element.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Rhynia
 */
@Data
public class SwitchLocalSet {
    private final String pre;
    private final String alt;

    @JsonCreator
    public SwitchLocalSet(@JsonProperty("pre") String pre, @JsonProperty("alt") String alt) {
        this.pre = pre;
        this.alt = alt;
    }
}
