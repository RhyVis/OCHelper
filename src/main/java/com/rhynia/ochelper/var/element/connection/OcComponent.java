package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
@Builder
@AllArgsConstructor
public class OcComponent {
    private final String address;
    private final String name;
}
