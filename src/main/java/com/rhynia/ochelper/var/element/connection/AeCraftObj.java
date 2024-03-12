package com.rhynia.ochelper.var.element.connection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.base.AbstractAeObject;

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
public class AeCraftObj extends AbstractAeObject {
    protected final String name;
    protected final String label;
    protected final int meta;
    protected final boolean isCraftable;
    protected final String local;
    @Setter
    protected String imgPath;

    @JsonCreator
    public AeCraftObj(
            @JsonProperty("name") String name,
            @JsonProperty("label") String label,
            @JsonProperty("damage") int meta,
            @JsonProperty("isCraftable") boolean isCraftable) {
        super(Format.assembleItemUniqueName(name, meta));
        this.name = name;
        this.label = label;
        this.meta = meta;
        this.isCraftable = isCraftable;
        if (!this.un.endsWith("drop$0")) {
            this.local = Format.tryTranslateItemUn(this.un);
        } else {
            // For drop to cell processing
            String tmp1 = this.label.substring(8);
            String tmp2 = Format.trySwitchFluidLocal(tmp1);
            this.local = tmp2 + "单元";
        }
    }
}
