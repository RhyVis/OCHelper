package com.rhynia.ochelper.var;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhynia.ochelper.util.Format;
import lombok.Data;

@Data
public class AECPU {
    private String name;
    private int cpuid;
    private int coprocessors;
    private String storage;
    private boolean busy;

    @JsonCreator
    public AECPU(@JsonProperty("coprocessors") int coprocessors,
                 @JsonProperty("cpuid") int cpuid,
                 @JsonProperty("storage") long storage,
                 @JsonProperty("busy") boolean busy,
                 @JsonProperty("name") String name) {
        this.name = name.isEmpty() ? "UNNAMED" : name;
        this.cpuid = cpuid;
        this.coprocessors = coprocessors;
        this.storage = Format.formatStringByte(String.valueOf(storage));
        this.busy = busy;
    }
}
