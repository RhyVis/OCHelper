package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@AllArgsConstructor(staticName = "of")
public class CommandPack {
    private final String key;
    private final String command;
}
