package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@AllArgsConstructor(staticName = "of")
public class OcComponentDoc {
    private final String method;
    private final String doc;
}
