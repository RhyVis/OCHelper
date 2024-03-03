package com.rhynia.ochelper.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.util.CommandPackEnum;
import com.rhynia.ochelper.util.LuaScriptFactory;
import com.rhynia.ochelper.var.AECPU;
import com.rhynia.ochelper.var.AEFluid;
import com.rhynia.ochelper.var.AEItem;
import com.rhynia.ochelper.var.CommandPack;
import com.rhynia.ochelper.var.MsSet;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataProcessor {

    private final DatabaseUpdater du;
    private final LuaScriptFactory ls;
    private final List<AECPU> cpus = new ArrayList<>();
    private final List<OCComponent> components = new ArrayList<>();
    private final List<OCComponentMethod> componentMethods = new ArrayList<>();
    private final List<OCComponentDoc> componentDocs = new ArrayList<>();
    private final List<MsSet> msSets = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition cCpuFetch = lock.newCondition();
    private final Condition cComponentFetch = lock.newCondition();
    private final Condition cMethodFetch = lock.newCondition();
    private final Condition cDocFetch = lock.newCondition();
    private final Condition cTPSFetch = lock.newCondition();
    private boolean duringDocFetch = false;
    private int docIndex = 0;
    private final String test = "";

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

        log.info("Requesting component list.");
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.OC_GET_COMPONENT.getPack());
            cComponentFetch.await();
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
        }

        if (!components.isEmpty()) {
            log.info("Components successfully fetched.");
            return components;
        } else {
            log.error("Fetch no available components.");
            return List.of(new OCComponent("Fetched NOTHING!", "NULL"));
        }
    }

    public List<OCComponentDoc> requestComponentDetail(String address) {

        log.info("Start requesting component detail.");
        String fetchMethodCommand = "return c.methods(\"" + address + "\")";
        lock.lock();
        try {
            ls.injectMission(new CommandPack(CommandPackEnum.OC_GET_COMPONENT_METHOD.getKey(), fetchMethodCommand));
            cMethodFetch.await();
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
        }

        if (componentMethods.isEmpty()) {
            log.error("Fetch no available methods.");
            return List.of(new OCComponentDoc("NULL", "This component has no methods."));
        }

        log.info("Method list fetched, start requesting docs.");
        List<CommandPack> fetchDocCommands = new ArrayList<>();
        for (docIndex = 0; docIndex < componentMethods.size(); docIndex++) {
            String fetchDocCommand = "return c.doc(\"" + address + "\", \"" + componentMethods.get(docIndex).getMethod() + "\")";
            fetchDocCommands.add(new CommandPack(CommandPackEnum.OC_GET_COMPONENT_DOC.getKey() + "_" + componentMethods.get(docIndex).getMethod(), fetchDocCommand));
        }

        lock.lock();
        try {
            ls.injectMission(fetchDocCommands);
            duringDocFetch = true;
            cDocFetch.await();
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
        }

        if (!componentDocs.isEmpty()) {
            log.info("Methods & Docs successfully fetched.");
            return componentDocs;
        } else {
            log.error("Fetched no available docs.");
            return List.of(new OCComponentDoc("NULL", "Fetched nothing at all."));
        }
    }

    public void postProcessDocFetch() {
        componentMethods.clear();
        componentDocs.clear();
    }

    public void test() {
        requestAeCpuInfo();
        requestTPSReport();
    }

    public List<AECPU> requestAeCpuInfo() {

        log.info("Requested CPU info.");
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.AE_GET_CPU_INFO.getPack());
            cCpuFetch.await();
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
        }

        if (!cpus.isEmpty()) {
            log.info("AE Cpu info successfully fetched.");
            return cpus;
        } else {
            log.error("Fetched no cpus.");
            return List.of(new AECPU(0, 0, true, "NO_CPU"));
        }
    }

    public List<MsSet> requestTPSReport() {

        log.info("Requested TPS.");
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.TPS_ALL_TICK_TIMES.getPack());
            cTPSFetch.await();
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
        }

        if (!msSets.isEmpty()) {
            return msSets;
        } else {
            return List.of(new MsSet(0, 1D));
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
                    case "AE_GET_CPU_INFO" -> {
                        try {
                            cpus.clear();
                            List<AECPU> temp = mapper.readValue(v, new TypeReference<>() {
                            });
                            cpus.addAll(temp);
                            lock.lock();
                            try {
                                cCpuFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "OC_GET_COMPONENT" -> {
                        try {
                            var temp = mapper.readValue(v, Map.class);
                            components.clear();
                            temp.forEach((m, n) -> components.add(new OCComponent((String) m, (String) n)));
                            lock.lock();
                            try {
                                cComponentFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                            log.info("Received component info: " + temp);
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "OC_GET_COMPONENT_METHOD" -> {
                        try {
                            if (!Objects.equals(v, "[]")) {
                                var temp = mapper.readValue(v, Map.class);
                                componentMethods.clear();
                                temp.forEach((m, n) -> componentMethods.add(new OCComponentMethod((String) m, (boolean) n)));
                                log.info("Received method info: " + temp);
                            } else {
                                log.info("Requested a component that has no methods.");
                                componentMethods.clear();
                            }
                            lock.lock();
                            try {
                                cMethodFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in " + k + ":", e);
                        }
                    }
                    case "TPS_ALL_TICK_TIMES" -> {
                        try {
                            var temp = mapper.readValue(v, Map.class);
                            msSets.clear();
                            temp.forEach((m, n) -> msSets.add(new MsSet(Integer.parseInt((String) m), (double) n)));
                            lock.lock();
                            try {
                                log.info("Received TPS report.");
                                cTPSFetch.signal();
                            } finally {
                                lock.unlock();
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
        if (duringDocFetch && componentDocs.size() >= docIndex) {
            log.info("Doc fetch complete.");
            lock.lock();
            try {
                cDocFetch.signal();
            } finally {
                lock.unlock();
                duringDocFetch = false;
            }
        }
    }
}
