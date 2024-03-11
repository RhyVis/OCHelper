package com.rhynia.ochelper.component;

import com.rhynia.ochelper.config.CsvAssemble;
import com.rhynia.ochelper.config.EnergyStationConfig;
import com.rhynia.ochelper.config.SwitchLocalAssemble;
import com.rhynia.ochelper.database.DatabaseUpdater;
import com.rhynia.ochelper.util.LuaScriptFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Preloader implements ApplicationRunner {

    private final CsvAssemble ca;
    private final EnergyStationConfig es;
    private final DatabaseUpdater dbu;
    private final SwitchLocalAssemble sl;
    private final LuaScriptFactory lp;

    @Override
    public void run(ApplicationArguments args) {

        // CSV Loader
        ca.initCsvLocalMap();

        // JSON Loader
        sl.initMap();
        es.initAddressSet();

        // Database init
        dbu.initDatabase();

        // Init lua script base
        lp.initLuaScript();
        lp.setPreloadCompleted(true);

        log.info("Preload complete.");

    }
}
