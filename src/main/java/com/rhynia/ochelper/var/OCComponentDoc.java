package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OCComponentDoc {
    private final String method;
    private final String doc;
}
