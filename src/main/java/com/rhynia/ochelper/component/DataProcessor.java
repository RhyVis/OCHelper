package com.rhynia.ochelper.component;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhynia.ochelper.database.DatabaseUpdater;
import com.rhynia.ochelper.util.CommandPackEnum;
import com.rhynia.ochelper.util.LuaScriptFactory;
import com.rhynia.ochelper.var.element.connection.AeCpu;
import com.rhynia.ochelper.var.element.connection.AeReportFluidObj;
import com.rhynia.ochelper.var.element.connection.AeReportItemObj;
import com.rhynia.ochelper.var.element.connection.CommandPack;
import com.rhynia.ochelper.var.element.connection.MsSet;
import com.rhynia.ochelper.var.element.connection.OcComponent;
import com.rhynia.ochelper.var.element.connection.OcComponentDoc;
import com.rhynia.ochelper.var.element.connection.OcComponentMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataProcessor {

    private final DatabaseUpdater du;
    private final LuaScriptFactory ls;
    private final List<AeCpu> cpus = new ArrayList<>();
    private final List<OcComponent> components = new ArrayList<>();
    private final List<OcComponentMethod> componentMethods = new ArrayList<>();
    private final List<OcComponentDoc> componentDocs = new ArrayList<>();
    private final List<MsSet> msSets = new ArrayList<>();
    private final List<String> sensorInfo = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition cCpuFetch = lock.newCondition();
    private final Condition cCpuDetailFetch = lock.newCondition();
    private final Condition cComponentFetch = lock.newCondition();
    private final Condition cMethodFetch = lock.newCondition();
    private final Condition cDocFetch = lock.newCondition();
    private final Condition cTPSFetch = lock.newCondition();
    private final Condition cGtSensorFetch = lock.newCondition();
    private final Condition cCustomFetch = lock.newCondition();
    private final AeReportItemObj dummy = new AeReportItemObj("无", "NULL", 0, false, false, "0");
    @SuppressWarnings("unchecked")
    private final List<AeReportItemObj>[] cpuDetailList = new ArrayList[3];
    private AeReportItemObj cpuDetailFinal = dummy;
    private String customReturn = "";
    private boolean duringDocFetch = false, duringCpuDetailFetch = false;
    private int docIndex = 0, cpuDetailIndex = 0;

    private void updateAeItemData(List<AeReportItemObj> list) {
        var opt = Optional.ofNullable(list);
        opt.ifPresent(l -> {
            var d = l.stream().peek(obj -> {
                if (UNI_NAME_MAP_ITEM.containsKey(obj.getUn())) {
                    UNI_NAME_MAP_ITEM.put(obj.getUn(), obj.getLabel());
                    obj.setLocal(obj.getLabel());
                }
            }).toList();
            long begin = System.currentTimeMillis();
            du.updateItemDatabase(d);
            long end = System.currentTimeMillis();
            log.info("Received item report form OC, size: {}, using {} ms.", l.size(), end - begin);
        });
    }

    private void updateAeFluidData(List<AeReportFluidObj> list) {
        var opt = Optional.ofNullable(list);
        opt.ifPresent(l -> {
            long begin = System.currentTimeMillis();
            du.updateFluidDatabase(l);
            long end = System.currentTimeMillis();
            log.info("Received fluid report form OC, size: {}, using {} ms.", l.size(), end - begin);
        });
    }

    public List<OcComponent> requestComponentList() {

        log.info("Requesting component list.");
        boolean timeout = false;
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.OC_GET_COMPONENT.getPack());
            timeout = !cComponentFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }

        if (!components.isEmpty()) {
            log.info("Components successfully fetched.");
            return components;
        } else {
            log.error("Fetch no available components.");
            return List.of(new OcComponent("Fetched NOTHING!", "NULL"));
        }
    }

    public List<OcComponentDoc> requestComponentDetail(String address) {

        log.info("Start requesting component detail.");
        String fetchMethodCommand = "return c.methods(\"" + address + "\")";
        boolean timeout = false;
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.OC_GET_COMPONENT_METHOD.ofCommand(fetchMethodCommand));
            timeout = !cMethodFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }

        if (componentMethods.isEmpty()) {
            log.error("Fetch no available methods.");
            return List.of(new OcComponentDoc("NULL", "This component has no methods."));
        }

        log.info("Method list fetched, start requesting docs.");
        List<CommandPack> fetchDocCommands = new ArrayList<>();
        for (docIndex = 0; docIndex < componentMethods.size(); docIndex++) {
            String fetchDocCommand =
                "return c.doc(\"" + address + "\", \"" + componentMethods.get(docIndex).getMethod() + "\")";
            fetchDocCommands.add(new CommandPack(
                CommandPackEnum.OC_GET_COMPONENT_DOC.getKey() + "_" + componentMethods.get(docIndex).getMethod(),
                fetchDocCommand));
        }

        lock.lock();
        try {
            ls.injectMission(fetchDocCommands);
            duringDocFetch = true;
            timeout = !cDocFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }

        if (!componentDocs.isEmpty()) {
            log.info("Methods & Docs successfully fetched.");
            return componentDocs;
        } else {
            log.error("Fetched no available docs.");
            return List.of(new OcComponentDoc("NULL", "Fetched nothing at all."));
        }
    }

    public void postProcessDocFetch() {
        componentMethods.clear();
        componentDocs.clear();
    }

    public String executeCustomCommand(String command) {

        log.info("Requested executing a custom command: {}", command);
        var cc = new CommandPack(CommandPackEnum.CUSTOM.getKey(), command);
        boolean timeout = false;
        lock.lock();
        try {
            ls.injectMission(cc);
            timeout = !cCustomFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }
        return customReturn;
    }

    public List<AeCpu> requestAeCpuInfo() {

        log.info("Requested CPU info.");
        boolean timeout = false;
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.AE_GET_CPU_INFO.getPack());
            timeout = !cCpuFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }

        if (!cpus.isEmpty()) {
            log.info("AE CPU info successfully fetched.");
            return cpus;
        } else {
            log.error("Fetched no cpus.");
            return List.of(new AeCpu(0, 0, "0", true, "NO_CPU"));
        }
    }

    public Pair<List<AeReportItemObj>[], AeReportItemObj> requestAeCpuDetail(int cpuid) {

        log.info("Requesting CPU detail.");
        cpuDetailFinal = dummy;
        cpuDetailIndex = 0;
        var cpl = List.of(CommandPackEnum.AE_GET_CPU_DETAIL_ACTIVE.ofCommand("return aeCpuDetail1(" + cpuid + ")"),
            CommandPackEnum.AE_GET_CPU_DETAIL_STORE.ofCommand("return aeCpuDetail2(" + cpuid + ")"),
            CommandPackEnum.AE_GET_CPU_DETAIL_PENDING.ofCommand("return aeCpuDetail3(" + cpuid + ")"),
            CommandPackEnum.AE_GET_CPU_DETAIL_FINAL.ofCommand("return aeCpuDetail4(" + cpuid + ")"));
        boolean timeout = false;
        duringCpuDetailFetch = true;
        lock.lock();
        try {
            ls.injectMission(cpl);
            timeout = !cCpuDetailFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }
        return Pair.of(cpuDetailList, cpuDetailFinal);
    }

    public List<String> requestGtMachineSensor(String proxyAddress) {

        log.info("Requesting GT Sensor of {}.", proxyAddress);
        boolean timeout = false;
        var cc =
            CommandPackEnum.GT_GET_SENSOR.ofCommand("return c.proxy(\"" + proxyAddress + "\").getSensorInformation()");
        lock.lock();
        try {
            ls.injectMission(cc);
            timeout = !cGtSensorFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in requesting.");
            }
        }
        return sensorInfo;
    }

    public List<MsSet> requestTpsReport() {

        log.info("Requested TPS.");
        boolean timeout = false;
        lock.lock();
        try {
            ls.injectMission(CommandPackEnum.TPS_ALL_TICK_TIMES.getPack());
            timeout = !cTPSFetch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Method interrupted.", e);
        } finally {
            lock.unlock();
            if (timeout) {
                log.error("Timeout in fetching.");
            }
        }

        if (!msSets.isEmpty()) {
            return msSets;
        } else {
            return List.of(new MsSet(0, 1D));
        }

    }

    @SuppressWarnings("unchecked")
    private Map<String, String> readResult(String raw) {
        if (raw == null || raw.isEmpty() || "[]".equals(raw)) {
            return Map.of("NULL", "NULL");
        }
        Map<String, String> result = Map.of("NULL", "NULL");
        try {
            ObjectMapper mapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            result = mapper.readValue(raw, Map.class);
        } catch (Exception e) {
            log.error("Exception in decoding: ", e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void processResult(Map<String, String> map) {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        map.forEach((k, v) -> {
            boolean error = Objects.equals(v, "ERROR") || Objects.equals(v, "\"ERROR\"");
            if (error) {
                log.error("Received ERROR report in action " + k);
            }
            if (k.startsWith("OC_GET_COMPONENT_DOC_")) {
                String method = k.substring(21);
                componentDocs.add(new OcComponentDoc(method, v));
            } else {
                switch (k) {
                    case "AE_GET_ITEM" -> {
                        try {
                            List<AeReportItemObj> temp = mapper.readValue(v, new TypeReference<>() {});
                            updateAeItemData(temp);
                        } catch (Exception e) {
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "AE_GET_FLUID" -> {
                        try {
                            List<AeReportFluidObj> temp = mapper.readValue(v, new TypeReference<>() {});
                            updateAeFluidData(temp);
                        } catch (Exception e) {
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "AE_GET_CPU_INFO" -> {
                        try {
                            cpus.clear();
                            List<AeCpu> temp = mapper.readValue(v, new TypeReference<>() {});
                            cpus.addAll(temp);
                            lock.lock();
                            try {
                                cCpuFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "AE_GET_CPU_DETAIL_ACTIVE" -> {
                        if (Objects.equals(v, "[]")) {
                            cpuDetailList[0] = new ArrayList<>();
                            cpuDetailList[0].add(dummy);
                        } else {
                            try {
                                List<AeReportItemObj> temp = mapper.readValue(v, new TypeReference<>() {});
                                cpuDetailList[0] = temp;
                            } catch (Exception e) {
                                log.error("Map fail in {} :", k, e);
                            }
                        }
                        cpuDetailIndex++;
                    }
                    case "AE_GET_CPU_DETAIL_STORE" -> {
                        if (Objects.equals(v, "[]")) {
                            cpuDetailList[1] = new ArrayList<>();
                            cpuDetailList[1].add(dummy);
                        } else {
                            try {
                                List<AeReportItemObj> temp = mapper.readValue(v, new TypeReference<>() {});
                                cpuDetailList[1] = temp;
                            } catch (Exception e) {
                                log.error("Map fail in {} :", k, e);
                            }
                        }
                        cpuDetailIndex++;
                    }
                    case "AE_GET_CPU_DETAIL_PENDING" -> {
                        if (Objects.equals(v, "[]")) {
                            cpuDetailList[2] = new ArrayList<>();
                            cpuDetailList[2].add(dummy);
                        } else {
                            try {
                                List<AeReportItemObj> temp = mapper.readValue(v, new TypeReference<>() {});
                                cpuDetailList[2] = temp;
                            } catch (Exception e) {
                                log.error("Map fail in {} :", k, e);
                            }
                        }
                        cpuDetailIndex++;
                    }
                    case "AE_GET_CPU_DETAIL_FINAL" -> {
                        if (Objects.equals(v, "null") || Objects.equals(v, "[]")) {
                            cpuDetailFinal = dummy;
                        } else {
                            try {
                                cpuDetailFinal = mapper.readValue(v, new TypeReference<>() {});
                            } catch (Exception e) {
                                log.error("Map fail in {} :", k, e);
                            }
                        }
                        cpuDetailIndex++;
                    }
                    case "OC_GET_COMPONENT" -> {
                        try {
                            var temp = mapper.readValue(v, Map.class);
                            components.clear();
                            temp.forEach((address, name) -> components
                                .add(OcComponent.builder().address((String)address).name((String)name).build()));
                            lock.lock();
                            try {
                                cComponentFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                            log.info("Received component info: " + temp);
                        } catch (Exception e) {
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "OC_GET_COMPONENT_METHOD" -> {
                        try {
                            if (!Objects.equals(v, "[]")) {
                                var temp = mapper.readValue(v, Map.class);
                                componentMethods.clear();
                                temp.forEach((method, valid) -> componentMethods.add(
                                    OcComponentMethod.builder().method((String)method).valid((Boolean)valid).build()));
                                log.info("Received method info: {}", temp);
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
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "GT_GET_SENSOR" -> {
                        log.info("Fetched sensor information of " + v);
                        try {
                            sensorInfo.clear();
                            List<String> tmp = mapper.readValue(v, new TypeReference<>() {});
                            sensorInfo.addAll(tmp);
                            lock.lock();
                            try {
                                cGtSensorFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "GT_GET_ENERGY_WIRELESS" -> {
                        if (!error) {
                            try {
                                List<String> tmp = mapper.readValue(v, new TypeReference<>() {});
                                var tmp1 = tmp.stream().filter(s -> s.startsWith("Total wireless EU"))
                                    .map(s -> s.substring(21)).findFirst().orElse("0");
                                log.info("Fetched energy information of " + tmp1);
                                var tmp2 = new BigDecimal(tmp1.replaceAll(",", "")).stripTrailingZeros();
                                du.updateEnergyDatabase(tmp2);
                            } catch (Exception e) {
                                log.error("Map fail in {} :", k, e);
                            }
                        } else {
                            log.error(
                                "Caught error in GT_GET_ENERGY_WIRELESS, energy station connection may not set properly.");
                        }
                    }
                    case "TPS_ALL_TICK_TIMES" -> {
                        try {
                            var temp = mapper.readValue(v, Map.class);
                            msSets.clear();
                            temp.forEach((dim, mspt) -> MsSet.builder().dim((Integer)dim).mspt((Double)mspt).build());
                            lock.lock();
                            try {
                                log.info("Received TPS report.");
                                cTPSFetch.signal();
                            } finally {
                                lock.unlock();
                            }
                        } catch (Exception e) {
                            log.error("Map fail in {} :", k, e);
                        }
                    }
                    case "CUSTOM" -> {
                        log.info("Received CUSTOM pack respond.");
                        customReturn = v;
                        lock.lock();
                        try {
                            cCustomFetch.signal();
                        } finally {
                            lock.unlock();
                        }
                    }
                    case "NULL" -> log.debug("Received request from OC.");
                    case "ERROR" ->
                        log.error("Encountered ERROR key package, seems exception in lua scripts assembling.");
                    default -> {
                        log.error("Encountered an unexpected key in command return: {}", k);
                        log.error("The content of result is: {}", v);
                    }
                }
            }
        });
        postProcessCheck();
    }

    private void postProcessCheck() {
        if (duringDocFetch && componentDocs.size() >= docIndex) {
            log.info("Doc fetch complete.");
            lock.lock();
            try {
                cDocFetch.signal();
            } finally {
                lock.unlock();
                duringDocFetch = false;
            }
            return;
        }
        if (duringCpuDetailFetch && cpuDetailIndex >= 4) {
            log.info("CPU information fetch complete.");
            lock.lock();
            try {
                cCpuDetailFetch.signal();
            } finally {
                lock.unlock();
                duringCpuDetailFetch = false;
                docIndex = 0;
            }
        }
    }

    public void readAndProcessResult(String raw) {
        processResult(readResult(raw));
    }
}
