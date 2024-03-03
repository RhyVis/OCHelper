package com.rhynia.ochelper.var;

import lombok.Data;

@Data
public class MsSet {
    private final int dim;
    private final double mspt;
    private final double tps;
    private final boolean healthy;

    public MsSet(int dim, double mspt) {
        this.dim = dim;
        this.mspt = mspt;
        double temp = 1000D / mspt > 20 ? 20 : 1000D / mspt;
        this.tps = temp;
        //this.healthy = temp >= 19.99999D;
        this.healthy = (temp == 0);
    }
}
