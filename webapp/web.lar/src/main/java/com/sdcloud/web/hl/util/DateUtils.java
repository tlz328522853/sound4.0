package com.sdcloud.web.hl.util;

import org.apache.log4j.Logger;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * 时间格式统计工具类
 * @author jzc
 * 2017年5月16日
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	protected static final Logger logger = Logger.getLogger(DateUtils.class);

	public static final String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATE01 = "yyyy-MM-01 00:00:00";
	public static final String FORMAT_DEFAULT01 = "yyyy-MM-dd 00:00:00";
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static final String FORMAT_MONTH = "yyyy-MM";
	
	public static final int DEFAULT_MONTH=6;//首页默认月数
	public static final int DEFAULT_MONTH_CHILD=12;//子页默认月数
	public static final int DEFAULT_DAY_CHILD=15;//子页默认天数
	public static final int DEFAULT_MONTH_MAX=12;//最大月数
	public static final int DEFAULT_DAY_MAX=31;//最大天数
	
	private static Map<String, SimpleDateFormat> formaters = new HashMap<String, SimpleDateFormat>();

	static {
		SimpleDateFormat defaultFormater = new SimpleDateFormat(FORMAT_DEFAULT,
				Locale.CHINA);
		formaters.put(FORMAT_DEFAULT, defaultFormater);
		formaters.put(FORMAT_DATE, new SimpleDateFormat(FORMAT_DATE,
				Locale.CHINA));
		formaters.put(FORMAT_MONTH, new SimpleDateFormat(FORMAT_MONTH,
				Locale.CHINA));
		formaters.put(FORMAT_DATE01, new SimpleDateFormat(FORMAT_MONTH,
				Locale.CHINA));

	}
	/**
	 * 把日期转为 格式化的字符串
	 * @author jzc 2017年5月17日
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		if (date != null) {
			// 日期不为空时才格式
			try {
				return new SimpleDateFormat(format).format(date);
			} catch (Exception e) {
				logger.error("尝试将日期:" + date + "转为“" + format + "”的格式字符串--失败=.=!");
			}
		} 
		return "";
	}
	/**
	 *把字符串转换为日期格式
	 * @author jzc 2017年5月17日
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parse(String dateStr, String pattern) {
		Date date = null;
		try {
			date = new SimpleDateFormat(pattern).parse(dateStr);
		} catch (ParseException e) {// 是格式失败
			logger.error("尝试将日期:" + dateStr + "以“" + pattern + "”格式化--失败=.=!");
			
		}
		return date;
	}

	/**
	 * defaultNum 默认数 6 或 12
	 * 自定义根据月查询 map集合,map获取
	 * @return
	 * map获取 startDate 开始日期
	 * map获取 endDate   结束日期
	 * map获取 dateStrs  对比格式化日期结合
	 */
	public static Map<String,Object> defaultMonth(int defaultNum) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
		Date endDate=parse(format(c.getTime(), FORMAT_DATE01),FORMAT_DATE01);
		c.add(Calendar.MONTH, -defaultNum);
		Date startDate=parse(format(c.getTime(), FORMAT_DATE01),FORMAT_DATE01);
		return monthSelect(startDate, endDate);
	}
	
	/**
	 * 自定义根据月查询 map集合,map获取
	 * startStr 开始日期 2017-01
	 * endStr   结束日期 2017-05
	 * @return
	 * map获取 startDate 开始日期
	 * map获取 endDate   结束日期
	 * map获取 dateStrs  对比格式化日期结合
	 */
	private static String MONTH_ADD="-01 00:00:00";
	public static Map<String,Object> selfMonth(String startStr,String endStr) {
		Date startDate=parse(startStr+MONTH_ADD,FORMAT_DATE01);
		Date endDate=parse(endStr+MONTH_ADD,FORMAT_DATE01);
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.MONTH, 1);
		endDate=c.getTime();
		//System.out.println(format(startDate, FORMAT_DEFAULT));
		//System.out.println(format(endDate, FORMAT_DEFAULT));
		return monthSelect(startDate, endDate);
	}
	
	/**
	 * 例如 要 计算1月、2月、3月的
	 * 那么:
	 * startDate: yyyy-01-01 00:00:00
	 * endDate: yyyy-04-01 00:00:00
	 * @return
	 * map获取 startDate 开始日期
	 * map获取 endDate   结束日期
	 * map获取 dateStrs  对比格式化日期结合
	 */
	private static Map<String,Object> monthSelect(Date startDate,Date endDate){
		Map<String,Object> returnMap=new HashMap<>();
		int i=0;
		List<String> dateStr=new ArrayList<>();
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		
		dateStr.add(i, format(calendar.getTime(),FORMAT_MONTH));
		calendar.add(Calendar.MONTH,1);
		
		while(calendar.before(endCalendar)){
			i++;
			dateStr.add(i, format(calendar.getTime(),FORMAT_MONTH));
			calendar.add(Calendar.MONTH,1);
		}
		
		String[] dateStrs=new String[dateStr.size()];
		dateStr.toArray(dateStrs);
		returnMap.put("startDate", startDate);
		returnMap.put("endDate", endDate);
		returnMap.put("dateStrs", dateStrs);
		return returnMap;
	}
	
	
	/**
	 * defaultNum 默认数 31
	 * 自定义根据月查询 map集合,map获取
	 * @return
	 * map获取 startDate 开始日期
	 * map获取 endDate   结束日期
	 * map获取 dateStrs  对比格式化日期结合
	 */
	public static Map<String,Object> defaultDay(int defaultNum) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		Date endDate=parse(format(c.getTime(), FORMAT_DEFAULT01),FORMAT_DEFAULT01);
		c.add(Calendar.DATE, -defaultNum);
		Date startDate=parse(format(c.getTime(), FORMAT_DEFAULT01),FORMAT_DEFAULT01);
		//System.out.println(format(startDate, FORMAT_DEFAULT));
		//System.out.println(format(endDate, FORMAT_DEFAULT));
		return daySelect(startDate, endDate);
	}
	
	/**
	 * 自定义根据月查询 map集合,map获取
	 * startStr 开始日期
	 * endStr   结束日期
	 * @return
	 * map获取 startDate 开始日期
	 * map获取 endDate   结束日期
	 * map获取 dateStrs  对比格式化日期结合
	 */
	private static String DAY_ADD=" 00:00:00";
	public static Map<String,Object> selfDay(String startStr,String endStr) {
		Date startDate=parse(startStr+DAY_ADD,FORMAT_DEFAULT01);
		Date endDate=parse(endStr+DAY_ADD,FORMAT_DEFAULT01);
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, 1);
		endDate=c.getTime();
		//System.out.println(format(startDate, FORMAT_DEFAULT));
		//System.out.println(format(endDate, FORMAT_DEFAULT));
		return daySelect(startDate, endDate);
	}
	
	/**
	 * 例如 要 计算1号、2号、3号的
	 * 那么:
	 * startDate: yyyy-01-01 00:00:00
	 * endDate: yyyy-04-01 00:00:00
	 * @author jzc 2017年5月17日
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static Map<String,Object> daySelect(Date startDate,Date endDate){
		Map<String,Object> returnMap=new HashMap<>();
		int i=0;
		List<String> dateStr=new ArrayList<>();
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		
		dateStr.add(i, format(calendar.getTime(),FORMAT_DATE));
		calendar.add(Calendar.DATE,1);
		
		while(calendar.before(endCalendar)){
			i++;
			dateStr.add(i, format(calendar.getTime(),FORMAT_DATE));
			calendar.add(Calendar.DATE,1);
		}
		
		String[] dateStrs=new String[dateStr.size()];
		dateStr.toArray(dateStrs);
		returnMap.put("startDate", startDate);
		returnMap.put("endDate", endDate);
		returnMap.put("dateStrs", dateStrs);
		return returnMap;
	}
	
	/**
	 * 格式化日期2017-01-01为  01-01
	 * @author jzc 2017年5月18日
	 * @param dateStr
	 * @return
	 */
	public static String[] slitDateStr(String[] dateStr){
		String val=null;
		for(int i=0;i<dateStr.length;i++){
			val=dateStr[i];
			dateStr[i]=val.substring(5);
		}
		return dateStr;
	}
	

	public static void main(String[] args) {
		//System.out.println(beforeSixMonth());
//		defaultMonth(6);
//		selfMonth("2017-01", "2017-05");
//		defaultDay(10);
//		selfDay("2017-04-21", "2017-05-17");
		String[] str={"2017-09-09"};
		slitDateStr(str);

	}

}
