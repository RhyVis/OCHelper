package com.rhynia.ochelper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "path")
public class Path {
    private String csvPath;
    private String csvItemName;
    private String csvFluidName;
    private String jsonPath;
    private String jsonItemName;
    private String jsonFluidName;
    private String iconPanelPath;
    private String luaScriptsPath;
    private String databasePath;
}
