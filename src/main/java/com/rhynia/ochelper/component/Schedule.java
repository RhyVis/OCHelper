package com.rhynia.ochelper.component;

import java.util.Optional;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rhynia.ochelper.config.ConfigValues;
import com.rhynia.ochelper.config.EnergyStationConfig;
import com.rhynia.ochelper.database.DatabaseUpdater;
import com.rhynia.ochelper.util.CommandPackEnum;
import com.rhynia.ochelper.util.LuaScriptFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Schedule {

    private final ConfigValues cv;
    private final EnergyStationConfig es;
    private final LuaScriptFactory ls;
    private final DatabaseUpdater du;

    private int sign = 0;

    @Scheduled(initialDelayString = "${scheduled.init}", fixedRateString = "${scheduled.rate}")
    public void scheduledTask() {
        doSchedule();
        sign++;
    }

    private void doSchedule() {
        if (sign > cv.getCleanSchedule()) {
            du.cleanupAllDataBase();
            log.info("Committed a cleanup.");
        }
        ls.injectMission(Set.of(CommandPackEnum.AE_GET_ITEM.getPack(), CommandPackEnum.AE_GET_FLUID.getPack()));
        var opt = Optional.ofNullable(es.getEsData().get(0));
        opt.ifPresentOrElse(
            address -> ls.injectMission(CommandPackEnum.GT_GET_ENERGY_WIRELESS
                .ofCommand("return c.proxy('" + address + "').getSensorInformation()")),
            () -> log.info("Wireless energy address doesnt exist, skip request."));
        log.info("Normal command pack set.");
    }
}
