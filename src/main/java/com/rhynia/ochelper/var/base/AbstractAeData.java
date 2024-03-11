package com.rhynia.ochelper.var.base;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.ToString;

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
        BigDecimal tmp = new BigDecimal(sizeRaw).stripTrailingZeros();
        this.sizeRaw = sizeRaw;
        this.size = tmp;
        this.sizeString = tmp.toPlainString();
    }
}