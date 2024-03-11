package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@AllArgsConstructor
public class CommandPack {
    private final String type;
    private final String command;
}
