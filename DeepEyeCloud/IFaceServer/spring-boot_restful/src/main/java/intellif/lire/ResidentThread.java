package intellif.lire;


import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.ResidentAreaDao;
import intellif.settings.ResidentSetting;
import intellif.utils.RemoteShellTool;
import intellif.database.entity.ResidentArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每天定时计算更新常住人口信息
 * @author shixiaohua
 *
 */
@Component
public class ResidentThread {

    private static Logger LOG = LogManager.getLogger(ResidentThread.class);
   
    @Autowired
    private ResidentAreaDao residentAreaRepository;
    public static void main(String[] args) {

    }

    //@Scheduled(cron = "0 0 1 * * ?")
    public void run() {
    	if(!GlobalConsts.run){
    		return;
    	}
   	 RemoteShellTool tool = new RemoteShellTool(ResidentSetting.getIp(), ResidentSetting.getUsername(),  
             ResidentSetting.getPassword(), "utf-8");  
      List<ResidentArea> areaList = residentAreaRepository.findAll();
         LOG.info("resident area size:"+areaList.size());
      for(ResidentArea item : areaList){
    	  Long id = item.getId();
    	  if(0 != id){
    		  String result = tool.exec(ResidentSetting.getCommand()+" "+ id);  
    		  System.out.print(result);  
    		  LOG.info(" resident shell result:"+result);
    	  }
      }
    	
    }


}
