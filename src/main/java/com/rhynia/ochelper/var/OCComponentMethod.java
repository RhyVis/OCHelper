package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OCComponentMethod {
    private final String method;
    private final boolean valid;
}
