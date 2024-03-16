package com.rhynia.ochelper.var.element.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Rhynia
 */
@Data
public class EnergyStationSet {
    private final int ordinal;
    private final String address;

    @JsonCreator
    public EnergyStationSet(@JsonProperty("ordinal") int ordinal,
                            @JsonProperty("address") String address) {
        this.ordinal = ordinal;
        this.address = address;
    }
}
