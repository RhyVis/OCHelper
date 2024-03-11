package com.rhynia.ochelper.config;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM_SWITCH;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.var.element.config.SwitchLocalSet;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@Component
public class SwitchLocalAssemble {

    @Value("classpath:/config/switch_item.json")
    private Resource siSource;
    @Value("classpath:/config/switch_fluid.json")
    private Resource sfSource;

    private List<SwitchLocalSet> getSiList() {
        String json = "";
        List<SwitchLocalSet> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = IOUtils.toString(siSource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error in reading JSON config.", e);
        }
        try {
            list = mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error in mapping JSON config", e);
        }
        return list;
    }

    private List<SwitchLocalSet> getSfList() {
        String json = "";
        List<SwitchLocalSet> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = IOUtils.toString(sfSource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error in reading JSON config.", e);
        }
        try {
            list = mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error in mapping JSON config", e);
        }
        return list;
    }

    public void initMap() {
        // SWFluid
        var sfList = getSfList();
        for (SwitchLocalSet sf : sfList) {
            NAME_MAP_FLUID_SWITCH.put(sf.getPre(), sf.getAlt());
        }

        // SWItem
        var silist = getSiList();
        for (SwitchLocalSet si : silist) {
            UNI_NAME_MAP_ITEM_SWITCH.put(si.getPre(), si.getAlt());
        }
    }
}
