package com.rhynia.ochelper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "path")
public class Path {

    private String csvPath;
    private String jsonPath;

    @Override
    public String toString() {
        return "Path:" + csvPath + "|" + jsonPath;
    }
}
