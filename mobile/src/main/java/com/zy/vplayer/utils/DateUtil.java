package com.zy.vplayer.utils;

import java.util.Locale;

public class DateUtil {
    private static class UtilInner {
        private static DateUtil util = new DateUtil();
    }

    public static DateUtil getInstance() {
        return UtilInner.util;
    }

    public String getTime(Long mis) {
        long h = mis / (60L * 60L * 1000L);
        long m = (mis - h * 60L * 60L * 1000L) / (60L * 1000L);
        long s = (mis % (60L * 1000L)) / 1000L;
        if (h > 0) {
            return String.format(Locale.CHINA, "%d:%02d:%02d", h, m, s);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", m, s);
        }
    }
}
