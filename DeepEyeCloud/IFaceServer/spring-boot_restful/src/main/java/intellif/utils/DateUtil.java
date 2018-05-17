package intellif.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import org.apache.commons.lang3.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateUtil {
	 private static Logger LOG = LogManager .getLogger(DateUtil.class);
public static void main(String[] args) {
    System.out.println(getformatDate(new Date()));
}
	 /**
	  * 日期字段时分秒添加
	  * @param dateStr
	  * @param append
	  * @return
	  */
	 public static String checkDateStrSFM(String dateStr,String append){
	     if(!StringUtils.isEmpty(dateStr)){
	         if(dateStr.indexOf(":") > 0){
	             return dateStr;
	         }else{
	             return dateStr + append;
	         }
	     }else{
	         return null;
	     }
	 }

	 /**
	  * 获取日期对应的星期数（星期日~星期六 1~7)
	  * @param date
	  */
	 public static int convertDateToWeek(Date date){
			 Calendar cal = Calendar.getInstance();  
		     cal.setTime(date);  
		     return cal.get(Calendar.DAY_OF_WEEK);
	 }
	 
	 /**
	  * long型时间转format时间格式字符串
	  * @param timestamps
	  * @return
	  */
	 public  static String getformatDate(long timestamps){

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Calendar cal = Calendar.getInstance();
			 cal.setTimeInMillis(timestamps);
			String fStr = sdf.format(cal.getTime());
			return fStr;
	 }
	 /**
      * long型时间转format时间格式字符串
      * 
      * @param timestamps
      * @return
      */
     public static String getformatDate(Date date) {

         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         String fStr = sdf.format(date);
         return fStr;
     }

	 
	 /**
	  * 根据date获取当天日期
	  * @param timestamps
	  * @return
	  */
	 public  static String getDateString(Date date){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String fStr = sdf.format(date);
			return fStr;
	 }
	 
	 /**
	  * String日期转型为Date日期
	  * @param timestamps
	  * @return
	 * @throws ParseException 
	  */
	 public  static Date getFormatDate(String dateStr, String formatPattern) throws ParseException{
			SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
			Date date = sdf.parse(dateStr);
			return date;
	 }
	 
	 public static String getMonthReduce(Object date, int month) {
		 String datetime = "";
		 Date dateByFormat = null;
		 try {
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化对象
			 Calendar calendar = Calendar.getInstance();// 日历对象 
			 if(date instanceof Date) {
				 dateByFormat = (Date) date;
			 } else if(date instanceof String) {
				 dateByFormat = sdf.parse((String) date);
			 }else{
				 return "传入的既不是date类型也不是string类型的时间，无法转换";   //////   findbugs Null passed for non-null parameter 同下
			 }
			 calendar.setTime(dateByFormat);
			 calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
			 calendar.set(Calendar.HOUR_OF_DAY, 0);
			 calendar.set(Calendar.MINUTE, 0);
			 calendar.set(Calendar.SECOND, 0);
			 calendar.set(Calendar.MILLISECOND, 0);
			 datetime = sdf.format(calendar.getTime());// 输出格式
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return datetime;
	 }
	 
	 public static String getYearReduce(Object date, int year) {
		 String datetime = "";
		 Date dateByFormat = null;
		 try {
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化对象
			 Calendar calendar = Calendar.getInstance();// 日历对象 
			 if(date instanceof Date) {
				 dateByFormat = (Date) date;
			 } else if(date instanceof String) {
			  // date = sdf.parse((String) date);  //   findbugs Null passed for non-null parameter 
				 dateByFormat = sdf.parse((String) date);
			 }else{
				 return "传入的既不是date类型也不是string类型的时间，无法转换";
			 }
			 calendar.setTime(dateByFormat);
			 calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year);
			 datetime = sdf.format(calendar.getTime());// 输出格式
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return datetime;
	 }
	 
}
