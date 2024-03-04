package com.rhynia.ochelper.var;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Format;
import lombok.Data;

import java.math.BigDecimal;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;

@Data
public class AEItem {

    private final String name;
    private final String un;
    private final String label;
    private final String sizeRaw;
    private final String sizeString;
    private final BigDecimal size;
    private final int meta;
    private final boolean hasTag;
    private final boolean isCraftable;

    @JsonCreator
    public AEItem(
            @JsonProperty("label") String label,
            @JsonProperty("name") String name,
            @JsonProperty("damage") int damage,
            @JsonProperty("hasTag") boolean hasTag,
            @JsonProperty("isCraftable") boolean isCraftable,
            @JsonProperty("size") String size) {
        BigDecimal tmpSize = new BigDecimal(size);
        this.name = name;
        this.un = Format.assembleItemUN(name, damage);
        this.label = label;
        this.meta = damage;
        this.sizeRaw = size;
        this.size = tmpSize;
        this.sizeString = tmpSize.toPlainString();
        this.hasTag = hasTag;
        this.isCraftable = isCraftable;
    }

    public AEItemDisplay getDisplay() {
        if (!this.getUn().endsWith("drop$0")) {
            return new AEItemDisplay(this.un, this.label, this.sizeString);
        } else {
            // For drop to cell processing
            String tmp = this.label.substring(8);
            String mapLocal = NAME_MAP_FLUID_SWITCH.getOrDefault(tmp, tmp);
            return new AEItemDisplay(this.un, mapLocal, this.sizeString);
        }
    }

}
