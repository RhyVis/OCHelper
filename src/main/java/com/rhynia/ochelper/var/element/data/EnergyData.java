package com.rhynia.ochelper.var.element.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Rhynia
 */
@Data
public class EnergyData {

    private final long id;
    private final BigDecimal size;
    private final String sizeRaw;
    private final String sizeString;
    private final String time;

    public EnergyData(long id, String sizeRaw, String time) {
        BigDecimal temp = new BigDecimal(sizeRaw).stripTrailingZeros();
        this.id = id;
        this.sizeRaw = sizeRaw;
        this.time = time;
        this.size = temp;
        this.sizeString = temp.toPlainString();
    }

    public static EnergyData of(long id, String sizeRaw, String time) {
        return new EnergyData(id, sizeRaw, time);
    }
}
