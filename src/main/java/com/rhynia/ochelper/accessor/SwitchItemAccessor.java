package com.rhynia.ochelper.accessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.var.SwitchItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SwitchItemAccessor {

    @Value("classpath:/config/switch_item.json")
    private Resource siSource;

    public List<SwitchItem> getSIList() throws Exception {
        String json = IOUtils.toString(siSource.getInputStream(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<>() {
        });
    }

}
