package com.rhynia.ochelper.var;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyData {
    private final long id;
    private final BigDecimal size;
    private final String sizeRaw;
    private final String sizeString;
    private final String time;

    @Builder
    public EnergyData(long id, String sizeRaw, String time) {
        BigDecimal temp = new BigDecimal(sizeRaw).stripTrailingZeros();
        this.id = id;
        this.sizeRaw = sizeRaw;
        this.time = time;
        this.size = temp;
        this.sizeString = temp.toPlainString();
    }
}
