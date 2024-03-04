package com.rhynia.ochelper.util;

import com.rhynia.ochelper.var.CommandPack;
import lombok.Getter;

@Getter
public enum CommandPackEnum {
    NULL(),
    ERROR("return \"ERROR\""),
    CUSTOM(),
    AE_GET_ITEM("return aeItem()"),
    AE_GET_FLUID("return aeFluid()"),
    AE_GET_CPU_INFO("return aeCpuInfo()"),
    AE_GET_CPU_DETAIL(),
    OC_GET_COMPONENT("return c.list()"),
    OC_GET_COMPONENT_METHOD(),
    OC_GET_COMPONENT_DOC(),
    GT_GET_SENSOR("return c.proxy('val').getSensorInformation()"),
    TPS_ALL_TICK_TIMES("return tpsAll()"),
    ;

    private final String key, command;

    CommandPackEnum(String command) {
        this.key = this.toString();
        this.command = command;
    }

    CommandPackEnum() {
        this.key = this.toString();
        this.command = "return \"NULL\"";
    }

    public CommandPack getPack() {
        return new CommandPack(this.key, this.command);
    }

    /**
     * Override certain pack with command.
     */
    public CommandPack ofCommand(String command) {
        return new CommandPack(this.key, command);
    }

}
