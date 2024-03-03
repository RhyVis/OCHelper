package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OCComponentMethod {
    private final String method;
    private final boolean valid;
}
