package com.rhynia.ochelper.config;

import com.csvreader.CsvReader;
import com.rhynia.ochelper.util.Format;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvAssemble {

    private final PathAssemble pa;

    public void initCsvLocalMap() {
        try {
            CsvReader csvItem = new CsvReader(pa.getPath_csv_item(), ',', StandardCharsets.UTF_8);
            csvItem.readHeaders();
            while (csvItem.readRecord()) {
                String name = csvItem.get(0);
                String local = csvItem.get(4);
                int meta = Integer.parseInt(csvItem.get(2));

                UNI_NAME_MAP_ITEM.put(Format.assembleItemUN(name, meta), local);
            }
            CsvReader fluidCSV = new CsvReader(pa.getPath_csv_fluid(), ',', StandardCharsets.UTF_8);
            fluidCSV.readHeaders();
            while (fluidCSV.readRecord()) {
                String name = fluidCSV.get(1);
                String local = fluidCSV.get(2);

                UNI_NAME_MAP_FLUID.put(Format.assembleFluidUN(name), local);
            }
        } catch (Exception e) {
            log.error("Exception in CSV loading, please check if exists.", e);
        }
    }

}
