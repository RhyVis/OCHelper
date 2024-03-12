package com.rhynia.ochelper.component;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.rhynia.ochelper.config.CsvAssemble;
import com.rhynia.ochelper.config.EnergyStationConfig;
import com.rhynia.ochelper.config.SwitchLocalAssemble;
import com.rhynia.ochelper.database.DatabaseUpdater;
import com.rhynia.ochelper.util.LuaScriptFactory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
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

        log.info("Started application with following options: {}", Arrays.toString(args.getSourceArgs()));
        var optCsv = Optional.ofNullable(args.getOptionValues("csv"));

        // CSV Loader
        optCsv.ifPresentOrElse((obj) -> {
            log.info("CSV Args found with {}.", obj);
            var tmp = Boolean.parseBoolean(obj.getFirst());
            if (tmp) {
                ca.initCsvLocalMap();
            }
        }, ca::initCsvLocalMap);

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
