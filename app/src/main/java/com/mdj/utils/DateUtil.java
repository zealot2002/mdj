package com.mdj.utils;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class DateUtil {
	private static final String STR_SPACE=" ";
	private static final String STR_GANG="-";
	private static final String STR_MAOHAO=":";
	
	private static final String STR_DATE="yyyy-MM-dd";
	private static final String STR_TIME="yyyy-MM-dd HH:mm";
	private static final String STR_FULL_TIME="yyyy-MM-dd HH:mm:ss";
	
	public static SimpleDateFormat getDateFormat(){
		return new SimpleDateFormat(STR_DATE);
	}
	public static SimpleDateFormat getTimeFormat(){
		return new SimpleDateFormat(STR_TIME);
	}
	public static SimpleDateFormat getFullTimeFormat(){
		return new SimpleDateFormat(STR_FULL_TIME);
	}
	
	public static Date getNowTime() {
		return new Date();
	}
	public static Date getNowDate() {
		return parseStringToDate(parseDateToString(getNowTime()));
	}
	
	public static String parseDateToString(Date date) {
		String rtn = getDateFormat().format(date);
		return rtn;
	}
	public static String parseTimeToString(Date date) {
		if (date==null)
			return "";
		else
			return getTimeFormat().format(date);
	}
	public static String parseFullTimeToString(Date date) {
		if (date==null)
			return "";
		else
			return getFullTimeFormat().format(date);
	}
	
	public static Date parseStringToDate(String sDate) {
		if (sDate==null || sDate.trim().length()==0)
			return null;
		else
			sDate=sDate.trim();
		
		try {
			return getDateFormat().parse(sDate);
		} catch (Exception e) {
			return null;
		}
	}
	public static Date parseStringToTime(String sDate) {
		if (sDate==null || sDate.trim().length()==0)
			return null;
		else
			sDate=sDate.trim();
		
		try {
			return getTimeFormat().parse(sDate);
		} catch (Exception e) {
			return null;
		}
	}
	public static Date parseStringToFullTime(String sDate) {
		if (sDate==null || sDate.trim().length()==0)
			return null;
		else
			sDate=sDate.trim();
		
		try {
			return getFullTimeFormat().parse(sDate);
		} catch (Exception e) {
			return null;
		}
	}
	//获取日期    "yyyy-MM-dd HH:mm:ss";
	public static String getDateString(String time) throws Exception {
		return parseDateToString(parseStringToFullTime(time));
	}
	
	public static String getNowDateString() {
		return parseDateToString(getNowTime());
	}
	public static String getNowTimeString() {
		return parseTimeToString(getNowTime());
	}
	public static String getNowFullTimeString() {
		return parseFullTimeToString(getNowTime());
	}
	public static Date getNextDay(Date date, int days){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE,days);
		return cal.getTime();
	}
	
	public static Date getNextMonth(Date date, int mons){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH,mons);
		return cal.getTime();
	}
	
	public static int getDateInt(String date){
		return Integer.parseInt(date.substring(0,4)+date.substring(5,7)+date.substring(8,10));
	}
	
	public static int getMonthInt(String month){
		return Integer.parseInt(month.substring(0,4)+month.substring(5,7));
	}
	
	public static String isValidDate(int date){
    	String s=Integer.toString(date);
    	s=s.substring(0,4)+"-"+s.substring(4,6)+"-"+s.substring(6,8);
    	SimpleDateFormat fmt=new SimpleDateFormat(STR_DATE);
    	
        Date tmpDate=null;
        try {
            tmpDate=fmt.parse(s);
        } catch(Exception e) {
            return null;
        }
    	
        String tmps=fmt.format(tmpDate);
        if(tmps.equals(s))
        	return s;
        else
        	return null;
    }
	
	public static String isValidMonth(int month){
    	String s=Integer.toString(month);
    	s=s.substring(0,4)+"-"+s.substring(4)+"-01";
    	SimpleDateFormat fmt=new SimpleDateFormat(STR_DATE);
    	
        Date tmpDate=null;
        try {
            tmpDate=fmt.parse(s);
        } catch(Exception e) {
            return null;
        }
    	
        String tmps=fmt.format(tmpDate);
        if(tmps.equals(s))
        	return s.substring(0,7);
        else
        	return null;
    }
	
	public static String getCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strTime = "";
		try {
			strTime = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strTime;
	}
	//单位：分钟 time1<time2
	//要求time的格式：yyyy-MM-dd HH:mm:ss
	public static String timeDuration(String time1, String time2) {
		return String.valueOf(timeDurationInt(time1,time2));
	}
	
	public static long timeDurationInt(String time1, String time2) {
		if(time1==null||time2==null)
			return 0;
		java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long duration = 0;
		try {
			Date b_time=sdf.parse(time1);
			Date e_time=sdf.parse(time2);
			duration=(e_time.getTime()-b_time.getTime())/60000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(duration == 0)
			duration = 1;
		return duration;
	}
	
	public static long timeStringCompare(String time1, String time2) throws Exception{
		if(time1==null||time2==null||!isValidDateAndTime(time1)||!isValidDateAndTime(time2))
			throw new Exception("timeCompare(): 参数错误");
		java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date b_time=sdf.parse(time1);
			Date e_time=sdf.parse(time2);
			return e_time.getTime()-b_time.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	static int[] DAYS = { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };  
	  
	/** 
	 * @param date yyyy-MM-dd HH:mm:ss 
	 * @return 
	 */  
	public static boolean isValidDateAndTime(String dateAndTime) {  
	    try {  
	        int year = Integer.parseInt(dateAndTime.substring(0, 4));  
	        if (year <= 0)  
	            return false;  
	        int month = Integer.parseInt(dateAndTime.substring(5, 7));  
	        if (month <= 0 || month > 12)  
	            return false;  
	        int day = Integer.parseInt(dateAndTime.substring(8, 10));  
	        if (day <= 0 || day > DAYS[month])  
	            return false;  
	        if (month == 2 && day == 29 && !isGregorianLeapYear(year)) {  
	            return false;  
	        }  
	        int hour = Integer.parseInt(dateAndTime.substring(11, 13));  
	        if (hour < 0 || hour > 23)  
	            return false;  
	        int minute = Integer.parseInt(dateAndTime.substring(14, 16));  
	        if (minute < 0 || minute > 59)  
	            return false;  
	        int second = Integer.parseInt(dateAndTime.substring(17, 19));  
	        if (second < 0 || second > 59)  
	            return false;  
	  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return false;  
	    }  
	    return true;  
	}  
	public static final boolean isGregorianLeapYear(int year) {  
	    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);  
	}  
	
	/**
	 * 得到全时间的毫秒数
	 */
	public static long getNowFullTimeForLong(String str){
		try {
			long time = getFullTimeFormat().parse(str).getTime();
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 得到时间毫秒数：到分
	 * @param str
	 * @return
	 */
	public static long getNowTimeToMinutesForLong(String str){
		try {
			long time = getTimeFormat().parse(str).getTime();
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 得到时间毫秒数：到天
	 * @param str
	 * @return
	 */
	public static long getNowTimeToDaysForLong(String str){
		try {
			long time = getDateFormat().parse(str).getTime();
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
    //得到n天后日期
	//n==1 明天
	public static String getComingDate(String today,int n) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	    Date date = sdf.parse(today);  
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(calendar.DATE,n);//把日期往后增加一天.整数往后推,负数往前移动
	    date=calendar.getTime(); //这个时间就是日期往后推一天的结果        
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(date);
	      
	    return dateString;
	}
	
	//得到n天后是星期几
	//n==1 明天
	public static String getComingWeek(String today,int n) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	    Date date = sdf.parse(today);  
	    Calendar calendar = new GregorianCalendar();
	    
	    calendar.add(Calendar.DATE, n);
		
		String tomorrow = new DateFormatSymbols().getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
	    return tomorrow;
	}
	
	//得到n天后日期
	//n==1 明天
	public static String getComingDate(int n){
		Date date=new Date();//取时间
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(calendar.DATE,n);//把日期往后增加一天.整数往后推,负数往前移动
	    date=calendar.getTime(); //这个时间就是日期往后推一天的结果        
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(date);
	      
	    return dateString;
	}
	
	//得到n天后是星期几
	//n==1 明天
	public static String getComingWeek(int n){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, n);
		
		String tomorrow = new DateFormatSymbols().getShortWeekdays()[cal.get(Calendar.DAY_OF_WEEK)];
	    return tomorrow;
	}
	//0: s1=s2
	//1: s1>s2
	//-1: s1<s2
	public static int compareDate(String s1,String s2) throws Exception{
		java.text.DateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Calendar c1=java.util.Calendar.getInstance();
		java.util.Calendar c2=java.util.Calendar.getInstance();
		try{
			c1.setTime(df.parse(s1));
			c2.setTime(df.parse(s2));
		}catch(java.text.ParseException e){
			throw new Exception("格式不正确");
		}
		int result=c1.compareTo(c2);
		if(result==0)
			return 0;
		else if(result<0)
			return -1;
		else
			return 1;
	}
	//距离结束还有多少时间
	public static void compareDate(String begin, String end,StringBuffer sbHour,
			StringBuffer sbMinute,StringBuffer sbSecond) throws Exception {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date begin1 = dfs.parse(begin);
		java.util.Date end1 = dfs.parse(end);
		long between = (end1.getTime() - begin1.getTime()) / 1000;// 除以1000转换成秒

		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60;
		Log.e("zzy","" + day + "天" + hour + "小时" + minute + "分"
				+ second + "秒");
		
		if(day>0){//大于1天
			hour = day*24+hour;
			sbHour.append(hour);
		}else{
			if(hour<10)
				sbHour.append("0"+hour);
			else
				sbHour.append(hour);
		}
		
		if(minute<10)
			sbMinute.append("0"+minute);
		else
			sbMinute.append(minute);
		
		Log.e("zzy","second = " + second + "秒");
		
		if(second<10)
			sbSecond.append("0"+second);
		else
			sbSecond.append(second);
		
	}

    //计算时间差
    public static long getTimeDiffer(String begin, String end) throws Exception {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date begin1 = dfs.parse(begin);
        java.util.Date end1 = dfs.parse(end);
        return (end1.getTime() - begin1.getTime()) / 1000;// 除以1000转换成秒;
    }

	
	//将秒数转化为时分秒
	public static void second2HourMinuteSecond(long between,StringBuffer sbHour,
			StringBuffer sbMinute,StringBuffer sbSecond) throws Exception {
		
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 ;

		if(hour<10)
			sbHour.append("0"+hour);
		else
			sbHour.append(hour);
		
		if(minute<10)
			sbMinute.append("0"+minute);
		else
			sbMinute.append(minute);
		
		if(second<10)
			sbSecond.append("0"+second);
		else
			sbSecond.append(second);
	}
		
//距离结束还有多少毫秒
	public static long compareSecond(String begin, String end) throws Exception {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date begin1 = dfs.parse(begin);
		java.util.Date end1 = dfs.parse(end);
		return (end1.getTime() - begin1.getTime());/// 1000;// 除以1000转换成秒
	}
	
	
	public static void main(String[] args){
		System.out.println(getNowFullTimeForLong("2014-10-24 11:23:28"));
	}
}
