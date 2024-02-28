package com.rhynia.ochelper.accessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.var.AEItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AEItemAccessor {

    private final String item_loc;

    AEItemAccessor(PathAccessor pa) {
        this.item_loc = pa.getPath_json_item();
    }

    public List<AEItem> getAEItemList() throws Exception {
        String json = IOUtils.toString(new FileInputStream(item_loc), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, new TypeReference<>() {
        });
    }
}
