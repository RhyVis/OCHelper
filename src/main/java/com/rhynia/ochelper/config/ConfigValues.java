package com.rhynia.ochelper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@Component
@ConfigurationProperties(prefix = "values")
public class ConfigValues {
    private int aeDataKeepSize;
    private int energyDataKeepSize;
    private int cleanSchedule;
    private int insightSize;
}
