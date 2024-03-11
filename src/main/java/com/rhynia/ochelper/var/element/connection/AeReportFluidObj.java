package com.rhynia.ochelper.var.element.connection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Format;
import com.rhynia.ochelper.var.base.AbstractAeData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class AeReportFluidObj extends AbstractAeData {
    private final String name;
    private final String label;
    private final String local;

    @JsonCreator
    public AeReportFluidObj(
            @JsonProperty("name") String name,
            @JsonProperty("label") String label,
            @JsonProperty("amount") String amount) {
        super(Format.assembleFluidUN(name), amount);
        this.name = name;
        this.label = label;
        this.local = Format.tryTranslateFluidUn(un);
    }

}
