package intellif.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: IntellifUtil.java
 * @Package intellif.utils
 * @Description
 * @date 2018 05-15 20:47.
 */
public class IntellifUtil {
	/**
	 * 将对象转化为字符串
	 * @param o
	 * @return 转化后字符串
	 */
	public static String obj2str(Object o){
		return o != null ? o.toString() : null;
	}

	/**
	 * 将value转化成date对象
	 * @param value 可为Date或Long或者String("2000-9-7",  "2002-6-1 14:15:30")
	 * @return 转化后date对象
	 */
	public static Date obj2date(Object value){
		if(value instanceof String){
			//return textToDate((String) value);
			Date d = null;
			try {
				d = new SimpleDateFormat("yyyy-MM-dd").parse((String)value);
			} catch (ParseException e) {
				try {
					d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String)value);
				}catch (Exception ignore){}
			}
			return d;
		}else if(value instanceof Date){
			return (Date) value;
		}else if(value instanceof Long)
			return new Date(((Long) value).longValue());
		return null;
	}
	/**
	 * 将对象转化为int类型的数
	 * @param o
	 * @return 转化后int
	 */
	public static final int obj2int(Object o){
		return obj2int(o, 0);
	}

	/**
	 * 从一个对象(Number,String)得到一个整数
	 * @param o Number,String类型的对象
	 * @param defaultValue
	 * @return 转化后int
	 */
	public static final int obj2int(Object o, int defaultValue){
		if(o instanceof Number)
			return ((Number) o).intValue();
		if(o instanceof String){
			String s = ((String) o).trim();
			try{
				return parseInt(s);
			}catch(Exception ex){
			}
			try{
				return new Long(parseLong(s)).intValue();
			}catch(Exception ex){
			}
		}
		return defaultValue;
	}
	/**
	 * 将text转化成int<br/>
	 *  0x作为前缀的看成16进制, 0b作为前缀的看成二进制<br/>
	 *  例如  parseInt("0x1F")结果为 31, parseInt("0b1011011") 结果为 91
	 * @param text
	 * @return 转化后int值
	 */
	public static final int parseInt(String text){
		text = text.trim();
		int start = 0, base = 10;
		if(text.startsWith("0x") || text.startsWith("0X")){
			start = 2;
			base = 16;
		}else if(text.startsWith("0b") || text.startsWith("0B")){
			start = 2;
			base = 2;
		}
		return Integer.parseInt(text.substring(start), base);
	}
	/**
	 * 将text转化成long<br/>
	 *  0x作为前缀的看成16进制, 0b作为前缀的看成二进制<br/>
	 *  例如  parseInt("0x1F") 结果为 31, parseInt("0b1011011") 结果为 91
	 * @param text
	 * @return 转化后long值
	 */
	public static final long parseLong(String text){
		text = text.trim();
		int start = 0, base = 10;
		if(text.startsWith("0x") || text.startsWith("0X")){
			start = 2;
			base = 16;
		}else if(text.startsWith("0b") || text.startsWith("0B")){
			start = 2;
			base = 2;
		}
		return Long.parseLong(text.substring(start), base);
	}

	/**
	 * 将对象转为为long
	 * @param o
	 * @return 转化后long值
	 */
	public static final long obj2long(Object o){
		return obj2long(o, 0);
	}
	/**
	 * 将对象转为为long
	 * @param o
	 * @param defaultValue
	 * @return 转化后long值
	 */
	public static final long obj2long(Object o, int defaultValue){
		if(o instanceof Number)
			return ((Number) o).longValue();
		if(o instanceof String){
			String s = ((String) o).trim();
			try{
				return Long.parseLong(s);
			}catch(Exception ex){
			}
			try{
				return Double.valueOf(s).longValue();
			}catch(Exception ex){
			}
		}
		return defaultValue;
	}
}
