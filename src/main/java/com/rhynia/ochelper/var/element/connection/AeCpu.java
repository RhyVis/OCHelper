package com.rhynia.ochelper.var.element.connection;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Format;

import lombok.Data;

/**
 * @author Rhynia
 */
@Data
public class AeCpu {
    private String name;
    private int cpuid;
    private int coprocessors;
    private BigDecimal realStorage;
    private String storage;
    private boolean busy;

    @JsonCreator
    public AeCpu(@JsonProperty("coprocessors") int coprocessors,
                 @JsonProperty("cpuid") int cpuid,
                 @JsonProperty("storage") String storage,
                 @JsonProperty("busy") boolean busy,
                 @JsonProperty("name") String name) {
        BigDecimal tmp = new BigDecimal(storage);
        this.name = name.isEmpty() ? "UNNAMED" : name;
        this.cpuid = cpuid;
        this.coprocessors = coprocessors;
        this.realStorage = tmp;
        this.storage = Format.formatStringByte(tmp.toPlainString());
        this.busy = busy;
    }

    public static AeCpu of(int cpuid,  String name, int coprocessors, String storage, boolean busy) {
        return new AeCpu(coprocessors, cpuid, storage, busy, name);
    }
}
