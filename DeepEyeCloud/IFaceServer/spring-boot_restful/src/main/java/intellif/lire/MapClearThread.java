package intellif.lire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.audit.EntityAuditListener;
import intellif.consts.GlobalConsts;
import intellif.controllers.FaceController;
import intellif.database.entity.ExcelProcessInfo;

@Component
public class MapClearThread extends Thread {

    private static Logger LOG = LogManager.getLogger(MapClearThread.class);
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void run() {
    	try{
    		processFileUploadClear(GlobalConsts.fileUploadMap);
   			FaceController.userSearchMap.clear();
   			GlobalConsts.downloadMap.clear();
   			GlobalConsts.userBukongMap.clear();
   			EntityAuditListener.RedPersonStatusMap.clear();//更新操作的记录map
   			EntityAuditListener.UserStatusMap.clear();
   			EntityAuditListener.PoliceStationStatusMap.clear();
   			EntityAuditListener.BlackBankStatusMap.clear();
   			EntityAuditListener.RuleInfoStatusMap.clear();
    	}catch(Exception e){
    		LOG.error("定时删除map数据出错！",e);
    	}
    }
    private void processFileUploadClear(Map<Integer, ExcelProcessInfo> map){
    	List<Integer> keyList = new ArrayList<Integer>();
    	Iterator<Integer> iterator = map.keySet().iterator();
    	while(iterator.hasNext()){
    		Integer key = iterator.next();
    		ExcelProcessInfo epi = map.get(key);
    		if(!epi.isImportState()){
    			keyList.add(key);
    		}
    	}
    	
    	for(Integer item : keyList){
    		map.remove(item);
    	}
    }
}
