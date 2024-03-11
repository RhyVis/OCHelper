package com.rhynia.ochelper.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rhynia.ochelper.database.DatabaseAccessor;
import com.rhynia.ochelper.util.Format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GtController {

    private final DatabaseAccessor da;

    @GetMapping("gt-sensor-info")
    public String requestGtSensorInfo(Model model) {
        // var tmp = dp.requestGtMachineSensor(cv.getEnergyStationAddressForRecord());
        // model.addAttribute("result", tmp);
        model.addAttribute("result", "NULL");
        return "gt/gt-sensor-info";
    }

    @GetMapping("gt-energy-data")
    public String requestEnergyData(Model model) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat nf = new DecimalFormat("0.000%");
        int insightSize = 200;
        var list = da.getEnergyWirelessDataN(insightSize);
        ArrayList<BigDecimal[]> tmpBdl = new ArrayList<>();

        var processed = list.stream().peek(data -> {
            BigDecimal[] tmpArray = new BigDecimal[2];
            try {
                tmpArray[0] = BigDecimal.valueOf(df.parse(data.getTime()).getTime());
            } catch (ParseException e) {
                log.error("Error caught in reading date: ", e);
                tmpArray[0] = BigDecimal.ONE;
            }
            tmpArray[1] = data.getSize();
            tmpBdl.add(tmpArray);
        }).toList();

        var older = processed.getLast().getSize();
        var newer = processed.getFirst().getSize();
        var rateRaw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        var rateDisplay = nf.format(rateRaw);
        var increase = rateRaw.compareTo(BigDecimal.ZERO) > -1;

        String latest = Format.formatSizeWithComma(newer) + Format.formatSizeWithByte(newer);

        model.addAttribute("bdl", tmpBdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("rate", rateDisplay);
        model.addAttribute("latest", latest);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insightSize);
        model.addAttribute("name_to_display", "General Energy Status");
        model.addAttribute("list", list);

        return "gt/gt-energy-data";
    }
}
