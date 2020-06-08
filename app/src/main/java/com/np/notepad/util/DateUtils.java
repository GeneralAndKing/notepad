package com.np.notepad.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /** 时间格式(yyyy.MM.dd HH:mm:ss) */
    public final static String DATE_TIME_FORMAT_1 = "yyyy.MM.dd HH:mm:ss";

    /**
     * 将时间转换为指定格式
     * 异常格式返回-1
     * @param date date
     * @param pattern 格式
     * @return string
     */
    public static String getDateTimeByFormat(Date date, String pattern) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        res = simpleDateFormat.format(date);
        return res;
    }
}
