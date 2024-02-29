package com.rhynia.ochelper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "c-value")
public class CommonValue {
    private int keepSize;
    private int cleanSchedule;
    private int insightSize;
}
