package com.rhynia.ochelper.var;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyData {
    private Long id;
    private String size;
    private String time;
}
