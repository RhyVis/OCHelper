package com.rhynia.ochelper.var.element.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OcComponentMethod {
    private final String method;
    private final boolean valid;
}
