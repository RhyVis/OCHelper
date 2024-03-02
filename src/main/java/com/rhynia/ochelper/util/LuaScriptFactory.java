package com.rhynia.ochelper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.var.CommandPack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuaScriptFactory {

    private final PathAccessor pa;
    @Getter
    private final HashSet<CommandPack> commonPackNormal = new HashSet<>(
            Arrays.asList(
                    CommandPackEnum.AE_GET_ITEM.getPack(),
                    CommandPackEnum.AE_GET_FLUID.getPack()
            )
    );
    private String luaScriptsBase = "";
    private HashSet<CommandPack> commandPacks;

    public void initLuaScript() {
        commandPacks = new HashSet<>(List.of(CommandPackEnum.NULL.getPack()));
        File filePath = new File(pa.getPath().getLuaScriptsPath());
        File[] fileList = filePath.listFiles();
        if (fileList != null) {
            String div = "\n";
            StringBuilder builder = new StringBuilder();
            try {
                for (File script : fileList) {
                    String scriptLine = FileUtils.readFileToString(script, StandardCharsets.UTF_8);
                    builder.append(div).append(scriptLine);
                }
            } catch (IOException e) {
                log.error("Error in reading script blocks: ", e);
            }
            luaScriptsBase = builder.toString();
            log.info("Committed a lua script base update.");
        }
    }

    public void injectMission(HashSet<CommandPack> packs) {
        commandPacks.addAll(packs);
    }

    public void resetCommandPacks() {
        commandPacks.clear();
        commandPacks.add(CommandPackEnum.NULL.getPack());
    }

    public String assembleLuaScript(HashSet<CommandPack> packs) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        if (!luaScriptsBase.isEmpty()) {
            String codeBase = luaScriptsBase;
            String div = "\n";
            for (CommandPack cp : packs) {
                String codeChunk = codeBase + div + cp.getCommand();
                map.put(cp.getType(), codeChunk);
            }
        } else {
            map.put("ERROR", "return ERROR");
        }
        String jsonMappedScript = "";
        try {
            jsonMappedScript = mapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Exception in assembling scripts:", e);
        }
        return jsonMappedScript;
    }

    public String assembleLuaScript() {
        return assembleLuaScript(commandPacks);
    }

}
