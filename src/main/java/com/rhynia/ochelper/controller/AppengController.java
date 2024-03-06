package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.database.DatabaseAccessor;
import com.rhynia.ochelper.var.AECPU;
import com.rhynia.ochelper.var.element.AeDataSetObj;
import com.rhynia.ochelper.var.element.AeReportItemObj;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Comparator;
import java.util.stream.Collectors;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AppengController {

    private final PathAccessor pa;
    private final CommonValue cv;
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

        var items = tmp1.stream()
                .filter(obj -> !obj.getUn().endsWith("drop$0"))
                .filter(obj -> !obj.getSizeRaw().equals("0"))
                .toList();

        var fluids = tmp2.stream()
                .filter(obj -> !obj.getSizeRaw().equals("0"))
                .map(obj -> Pair.of(obj, NAME_MAP_FLUID_SWITCH.getOrDefault(obj.getLocal(), obj.getLocal())))
                .toList();

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("itemList", items);
        model.addAttribute("fluidList", fluids);

        return "ae/ae-storage-info";
    }

    @GetMapping("ae-insight-item")
    public String transportItemData(String it_un, String latest, Model model) {

        if (it_un == null) return getInfoPageIndex(model);

        int insight_size = cv.getInsightSize();
        var name = "Insight - " + UNI_NAME_MAP_ITEM.get(it_un);
        var imgPath = pa.getPath().getIconPanelPath();
        var original = da.getAeItemDataObjN(it_un, insight_size);

        // First element latest
        ArrayList<BigDecimal[]> tmpBdl = new ArrayList<>();
        var processed = original.sorted(Comparator.comparingLong(AeDataSetObj::getId).reversed())
                .map(AeDataSetObj::getAeItemDisplayObj)
                .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png"))
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
        var rate_raw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        var rateDisplay = nf.format(rate_raw);
        var increase = rate_raw.compareTo(BigDecimal.ZERO) > -1;

        model.addAttribute("bdl", tmpBdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rateDisplay);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);
        model.addAttribute("processed", processed);

        return "ae/ae-storage-insight";
    }

    @GetMapping("ae-insight-fluid")
    public String transportFluidData(String it_un, String latest, Model model) {

        if (it_un == null) return getInfoPageIndex(model);

        int insight_size = cv.getInsightSize();
        var name = "Insight - " + UNI_NAME_MAP_FLUID.get(it_un);
        var imgPath = pa.getPath().getIconPanelPath();
        var original = da.getAeFluidDataObjN(it_un, insight_size);

        // First element latest
        ArrayList<BigDecimal[]> tmpBdl = new ArrayList<>();
        var processed = original.sorted(Comparator.comparingLong(AeDataSetObj::getId).reversed())
                .map(AeDataSetObj::getAeFluidDisplayObj)
                .peek(obj -> obj.setImgPath(imgPath
                        + NAME_MAP_FLUID_SWITCH.getOrDefault(obj.getLocal(), obj.getLocal())
                        + "单元.png"))
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
        var rate_raw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        var rateDisplay = nf.format(rate_raw);
        boolean increase = rate_raw.compareTo(BigDecimal.ZERO) > -1;

        model.addAttribute("bdl", tmpBdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("name_to_display", name);
        model.addAttribute("size_latest", latest);
        model.addAttribute("rate", rateDisplay);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);
        model.addAttribute("processed", processed);

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
        String imgPath = pa.getPath().getIconPanelPath();

        var pair = dp.requestAeCpuDetail(cpuid);
        var listP = pair.getLeft();
        var finalOutput = pair.getRight().getDisplay();

        var active = listP[0].stream()
                .map(AeReportItemObj::getDisplay)
                .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png"))
                .toList();
        var store = listP[1].stream()
                .map(AeReportItemObj::getDisplay)
                .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png"))
                .toList();
        var pending = listP[2].stream()
                .map(AeReportItemObj::getDisplay)
                .peek(obj -> obj.setImgPath(imgPath + obj.getLocal() + ".png"))
                .toList();

        model.addAttribute("active", active);
        model.addAttribute("store", store);
        model.addAttribute("pending", pending);
        model.addAttribute("final", finalOutput);

        return "ae/ae-cpu-detail";
    }

}
