package com.rhynia.ochelper.config;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.csvreader.CsvReader;
import com.rhynia.ochelper.util.Format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
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

                UNI_NAME_MAP_ITEM.put(Format.assembleItemUniqueName(name, meta), local);
            }
            CsvReader csvFluid = new CsvReader(pa.getPath_csv_fluid(), ',', StandardCharsets.UTF_8);
            csvFluid.readHeaders();
            while (csvFluid.readRecord()) {
                String name = csvFluid.get(1);
                String local = csvFluid.get(2);

                UNI_NAME_MAP_FLUID.put(Format.assembleFluidUniqueName(name), local);
            }
        } catch (Exception e) {
            log.error("Exception in CSV loading, please check if exists.", e);
        }
    }

}
