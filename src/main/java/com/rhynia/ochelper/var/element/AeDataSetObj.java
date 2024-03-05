package com.rhynia.ochelper.var.element;

import com.rhynia.ochelper.var.base.AbstractAeDataSet;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class AeDataSetObj extends AbstractAeDataSet {
    @Builder
    protected AeDataSetObj(String un, String size, long id, String time) {
        super(un, size, id, time);
    }

    public AeDisplayItemObj getAeItemDisplayObj() {
        return new AeDisplayItemObj(un, sizeRaw, id, time);
    }

    public AeDisplayFluidObj getAeFluidDisplayObj() {
        return new AeDisplayFluidObj(un, sizeRaw, id, time);
    }
}
