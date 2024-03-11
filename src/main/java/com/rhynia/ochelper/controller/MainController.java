package com.rhynia.ochelper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Rhynia
 */
@Controller
public class MainController {
    @GetMapping("/")
    public String getMainRedirect() {
        return "dashboard";
    }

    @GetMapping("dashboard")
    private String getDashboardIndex() {
        return "dashboard";
    }

    @GetMapping("examples")
    private String getExampleIndex() {
        return "redirect:dist/pages/index.html";
    }
}
