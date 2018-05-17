package intellif.lire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.consts.GlobalConsts;

@Component
public class ZipInfoMapClearThread extends Thread {

private static Logger LOG = LogManager.getLogger(MapClearThread.class);
    
    //@Scheduled(cron = "0 0 * * * ?")
    public void run() {
    	try{
    		GlobalConsts.zipMap.keySet().forEach(key -> {
    			if (GlobalConsts.zipMap.get(key).getFinishedTime() - System.currentTimeMillis() > 60000) {
    				GlobalConsts.zipMap.remove(key);
    			}
    		});
    		GlobalConsts.downloadPkResultMap.keySet().forEach(key -> {
    			if (GlobalConsts.downloadPkResultMap.get(key).getFinishedTime() - System.currentTimeMillis() > 60000) {
    				GlobalConsts.downloadPkResultMap.remove(key);
    			}
    		});
    	}catch(Exception e){
    		LOG.error("定时删除ZipInfoMap数据出错！",e);
    	}
    }
    
}
