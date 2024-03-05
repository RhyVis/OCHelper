package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OCComponent {
    private final String address;
    private final String name;
}
