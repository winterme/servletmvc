package com.zzq.util;

public class StringUtils {

    public static boolean isNotBlank(String s) {
        if (s == null || "".equals(s)) {
            return false;
        }
        return true;
    }

}
