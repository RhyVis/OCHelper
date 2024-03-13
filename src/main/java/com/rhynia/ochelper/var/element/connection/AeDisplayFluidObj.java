package com.rhynia.ochelper.var.element.connection;

import com.rhynia.ochelper.util.Utilities;
import com.rhynia.ochelper.var.element.data.AeDataSetObj;

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
public class AeDisplayFluidObj extends AeDataSetObj {
    protected final String local;
    protected final String switchedLocal;
    protected final String sizeFormatted;
    protected final String sizeByte;
    @Setter protected String imgPath;

    public AeDisplayFluidObj(String un, String size, long id, String time) {
        super(un, size, id, time);
        String tmpLocal = Utilities.tryTranslateFluidUn(un);
        this.local = tmpLocal;
        this.switchedLocal = Utilities.trySwitchFluidLocal(tmpLocal);
        this.sizeFormatted = Utilities.formatSizeWithComma(sizeString);
        this.sizeByte = Utilities.formatStringByte(sizeString);
    }

    public static AeDisplayFluidObj getDummy() {
        return new AeDisplayFluidObj("null$null", "0", 0, "2024-01-01 00:00:00");
    }
}
