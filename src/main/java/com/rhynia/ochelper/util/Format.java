package com.rhynia.ochelper.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

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

    private static String formatStringSizeNum(String val) {
        if (val.contains("E")) {
            BigDecimal bd = new BigDecimal(val);
            return bd.toPlainString();
        }
        if (val.contains(".0"))
            return val.substring(0, val.indexOf("."));
        return val;
    }

    public static String formatStringSizeDisplay(String val) {
        BigDecimal bd = new BigDecimal(formatStringSizeNum(val));
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(bd);
    }

    public static String formatStringSizeByte(String val) {
        String temp = formatStringSizeNum(val);
        String out = Format.formatStringByte(temp);
        if (Objects.equals(temp, out))
            return "";
        return "(" + out + ")";
    }

    public static String trySwitchFluidName(String s) {
        if (NAME_MAP_FLUID_SWITCH.containsKey(s))
            return NAME_MAP_FLUID_SWITCH.get(s);
        return s;
    }
}
