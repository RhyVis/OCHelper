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
public class AEFluid {
    private String name;
    private String label;
    private String amount;

    public String processRawAeSize() {
        if (amount.contains("E") || amount.contains("e")) {
            BigDecimal bd = new BigDecimal(amount);
            return bd.toPlainString();
        }
        if (amount.contains(".0"))
            return amount.substring(0, amount.indexOf("."));
        return amount;
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
            return "";
        return "(" + out + ")";
    }

    public String getUniqueName() {
        return "fluid$" + Format.removeUnavailableChar(this.name);
    }
}
