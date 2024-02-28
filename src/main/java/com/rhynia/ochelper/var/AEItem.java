package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

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

    private String processRawAeSize() {
        if (size.contains("E") || size.contains("e")) {
            BigDecimal bd = new BigDecimal(size);
            return bd.toPlainString();
        }
        if (size.contains(".0"))
            return size.substring(0, size.indexOf("."));
        return size;
    }

    public String getAeSizeDisplay() {
        BigDecimal bd = new BigDecimal(this.processRawAeSize());
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(bd);
    }

    public String getAeSizeByteDisplay() {
        String temp = this.processRawAeSize();
        String out = Format.formatStringByte(this.processRawAeSize());
        if (Objects.equals(temp, out))
            return "-";
        return "(" + out + ")";
    }
}
