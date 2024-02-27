package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AEItem {
    private String name;
    private String label;
    private String size;
    private int damage;
    private boolean hasTag;
    private boolean isCraftable;
}
