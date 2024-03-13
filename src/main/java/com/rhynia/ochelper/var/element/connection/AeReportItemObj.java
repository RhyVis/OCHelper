package com.rhynia.ochelper.var.element.connection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Utilities;
import com.rhynia.ochelper.var.base.AbstractAeData;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rhynia
 */
@Getter
@ToString
@EqualsAndHashCode
public class AeReportItemObj extends AbstractAeData {
    protected final String name;
    protected final String label;
    protected final int meta;
    protected final boolean hasTag;
    protected final boolean isCraftable;
    @Setter protected String local;

    @JsonCreator
    public AeReportItemObj(
            @JsonProperty("label") String label,
            @JsonProperty("name") String name,
            @JsonProperty("damage") int damage,
            @JsonProperty("hasTag") boolean hasTag,
            @JsonProperty("isCraftable") boolean isCraftable,
            @JsonProperty("size") String size) {
        super(Utilities.assembleItemUniqueName(name, damage), size);
        this.name = name;
        this.label = label;
        this.local = Utilities.tryTranslateItemUn(un);
        this.meta = damage;
        this.hasTag = hasTag;
        this.isCraftable = isCraftable;
    }

    public static AeReportItemObj getDummy() {
        return new AeReportItemObj("无", "NULL", 0, false, false, "0");
    }

    // Used in CPU detail fetching
    public AeDisplayItemObj getDisplay() {
        if (!this.getUn().endsWith("drop$0")) {
            // Try to translate label
            return new AeDisplayItemObj(
                    this.un, this.sizeString, this.local, 0, "2024-01-01 00:00:00");
        } else {
            // For drop to cell processing
            String tmp1 = this.label.substring(8);
            String tmp2 = Utilities.trySwitchFluidLocal(tmp1);
            return new AeDisplayItemObj(
                    this.un, this.sizeString, tmp2 + "单元", 0, "2024-01-01 00:00:00");
        }
    }
}
