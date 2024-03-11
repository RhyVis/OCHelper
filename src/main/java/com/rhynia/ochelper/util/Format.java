package com.rhynia.ochelper.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rhynia.ochelper.util.LocalizationMap.NAME_MAP_FLUID_SWITCH;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_FLUID;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM;
import static com.rhynia.ochelper.util.LocalizationMap.UNI_NAME_MAP_ITEM_SWITCH;

public class Format {
    private static final String[] byteList = {"", "K", "M", "G", "T", "P", "E", "Z", "Y",
            "KY", "MY", "GY", "TY", "PY", "EY", "ZY", "YY",
            "KYY", "MYY", "GYY", "TYY", "PYY", "EYY", "ZYY", "YYY"};

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

    public static String removeUnavailableChar(String s) {
        String regEx = "[\n`~!@#$%^&*()+=\\-_|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
        String str = "_";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.replaceAll(str).trim();
    }

    public static String assembleItemUN(String name, int meta) {
        return "item$" + Format.removeUnavailableChar(name) + "$" + meta;
    }

    public static String assembleFluidUN(String name) {
        return "fluid$" + Format.removeUnavailableChar(name);
    }

    public static String formatSizeWithComma(String val) {
        return formatSizeWithComma(new BigDecimal(val));
    }

    public static String formatSizeWithComma(BigDecimal val) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(val);
    }

    public static String formatSizeWithByte(BigDecimal val) {
        return formatSizeWithByte(val.toPlainString());
    }

    public static String formatSizeWithByte(String val) {
        String tmp = formatStringByte(val);
        if (Objects.equals(val, tmp))
            return "-";
        return "(" + tmp + ")";
    }

    public static String tryTranslateItemUn(String un) {
        return UNI_NAME_MAP_ITEM.getOrDefault(un, UNI_NAME_MAP_ITEM_SWITCH.getOrDefault(un, un));
    }

    public static String tryTranslateFluidUn(String un) {
        return UNI_NAME_MAP_FLUID.getOrDefault(un, un);
    }

    public static String trySwitchFluidLocal(String original) {
        return NAME_MAP_FLUID_SWITCH.getOrDefault(original, original);
    }
}
