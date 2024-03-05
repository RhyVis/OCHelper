package com.rhynia.ochelper.database;

import com.rhynia.ochelper.var.EnergyData;
import com.rhynia.ochelper.var.element.AeDataSetObj;
import com.rhynia.ochelper.var.element.AeDisplayFluidObj;
import com.rhynia.ochelper.var.element.AeDisplayItemObj;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class DatabaseAccessor {

    private final SQLFactory sf;
    private final JdbcTemplate idt;
    private final JdbcTemplate fdt;
    private final JdbcTemplate edt;
    private final String requestAllSql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";

    DatabaseAccessor(SQLFactory sf,
                     @Qualifier("itemJdbcTemplate") JdbcTemplate idt,
                     @Qualifier("fluidJdbcTemplate") JdbcTemplate fdt,
                     @Qualifier("energyJdbcTemplate") JdbcTemplate edt) {
        this.sf = sf;
        this.idt = idt;
        this.fdt = fdt;
        this.edt = edt;
    }

    private AeDataSetObj getAeItemDataObjLatest(String un) {
        return idt.queryForObject(sf.generateSelectLatest(un), (rs, rowNum) -> AeDataSetObj.builder()
                .un(un)
                .id(rs.getLong(1))
                .size(rs.getString(2))
                .time(rs.getString(3))
                .build());
    }

    private AeDataSetObj getAeFluidDataObjLatest(String un) {
        return fdt.queryForObject(sf.generateSelectLatest(un), (rs, rowNum) -> AeDataSetObj.builder()
                .un(un)
                .id(rs.getLong(1))
                .size(rs.getString(2))
                .time(rs.getString(3))
                .build());
    }

    private List<AeDisplayItemObj> getAeItemDataObjLatestList() {
        return idt.queryForList(requestAllSql, String.class).stream()
                .map(this::getAeItemDataObjLatest)
                .map(AeDataSetObj::getAeItemDisplayObj)
                .toList();
    }

    private List<AeDisplayFluidObj> getAeFluidDataObjLatestList() {
        return fdt.queryForList(requestAllSql, String.class).stream()
                .map(this::getAeFluidDataObjLatest)
                .map(AeDataSetObj::getAeFluidDisplayObj)
                .toList();
    }

    public Pair<List<AeDisplayItemObj>, List<AeDisplayFluidObj>> getLatestData() {
        var tmp1 = getAeItemDataObjLatestList();
        var tmp2 = getAeFluidDataObjLatestList();
        if (!tmp1.isEmpty() && !tmp2.isEmpty()) {
            return Pair.of(tmp1, tmp2);
        } else {
            log.error("At least one of the fetched list is empty, the lists are " + tmp1 + ", " + tmp2);
            return Pair.of(List.of(AeDisplayItemObj.getDummy()), List.of(AeDisplayFluidObj.getDummy()));
        }
    }

    public Stream<AeDataSetObj> getAeItemDataObjN(String un, int size) {
        return idt.queryForStream(sf.generateSelect(un, size), (rs, rowNum) -> AeDataSetObj.builder()
                .un(un)
                .id(rs.getLong(1))
                .size(rs.getString(2))
                .time(rs.getString(3))
                .build());
    }

    public Stream<AeDataSetObj> getAeFluidDataObjN(String un, int size) {
        return fdt.queryForStream(sf.generateSelect(un, size), (rs, rowNum) -> AeDataSetObj.builder()
                .un(un)
                .id(rs.getLong(1))
                .size(rs.getString(2))
                .time(rs.getString(3))
                .build());
    }

    public List<EnergyData> getEnergyWirelessDataN(int size) {
        return edt.query(sf.generateEnergyWirelessDataSelect(size), (rs, rowNum) -> EnergyData.builder()
                .id(rs.getLong(1))
                .sizeRaw(rs.getString(2))
                .time(rs.getString(3))
                .build());
    }
}
