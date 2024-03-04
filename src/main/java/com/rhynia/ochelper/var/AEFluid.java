package com.rhynia.ochelper.var;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Format;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AEFluid {

    private final String name;
    private final String un;
    private final String label;
    private final String sizeRaw;
    private final String sizeString;
    private final BigDecimal size;

    @JsonCreator
    public AEFluid(
            @JsonProperty("name") String name,
            @JsonProperty("label") String label,
            @JsonProperty("amount") String amount) {
        BigDecimal tmpSize = new BigDecimal(amount);
        this.name = name;
        this.un = Format.assembleFluidUN(name);
        this.label = label;
        this.sizeRaw = amount;
        this.size = tmpSize;
        this.sizeString = tmpSize.toPlainString();
    }

}
