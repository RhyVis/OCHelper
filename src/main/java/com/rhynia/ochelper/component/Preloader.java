package com.rhynia.ochelper.component;

import com.csvreader.CsvReader;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.accessor.SwitchFluidAccessor;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.SwitchFluid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

@Slf4j
@Component
@AllArgsConstructor
public class Preloader implements CommandLineRunner {

    private final PathAccessor pa;
    private final SwitchFluidAccessor sf;
    private final DatabaseUpdater dbu;

    @Override
    public void run(String... args) throws Exception {

        // CSV Loader
        CsvReader csvItem = new CsvReader(pa.getPath_csv_item(), ',', StandardCharsets.UTF_8);
        csvItem.readHeaders();
        while (csvItem.readRecord()) {
            String name = csvItem.get(0);
            String local = csvItem.get(4);
            int meta = Integer.parseInt(csvItem.get(2));

            NAME_MAP_ITEM.put(Pair.of(name, meta), local);
            UNI_NAME_MAP_ITEM.put(Format.assembleItemUName(name, meta), local);
        }
        CsvReader fluidCSV = new CsvReader(pa.getPath_csv_fluid(), ',', StandardCharsets.UTF_8);
        fluidCSV.readHeaders();
        while (fluidCSV.readRecord()) {
            String name = fluidCSV.get(1);
            String local = fluidCSV.get(2);

            NAME_MAP_FLUID.put(name, local);
            UNI_NAME_MAP_FLUID.put(Format.assembleFluidUName(name), local);
        }

        // JSON Loader
        List<SwitchFluid> sf_List = sf.getSFList();
        for (SwitchFluid sf : sf_List) {
            NAME_MAP_FLUID_SWITCH.put(sf.getPre(), sf.getAlt());
        }

        // Database init
        dbu.configDatabase();
        dbu.initDatabase();

        log.info("Preload complete.");

    }
}
