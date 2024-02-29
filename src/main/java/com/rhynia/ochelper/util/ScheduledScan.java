package com.rhynia.ochelper.util;

import com.rhynia.ochelper.accessor.AEFluidAccessor;
import com.rhynia.ochelper.accessor.AEItemAccessor;
import com.rhynia.ochelper.component.DatabaseUpdater;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledScan {
    private final CommonValue cv;
    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;
    private final DatabaseUpdater dbu;

    private int cleanIndex = 0;

    @Scheduled(cron = "${scheduled.cron}")
    public void scanJsonData() throws Exception {
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
        for (AEItem item : aei.getAEItemList()) {
            dbu.updateItemDatabase(item);
        }
        return aei.getAEItemList().size();
    }

    private int scanJsonFluidData() throws Exception {
        for (AEFluid fluid : aef.getAEFluidList()) {
            dbu.updateFluidDatabase(fluid);
        }
        return aef.getAEFluidList().size();
    }

    private int scanJsonItemDataWithClean() throws Exception {
        for (AEItem item : aei.getAEItemList()) {
            dbu.updateItemDatabase(item);
            dbu.cleanupData(item.getUniqueName());
        }
        return aei.getAEItemList().size();
    }

    private int scanJsonFluidDataWithClean() throws Exception {
        for (AEFluid fluid : aef.getAEFluidList()) {
            dbu.updateFluidDatabase(fluid);
            dbu.cleanupData(fluid.getUniqueName());
        }
        return aef.getAEFluidList().size();
    }
}
