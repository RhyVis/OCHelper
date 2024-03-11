package com.rhynia.ochelper.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.var.element.config.EnergyStationSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class EnergyStationConfig {

    @Value("classpath:/config/energy_station.json")
    private Resource resource;

    @Getter
    private final Map<Integer, String> esData = new HashMap<>();

    /**
     * For es config in file, the structure should follow the example
     * <p>
     *     [{"id":0,"address":"***-***-***"},...]
     *     The one with id 0 should be recognized as wireless energy reader
     * </p>
     */
    public void initAddressSet() {
        String json;
        try {
            json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Energy station conf init failed, the file may not exist.");
            return;
        }
        if (Objects.equals(json, "[]")) {
            log.info("Loaded energy station conf but empty.");
        } else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Set<EnergyStationSet> tmp = mapper.readValue(json, new TypeReference<>() {
                });
                tmp.forEach((v) -> esData.put(v.getOrdinal(), v.getAddress()));
            } catch (JsonProcessingException e) {
                log.error("Found energy station conf but load failed:", e);
            }
        }
    }
}
