package com.rhynia.ochelper.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.util.CommandPackEnum;
import com.rhynia.ochelper.util.LuaScriptFactory;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import com.rhynia.ochelper.var.CommandPack;
import com.rhynia.ochelper.var.OCComponent;
import com.rhynia.ochelper.var.OCComponentDoc;
import com.rhynia.ochelper.var.OCComponentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataProcessor {

    private final DatabaseUpdater du;
    private final LuaScriptFactory ls;
    private final List<OCComponent> components = new ArrayList<>();
    private final List<OCComponentMethod> componentMethods = new ArrayList<>();
    private final List<OCComponentDoc> componentDocs = new ArrayList<>();
    private final Object lock_componentsFetch = new Object();
    private final Object lock_componentMethodFetch = new Object();
    private final Object lock_componentDocFetch = new Object();
    private final Object lock_tpsFetch = new Object();
    private boolean completedFetch = false, completedMethodFetch = false, completedDocFetch = false, completedTPSFetch = false;
    private boolean duringDocFetch = false;
    private int docIndex = 0;
    private String test = "";

    private void updateAEItemData(List<AEItem> list) {
        long begin = System.currentTimeMillis();
        du.updateItemDatabase(list);
        long end = System.currentTimeMillis();
        log.info("Received item report form OC, size: " + list.size() + ", using " + (end - begin) + " ms.");
    }

    private void updateAEFluidData(List<AEFluid> list) {
        long begin = System.currentTimeMillis();
        du.updateFluidDatabase(list);
        long end = System.currentTimeMillis();
        log.info("Received fluid report form OC, size: " + list.size() + ", using " + (end - begin) + " ms.");
    }

    public List<OCComponent> requestComponentList() {
        completedFetch = false;
        ls.injectMission(CommandPackEnum.OC_GET_COMPONENT.getPack());
        log.info("Requested component list.");
        synchronized (lock_componentsFetch) {
            while (!completedFetch) {
                try {
                    lock_componentsFetch.wait();
                } catch (InterruptedException e) {
                    log.error("Method interrupted.", e);
                }
            }
            return components;
        }
    }

    public List<OCComponentDoc> requestComponentDetail(String address) {
        log.info("Start requesting component detail.");
        completedMethodFetch = completedDocFetch = false;
        String fetchMethodCommand = "return c.methods(\"" + address + "\")";
        ls.injectMission(new CommandPack(CommandPackEnum.OC_GET_COMPONENT_METHOD.getKey(), fetchMethodCommand));
        synchronized (lock_componentMethodFetch) {
            while (!completedMethodFetch) {
                try {
                    lock_componentMethodFetch.wait();
                } catch (InterruptedException e) {
                    log.error("Method interrupted.", e);
                }
            }
        }
        log.info("Method list fetched, start requesting docs.");
        List<CommandPack> fetchDocCommands = new ArrayList<>();
        for (docIndex = 0; docIndex < componentMethods.size(); docIndex++) {
            String fetchDocCommand = "return c.doc(\"" + address + "\", \"" + componentMethods.get(docIndex).getMethod() + "\")";
            fetchDocCommands.add(new CommandPack(CommandPackEnum.OC_GET_COMPONENT_DOC.getKey() + "_" + componentMethods.get(docIndex).getMethod(), fetchDocCommand));
        }
        ls.injectMission(fetchDocCommands);
        synchronized (lock_componentDocFetch) {
            while (!completedDocFetch) {
                try {
                    duringDocFetch = true;
                    lock_componentDocFetch.wait();
                } catch (InterruptedException e) {
                    log.error("Method interrupted.", e);
                }
            }
        }
        log.info("Methods & Docs successfully fetched.");
        return componentDocs;
    }

    public void postProcessDocFetch() {
        componentMethods.clear();
        componentDocs.clear();
    }

    public String requestTPSReport() {
        completedTPSFetch = false;
        ls.injectMission(CommandPackEnum.TPS_ALL_TICK_TIMES.getPack());
        synchronized (lock_tpsFetch) {
            try {
                while (!completedTPSFetch) {
                    lock_tpsFetch.wait();
                }
            } catch (InterruptedException e) {
                log.error("Method interrupted.", e);
            }
            return test;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> readResult(String raw) {
        Map<String, String> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            result = mapper.readValue(raw, Map.class);
        } catch (Exception e) {
            log.error("Exception in decoding: ", e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public void processResult(Map<String, String> map) {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        map.forEach((k, v) -> {
            if (Objects.equals(v, "ERROR")) {
                log.error("Received ERROR report.");
            }
            if (!k.startsWith("OC_GET_COMPONENT_DOC_")) {
                switch (k) {
                    case "AE_GET_ITEM" -> {
                        try {
                            List<AEItem> temp = mapper.readValue(v, new TypeReference<>() {
                            });
                            updateAEItemData(temp);
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "AE_GET_FLUID" -> {
                        try {
                            List<AEFluid> temp = mapper.readValue(v, new TypeReference<>() {
                            });
                            updateAEFluidData(temp);
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "OC_GET_COMPONENT" -> {
                        try {
                            var temp = mapper.readValue(v, Map.class);
                            components.clear();
                            temp.forEach((m, n) -> components.add(new OCComponent((String) m, (String) n)));
                            synchronized (lock_componentsFetch) {
                                completedFetch = true;
                                lock_componentsFetch.notifyAll();
                            }
                            log.info("Received component info: " + temp);
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "OC_GET_COMPONENT_METHOD" -> {
                        try {
                            var temp = mapper.readValue(v, Map.class);
                            componentMethods.clear();
                            temp.forEach((m, n) -> componentMethods.add(new OCComponentMethod((String) m, (boolean) n)));
                            log.info(componentMethods.toString());
                            synchronized (lock_componentMethodFetch) {
                                completedMethodFetch = true;
                                lock_componentMethodFetch.notifyAll();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "TPS_ALL_TICK_TIMES" -> {
                        try {
                            log.info(k, v);
                            test = v;
                            synchronized (lock_tpsFetch) {
                                completedTPSFetch = true;
                                lock_tpsFetch.notifyAll();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "NULL" -> log.info("Received request from OC.");
                    case "ERROR" ->
                            log.error("Encountered ERROR key package, seems exception in lua scripts assembling.");
                    default -> {
                        log.error("Encountered an unexpected key in command return: " + k);
                        log.error("The content of result is: " + v);
                    }
                }
            } else {
                String method = k.substring(21);
                componentDocs.add(new OCComponentDoc(method, v));
            }
        });
        if (duringDocFetch) {
            synchronized (lock_componentDocFetch) {
                if (componentDocs.size() >= docIndex) {
                    log.info("Doc fetch complete.");
                    completedDocFetch = true;
                    duringDocFetch = false;
                    lock_componentDocFetch.notifyAll();
                }
            }
        }
    }
}
