package com.rhynia.ochelper.component;

import cn.hutool.core.lang.Snowflake;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.mapper.DataMapper;
import com.rhynia.ochelper.util.SQLFactory;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DatabaseUpdater {


    private final CommonValue cv;
    private final JdbcTemplate jt;
    private final DataMapper mp;
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

    private void initCreateDatabase() {
        List<String> init_l = mp.getAllNamesInDataBase();
        jt.update(sf.generateEnergyDataCheck());
        jt.update(sf.generateEnergyDataClean(cv.getEnergyDataKeepSize()));
        jt.batchUpdate(sf.generateBatchCheck(init_l));
        jt.batchUpdate(sf.generateCleanup(init_l, cv.getAeDataKeepSize()));
        freeDataSize();
    }

    @SuppressWarnings("unchecked")
    private void createIfNotExist(List<?> list) {
        var k = list.getFirst();
        List<String> unl = new ArrayList<>();
        if (k instanceof AEItem) {
            for (AEItem item : (List<AEItem>) list) {
                unl.add(item.getUn());
            }
        } else if (k instanceof AEFluid) {
            for (AEFluid fluid : (List<AEFluid>) list) {
                unl.add(fluid.getUn());
            }
        } else {
            log.error("Wrong list of " + k.toString());
            return;
        }
        jt.batchUpdate(sf.generateBatchCheck(unl));
    }

    @SuppressWarnings("unchecked")
    public void cleanupDatabase(List<?> list) {
        var k = list.getFirst();
        List<String> unl = new ArrayList<>();
        if (k instanceof AEItem) {
            for (AEItem item : (List<AEItem>) list) {
                unl.add(item.getUn());
            }
        } else if (k instanceof AEFluid) {
            for (AEFluid fluid : (List<AEFluid>) list) {
                unl.add(fluid.getUn());
            }
        } else {
            log.error("Wrong list of " + k.toString());
            return;
        }
        jt.batchUpdate(sf.generateCleanup(unl, cv.getAeDataKeepSize()));
    }

    public void cleanupAllDataBase() {
        List<String> un_l = mp.getAllNamesInDataBase();
        jt.batchUpdate(sf.generateCleanup(un_l, cv.getAeDataKeepSize()));
    }

    public void updateItemDatabase(List<AEItem> itemList) {
        if (itemList == null || itemList.isEmpty()) return;
        createIfNotExist(itemList);
        jt.batchUpdate(sf.generateItemBatchInsert(itemList));
    }

    public void updateFluidDatabase(List<AEFluid> fluidList) {
        if (fluidList == null || fluidList.isEmpty()) return;
        createIfNotExist(fluidList);
        jt.batchUpdate(sf.generateFluidBatchInsert(fluidList));
    }

    public void updateEnergyDatabase(BigDecimal val) {
        mp.updateEnergyInfo(sid.nextId(), val.toPlainString());
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
