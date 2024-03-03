package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    public String processRawAeSize() {
        if (size.contains("E") || size.contains("e")) {
            BigDecimal bd = new BigDecimal(size);
            return bd.toPlainString();
        }
        if (size.contains(".0"))
            return size.substring(0, size.indexOf("."));
        return size;
    }

    public String getUniqueName() {
        return "item$" + Format.removeUnavailableChar(this.name) + "$" + this.damage;
    }
}
