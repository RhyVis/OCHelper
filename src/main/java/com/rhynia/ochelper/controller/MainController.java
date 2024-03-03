package com.rhynia.ochelper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String getMainRedirect() {
        return "dashboard";
    }

    @GetMapping("dashboard")
    private String getDIndex() {
        return "dashboard";
    }

    @GetMapping("examples")
    private String getIndex() {
        return "redirect:dist/pages/index.html";
    }
}
