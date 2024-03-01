package com.rhynia.ochelper.accessor;

import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class AEDataAccessor {

    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;

    @Getter
    private HashSet<String> uniqueItemNameList = new HashSet<>();
    @Getter
    private HashSet<String> uniqueFluidNameList = new HashSet<>();
    @Getter
    private HashSet<String> uniqueNameList = new HashSet<>();

    public HashSet<String> mapName() throws Exception {
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
