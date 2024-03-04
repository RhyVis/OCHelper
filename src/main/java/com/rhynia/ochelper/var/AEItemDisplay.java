package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.Data;

import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;

@Data
public class AEItemDisplay {

    private final String un;
    private final String local;
    private final String sizeRaw;
    private final String sizeFormatted;
    private final String sizeByte;

    public AEItemDisplay(String un, String local, String sizeRaw) {
        this.un = un;
        this.local = local;
        this.sizeRaw = sizeRaw;
        this.sizeFormatted = Format.formatSizeDisplay(sizeRaw);
        this.sizeByte = Format.formatSizeByteDisplay(sizeRaw);
    }

    public String getLocalFromUn() {
        return UNI_NAME_MAP_ITEM.getOrDefault(this.un, this.un);
    }

}
