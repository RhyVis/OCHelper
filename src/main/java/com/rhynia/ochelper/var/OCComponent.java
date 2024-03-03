package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OCComponent {
    private final String address;
    private final String name;
}
