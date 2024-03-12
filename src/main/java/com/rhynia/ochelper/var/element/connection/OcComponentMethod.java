package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@AllArgsConstructor(staticName = "of")
public class OcComponentMethod {
    private final String method;
    private final boolean valid;
}
