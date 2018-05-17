package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.service.ImageServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImageMinCountThread extends Thread {

    private static Logger LOG = LogManager.getLogger(ImageMinCountThread.class);

    @Autowired
    private ImageServiceItf _imageServiceItf;
  
   // @Scheduled(fixedRate = 60000)
    public void run() {
    	try{
    		GlobalConsts.imageMinCount = this._imageServiceItf.countMinute();
    		for(int i=0; i<60; i++){
    			GlobalConsts.imageDayCount = GlobalConsts.imageDayCount + (float)GlobalConsts.imageMinCount/(float)60;
    			Thread.sleep(1000);
    		}
    		LOG.info("count image num every min, imageDayCount:"+GlobalConsts.imageDayCount);
    	}catch(Exception e){
    		LOG.error("count day image error,",e);
    	}
    }

}
