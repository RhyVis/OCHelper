package com.rhynia.ochelper.component;

import cn.hutool.core.lang.Snowflake;
import com.rhynia.ochelper.accessor.AEDataAccessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.mapper.AEDataMapper;
import com.rhynia.ochelper.util.SQLFactory;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DatabaseUpdater {

    private final JdbcTemplate jt;
    private final CommonValue cv;
    private final AEDataAccessor aed;
    private final AEDataMapper mp;
    private final SQLFactory sf;

    private final Snowflake sid = new Snowflake(1);

    private void configDatabase() {
        jt.execute("""
                PRAGMA journal_mode = WAL;
                PRAGMA wal_autocheckpoint=5000;
                PRAGMA SYNCHRONOUS=NORMAL;
                """
        );
    }

    private void initCreateDatabase() throws Exception {
        jt.batchUpdate(sf.generateBatchCheck(aed.mapName()));
        jt.batchUpdate(sf.generateCleanup(aed.mapName(), cv.getKeepSize()));
        freeDataSize();
    }

    @SuppressWarnings("unchecked")
    private void createIfNotExist(List<?> list) {
        var k = list.get(0);
        HashSet<String> unl = new HashSet<>();
        if (k instanceof AEItem) {
            for (AEItem item : (List<AEItem>) list) {
                unl.add(item.getUniqueName());
            }
        } else if (k instanceof AEFluid) {
            for (AEFluid fluid : (List<AEFluid>) list) {
                unl.add(fluid.getUniqueName());
            }
        } else {
            log.error("Wrong list of " + k.toString());
            return;
        }
        jt.batchUpdate(sf.generateBatchCheck(unl));
    }

    @SuppressWarnings("unchecked")
    public void cleanupDatabase(List<?> list) {
        var k = list.get(0);
        HashSet<String> unl = new HashSet<>();
        if (k instanceof AEItem) {
            for (AEItem item : (List<AEItem>) list) {
                unl.add(item.getUniqueName());
            }
        } else if (k instanceof AEFluid) {
            for (AEFluid fluid : (List<AEFluid>) list) {
                unl.add(fluid.getUniqueName());
            }
        } else {
            log.error("Wrong list of " + k.toString());
            return;
        }
        jt.batchUpdate(sf.generateCleanup(unl, cv.getKeepSize()));
    }

    public void updateItemDatabase(List<AEItem> itemList) {
        createIfNotExist(itemList);
        jt.batchUpdate(sf.generateItemBatchInsert(itemList));
    }

    public void updateFluidDatabase(List<AEFluid> fluidList) {
        createIfNotExist(fluidList);
        jt.batchUpdate(sf.generateFluidBatchInsert(fluidList));
    }

    public void initDatabase() {
        try {
            configDatabase();
            initCreateDatabase();
        } catch (Exception e) {
            log.info("Error in init, ", e);
        }
    }

    private void freeDataSize() {
        jt.execute("VACUUM;");
    }
}
