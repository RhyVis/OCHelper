package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultPack {
    private final String keyRaw;
    private final String content;
}
