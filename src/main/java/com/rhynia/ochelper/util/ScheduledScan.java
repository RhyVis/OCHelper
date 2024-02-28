package com.rhynia.ochelper.util;

import com.rhynia.ochelper.accessor.AEFluidAccessor;
import com.rhynia.ochelper.accessor.AEItemAccessor;
import com.rhynia.ochelper.component.DatabaseUpdater;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduledScan {
    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;
    private final DatabaseUpdater dbi;

    @Scheduled(cron = "0/20 * * * * ?")
    public void scanJsonData() throws Exception {
        int k = scanJsonItemData();
        int j = scanJsonFluidData();
        System.out.println("Scan update " + k + " items, " + j + " fluids.");
    }

    private int scanJsonItemData() throws Exception {
        for (AEItem item : aei.getAEItemList()) {
            dbi.updateItemDatabase(item);
        }
        return aei.getAEItemList().size();
    }

    private int scanJsonFluidData() throws Exception {
        for (AEFluid fluid : aef.getAEFluidList()) {
            dbi.updateFluidDatabase(fluid);
        }
        return aef.getAEFluidList().size();
    }
}
