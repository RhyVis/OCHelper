package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@AllArgsConstructor(staticName = "of")
public class OcComponent {
    private final String address;
    private final String name;
}
