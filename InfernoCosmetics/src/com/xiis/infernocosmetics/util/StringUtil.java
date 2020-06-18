package com.xiis.infernocosmetics.util;

public class StringUtil {

    public static String ClearFormatting(String s){
        s = s.replace("§0", "");
        s = s.replace("§1", "");
        s = s.replace("§2", "");
        s = s.replace("§3", "");
        s = s.replace("§4", "");
        s = s.replace("§5", "");
        s = s.replace("§6", "");
        s = s.replace("§7", "");
        s = s.replace("§8", "");
        s = s.replace("§9", "");
        s = s.replace("§a", "");
        s = s.replace("§b", "");
        s = s.replace("§c", "");
        s = s.replace("§d", "");
        s = s.replace("§e", "");
        s = s.replace("§f", "");
        return s;
    }

}
