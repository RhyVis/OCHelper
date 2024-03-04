package com.rhynia.ochelper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.config.CommonValue;
import com.rhynia.ochelper.var.CommandPack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuaScriptFactory {

    private final PathAccessor pa;
    private final CommonValue cv;

    @Getter
    private final List<CommandPack> commonPackNormal = List.of(
            CommandPackEnum.AE_GET_ITEM.getPack(),
            CommandPackEnum.AE_GET_FLUID.getPack()
    );
    @Setter
    private boolean preloadCompleted = false;
    private String luaScriptsBase = "";
    private List<CommandPack> commandPacks = Stream.of(CommandPackEnum.AE_GET_ITEM.getPack(), CommandPackEnum.AE_GET_FLUID.getPack()).collect(Collectors.toList());

    public void initLuaScript() {
        commandPacks = Stream.of(CommandPackEnum.AE_GET_ITEM.getPack(), CommandPackEnum.AE_GET_FLUID.getPack()).collect(Collectors.toList());
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

    public void injectMission(CommandPack pack) {
        commandPacks.add(pack);
    }

    public void injectMission(List<CommandPack> packs) {
        commandPacks.addAll(packs);
    }

    public void resetCommandPacks() {
        commandPacks = new ArrayList<>();
        commandPacks.add(CommandPackEnum.NULL.getPack());
    }

    public String assembleLuaScript(List<CommandPack> packs) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        if (!luaScriptsBase.isEmpty()) {
            String codeBase = luaScriptsBase;
            String div = "\n";
            for (CommandPack cp : packs) {
                String codeChunk = codeBase + div + cp.getCommand();
                map.put(cp.getType(), codeChunk);
            }
        } else if (!preloadCompleted) {
            map.put(CommandPackEnum.NULL.getKey(), CommandPackEnum.NULL.getCommand());
        } else {
            map.put(CommandPackEnum.ERROR.getKey(), CommandPackEnum.ERROR.getCommand());
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
