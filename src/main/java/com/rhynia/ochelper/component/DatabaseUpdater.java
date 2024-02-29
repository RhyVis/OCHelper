package com.rhynia.ochelper.component;

import cn.hutool.core.lang.Snowflake;
import com.rhynia.ochelper.accessor.AEDataAccessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.mapper.AEDataMapper;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DatabaseUpdater {

    private final JdbcTemplate jt;
    private final CommonValue cv;
    private final AEDataAccessor aed;
    private final AEDataMapper mp;

    private final Snowflake sid = new Snowflake(1);

    private void checkDatabaseOrCreate(String un) {
        String sql = "CREATE TABLE IF NOT EXISTS " + un + " (id INTEGER PRIMARY KEY NOT NULL, size TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        jt.execute(sql);
    }

    public void configDatabase() {
        jt.execute("""
                PRAGMA journal_mode = WAL;
                PRAGMA wal_autocheckpoint=5000;
                PRAGMA SYNCHRONOUS=NORMAL;
                """
        );
    }

    public void initDatabase() throws Exception {
        for (String un : aed.mapName()) {
            checkDatabaseOrCreate(un);
            cleanupData(un);
        }
        freeDataSize();
        log.info("Database sheets init, cleaning done.");
    }

    public void updateItemDatabase(AEItem item) {
        String un = item.getUniqueName();
        if (un == null || un.isEmpty()) return;
        checkDatabaseOrCreate(un);
        mp.insertAEData(un, sid.nextId(), item.processRawAeSize());
    }

    public void updateFluidDatabase(AEFluid fluid) {
        String un = fluid.getUniqueName();
        if (un == null || un.isEmpty()) return;
        checkDatabaseOrCreate(un);
        mp.insertAEData(un, sid.nextId(), fluid.processRawAeSize());
    }

    public void cleanupData(String un) {
        mp.cleanupAEData(un, cv.getKeepSize());
    }

    public void freeDataSize() {
        jt.execute("VACUUM;");
    }
}
