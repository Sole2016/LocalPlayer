package com.zy.vplayer.tv.simple;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public String formatTime(Date date){
        SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        format.applyLocalizedPattern("HH:mm");
        return format.format(date);
    }

    public String getDateWithTime(long mis){
        SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        format.applyLocalizedPattern("yyyy/MM/dd HH:mm:ss");
        return format.format(new Date(mis));
    }

    public String getDate(long mis){
        SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        format.applyLocalizedPattern("yyyy/MM/dd");
        return format.format(new Date(mis));
    }
}
