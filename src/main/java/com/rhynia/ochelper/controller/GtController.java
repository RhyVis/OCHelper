package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.database.DatabaseAccessor;
import com.rhynia.ochelper.util.Format;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class GtController {

    private final DataProcessor dp;
    private final DatabaseAccessor da;
    private final CommonValue cv;

    @GetMapping("gt-sensor-info")
    public String requestGtSensorInfo(Model model) {
        var tmp = dp.requestGtMachineSensor(cv.getEnergyStationAddressForRecord());
        model.addAttribute("result", tmp);
        return "gt/gt-sensor-info";
    }

    @GetMapping("gt-energy-data")
    public String requestEnergyData(Model model) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat nf = new DecimalFormat("0.000%");
        int insight_size = 200;
        var list = da.getEnergyWirelessDataN(insight_size);
        ArrayList<BigDecimal[]> tmpBdl = new ArrayList<>();

        var processed = list.stream()
                .peek(data -> {
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
        var rate_raw = newer.divide(older, 6, RoundingMode.FLOOR).subtract(BigDecimal.ONE);
        var rate_display = nf.format(rate_raw);
        var increase = rate_raw.compareTo(BigDecimal.ZERO) > -1;

        String latest = Format.formatSizeDisplay(newer) + Format.formatSizeByteDisplay(newer);

        model.addAttribute("bdl", tmpBdl.toArray(new BigDecimal[0][0]));
        model.addAttribute("rate", rate_display);
        model.addAttribute("latest", latest);
        model.addAttribute("increase", increase);
        model.addAttribute("insight_size", insight_size);
        model.addAttribute("name_to_display", "General Energy Status");
        model.addAttribute("list", list);

        return "gt/gt-energy-data";
    }
}
