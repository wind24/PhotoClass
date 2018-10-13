package com.wind.photoclass.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static SimpleDateFormat dateTimeFormat;

    static {
        dateTimeFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        dateTimeFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static String parseTimeToYMDTime(long time) {
        return dateTimeFormat.format(new Date(time));
    }

}
