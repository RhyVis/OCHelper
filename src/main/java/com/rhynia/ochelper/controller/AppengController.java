package com.rhynia.ochelper.controller;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.element.connection.AeCraftObj;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.config.ConfigValues;
import com.rhynia.ochelper.config.PathAssemble;
import com.rhynia.ochelper.database.DatabaseAccessor;
import com.rhynia.ochelper.var.element.connection.AeCpu;
import com.rhynia.ochelper.var.element.connection.AeReportItemObj;
import com.rhynia.ochelper.var.element.data.AeDataSetObj;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Rhynia
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AppengController {

    private final PathAssemble pa;
    private final ConfigValues cv;
    private final DatabaseAccessor da;
    private final DataProcessor dp;

    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DecimalFormat nf = new DecimalFormat("0.000%");

    @GetMapping("ae-storage-info")
    public String getInfoPageIndex(Model model) {

        String iconPath = pa.getPath().getIconPanelPath();

        var pair = da.getLatestData();

        var tmp1 = pair.getLeft();
        var tmp2 = pair.getRight();

        var items = tmp1.stream().filter(obj -> (!obj.getUn().endsWith("drop$0")))
            .filter(obj -> !"0".equals(obj.getSizeRaw())).toList();

        var fluids = tmp2.stream().filter(obj -> !"0".equals(obj.getSizeRaw()))
            .map(obj -> Pair.of(obj, NAME_MAP_FLUID_SWITCH.getOrDefault(obj.getLocal(), obj.getLocal()))).toList();

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("itemList", items);
        model.addAttribute("fluidList", fluids);

        return "ae/ae-storage-info";
    }

    @GetMapping("ae-insight-item")
    public String transportItemData(String un, String latest, Model model) {

        if (un == null) {
            return getInfoPageIndex(model);
        }

        int insightSize = cv.getInsightSize();
        var name = "Insight - " + UNI_NAME_MAP_ITEM.get(un);
        var imgPath = pa.getPath().getIconPanelPath();
        var original = da.getAeItemDataObjN(un, insightSize);

        // First element latest
        ArrayList<BigDecimal[]> tmpBdl = new ArrayList<>();
        var processed = original.sorted(Comparator.comparingLong(AeDataSetObj::getId).reversed())
            .map(AeDataSetObj::getAeItemDisplayObj).peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png"))
            .peek(obj -> {
                BigDecimal[] tmpArray = new BigDecimal[2];
                try {
                    tmpArray[0] = BigDecimal.valueOf(df.parse(obj.getTime()).getTime());
                } catch (ParseException e) {
                    log.error("Error caught in reading data: ", e);
                    tmpArray[0] = BigDecimal.ZERO;
                }
                tmpArray[1] = obj.getSize();
                tmpBdl.add(tmpArray);
            }).toList();

        var older = processed.getLast().getSize();
        var newer = processed.getFirst().getSize();
        var rateRaw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        var rateDisplay = nf.format(rateRaw);
        var increase = rateRaw.compareTo(BigDecimal.ZERO) > -1;

        model.addAttribute("bdl", tmpBdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rateDisplay);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insightSize);
        model.addAttribute("processed", processed);

        return "ae/ae-storage-insight";
    }

    @GetMapping("ae-insight-fluid")
    public String transportFluidData(String un, String latest, Model model) {

        if (un == null) {
            return getInfoPageIndex(model);
        }

        int insightSize = cv.getInsightSize();
        var name = "Insight - " + UNI_NAME_MAP_FLUID.get(un);
        var imgPath = pa.getPath().getIconPanelPath();
        var original = da.getAeFluidDataObjN(un, insightSize);

        // First element latest
        ArrayList<BigDecimal[]> tmpBdl = new ArrayList<>();
        var processed = original.sorted(Comparator.comparingLong(AeDataSetObj::getId).reversed())
            .map(AeDataSetObj::getAeFluidDisplayObj)
            .peek(obj -> obj
                .setImgPath(imgPath + NAME_MAP_FLUID_SWITCH.getOrDefault(obj.getLocal(), obj.getLocal()) + "单元.png"))
            .peek(obj -> {
                BigDecimal[] tmpArray = new BigDecimal[2];
                try {
                    tmpArray[0] = BigDecimal.valueOf(df.parse(obj.getTime()).getTime());
                } catch (ParseException e) {
                    log.error("Error caught in reading date: ", e);
                    tmpArray[0] = BigDecimal.ZERO;
                }
                tmpArray[1] = obj.getSize();
                tmpBdl.add(tmpArray);
            }).toList();

        var older = processed.getLast().getSize();
        var newer = processed.getFirst().getSize();
        var rateRaw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        var rateDisplay = nf.format(rateRaw);
        boolean increase = rateRaw.compareTo(BigDecimal.ZERO) > -1;

        model.addAttribute("bdl", tmpBdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rateDisplay);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insightSize);
        model.addAttribute("processed", processed);

        return "ae/ae-storage-insight";
    }

    @GetMapping("ae-cpu-info")
    public String requestCpuInfo(Model model) {
        var list = dp.requestAeCpuInfo();
        list = list.stream().sorted(Comparator.comparing(AeCpu::getName)).collect(Collectors.toList());
        model.addAttribute("c_list", list);
        return "ae/ae-cpu-info";
    }

    @GetMapping("ae-cpu-detail")
    public String requestCpuDetail(int cpuid, Model model) {
        String imgPath = pa.getPath().getIconPanelPath();

        var pair = dp.requestAeCpuDetail(cpuid);
        var listP = pair.getLeft();
        var finalOutput = pair.getRight().getDisplay();

        var active = listP[0].stream().map(AeReportItemObj::getDisplay)
            .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png")).toList();
        var store = listP[1].stream().map(AeReportItemObj::getDisplay)
            .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png")).toList();
        var pending = listP[2].stream().map(AeReportItemObj::getDisplay)
            .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png")).toList();

        model.addAttribute("active", active);
        model.addAttribute("store", store);
        model.addAttribute("pending", pending);
        model.addAttribute("final", finalOutput);

        return "ae/ae-cpu-detail";
    }

    // For element transport in ae-craftables & ae-craft
    private List<AeCraftObj> tempCraftableList = new ArrayList<>();

    @GetMapping("ae-craft")
    public String requestCraftables(Model model) {
        var imgPath = pa.getPath().getIconPanelPath();

        var list = dp.requestAeCraftList().stream()
                .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png"))
                .toList();
        tempCraftableList = list;

        model.addAttribute("amount", 0);
        model.addAttribute("list", list);
        model.addAttribute("result", false);

        return "ae/ae-craft";
    }

    @RequestMapping(value = "ae-craft", method = RequestMethod.POST)
    public String requestCraft(@RequestParam("amount") int amount,
                               @RequestParam("name") String name,
                               @RequestParam("meta") int meta,
                               Model model) {

        var local = Format.tryTranslateItemUn(Format.assembleItemUniqueName(name, meta));
        log.info("Received crafting request of '{}:{}' with amount of {}, its local may be {}.",
                name, meta, amount, local);
        var success = dp.requestAeCraft(name, meta, amount);

        model.addAttribute("list", tempCraftableList);
        model.addAttribute("local", local);
        model.addAttribute("success", success);
        model.addAttribute("result", true);

        return "ae/ae-craft";
    }

}
