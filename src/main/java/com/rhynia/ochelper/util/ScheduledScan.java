package com.rhynia.ochelper.util;

import com.rhynia.ochelper.accessor.AEFluidAccessor;
import com.rhynia.ochelper.accessor.AEItemAccessor;
import com.rhynia.ochelper.component.DatabaseUpdater;
import com.rhynia.ochelper.config.CommonValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Deprecated
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledScan {

    private final CommonValue cv;
    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;
    private final DatabaseUpdater dbu;

    private int cleanIndex = 0;

    //@Scheduled(cron = "${scheduled.cron}")
    public void scanJsonData() throws Exception {
        doScan();
    }

    private void doScan() throws Exception {
        if (cleanIndex <= cv.getCleanSchedule()) {
            long m = System.currentTimeMillis();
            int k = scanJsonItemData();
            int j = scanJsonFluidData();
            long n = System.currentTimeMillis();
            cleanIndex++;
            log.info("Scheduled scan update " + k + " items, " + j + " fluids, using " + (n - m) + " ms.");
        } else {
            long m = System.currentTimeMillis();
            int k = scanJsonItemDataWithClean();
            int j = scanJsonFluidDataWithClean();
            long n = System.currentTimeMillis();
            cleanIndex = 0;
            log.info("Scheduled scan with clean update " + k + " items, " + j + " fluids, using " + (n - m) + " ms.");
        }
    }

    private int scanJsonItemData() throws Exception {
        dbu.updateItemDatabase(aei.getAEItemList());
        return aei.getAEItemList().size();
    }

    private int scanJsonFluidData() throws Exception {
        dbu.updateFluidDatabase(aef.getAEFluidList());
        return aef.getAEFluidList().size();
    }

    private int scanJsonItemDataWithClean() throws Exception {
        dbu.updateItemDatabase(aei.getAEItemList());
        dbu.cleanupDatabase(aei.getAEItemList());
        return aei.getAEItemList().size();
    }

    private int scanJsonFluidDataWithClean() throws Exception {
        dbu.updateFluidDatabase(aef.getAEFluidList());
        dbu.cleanupDatabase(aef.getAEFluidList());
        return aef.getAEFluidList().size();
    }
}
