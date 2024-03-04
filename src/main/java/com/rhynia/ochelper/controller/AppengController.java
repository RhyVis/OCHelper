package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.accessor.DatabaseAccessor;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.var.AECPU;
import com.rhynia.ochelper.var.AEFluidData;
import com.rhynia.ochelper.var.AEFluidDisplay;
import com.rhynia.ochelper.var.AEItemData;
import com.rhynia.ochelper.var.AEItemDisplay;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.support.ServletContextResource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM_SWITCH;

@Controller
@RequiredArgsConstructor
public class AppengController {

    private final PathAccessor pa;
    private final CommonValue cv;
    private final DatabaseAccessor da;
    private final DataProcessor dp;

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
            if (Objects.equals(item.getSizeRaw(), "0")) continue;
            if (item.getLocal() == null) {
                AEItemDisplay missing = new AEItemDisplay(item.getUn(), UNI_NAME_MAP_ITEM_SWITCH.getOrDefault(item.getUn(), item.getUn()), item.getSizeRaw());
                itemList.add(missing);
                continue;
            }
            itemList.add(item);
        }

        for (AEFluidDisplay fluid : fluids) {
            String localSwitched = NAME_MAP_FLUID_SWITCH.getOrDefault(fluid.getLocal(), fluid.getLocal());
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
        long current = System.currentTimeMillis();

        if (it_un == null) return getInfoPageIndex(model);

        int insight_size = cv.getInsightSize();
        var list = da.getItemDataLateN(it_un, insight_size);
        long sync = current - df.parse(list.get(0).getTime()).getTime();
        String nameLocal = UNI_NAME_MAP_ITEM_SWITCH.getOrDefault(it_un, it_un);
        String imgLink = pa.getPath().getIconPanelPath() + nameLocal + ".png";
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
        BigDecimal rate_raw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        String rate = nf.format(rate_raw);
        boolean increase = rate_raw.compareTo(BigDecimal.ZERO) > -1;

        model.addAttribute("bdl", bdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rate);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);
        model.addAttribute("imgLink", imgLink);
        model.addAttribute("list", list);

        return "ae/ae-storage-insight";
    }

    @GetMapping("insight-fluid")
    public String transportFluidData(String it_un, String latest, Model model) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat nf = new DecimalFormat("0.000%");
        long current = System.currentTimeMillis();

        if (it_un == null) return getInfoPageIndex(model);

        int insight_size = cv.getInsightSize();
        var list = da.getFluidDataLateN(it_un, insight_size);
        long sync = current - df.parse(list.get(0).getTime()).getTime();
        String nameLocal, nameLocalSwitch;
        nameLocal = UNI_NAME_MAP_FLUID.get(it_un);
        nameLocalSwitch = NAME_MAP_FLUID_SWITCH.getOrDefault(nameLocal, nameLocal);
        String imgLink = pa.getPath().getIconPanelPath() + nameLocalSwitch + "单元.png";
        ArrayList<BigDecimal[]> bdl = new ArrayList<>();

        String name = "Insight - " + nameLocal;

        for (AEFluidData aeFluidData : list) {
            BigDecimal[] temp = new BigDecimal[2];
            temp[0] = BigDecimal.valueOf(df.parse(aeFluidData.getTime()).getTime());
            temp[1] = new BigDecimal(aeFluidData.getSize());
            bdl.add(temp);
        }

        BigDecimal older = new BigDecimal(list.get(list.size() - 1).getSize());
        BigDecimal newer = new BigDecimal(list.get(0).getSize());
        BigDecimal rate_raw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        String rate = nf.format(rate_raw);
        boolean increase = rate_raw.compareTo(BigDecimal.ZERO) > -1;

        model.addAttribute("bdl", bdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rate);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);
        model.addAttribute("imgLink", imgLink);
        model.addAttribute("list", list);

        return "ae/ae-storage-insight";
    }

    @GetMapping("ae-cpu-info")
    public String requestCpuInfo(Model model) {
        var list = dp.requestAeCpuInfo();
        list = list.stream().sorted(Comparator.comparing(AECPU::getName)).collect(Collectors.toList());
        model.addAttribute("c_list", list);
        return "ae/ae-cpu-info";
    }

    @GetMapping("ae-cpu-detail")
    public String requestCpuDetail(int cpuid, Model model) {
        String iconPath = pa.getPath().getIconPanelPath();
        var pair = dp.requestAeCpuDetail(cpuid);
        var listP = pair.getLeft();
        var finalOutput = pair.getRight();

        List<AEItemDisplay> active = new ArrayList<>();
        List<AEItemDisplay> store = new ArrayList<>();
        List<AEItemDisplay> pending = new ArrayList<>();
        for (var item : listP[0]) {
            active.add(item.getDisplay());
        }
        for (var item : listP[1]) {
            store.add(item.getDisplay());
        }
        for (var item : listP[2]) {
            pending.add(item.getDisplay());
        }

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("active", active);
        model.addAttribute("store", store);
        model.addAttribute("pending", pending);
        model.addAttribute("final", finalOutput);
        return "ae/ae-cpu-detail";
    }

}
