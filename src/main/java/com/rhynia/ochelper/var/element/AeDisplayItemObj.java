package com.rhynia.ochelper.var.element;

import com.rhynia.ochelper.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM_SWITCH;

@Getter
@ToString
@EqualsAndHashCode
public class AeDisplayItemObj extends AeDataSetObj {
    protected final String local;
    protected final String sizeFormatted;
    protected final String sizeByte;
    @Setter
    protected String imgPath;

    protected AeDisplayItemObj(String un, String size, long id, String time) {
        super(un, size, id, time);
        this.local = UNI_NAME_MAP_ITEM.getOrDefault(un, UNI_NAME_MAP_ITEM_SWITCH.getOrDefault(un, un));
        this.sizeFormatted = Format.formatSizeDisplay(sizeString);
        this.sizeByte = Format.formatStringByte(sizeString);
    }

    public static AeDisplayItemObj getDummy() {
        return new AeDisplayItemObj("null$null", "0", 0, "2024-01-01 00:00:00");
    }
}
