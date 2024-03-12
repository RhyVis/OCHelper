package com.rhynia.ochelper.database;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.rhynia.ochelper.var.element.connection.AeReportFluidObj;
import com.rhynia.ochelper.var.element.connection.AeReportItemObj;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@Component
public class DatabaseUpdater {

    private final SQLFactory sf;
    private final JdbcTemplate idt;
    private final JdbcTemplate fdt;
    private final JdbcTemplate edt;
    private final String requestAllSql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";

    DatabaseUpdater(SQLFactory sf,
                    @Qualifier("itemJdbcTemplate") JdbcTemplate idt,
                    @Qualifier("fluidJdbcTemplate") JdbcTemplate fdt,
                    @Qualifier("energyJdbcTemplate") JdbcTemplate edt) {
        this.sf = sf;
        this.idt = idt;
        this.fdt = fdt;
        this.edt = edt;
    }

    private void configDatabase() {
        String sql = """
            PRAGMA journal_mode = WAL;
            PRAGMA wal_autocheckpoint=5000;
            PRAGMA SYNCHRONOUS=NORMAL;
            """;
        idt.execute(sql);
        fdt.execute(sql);
        edt.execute(sql);
    }

    private void freeDataSize() {
        idt.execute("VACUUM;");
        fdt.execute("VACUUM;");
        edt.execute("VACUUM;");
    }

    private void initCleanDatabase() {
        initCleanDatabaseItem();
        initCleanDatabaseFluid();
        initCheckCleanDatabaseEnergy();
        freeDataSize();
    }

    private void initCleanDatabaseItem() {
        var tmp = idt.queryForList(requestAllSql, String.class).stream().map(sf::generateClean).toArray(String[]::new);
        if (!(tmp.length == 0)) {
            idt.batchUpdate(tmp);
        }
    }

    private void initCleanDatabaseFluid() {
        var tmp = fdt.queryForList(requestAllSql, String.class).stream().map(sf::generateClean).toArray(String[]::new);
        if (!(tmp.length == 0)) {
            fdt.batchUpdate(tmp);
        }
    }

    private void initCheckCleanDatabaseEnergy() {
        edt.update(sf.generateEnergyWirelessDataCheck());
        initCleanDatabaseEnergy();
    }

    private void initCleanDatabaseEnergy() {
        edt.update(sf.generateEnergyWirelessDataClean());
    }

    public void cleanupAllDataBase() {
        initCleanDatabaseItem();
        initCleanDatabaseFluid();
        initCleanDatabaseEnergy();
    }

    public void updateItemDatabase(List<AeReportItemObj> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        var tmp = Stream.concat(itemList.stream().map(sf::generateCheck), itemList.stream().map(sf::generateInsert))
            .toArray(String[]::new);
        idt.batchUpdate(tmp);
    }

    public void updateFluidDatabase(List<AeReportFluidObj> fluidList) {
        if (fluidList == null || fluidList.isEmpty()) {
            return;
        }
        var tmp = Stream.concat(fluidList.stream().map(sf::generateCheck), fluidList.stream().map(sf::generateInsert))
            .toArray(String[]::new);
        fdt.batchUpdate(tmp);
    }

    public void updateEnergyDatabase(BigDecimal val) {
        if (val == null) {
            return;
        }
        edt.update(sf.generateEnergyWirelessDataInsert(val.toPlainString()));
    }

    public void initDatabase() {
        try {
            configDatabase();
            initCleanDatabase();
        } catch (Exception e) {
            log.info("Error in init, ", e);
        }
    }
}
