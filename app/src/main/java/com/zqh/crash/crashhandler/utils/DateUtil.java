package com.zqh.crash.crashhandler.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 * @author zqh
 *
 */
public class DateUtil {

//	private static final Logger log = Logger.getLogger(DateUtil.class);

	/** yyyy-MM-dd HH:mm:ss **/
	public static final String PATTERN1 = "yyyy-MM-dd HH:mm:ss";
	/** yyyy MM dd HH:mm:ss **/
	public static final String PATTERN2 = "yyyy MM dd HH:mm:ss";
	/** yyyy\MM\dd HH:mm:ss **/
	public static final String PATTERN3 = "yyyy\\MM\\dd HH:mm:ss";
	/** yyyyMMddHHmmss **/
	public static final String PATTERN4 = "yyyyMMddHHmmss";

	/** yyyy-MM-dd **/
	public static final String PATTERN_SIMPLE1 = "yyyy-MM-dd";
	/** yyyy MM dd **/
	public static final String PATTERN_SIMPLE2 = "yyyy MM dd";
	/** yyyy\MM\dd **/
	public static final String PATTERN_SIMPLE3 = "yyyy\\MM\\dd";
	/** yyyyMMdd **/
	public static final String PATTERN_SIMPLE4 = "yyyyMMdd";

	/**
	 * 按照传入的pattern格式，转化Date为String
	 * 
	 * @param date
	 * @return
	 */
	public static String date2String(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
		return sdf.format(date);
	}

	/**
	 * 按照传入的pattern格式，转化String为Date
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date string2Date(String strDate, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			Log.e("zqh","String：" + strDate + ". strDate-->date failed, error: "+e.toString());
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 按照“yyyy-MM-dd HH:mm:ss”格式转化Date为String
	 * 
	 * @param date
	 * @return
	 */
	public static String date2String(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN1, Locale.getDefault());
		return sdf.format(date);
	}

	/**
	 * 按照“yyyy-MM-dd HH:mm:ss”格式转化String为Date
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date string2Date(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN1, Locale.getDefault());
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			Log.e("zqh","String：" + strDate + ". strDate-->date failed, error: "+e.toString());
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 按照“yyyy-MM-dd”格式转化Date为String
	 * 
	 * @param date
	 * @return
	 */
	public static String date2SimpleString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_SIMPLE1, Locale.getDefault());
		return sdf.format(date);
	}

	/**
	 * 按照“yyyy-MM-dd HH:mm:ss”格式转化String为Date
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date simpleString2Date(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_SIMPLE1, Locale.getDefault());
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			Log.e("zqh","String：" + strDate + ". strDate-->date failed, error: "+e.toString());
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 年
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * 月
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 日
	 * 
	 * @param date
	 * @return
	 */
	public static int getDate(Date date) {
		return Calendar.getInstance().get(Calendar.DATE);
	}

	/**
	 * 星期
	 * 
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 时
	 * 
	 * @param date
	 * @return
	 */
	public static int getHours(Date date) {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 分
	 * 
	 * @param date
	 * @return
	 */
	public static int getMinutes(Date date) {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/**
	 * 秒
	 * 
	 * @param date
	 * @return
	 */
	public static int getSeconds(Date date) {
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	/**
	 * 根据
	 * 
	 * @param pattern
	 *            类似于"yyyy-MM-dd"的格式
	 * @param milliseconds
	 *            毫秒数（measured in milliseconds since January 1st, 1970,
	 *            midnight. ）
	 * @return
	 */
	public static String getLocalDateByMilliseconds(String pattern, long milliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		String strDate = new SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.getTime());
		return strDate;
	}

}
