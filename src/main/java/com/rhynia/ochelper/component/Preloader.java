package com.rhynia.ochelper.component;

import com.csvreader.CsvReader;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.accessor.SwitchLocalAccessor;
import com.rhynia.ochelper.database.DatabaseUpdater;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.util.LuaScriptFactory;
import com.rhynia.ochelper.var.SwitchLocal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM_SWITCH;

@Slf4j
@Component
@AllArgsConstructor
public class Preloader implements ApplicationRunner {

    private final PathAccessor pa;
    private final DatabaseUpdater dbu;
    private final SwitchLocalAccessor sl;
    private final LuaScriptFactory lp;

    @Override
    public void run(ApplicationArguments args) {

        // CSV Loader
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

        // JSON Loader
        try {
            var sf_List = sl.getSFList();
            for (SwitchLocal sf : sf_List) {
                NAME_MAP_FLUID_SWITCH.put(sf.getPre(), sf.getAlt());
            }
            var si_list = sl.getSIList();
            for (SwitchLocal si : si_list) {
                UNI_NAME_MAP_ITEM_SWITCH.put(si.getPre(), si.getAlt());
            }
        } catch (Exception e) {
            log.error("Exception in JSON build-in config loading, please check if exists.", e);
        }

        // Database init
        dbu.initDatabase();

        // Init lua script base
        lp.initLuaScript();
        lp.setPreloadCompleted(true);

        log.info("Preload complete.");

    }
}
