package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.service.FaceServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FaceMinCountThread extends Thread {

    private static Logger LOG = LogManager.getLogger(FaceMinCountThread.class);

    @Autowired
    private FaceServiceItf faceService;
  
    //@Scheduled(fixedRate = 60000)
    public void run() {
    	try{
    		GlobalConsts.faceMinCount = this.faceService.countMinute();
    		for(int i=0; i<60; i++){
    			GlobalConsts.faceDayCount = GlobalConsts.faceDayCount + (float)GlobalConsts.faceMinCount/(float)60;
    			Thread.sleep(1000);
    		}
    		LOG.info("count face num every min, faceDayCount:"+GlobalConsts.faceDayCount);
    	}catch(Exception e){
    		LOG.error("count day face error,",e);
    	}
    }

}
