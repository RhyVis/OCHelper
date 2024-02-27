package com.rhynia.ochelper.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.config.Path;
import com.rhynia.ochelper.var.AEItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AEItemDao {

    private final String item_loc;

    public AEItemDao(Path path) {
        this.item_loc = path.getJsonPath() + "indexItem.json";
    }

    public List<AEItem> getAEItemList() throws Exception {
        String json = IOUtils.toString(new FileInputStream(item_loc), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, new TypeReference<>() {
        });
    }
}
