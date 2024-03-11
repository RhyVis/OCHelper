package com.rhynia.ochelper.var.base;

import lombok.Getter;

/**
 * @author Rhynia
 */
@Getter
public abstract class AbstractAeDataSet extends AbstractAeData {
    protected final long id;
    protected final String time;

    protected AbstractAeDataSet(String un, String size, long id, String time) {
        super(un, size);
        this.id = id;
        this.time = time;
    }
}
