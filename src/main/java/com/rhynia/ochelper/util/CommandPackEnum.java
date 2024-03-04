package com.rhynia.ochelper.util;

import com.rhynia.ochelper.var.CommandPack;
import lombok.Getter;

@Getter
public enum CommandPackEnum {
    NULL("return \"NULL\""),
    ERROR("return \"ERROR\""),
    CUSTOM("return \"NULL\""), //Just a reminder
    AE_GET_ITEM("return aeItem()"),
    AE_GET_FLUID("return aeFluid()"),
    AE_GET_CPU_INFO("return aeCpuInfo()"),
    AE_GET_CPU_DETAIL("return aeCpuDetail(val)"), //Just a reminder
    OC_GET_COMPONENT("return c.list()"),
    OC_GET_COMPONENT_METHOD("return \"NULL\""), //Just a reminder
    OC_GET_COMPONENT_DOC("return \"NULL\""), //Just a reminder
    TPS_ALL_TICK_TIMES("return tpsAll()"),
    ;

    private final String key, command;

    CommandPackEnum(String command) {
        this.key = this.toString();
        this.command = command;
    }

    public CommandPack getPack() {
        return new CommandPack(this.key, this.command);
    }

}
