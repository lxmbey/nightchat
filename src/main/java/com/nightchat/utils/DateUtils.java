package com.nightchat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * yyyyMMdd
     */
    public static final String DateDay = "yyyyMMdd";
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String DateDayTime = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyyMMddHHmmss
     */
    public static final String DateDayTimeSeril = "yyyyMMddHHmmss";
    /**
     * YYYY-MM-DD
     */
    public static final String YearMonthDay = "yyy-MM-dd";
    /**
     * 
     * */
    public static final String DateDayTimeSec = "yyyy/MM/dd HH:mm:ss";

    public static String formatDate(String format, Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(time);
    }

    public static Date parseDate(String format, String timeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param dateSmall 较小的时间
     * @param dateBig 较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date dateSmall, Date dateBig) {
        Calendar calSmall = Calendar.getInstance();
        calSmall.setTime(dateSmall);
        calSmall.set(Calendar.HOUR_OF_DAY, 0);
        calSmall.set(Calendar.MINUTE, 0);
        calSmall.set(Calendar.SECOND, 0);

        Calendar calBig = Calendar.getInstance();
        calBig.setTime(dateBig);
        calBig.set(Calendar.HOUR_OF_DAY, 0);
        calBig.set(Calendar.MINUTE, 0);
        calBig.set(Calendar.SECOND, 0);

        Long between_days = (calBig.getTimeInMillis() - calSmall.getTimeInMillis()) / (1000 * 3600 * 24);

        return between_days.intValue();
    }
    
    public static void main(String[] args) {
        String timeStr = "2015-11-30 17:24:57.000";
        Date d = parseDate(DateDayTimeSec, timeStr);
        System.out.println(formatDate(DateDayTime, d));
    }
}
