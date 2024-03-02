package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandPack {
    private final String type;
    private final String command;
}
