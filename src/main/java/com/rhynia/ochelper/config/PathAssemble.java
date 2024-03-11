package com.rhynia.ochelper.config;

import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * @author Rhynia
 */
@Component
@Getter
public class PathAssemble {

    private final Path path;
    private final String path_csv_item;
    private final String path_csv_fluid;
    private final String path_json_item;
    private final String path_json_fluid;

    PathAssemble(Path path) {
        this.path = path;
        this.path_csv_item = path.getCsvPath() + path.getCsvItemName();
        this.path_csv_fluid = path.getCsvPath() + path.getCsvFluidName();
        this.path_json_item = path.getJsonPath() + path.getJsonItemName();
        this.path_json_fluid = path.getJsonPath() + path.getJsonFluidName();
    }
}
