package com.rhynia.ochelper.util;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;

public class Format {
    private static final String[] byteList = {"", "K", "M", "G", "T", "P", "E", "Z", "Y", "Tell me if you cheated"};

    public static String formatStringByte(String val) {
        if (val == null)
            return null;
        int len = val.length();
        int byteSeral = len / 3;
        if (byteSeral == 0)
            return val;
        String bytePrefix = val.substring(0, len - 3 * byteSeral);
        if (bytePrefix.isEmpty()) // RB
            return val.substring(0, len - 3 * (byteSeral - 1)) + byteList[byteSeral - 1];
        return bytePrefix + byteList[byteSeral];
    }

    public static String trySwitchFluidName(String s) {
        if (NAME_MAP_FLUID_SWITCH.containsKey(s))
            return NAME_MAP_FLUID_SWITCH.get(s);
        return s;
    }
}
