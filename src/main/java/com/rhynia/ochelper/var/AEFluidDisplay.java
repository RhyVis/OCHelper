package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.Data;

@Data
public class AEFluidDisplay {

    private final String un;
    private final String local;
    private final String sizeRaw;
    private final String sizeFormatted;
    private final String sizeByte;

    public AEFluidDisplay(String un, String local, String sizeRaw) {
        this.un = un;
        this.local = local;
        this.sizeRaw = sizeRaw;
        this.sizeFormatted = Format.formatSizeDisplay(sizeRaw);
        this.sizeByte = Format.formatSizeByteDisplay(sizeRaw);
    }

}
