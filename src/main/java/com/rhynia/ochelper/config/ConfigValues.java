package com.rhynia.ochelper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "values")
public class ConfigValues {
    private int aeDataKeepSize;
    private int energyDataKeepSize;
    private int cleanSchedule;
    private int insightSize;
    private String energyStationAddressForRecord;
}
