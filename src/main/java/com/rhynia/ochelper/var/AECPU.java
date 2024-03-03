package com.rhynia.ochelper.var;

import com.rhynia.ochelper.util.Format;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AECPU {
    private String name;
    private int coprocessors;
    private String storage;
    private boolean busy;

    public AECPU(int coprocessors, long storage, boolean busy, String name) {
        this.name = name.isEmpty() ? "UNNAMED" : name;
        this.coprocessors = coprocessors;
        this.storage = Format.formatStringByte(String.valueOf(storage));
        this.busy = busy;
    }
}
