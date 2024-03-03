package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.component.DataProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UtilController {

    private final DataProcessor dp;

    @GetMapping("util/tps-report")
    public ResponseEntity<String> requestTPS(Model model) {
        //String k = dp.requestTPSReport();
        return new ResponseEntity<>("TEST", HttpStatus.OK);
    }
}
