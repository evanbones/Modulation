package com.evandev.modulation.util;

import java.util.regex.Pattern;

public class Markdown {

    public static final Pattern STRIKE = Pattern.compile("~~(.+?)~~");
    public static final Pattern UNDER = Pattern.compile("~(.+?)~");
    public static final Pattern BOLD = Pattern.compile("__(.+?)__|\\*\\*(.+?)\\*\\*");
    public static final Pattern ITALIC = Pattern.compile("_(.+?)_|\\*(.+?)\\*");
    public static final Pattern DEDUP = Pattern.compile("(§r)+");

    public static String convert(String in) {
        if (in == null) return null;
        return DEDUP.matcher(ITALIC.matcher(BOLD.matcher(UNDER.matcher(STRIKE.matcher(in).replaceAll("§m$1§r")).replaceAll("§n$1§r")).replaceAll("§l$1$2§r")).replaceAll("§o$1$2§r")).replaceAll("§r");
    }
}
