package com.rhynia.ochelper.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.config.Path;
import com.rhynia.ochelper.var.AEFluid;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AEFluidDao {

    private final String fluid_loc;

    public AEFluidDao(Path path) {
        this.fluid_loc = path.getJsonPath() + "indexFluid.json";
    }

    public List<AEFluid> getAEFluidList() throws Exception {
        String json = IOUtils.toString(new FileInputStream(fluid_loc), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, new TypeReference<>() {
        });
    }
}
