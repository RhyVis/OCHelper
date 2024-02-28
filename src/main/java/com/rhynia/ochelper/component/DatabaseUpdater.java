package com.rhynia.ochelper.component;

import cn.hutool.core.lang.Snowflake;
import com.rhynia.ochelper.accessor.AEDataAccessor;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DatabaseUpdater {
    private final JdbcTemplate jt;
    private final AEDataAccessor aed;

    private void checkDatabaseOrCreate(String un) {
        if (un == null || un.isEmpty()) return;
        String sql = "CREATE TABLE IF NOT EXISTS " + un + " (id INTEGER PRIMARY KEY NOT NULL, size TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        jt.execute(sql);
    }

    public void initDatabase() throws Exception {
        for (String un : aed.mapName()) {
            checkDatabaseOrCreate(un);
        }
    }

    public void updateItemDatabase(AEItem item) {
        String un = item.getUniqueName();
        Snowflake sid = new Snowflake(1);
        if (un == null || un.isEmpty()) return;
        checkDatabaseOrCreate(un);
        String sql = "INSERT INTO " + un + " (`id`, `size`) VALUES (?, ?);";
        jt.update(sql, sid.nextId(), item.processRawAeSize());
    }

    public void updateFluidDatabase(AEFluid fluid) {
        String un = fluid.getUniqueName();
        Snowflake sid = new Snowflake(1);
        if (un == null || un.isEmpty()) return;
        checkDatabaseOrCreate(un);
        String sql = "INSERT INTO " + un + " (`id`, `size`) VALUES (?, ?);";
        jt.update(sql, sid.nextId(), fluid.processRawAeSize());
    }
}
