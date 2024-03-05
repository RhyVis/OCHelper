package com.rhynia.ochelper.var.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;

@Getter
@ToString
@EqualsAndHashCode
public class AeDisplayFluidObj extends AeDataSetObj {
    protected final String local;
    @Setter
    protected String imgPath;

    protected AeDisplayFluidObj(String un, String size, long id, String time) {
        super(un, size, id, time);
        this.local = UNI_NAME_MAP_FLUID.getOrDefault(un, un);
    }

    public static AeDisplayFluidObj getDummy() {
        return new AeDisplayFluidObj("null$null", "0", 0, "2024-01-01 00:00:00");
    }
}
