package com.rhynia.ochelper.var.element.connection;

import java.text.DecimalFormat;

import lombok.Builder;
import lombok.Data;

/**
 * @author Rhynia
 */
@Data
public class MsSet {
    private final int dim;
    private final double mspt;
    private final String msptDisplay;
    private final double tps;
    private final boolean healthy;

    @Builder
    public MsSet(int dim, double mspt) {
        DecimalFormat df = new DecimalFormat("0.000000");
        this.dim = dim;
        this.mspt = mspt;
        this.msptDisplay = df.format(mspt);
        double temp = 1000D / mspt > 20 ? 20 : 1000D / mspt;
        this.tps = temp;
        this.healthy = temp >= 19.99999D;
    }
}
