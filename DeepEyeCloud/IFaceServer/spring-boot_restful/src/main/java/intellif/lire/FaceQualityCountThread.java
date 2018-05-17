package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.FaceCameraCountDao;
import intellif.dao.FaceQualityCameraCountDao;
import intellif.service.FaceServiceItf;
import intellif.utils.DateUtil;
import intellif.database.entity.FaceCameraCount;
import intellif.database.entity.FaceQualityCameraCount;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FaceQualityCountThread extends Thread {
    private final static String dateFormatYMD = GlobalConsts.YMD;
    private static Logger LOG = LogManager.getLogger(FaceQualityCountThread.class);

    @Autowired
    private FaceServiceItf faceService;

    @Autowired
    private FaceQualityCameraCountDao faceCountRepository;
    
    public static void main(String[] args) {

    }

   // @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(fixedRate = 60000)
    public void run() {

    	if(GlobalConsts.run){
    		 String sTime = DateUtil.getDateString(new Date());
    		 scanFaceQualityRecord(sTime);
        	}
    }

    
  private void scanFaceQualityRecord(String dateStr){
	  try{
		  Date initDate = DateUtil.getFormatDate("2015-01-01", dateFormatYMD);  
		  Date date = DateUtil.getFormatDate(dateStr, dateFormatYMD); 
		  if(date.getTime() < initDate.getTime()){
			  return;
		  }
		  String sTime = DateUtil.getDateString(new Date(date.getTime()-24 * 3600 * 1000));
		  Date sDate = DateUtil.getFormatDate(sTime, dateFormatYMD);
		  sTime = DateUtil.getDateString(sDate);
		  List<FaceQualityCameraCount> list = faceCountRepository.findByTime(sTime);
		  if((null == list || list.isEmpty())){
	        	// 统计某天各摄像头采集高低质量总数并缓存，迭代判断历史数据是否已记录
	    		Map<Long,FaceQualityCameraCount> countMap = new HashMap<Long,FaceQualityCameraCount>();
	        	List<Object[]> statisticDayList = faceService.statisticQualityYesterdayByCamera(date);
	        	if(null != statisticDayList&&!statisticDayList.isEmpty()) {
	        		for(Object[] statistic : statisticDayList) {
	        			int quality = Integer.valueOf(statistic[0].toString());
	        			long count = Long.valueOf(statistic[3].toString());
	        			long sourceId = Long.valueOf(statistic[1].toString());
	        			FaceQualityCameraCount fqcc = countMap.get(sourceId);
	        			if(null == fqcc){
	        				 fqcc = new FaceQualityCameraCount();
	        				 fqcc.setSourceId(sourceId);
	        				 fqcc.setTime((Date) statistic[2]);
	        				countMap.put(sourceId, fqcc);
	        			}
	   				if(quality == 0){
	   					fqcc.setHighTotal(fqcc.getHighTotal()+count);
	   				}else{
	   					fqcc.setLowTotal(fqcc.getLowTotal()+count);
	   				}
	        		}
	        		
	        		Iterator<Long> iterator = countMap.keySet().iterator();
	        		
	        		while(iterator.hasNext()){
	        			long sourceId = iterator.next();
	        			FaceQualityCameraCount fqcc = countMap.get(sourceId);
	        			faceCountRepository.save(fqcc);
	        		}
	        		
	        	}
	    	//scan前一天数据看是否统计过，没有统计继续统计
			  scanFaceQualityRecord(sTime);
		  }
	  }catch(Exception e){
		  LOG.error("face quality count error,e:",e);
	  }
	  
  }
}
