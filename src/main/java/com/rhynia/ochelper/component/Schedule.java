package com.rhynia.ochelper.component;

import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.database.DatabaseUpdater;
import com.rhynia.ochelper.util.CommandPackEnum;
import com.rhynia.ochelper.util.LuaScriptFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Schedule {

    private final LuaScriptFactory ls;
    private final CommonValue cv;
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
        ls.injectMission(ls.getCommonPackNormal());
        ls.injectMission(CommandPackEnum.GT_GET_ENERGY_WIRELESS.ofCommand("return c.proxy(\"" + cv.getEnergyStationAddressForRecord() + "\").getSensorInformation()"));
        log.info("Normal command pack set.");
    }
}
