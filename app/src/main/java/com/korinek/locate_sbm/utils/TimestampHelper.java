package com.korinek.locate_sbm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampHelper {

    public static String toLocalDateTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("d.M.yyyy HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }
}
