package intellif.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import intellif.settings.ImageSettings;
import intellif.settings.ServerSetting;
import intellif.database.entity.EventInfo;
import intellif.database.entity.ResidentPerson;

public class CommonUtil {
	
	private static long sequence = 1;
	
	private static long time = 0;
	
	static {
		String dateStr = DateUtil.getYearReduce(new Date(), -40);
		Date date = null;
		try {
			date = DateUtil.getFormatDate(dateStr, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//time = date.getTime()/10;  // find bugs possible null pointer dereference in method on exception path  空指针隐患
		if(date!=null){time = date.getTime()/10; }
	}
	
	public static long getCode(long id) {
		long code = id>>48;
		if(code == 0) return 1;
		return code;
	}
	
	public static synchronized long createId(long tableCode)  {
		long code = tableCode<<48;
		long id = code + time + sequence;
		sequence++;
		return id;
	}
	
	public static boolean checkWeekDay(String dayStr, int week){
		String[] ss = dayStr.split(",");
		for(String item : ss){
			if(Integer.parseInt(item)==week){
				return true;
			}
		}
		return false;
	}
	public static boolean checkAge(int inputAge, int resultAge){
		if(resultAge == inputAge || inputAge == resultAge+1 || inputAge == resultAge-1){
			return true;
		}else{
			return false;
		}
	}
	public static boolean checkRace(String inputRace, int resultRace){
		String[] strs = inputRace.split(",");
		for(String item : strs){
			int iValue = Integer.parseInt(item);
			if(resultRace == iValue){
				return true;
			}
		}
		return false;
	}
	
	public static Map<String, Integer> processResidentPerson(List<ResidentPerson> residentList){
		//1 ��ͯ 4 ������ 7 ����  9 ����
		int maleNum = 0;
		int femaleNum = 0;
		int age1Num = 0;
		int age4Num = 0;
		int age7Num = 0;
		int age9Num = 0;
		Map<String, Integer> statisticsMap = new HashMap<String,Integer>();
		
		for(ResidentPerson item : residentList){
			int age = item.getAge();
			int gender = item.getGender();
			if(gender == 1){
				maleNum++;
			}
			if(gender == 2){
				femaleNum++;
			}
			if(checkAge(1, age)){
					age1Num++;
				}
			if(checkAge(4, age)){
				age4Num++;
			}
			if(checkAge(7, age)){
				age7Num++;
			}
			if(checkAge(9, age)){
				age9Num++;
			}
		}
		statisticsMap.put("male", maleNum);
		statisticsMap.put("female", femaleNum);
		statisticsMap.put("children", age1Num);
		statisticsMap.put("youth", age4Num);
		statisticsMap.put("middle", age7Num);
		statisticsMap.put("old", age9Num);
		return statisticsMap;
		
	}
	
	/**
	 * 生成任意位数随机数
	 * @param n
	 * @return
	 */
	public static int getRandomNumber(int n){
		int temp = 0;
		int min = (int) Math.pow(10, n-1);
		int max = (int) Math.pow(10, n);
		Random rand = new Random();

		while(true){
		temp = rand.nextInt(max);
		if(temp >= min)
		break;
		}

		return temp;
		}
	
	/**
	 * 判断url是否为空
	 * @param urlStr
	 * @return
	 */
	public static boolean checkImage(String urlStr){
		InputStream inputStream = null;
		try {
			if(!urlStr.startsWith("http:")) {
				urlStr = "http://" + ImageSettings.getStoreRemoteHost() + ":" + ServerSetting.getWserverPort() + "/" + urlStr;
			}
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			inputStream = connection.getInputStream();
			if (null != inputStream) {
				return true;
			}else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e2) {
			}
		}
		
	}
	
	 public static long  calculatePages(int pageSize,long totalNum) {
		 long maxPages = 0;
	        if (pageSize > 0) {
	            if (totalNum % pageSize == 0) {
	                maxPages = totalNum / pageSize;
	            } else {
	                maxPages = (totalNum / pageSize) + 1;
	            }
	        }
	        return maxPages;
	    }
	public static String getFixLenthString(int strLength) {  
		Random random=new Random();
		StringBuffer buffer = new StringBuffer();
		//随机生成数字，并添加到字符串
		for(int i=0;i<8;i++){
			buffer.append(random.nextInt(10));
		}
		//将字符串转换为数字并输出
		String ss = buffer.toString();
		return ss;
	}
	
    public static List<EventInfo> addAreaId(List<EventInfo> eventList, Long areaId) {
        if (null == eventList) {
            return null;
        }
        for (EventInfo event : eventList) {
            if (null != event) {
                event.setAreaId(null == areaId ? "" : String.valueOf(areaId));
            }
        }
        return eventList;

    }
    
    /**
     * 将long安全转换成int
     * 
     * @param x
     * @return
     */
    public static int saftConvert(long x) {
        if (x > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (x < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else {
            return ((Long) x).intValue();
        }
    }



    


	/**
	 * 判断两个对象是否相等
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean ObjectEquals(Object o1,Object o2){
		if(null == o1){
			if(null == o2){
				return true;
			}else{
				return false;
			}
		}else{
			return o1.equals(o2);
		}
	}}

