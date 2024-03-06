package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.var.MsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;

@Controller
@RequiredArgsConstructor
public class UtilController {

    private final DataProcessor dp;

    @GetMapping("tps-report")
    public String requestTPS(Model model) {
        var list = dp.requestTPSReport();
        list = list.stream()
                .sorted(Comparator.comparingDouble(MsSet::getMspt).reversed())
                .toList();
        model.addAttribute("m_list", list);
        return "util/tps-report";
    }

    @GetMapping("sss")
    public ResponseEntity<String> test(Model model) {
        //dp.test();
        return new ResponseEntity<>("TEST", HttpStatus.OK);
    }
}
