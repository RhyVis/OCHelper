package com.rhynia.ochelper.accessor;

import com.rhynia.ochelper.mapper.DataMapper;
import com.rhynia.ochelper.var.AEFluidData;
import com.rhynia.ochelper.var.AEFluidDisplay;
import com.rhynia.ochelper.var.AEItemData;
import com.rhynia.ochelper.var.AEItemDisplay;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

@Component
@AllArgsConstructor
public class DatabaseAccessor {

    private final DataMapper mp;

    public Pair<List<AEItemDisplay>, List<AEFluidDisplay>> getLatestData() {
        List<AEItemDisplay> aeidp_l = new ArrayList<>();
        List<AEFluidDisplay> aefdp_l = new ArrayList<>();
        List<String> un_l = mp.getAllNamesInDataBase();

        for (String un : un_l) {
            if (un.startsWith("item$")) {
                AEItemData aeid = mp.getAEItemDataLatest(un);
                String local = UNI_NAME_MAP_ITEM.get(un);
                aeidp_l.add(new AEItemDisplay(un, local, aeid.getSize()));
            } else if (un.startsWith("fluid$")) {
                AEFluidData aefd = mp.getAEFluidDataLatest(un);
                String local = UNI_NAME_MAP_FLUID.get(un);
                aefdp_l.add(new AEFluidDisplay(un, local, aefd.getSize()));
            }
        }

        return Pair.of(aeidp_l, aefdp_l);
    }

    public List<AEItemData> getItemDataLate5(String un) {
        return mp.getAEItemDataLate5(un);
    }

    public List<AEFluidData> getFluidDataLate5(String un) {
        return mp.getAEFluidDataLate5(un);
    }

    public List<AEItemData> getItemDataLateN(String un, int size) {
        return mp.getAEItemDataLateN(un, size);
    }

    public List<AEFluidData> getFluidDataLateN(String un, int size) {
        return mp.getAEFluidDataLateN(un, size);
    }
}
