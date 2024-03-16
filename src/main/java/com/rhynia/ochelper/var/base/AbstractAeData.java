package com.rhynia.ochelper.var.base;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Rhynia
 */
@Getter
@ToString
public abstract class AbstractAeData extends AbstractAeObject {
    protected final BigDecimal size;
    protected final String sizeRaw;
    protected final String sizeString;

    protected AbstractAeData(String un, String sizeRaw) {
        super(un);
        BigDecimal tmp =
                new BigDecimal(sizeRaw).stripTrailingZeros().setScale(0, RoundingMode.DOWN);
        this.sizeRaw = sizeRaw;
        this.size = tmp;
        this.sizeString = tmp.toPlainString();
    }
}
