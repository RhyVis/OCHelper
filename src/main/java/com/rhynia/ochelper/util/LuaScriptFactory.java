package com.rhynia.ochelper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.accessor.PathAccessor;
import com.rhynia.ochelper.var.CommandPack;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuaScriptFactory {

    private final PathAccessor pa;

    private final String div = "\n";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Queue<CommandPack> queue = new LinkedList<>();
    @Setter
    private boolean preloadCompleted = false;
    private String luaScriptsBase = "";

    public void initLuaScript() {
        queue.offer(CommandPackEnum.AE_GET_ITEM.getPack());
        queue.offer(CommandPackEnum.AE_GET_FLUID.getPack());
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

    public void injectMission(CommandPack... packs) {
        var opt = Optional.ofNullable(packs);
        opt.ifPresent(cps -> Arrays.stream(cps).map(queue::offer).close());
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
                if (!preloadCompleted) tmp = CommandPackEnum.NULL.getPack();
                tmpMap.put(tmp.getType(), luaScriptsBase + div + tmp.getCommand());
            }
        } else {
            tmpMap.put(CommandPackEnum.NULL.getKey(), CommandPackEnum.NULL.getCommand());
        }
        return assembleLuaScript(tmpMap);
    }
}
