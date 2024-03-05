package com.rhynia.ochelper.database;

import cn.hutool.core.lang.Snowflake;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.var.element.AeReportFluidObj;
import com.rhynia.ochelper.var.element.AeReportItemObj;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SQLFactory {
    private final CommonValue cv;
    private final Snowflake sid = new Snowflake(1);

    public String generateCheck(String un) {
        return "CREATE TABLE IF NOT EXISTS " +
                un +
                " (id INTEGER PRIMARY KEY NOT NULL, size TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    }

    public String generateClean(String un, int keepSize) {
        return "DELETE FROM " + un + " WHERE id NOT IN (SELECT id FROM " + un + " ORDER BY id DESC LIMIT " + keepSize + ");";
    }

    public String generateInsert(String un, String size) {
        return "INSERT INTO " + un + " (id, size) VALUES ('" + sid.nextIdStr() + "', '" + size + "');";
    }

    public String generateSelect(String un, int size) {
        return "SELECT * FROM " + un + " ORDER BY id DESC LIMIT " + size + ";";
    }

    public String generateSelectLatest(String un) {
        return "SELECT * FROM " + un + " ORDER BY id DESC LIMIT 1";
    }

    public String generateCheck(AeReportItemObj item) {
        return generateCheck(item.getUn());
    }

    public String generateCheck(AeReportFluidObj fluid) {
        return generateCheck(fluid.getUn());
    }

    public String generateInsert(AeReportItemObj item) {
        return generateInsert(item.getUn(), item.getSizeString());
    }

    public String generateInsert(AeReportFluidObj fluid) {
        return generateInsert(fluid.getUn(), fluid.getSizeString());
    }

    public String generateClean(String un) {
        return generateClean(un, cv.getAeDataKeepSize());
    }

    public String generateEnergyWirelessDataCheck() {
        return "CREATE TABLE IF NOT EXISTS energy$wireless (id INTEGER PRIMARY KEY NOT NULL, size TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    }

    private String generateEnergyWirelessDataClean(int keepSize) {
        return "DELETE FROM energy$wireless WHERE id NOT IN (SELECT id FROM energy$wireless ORDER BY id DESC LIMIT " + keepSize + ");";
    }

    public String generateEnergyWirelessDataClean() {
        return generateEnergyWirelessDataClean(cv.getEnergyDataKeepSize());
    }

    public String generateEnergyWirelessDataInsert(String size) {
        return "INSERT INTO energy$wireless (id, size) VALUES ('" + sid.nextIdStr() + "', '" + size + "'); ";
    }

    public String generateEnergyWirelessDataSelect(int size) {
        return "SELECT * FROM energy$wireless ORDER BY id DESC LIMIT " + size + ";";
    }
}