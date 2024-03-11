package com.rhynia.ochelper.var.element.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnergyStationSet {
    private final int ordinal;
    private final String address;
}
