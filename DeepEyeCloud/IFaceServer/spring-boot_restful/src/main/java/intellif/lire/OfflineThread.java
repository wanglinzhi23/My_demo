package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.CrimeAlarmDao;
import intellif.dao.OtherDetailDao;
import intellif.dto.FaceResultDto;
import intellif.dto.SearchFaceDto;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.OfflineSetting;
import intellif.settings.ServerSetting;
import intellif.utils.MemcachedSpace;
import intellif.database.entity.CrimeAlarmInfo;
import intellif.database.entity.OtherDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OfflineThread extends Thread {

    private static Logger LOG = LogManager.getLogger(OfflineThread.class);
   
    @Autowired
    private OtherDetailDao otherDetailRepository;
    @Autowired
    private CrimeAlarmDao crimeAlarmDao;
    @Autowired
    private SolrDataServiceItf solrDataServiceItf;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private UserServiceItf userService;
    @Autowired
    private MemcachedSpace memcachedSpace;
    
	private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 24, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.CallerRunsPolicy());   


//    @Scheduled(fixedDelay = 3600*1000)

	 @SuppressWarnings("unused")
    @Scheduled(cron = "0 0 23 * * ?")  //   每天下午二十三点触发
    public void run() {
		 if(!OfflineSetting.isRun()){
	    		return;
	    	}
    	try{
    		LOG.debug("离线布控任务开始!");
    		System.out.println("离线布控任务开始!");
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 22:00:00");
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		int timepoint = 24;
    		if(df.parse(sdf.format(new Date())).getTime() > new Date().getTime()) {
    			timepoint = 48;
    		}
    		 String startTime = df.format(new Date(df.parse(sdf.format(new Date())).getTime()-3600*1000*timepoint));
    		 String endTime = df.format(new Date(df.parse(startTime).getTime()+3600*1000*24));
    		final float scoreThreshold = ServerSetting.getThreshold();
    		 int type = GlobalConsts.FACE_INFO_TYPE;
    		SearchFaceDto sfd = new SearchFaceDto();
    		sfd.setStarttime(startTime);
    		sfd.setEndtime(endTime);
    		sfd.setType(type);
    		sfd.setScoreThreshold(scoreThreshold);
    		Iterable<OtherDetail> blackList = otherDetailRepository.findByZplxmc(GlobalConsts.CRIME_INFO_TYPE+"");
    		if(!blackList.iterator().hasNext()) return;
    		for(final OtherDetail black : blackList) {
    			threadPool.submit(new Runnable() {
    				public void run() {
    					try {
    						List<CrimeAlarmInfo> alarmList = new ArrayList<CrimeAlarmInfo>();
//    						String faceFeature = black.getBase64FaceFeature();
    						String faceFeature = memcachedSpace.getFacefeatureFromId(GlobalConsts.CRIME_INFO_TYPE+":"+black.getId(), black.getId(), GlobalConsts.CRIME_INFO_TYPE);
    						List<FaceResultDto> faceList = solrDataServiceItf.searchFaceByTime(MD5.digest(faceFeature+":"+scoreThreshold+":"+type), faceFeature, sfd);
    						System.out.println("离线布控样本数量："+faceList.size());
    					//	if(null == faceList||faceList.size()==0) return;       //find bugs nullcheck of value previously dereferenced  
    						for(FaceResultDto face : faceList) {
    							CrimeAlarmInfo alarm = new CrimeAlarmInfo();
    							alarm.setCameraId(face.getCamera());
    							alarm.setConfidence(face.getScore());
    							alarm.setCrimeFaceId(black.getId());
    							alarm.setCrimePersonId(black.getFromCidId());
    							alarm.setFaceId(Long.valueOf(face.getId()));
    							alarm.setFaceUrl(face.getFile());
    							alarm.setTime(face.getTime());
    							alarm.setStatus(0);
    							alarmList.add(alarm);
    						}
    						crimeAlarmDao.save(alarmList);
    					} catch (Exception e) {
    						LOG.error("Crime detail id "+ black.getId()+" error !",e);
    					}
    				}
    			});
    		}
    	}catch(Exception e){
    		LOG.error("Offline Task Error !",e);
    	}
    }

}
