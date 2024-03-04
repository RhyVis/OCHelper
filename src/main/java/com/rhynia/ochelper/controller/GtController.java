package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.component.DataProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GtController {

    private final DataProcessor dp;

    @GetMapping("gt-sensor-info")
    public String requestGtSensorInfo(Model model) {
        String tmp = dp.requestGtMachineSensor("44a2cc68-baff-4045-92a0-ce0a0fa09b61");
        model.addAttribute("result", tmp);
        return "gt/gt-sensor-info";
    }
}
