package com.rhynia.ochelper.util;

import com.rhynia.ochelper.var.CommandPack;
import lombok.Getter;

@Getter
public enum CommandPackEnum {
    NULL("return \"NULL\""),
    AE_GET_ITEM("return aeItem()"),
    AE_GET_FLUID("return aeFluid()");

    private final String key, command;

    CommandPackEnum(String command) {
        this.key = this.toString();
        this.command = command;
    }

    CommandPackEnum(String key, String command) {
        this.key = key;
        this.command = command;
    }

    public CommandPack getPack() {
        return new CommandPack(this.key, this.command);
    }

}
