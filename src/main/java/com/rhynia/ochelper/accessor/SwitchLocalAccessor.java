package com.rhynia.ochelper.accessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.var.SwitchLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SwitchLocalAccessor {

    @Value("classpath:/config/switch_item.json")
    private Resource siSource;
    @Value("classpath:/config/switch_fluid.json")
    private Resource sfSource;

    public List<SwitchLocal> getSIList() {
        String json = "";
        List<SwitchLocal> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = IOUtils.toString(siSource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error in reading JSON config.", e);
        }
        try {
            list = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error in mapping JSON config", e);
        }
        return list;
    }

    public List<SwitchLocal> getSFList() {
        String json = "";
        List<SwitchLocal> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = IOUtils.toString(sfSource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error in reading JSON config.", e);
        }
        try {
            list = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error in mapping JSON config", e);
        }
        return list;
    }
}
