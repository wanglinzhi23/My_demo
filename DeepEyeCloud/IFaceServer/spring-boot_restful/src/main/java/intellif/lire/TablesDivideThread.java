package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.service.TableDivideServiceItf;
import intellif.settings.TableDivideSetting;
import intellif.database.entity.TableRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TablesDivideThread extends Thread {

    private static Logger LOG = LogManager.getLogger(TablesDivideThread.class);
   // private static SimpleDateFormat dateFormatHMS = new SimpleDateFormat(GlobalConsts.YMDHMS);  //find bugs   simpledateformat是线程不安全的
   // private static SimpleDateFormat dateFormatYMD = new SimpleDateFormat(GlobalConsts.YMD);
    private final static String dateFormatHMS = GlobalConsts.YMDHMS;
    private final static String dateFormatYMD = GlobalConsts.YMD;

    @Autowired
    private TableRecordDao tableRecordDao;
  
    @Autowired
    private TableDivideServiceItf iTableDivideServiceItf;



     
    @Scheduled(cron = "0 0 1 * * ?")
    public void run() {
    	if(!GlobalConsts.run){
    		return;
    	}
    //每天0点进行是否创建分表判断，查询tables表最新表对应的时间，
    //如果不存在记录或最新记录表start_time时间小于下个月月末，则分表至下个月末
       try{
    		long code = 1;
            Date startDate = null;
            Calendar calendar = Calendar.getInstance();  
            Date currentDate = calendar.getTime();
            calendar.setTime(currentDate);  
            calendar.add(Calendar.MONTH, 2);  
            calendar.set(Calendar.DATE, 0);
            Date nextMonthDate = calendar.getTime();//获取下一个月的最后一天
           // String nextMonthStr = dateFormatYMD.format(nextMonthDate);
            String nextMonthStr = DateUtil.formatDate(nextMonthDate, dateFormatYMD);
            
        	TableRecord firsttable = tableRecordDao.findFirstOrderByTime();
        	if(null == firsttable){
        		String startTime = TableDivideSetting.getTable_divide_starttime();//运营环境开始时间
        		//startDate = dateFormatHMS.parse(startTime);
        		startDate = intellif.utils.DateUtil.getFormatDate(startTime, dateFormatHMS);  
        	}else{
        		startDate = firsttable.getEndTime();
        		code = firsttable.getTableCode()+1;
        	}
        	//String startStr = dateFormatYMD.format(startDate);
        	String startStr = DateUtil.formatDate(startDate, dateFormatYMD);
        	if(!startStr.equals(nextMonthStr) && startDate.getTime() < nextMonthDate.getTime()){
        		//需要分表到下个月末
        		long startTimeStamp = startDate.getTime();
        		//Date nmDate = dateFormatHMS.parse(nextMonthStr+" 23:59:59");
        		Date nmDate = intellif.utils.DateUtil.getFormatDate(nextMonthStr+" 23:59:59", dateFormatHMS); 
        		long nextMonthStamp = nmDate.getTime();
        		int step = TableDivideSetting.getTable_divide_size();
        		do{
        			Calendar tCalendar = Calendar.getInstance();
        			tCalendar.setTimeInMillis(startTimeStamp);
        			Date tableStartTime = tCalendar.getTime();
        			tCalendar.setTimeInMillis(startTimeStamp+step*24*60*60*1000l);
        			Date tableEndTime = tCalendar.getTime();
        			iTableDivideServiceItf.createTables(tableStartTime, tableEndTime,code);
        			startTimeStamp  = startTimeStamp+step*24*60*60*1000l;
        			code++;
        			
        		}while(startTimeStamp <= nextMonthStamp);
        		
        	}
       }catch(Exception e){
    	   LOG.error("divide table error",e);
       }
    
    }

}
