package com.rhynia.ochelper.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.var.SwitchFluid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SwitchFluidDao {

    @Value("classpath:/config/switch_fluid.json")
    private Resource sfSource;

    public List<SwitchFluid> getSFList() throws Exception {
        String json = IOUtils.toString(sfSource.getInputStream(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<>() {});
    }

}
