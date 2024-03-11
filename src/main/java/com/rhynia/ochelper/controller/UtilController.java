package com.rhynia.ochelper.controller;

import java.util.Comparator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.var.element.connection.MsSet;

import lombok.RequiredArgsConstructor;

/**
 * @author Rhynia
 */
@Controller
@RequiredArgsConstructor
public class UtilController {

    private final DataProcessor dp;

    @GetMapping("tps-report")
    public String requestTps(Model model) {
        var list = dp.requestTpsReport();
        var sum = list.stream().map(MsSet::getMspt).reduce(Double::sum).orElse(0D);
        var sumSet = MsSet.builder().dim(-32768).mspt(sum).build();
        list = list.stream().sorted(Comparator.comparingDouble(MsSet::getMspt).reversed()).toList();

        model.addAttribute("m_list", list);
        model.addAttribute("sumSet", sumSet);

        return "util/tps-report";
    }

    @GetMapping("sss")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("TEST", HttpStatus.OK);
    }
}
