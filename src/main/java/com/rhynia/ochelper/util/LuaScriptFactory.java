package com.rhynia.ochelper.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.config.PathAssemble;
import com.rhynia.ochelper.var.element.connection.CommandPack;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LuaScriptFactory {

    private final PathAssemble pa;

    private final String div = "\n";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Queue<CommandPack> queue = new LinkedList<>();
    @Setter
    private boolean preloadCompleted = false;
    private String luaScriptsBase = "";

    public void initLuaScript() {
        injectMission(List.of(CommandPackEnum.AE_GET_ITEM.getPack(), CommandPackEnum.AE_GET_FLUID.getPack()));
        File filePath = new File(pa.getPath().getLuaScriptsPath());
        File[] fileList = filePath.listFiles();
        if (fileList != null) {
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
        queue.offer(pack);
    }

    public void injectMission(List<CommandPack> packs) {
        var opt = Optional.ofNullable(packs);
        opt.ifPresent(cps -> cps.forEach(queue::offer));
    }

    public void injectMission(Set<CommandPack> packs) {
        var opt = Optional.ofNullable(packs);
        opt.ifPresent(cps -> cps.forEach(queue::offer));
    }

    private String assembleLuaScript(Map<String, String> map) {
        var tmp = "";
        try {
            tmp = mapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Exception in assembling scripts:", e);
        }
        return tmp;
    }

    public String pullAllQuests() {
        var tmpMap = new HashMap<String, String>();
        if (!queue.isEmpty()) {
            while (!queue.isEmpty()) {
                var tmp = queue.poll();
                if (!preloadCompleted) {
                    tmp = CommandPackEnum.NULL.getPack();
                }
                tmpMap.put(tmp.getKey(), luaScriptsBase + div + tmp.getCommand());
            }
        } else {
            tmpMap.put(CommandPackEnum.NULL.getKey(), CommandPackEnum.NULL.getCommand());
        }
        return assembleLuaScript(tmpMap);
    }
}
