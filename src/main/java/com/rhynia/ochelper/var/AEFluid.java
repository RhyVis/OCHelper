package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    public String getUniqueName() {
        return "fluid$" + Format.removeUnavailableChar(this.name);
    }
}
