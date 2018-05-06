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

	/**
	 * 当前年月日时分秒
	 * 
	 * @return
	 */
	public static String currentDatetime() {
		return formatDate(DateDayTime, new Date());
	}
	
	/**
	 * 获取当前时间至指定时的点间隔毫秒数 如果当前钟点大于指定钟点数，则结果是当前时间至第二天指定时的间隔毫秒数
	 * 
	 * @param taskHour
	 * @param taskMiniute
	 * @return
	 */
	public static long betweenTaskHourMillis(int taskHour, int taskMiniute) {
		if (taskHour < 0) {
			taskHour = 0;
		}
		if (taskHour > 23) {
			taskHour = 23;
		}
		if (taskMiniute < 0) {
			taskMiniute = 0;
		}
		if (taskMiniute > 59) {
			taskMiniute = 59;
		}

		Calendar c = Calendar.getInstance();
		int nowHour = c.get(Calendar.HOUR_OF_DAY);
		if (nowHour > taskHour || (nowHour == taskHour && c.get(Calendar.MINUTE) >= taskMiniute)) {
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		c.set(Calendar.HOUR_OF_DAY, taskHour);
		c.set(Calendar.MINUTE, taskMiniute);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis() - System.currentTimeMillis();
	}
}
