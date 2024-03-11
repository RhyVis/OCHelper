package com.rhynia.ochelper.controller;

import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.var.element.connection.OcComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ComponentsController {

    private final DataProcessor dp;

    @GetMapping("oc-components")
    public String fetchOCComponents(Model model) {
        List<OcComponent> list = dp.requestComponentList();
        model.addAttribute("c_list", list);
        return "oc/oc-components";
    }

    @GetMapping("oc-components-detail")
    public String fetchOCComponentsDetail(String name, String address, Model model) {
        dp.postProcessDocFetch();
        var list = dp.requestComponentDetail(address);
        model.addAttribute("component_name", name);
        model.addAttribute("d_list", list);
        return "oc/oc-components-detail";
    }
}
