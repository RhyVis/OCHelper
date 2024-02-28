package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

@Data
@AllArgsConstructor
public class AEFluidDisplay {
    private final String un;
    private final String local;
    private final String size;

    public String getAeSizeDisplay() {
        BigDecimal bd = new BigDecimal(this.size);
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(bd);
    }

    public String getAeSizeByteDisplay() {
        String out = Format.formatStringByte(this.size);
        if (Objects.equals(this.size, out))
            return "-";
        return "(" + out + ")";
    }
}
