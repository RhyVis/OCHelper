package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.accessor.AEFluidAccessor;
import com.rhynia.ochelper.accessor.AEItemAccessor;
import com.rhynia.ochelper.accessor.DatabaseAccessor;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEFluidData;
import com.rhynia.ochelper.var.AEFluidDisplay;
import com.rhynia.ochelper.var.AEItem;
import com.rhynia.ochelper.var.AEItemData;
import com.rhynia.ochelper.var.AEItemDisplay;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM_SWITCH;

@Controller
@AllArgsConstructor
public class IndexController {
    private final AEItemAccessor aei;
    private final AEFluidAccessor aef;
    private final PathAccessor pa;
    private final DatabaseAccessor da;
    private final CommonValue cv;

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
            if (item.getLocal() == null) {
                AEItemDisplay missing;
                if (UNI_NAME_MAP_ITEM_SWITCH.containsKey(item.getUn())) {
                    missing = new AEItemDisplay(item.getUn(), UNI_NAME_MAP_ITEM_SWITCH.get(item.getUn()), item.getSize());
                } else {
                    missing = new AEItemDisplay(item.getUn(), item.getUn(), item.getSize());
                }
                itemList.add(missing);
                continue;
            }
            itemList.add(item);
        }

        for (AEFluidDisplay fluid : fluids) {
            String localSwitched;
            if (NAME_MAP_FLUID_SWITCH.containsKey(fluid.getLocal())) {
                localSwitched = NAME_MAP_FLUID_SWITCH.get(fluid.getLocal());
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

    @GetMapping("insight-item")
    public String transportItemData(String it_un, String latest, Model model) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat nf = new DecimalFormat("0.000%");
        if (it_un == null) return getInfoPageIndex(model);

        int insight_size = cv.getInsightSize();
        var list = da.getItemDataLateN(it_un, insight_size);
        ArrayList<BigDecimal[]> bdl = new ArrayList<>();

        String name = "Insight - " + UNI_NAME_MAP_ITEM.get(it_un);

        for (AEItemData aeItemData : list) {
            BigDecimal[] temp = new BigDecimal[2];
            temp[0] = BigDecimal.valueOf(df.parse(aeItemData.getTime()).getTime());
            temp[1] = new BigDecimal(aeItemData.getSize());
            bdl.add(temp);
        }

        BigDecimal older = new BigDecimal(list.get(list.size() - 1).getSize());
        BigDecimal newer = new BigDecimal(list.get(0).getSize());
        BigDecimal rate_raw = newer.divide(older, 6, RoundingMode.FLOOR);
        String rate = nf.format(rate_raw);
        boolean increase = rate_raw.compareTo(BigDecimal.ONE) > -1;

        model.addAttribute("bdl", bdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rate);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);

        return "ae/ae-storage-insight";
    }

    @GetMapping("insight-fluid")
    public String transportFluidData(String it_un, String latest, Model model) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat nf = new DecimalFormat("0.000%");
        if (it_un == null) return getInfoPageIndex(model);

        int insight_size = cv.getInsightSize();
        var list = da.getFluidDataLateN(it_un, insight_size);
        ArrayList<BigDecimal[]> bdl = new ArrayList<>();

        String name = "Insight - " + UNI_NAME_MAP_FLUID.get(it_un);

        for (AEFluidData aeFluidData : list) {
            BigDecimal[] temp = new BigDecimal[2];
            temp[0] = BigDecimal.valueOf(df.parse(aeFluidData.getTime()).getTime());
            temp[1] = new BigDecimal(aeFluidData.getSize());
            bdl.add(temp);
        }

        BigDecimal older = new BigDecimal(list.get(list.size() - 1).getSize());
        BigDecimal newer = new BigDecimal(list.get(0).getSize());
        BigDecimal rate_raw = newer.divide(older, 6, RoundingMode.FLOOR);
        double rate_p = rate_raw.doubleValue();
        String rate = nf.format(rate_p);
        boolean increase = rate_p > 1D;

        model.addAttribute("bdl", bdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rate);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);

        return "ae/ae-storage-insight";
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
