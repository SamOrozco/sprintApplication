package com.company.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

    public static String getFormattedDateString(Date date) {
        return dateFormat.format(date);
    }
}
