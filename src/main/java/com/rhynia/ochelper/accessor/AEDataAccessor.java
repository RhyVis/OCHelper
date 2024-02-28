package com.rhynia.ochelper.accessor;

import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class AEDataAccessor {
    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;
    @Getter
    private List<String> uniqueItemNameList = new ArrayList<>();
    @Getter
    private List<String> uniqueFluidNameList = new ArrayList<>();
    @Getter
    private List<String> uniqueNameList = new ArrayList<>();

    AEDataAccessor(AEItemAccessor aei, AEFluidAccessor aef) {
        this.aei = aei;
        this.aef = aef;
    }

    public List<String> mapName() throws Exception {
        for (AEItem item : aei.getAEItemList()) {
            uniqueItemNameList.add(item.getUniqueName());
        }
        for (AEFluid fluid : aef.getAEFluidList()) {
            uniqueFluidNameList.add(fluid.getUniqueName());
        }
        uniqueNameList.addAll(uniqueItemNameList);
        uniqueNameList.addAll(uniqueFluidNameList);
        return uniqueNameList;
    }

}
