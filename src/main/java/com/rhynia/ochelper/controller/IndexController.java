package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.accessor.AEFluidAccessor;
import com.rhynia.ochelper.accessor.AEItemAccessor;
import com.rhynia.ochelper.accessor.DatabaseAccessor;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEFluidDisplay;
import com.rhynia.ochelper.var.AEItem;
import com.rhynia.ochelper.var.AEItemDisplay;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;

@Controller
@AllArgsConstructor
public class IndexController {
    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;
    private final PathAccessor pa;
    private final DatabaseAccessor da;

    @GetMapping("/")
    public String getMainRedirect() {
        return "dashboard";
    }

    @GetMapping("home")
    public String getHomeIndex() {
        return "home";
    }

    @GetMapping("examples")
    private String getIndex() {
        return "redirect:dist/pages/index.html";
    }

    @GetMapping("dashboard")
    private String getDIndex() {
        return "dashboard";
    }

    @Deprecated
    //@GetMapping("ae-storage-info")
    private String getAeStorageInfoPageIndex(Model model) {

        String iconPath = pa.getPath().getIconPanelPath();

        List<AEItem> items;
        List<AEFluid> fluids;

        try {
            items = aei.getAEItemList();
            fluids = aef.getAEFluidList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Pair<String, AEItem>> assembleItems = new ArrayList<>();
        List<Pair<String, AEFluid>> assembleFluids = new ArrayList<>();

        for (AEItem item : items) {
            if (Objects.equals(item.getName(), "ae2fc:fluid_drop")) continue;
            if (Objects.equals(item.getSize(), "0")) continue;

            Pair<String, Integer> sign = Pair.of(item.getName(), item.getDamage());
            String local;
            if (NAME_MAP_ITEM.containsKey(sign)) {
                local = NAME_MAP_ITEM.get(sign);
            } else {
                local = item.getLabel();
            }

            assembleItems.add(Pair.of(local, item));
        }

        for (AEFluid fluid : fluids) {
            String local;
            if (NAME_MAP_FLUID.containsKey(fluid.getName())) {
                local = NAME_MAP_FLUID.get(fluid.getName());
            } else {
                local = fluid.getLabel();
            }
            local = Format.trySwitchFluidName(local);

            assembleFluids.add(Pair.of(local, fluid));
        }

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("assembleItems", assembleItems);
        model.addAttribute("assembleFluids", assembleFluids);

        return "ae/ae-storage-info";
    }

    @GetMapping("ae-storage-info")
    public String getInfoPageIndex(Model model) {

        String iconPath = pa.getPath().getIconPanelPath();

        var pair = da.getLatestData();

        List<AEItemDisplay> items = pair.getLeft();
        List<AEFluidDisplay> fluids = pair.getRight();

        List<AEItemDisplay> itemList = new ArrayList<>();
        List<Pair<AEFluidDisplay, String>> fluidList = new ArrayList<>();

        for (AEItemDisplay item : items) {
            if (item.getUn().endsWith("drop$0")) continue;
            if (Objects.equals(item.getSize(), "0")) continue;
            itemList.add(item);
        }

        for (AEFluidDisplay fluid : fluids) {
            String localSwitched;
            if (UNI_NAME_MAP_FLUID.containsKey(fluid.getUn())) {
                localSwitched = UNI_NAME_MAP_FLUID.get(fluid.getUn());
            } else {
                localSwitched = fluid.getLocal();
            }
            fluidList.add(Pair.of(fluid, localSwitched));
        }

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("itemList", itemList);
        model.addAttribute("fluidList", fluidList);

        return "ae/ae-storage-info";
    }

    @GetMapping("ae-storage-center")
    public String getAeStorageIndex(Model model) {

        String iconPath = pa.getPath().getIconPanelPath();

        List<AEItem> items;
        List<AEFluid> fluids;

        try {
            items = aei.getAEItemList();
            fluids = aef.getAEFluidList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Pair<String, AEItem>> assembleItems = new ArrayList<>();
        List<Pair<String, AEFluid>> assembleFluids = new ArrayList<>();

        for (AEItem item : items) {
            if (Objects.equals(item.getName(), "ae2fc:fluid_drop")) continue;
            if (Objects.equals(item.getSize(), "0")) continue;

            Pair<String, Integer> sign = Pair.of(item.getName(), item.getDamage());
            String local;
            if (NAME_MAP_ITEM.containsKey(sign)) {
                local = NAME_MAP_ITEM.get(sign);
            } else {
                local = item.getLabel();
            }

            assembleItems.add(Pair.of(local, item));
        }

        for (AEFluid fluid : fluids) {
            String local;
            if (NAME_MAP_FLUID.containsKey(fluid.getName())) {
                local = NAME_MAP_FLUID.get(fluid.getName());
            } else {
                local = fluid.getLabel();
            }
            local = Format.trySwitchFluidName(local);

            assembleFluids.add(Pair.of(local, fluid));
        }

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("assembleItems", assembleItems);
        model.addAttribute("assembleFluids", assembleFluids);

        return "ae/ae-storage-center";
    }
}
