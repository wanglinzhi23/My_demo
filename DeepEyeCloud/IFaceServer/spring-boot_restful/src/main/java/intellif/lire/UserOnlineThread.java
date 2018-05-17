package intellif.lire;

import intellif.dao.AuditLogDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserDao;
import intellif.database.entity.AuditLogInfo;
import intellif.service.impl.UserDetailsServiceImpl;
import intellif.database.entity.OnLineUserInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class UserOnlineThread extends Thread {


    private static Logger LOG = LogManager.getLogger(UserOnlineThread.class);

    @Autowired
    private UserDao userRepository;
    @Autowired
    private PoliceStationDao policestationDao;
    @Autowired
    private AuditLogDao auditLogRepository;
      
     public static Map<String,String> visitedusers=new HashMap<String,String>();
	 public static Map<String,Long> onlineusers=new HashMap<String,Long>();
	 public static Map<String,OnLineUserInfo> onlineusersinfo=new HashMap<String,OnLineUserInfo>();
	 public static Map<String,String> userOutOfDateState=new HashMap(); //1.2.0控制用户访问时限 新加  key为用户账号 value为是否过期的字符串
	 
	// @Scheduled(fixedRate = 10000)
	    public void run() {

	    	Iterator<Map.Entry<String,String>> entries = visitedusers.entrySet().iterator();
	  
	    	while (entries.hasNext()) {   
	    	  
	    	    Map.Entry entry = entries.next();  	    	  
	    	    long now=(new Date()).getTime();
	       	    long lasttime=Long.parseLong(((String) entry.getValue()).split(",")[0]);
	       	    //假设 十分钟未重新请求的话                   相当于掉线   ------即 注销登录
	       		//记录注销的操作日志表
	       	    if(onlineusers.containsKey(entry.getKey())){
	       	    	
	   	    	OnLineUserInfo onlineuser=onlineusersinfo.get(entry.getKey());	
	   	    	if(onlineuser==null){return;}     //fix 下面的bugs
	   	 	    String owner= onlineuser.getOwner();
		    	String userName = onlineuser.getName();
		        String accounttype=onlineuser.getAccounttype();
		        String stationname=policestationDao.findOne(onlineuser.getPoliceStationId()).getStationName();   //单位名称        
		    	//LOG.info("EntityAuditListener->touchForCreate->Auditable log off!!!");
				AuditLogInfo log = new AuditLogInfo();
			
	       	 if((now-lasttime)>600000){				
	       	    //	if(onlineuser!=null){	 	    //find bugs nullcheck of value previously dereferenced  	       
	    			log.setOwner(owner);
	    			log.setOperation("log off");
	    			log.setObject("");  //没有表咯
	    			log.setObjectId(onlineuser.getId());
	    			log.setObject_status(11);  
	    	    	log.setTitle(owner + "注销啦,"+userName+","+stationname);
	    			log.setMessage(accounttype+owner+"退出了系统");
	    			auditLogRepository.save(log);    			
	       	   // 	}       	    	
	    			String remove=entry.getKey()+"";
	       	    	onlineusers.remove(remove);
	       	    	onlineusersinfo.remove(remove); 
       	    		       	    	
	       	    }else if(((now-lasttime)<600000)&&(!onlineusersinfo.containsKey(onlineuser.getOwner()))){
   	
	       	        onlineusers.put(onlineuser.getOwner(), (new Date()).getTime());
	       	        onlineusersinfo.put(onlineuser.getOwner(),onlineuser);
	       	    	LOG.info("EntityAuditListener->touchForCreate->Auditable log off!!!");
	    			log.setOwner(owner);
	    			log.setOperation("log in");
	    			log.setObject("");  
	    			log.setObjectId(onlineuser.getId());
	    			log.setObject_status(11);  
	    	    	log.setTitle(owner + "登录啦,"+userName+","+stationname);
	    			log.setMessage(accounttype+owner+"登录了系统");  //并不只是通过登录界面 输入用户名和密码访问系统就算登录  重新打开界面  由于token还存在 访问 也算重新登录系统
	    			auditLogRepository.save(log);	       	    	
	       	    }
	    	  
	    	}
	       	    
	    	}
	    	


	 }

}
