package com.rhynia.ochelper.accessor;

import com.rhynia.ochelper.var.AEFluidData;
import com.rhynia.ochelper.var.AEFluidDisplay;
import com.rhynia.ochelper.var.AEItemData;
import com.rhynia.ochelper.var.AEItemDisplay;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

@Component
public class DatabaseAccessor {
    private final JdbcTemplate jt;
    private final AEDataAccessor aed;

    DatabaseAccessor(JdbcTemplate jt, AEDataAccessor aed) {
        this.jt = jt;
        this.aed = aed;
    }

    public Pair<List<AEItemDisplay>, List<AEFluidDisplay>> getLatestData() {
        List<AEItemDisplay> aeidp_l = new ArrayList<>();
        List<AEFluidDisplay> aefdp_l = new ArrayList<>();

        for (String un : aed.getUniqueNameList()) {
            if (un.startsWith("item$")) {
                String sql = "SELECT * FROM " + un + " ORDER BY id DESC LIMIT 1;";
                List<AEItemData> aeid_l = jt.query(sql, (rs, rowNum) -> new AEItemData(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3)
                ));
                if (aeid_l.isEmpty()) continue;
                AEItemData aeid = aeid_l.get(0);
                String local = UNI_NAME_MAP_ITEM.get(un);
                aeidp_l.add(new AEItemDisplay(un, local, aeid.getSize()));
            } else if (un.startsWith("fluid$")) {
                String sql = "SELECT * FROM " + un + " ORDER BY id DESC LIMIT 1;";
                List<AEFluidData> aefd_l = jt.query(sql, (rs, rowNum) -> new AEFluidData(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3)
                ));
                if (aefd_l.isEmpty()) continue;
                AEFluidData aefd = aefd_l.get(0);
                String local = UNI_NAME_MAP_FLUID.get(un);
                aefdp_l.add(new AEFluidDisplay(un, local, aefd.getSize()));
            }
        }

        return Pair.of(aeidp_l, aefdp_l);
    }
}
