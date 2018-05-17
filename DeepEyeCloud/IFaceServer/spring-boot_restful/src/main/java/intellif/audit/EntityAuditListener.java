package intellif.audit;

import intellif.consts.GlobalConsts;
import intellif.dao.AuditLogDao;
import intellif.dao.BlackBankDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.BlackBank;
import intellif.database.entity.InfoBase;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.RedPerson;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.RuleInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.MessageDto;
import intellif.enums.MqttTopicNames;
import intellif.mqtt.IfMqttClient;
import intellif.service.ResourceServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;

import java.util.*;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PreUpdate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by yangboz on 12/2/15.
 * //    @PrePersist, @PreRemove, @PostPersist, @PostRemove, @PreUpdate, @PostUpdate and @PostLoad
 *
 * @see http://stackoverflow.com/questions/22362534/can-i-use-spring-data-jpa-auditing-without-the-orm-xml-file-using-javaconfig-in/26240077#26240077
 * @see http://kurtstam.blogspot.com/2008/10/hibernate-interceptors-events-and-jpa.html
 */
@Configurable
@Component
public class EntityAuditListener {
	public static Map<Long, PersonDetail> statusMap = new HashMap<Long, PersonDetail>();
	public static Map<Long, UserInfo> UserStatusMap = new HashMap<Long, UserInfo>();
	public static Map<Long, PoliceStation> PoliceStationStatusMap = new HashMap<Long, PoliceStation>();
	public static Map<Long, BlackBank> BlackBankStatusMap = new HashMap<Long, BlackBank>();
	public static Map<Long, RuleInfo>RuleInfoStatusMap = new HashMap<Long, RuleInfo>();
	public static Map<Long, RedPerson>RedPersonStatusMap = new HashMap<Long, RedPerson>(); //红名单
	public static Map<Long, String> markInfoMap = new HashMap<Long,String>();
	private static Logger LOG = LogManager.getLogger(EntityAuditListener.class);
	private static AuditLogDao auditLogRepository;
	private static PersonDetailDao personDetailDao;
	private IfMqttClient ifMqttClient;
	
	private static UserDao userRepository;
	private static PoliceStationDao policestationDao;
	private static RoleDao roleRepository;
	private static BlackBankDao blackbankDao;
	private static UserServiceItf userService;
	private static ResourceServiceItf resourceService;

	@Autowired(required = false)
	public void setAuditLogRepository(AuditLogDao auditLogRepository) {
		EntityAuditListener.auditLogRepository = auditLogRepository;      
		LOG.info("Autowired EntityAuditListener.auditLogRepository:" + EntityAuditListener.auditLogRepository.toString());
	}

	@Autowired(required = false)
	public void setPersonDetailRepository(PersonDetailDao personDetailDao) {
		EntityAuditListener.personDetailDao = personDetailDao;
		LOG.info("Autowired EntityAuditListener.personDetailRepository:" + EntityAuditListener.personDetailDao.toString());
	}
	
	@Autowired(required = false)
	public void setUserRepository(UserDao userRepository) {
		EntityAuditListener.userRepository = userRepository;
		LOG.info("Autowired EntityAuditListener.userRepository:" + EntityAuditListener.userRepository.toString());
	}
	
	@Autowired(required = false)
	public void setPoliceStationRepository(PoliceStationDao policestationDao) {
		EntityAuditListener.policestationDao = policestationDao;
		LOG.info("Autowired EntityAuditListener.policestationRepository:" + EntityAuditListener.policestationDao.toString());
	}
	

	@Autowired(required = false)
	public void setBlackBankRepository(BlackBankDao blackbankDao) {
		EntityAuditListener.blackbankDao = blackbankDao;
		LOG.info("Autowired EntityAuditListener.blackbankRepository:" + EntityAuditListener.blackbankDao.toString());
	}
	
	
	@Autowired(required = false)
	public void setroleRepository(RoleDao roleRepository) {
		EntityAuditListener.roleRepository = roleRepository;
		LOG.info("Autowired EntityAuditListener.roleRepository:" + EntityAuditListener.roleRepository.toString());
	}

	@Autowired(required = false)
	public void setUserService(UserServiceItf userService) {
		EntityAuditListener.userService = userService;
		LOG.info("Autowired EntityAuditListener.userService:" + EntityAuditListener.userService.toString());
	}

	@Autowired(required = false)
	public void setUserService(ResourceServiceItf resourceService) {
		EntityAuditListener.resourceService = resourceService;
		LOG.info("Autowired EntityAuditListener.resourceService:" + EntityAuditListener.resourceService.toString());
	}
	@PostPersist
	public void touchForCreate(InfoBase target) {
		LOG.info("EntityAuditListener->touchForCreate with target:" + target.toString());

    	if (target instanceof PersonDetail) {/*
			
    	    int fkType = ((PersonDetail) target).getFkType();
            String fk = "";
            if(fkType!=0){
              fk = fkType+" 类反恐人员 ";  
            }
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			AuditLogInfo log = new AuditLogInfo();
			LOG.info("EntityAuditListener->touchForCreate->Auditable PersonDetail!!!");
			log.setOwner(user);
			log.setOperation("Create");
			log.setObject(GlobalConsts.T_NAME_PERSON_DETAIL);
			log.setObjectId(((PersonDetail) target).getId());
			log.setObject_status(1);
		
			log.setTitle(userrealname+"发布,"+stationname);
			//log.setMessage(accounttype+"("+user+")"  + "发布嫌疑人 ID:(" + log.getObject_id()+")"+((PersonDetail)target).getRealName());
			if(((PersonDetail) target).getType()==0){
				log.setMessage(accounttype+"("+user+")"  + "发布"+fk+"黑名单:" +((PersonDetail)target).getRealName());
			}else if(((PersonDetail) target).getType()==1){
				log.setMessage(accounttype+"("+user+")"  + "发布白名单 :" +((PersonDetail)target).getRealName());
				log.setObject_status(1001);
			}
			
			//log.setMessage(accounttype+"("+user+")"  + "发布嫌疑人 :" +((PersonDetail)target).getRealName());
			auditLogRepository.save(log);
			
			// 入库指令Mqtt通知所有客户端
			setMqtt((PersonDetail) target, GlobalConsts.CREATE_NAW_PERSON);
		
		*/}else if(target instanceof UserInfo){/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			AuditLogInfo log = new AuditLogInfo();
			
			LOG.info("EntityAuditListener->touchForCreate->Auditable userinfo!!!");
			log.setOwner(user);
			log.setOperation("Create");
			log.setObject(GlobalConsts.T_NAME_USER);
			log.setObjectId(((UserInfo) target).getId());
			log.setObject_status(12);  
			
			log.setTitle(user + "创建,"+userrealname+","+stationname);
			//log.setMessage(accounttype+"("+user+")"  + "创建用户ID:(" + log.getObject_id()+")"+((UserInfo)target).getName());
			log.setMessage(accounttype+"("+user+")"  + "创建用户:" +((UserInfo)target).getName()
			+ ", 功能权限设置为:[" + resourceService.queryResourceNames(((UserInfo) target).getResIds()) + "]");
			auditLogRepository.save(log);

		 */}else if(target instanceof PoliceStation){/*
			
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			AuditLogInfo log = new AuditLogInfo();
		
			LOG.info("EntityAuditListener->touchForCreate->Auditable PoliceStation!!!");
			log.setOwner(user);
			log.setOperation("Create");
			log.setObject(GlobalConsts.T_NAME_POLICE_STATION);
			log.setObjectId(((PoliceStation) target).getId());
			log.setObject_status(13);    
			
			//log.setMessage(accounttype+"("+user+")"  + "添加单位ID:("+log.getObject_id()+")"+((PoliceStation) target).getStationName());
			log.setMessage(accounttype+"("+user+")"  + "添加单位:"+((PoliceStation) target).getStationName());
			log.setTitle(user+ "添加,"+userrealname+","+stationname);
			auditLogRepository.save(log);
			
		*/} else if(target instanceof BlackBank){/*
			
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			AuditLogInfo log = new AuditLogInfo();
		
			LOG.info("EntityAuditListener->touchForCreate->Auditable BlackBank!!!");
			log.setOwner(user);
			log.setOperation("Create");
			log.setObject(GlobalConsts.T_NAME_BLACK_BANK);
			log.setObjectId(((BlackBank) target).getId());
			log.setObject_status(14);    
			
			//log.setMessage(accounttype+"("+user+")"  + "添加库ID:("+log.getObject_id()+")"+((BlackBank)target).getBankName());
			log.setMessage(accounttype+"("+user+")"  + "添加库:"+((BlackBank)target).getBankName());
			log.setTitle(user+ "添加,"+userrealname+","+stationname);
			auditLogRepository.save(log);
			
		*/}  else if(target instanceof PoliceStationAuthority){/*
		    UserInfo userinfo = null;
		    RoleInfo roleinfo = null;
		    String bank=blackbankDao.findOne(((PoliceStationAuthority) target).getBankId()).getBankName();   //库
		    String station=policestationDao.findOne(((PoliceStationAuthority) target).getStationId()).getStationName();   //单位名称
		    AuditLogInfo log = new AuditLogInfo();
		    LOG.info("EntityAuditListener->touchForCreate->Auditable PoliceStationAuthority!!!");
		    log.setOperation("Create");
		    log.setObject(GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY);
		    log.setObjectId(((PoliceStationAuthority) target).getId());
		    log.setObject_status(14);    
		    try {
		        userinfo=CurUserInfoUtil.getUserInfo();
		        roleinfo=CurUserInfoUtil.getRoleInfo();
            } catch (Exception e) {
            }
			if (userinfo == null || roleinfo == null) {
			    log.setMessage("(系统內部)授权库:"+bank+"给单位"+station);
			    log.setTitle("系统內部授权");
             } else {
    			Long uid=userinfo.getId();
    			Long policeStationId =userinfo.getPoliceStationId();
    			String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
    			String userrealname=userinfo.getName();
    			String accounttype=roleinfo.getCnName();
    			String user =userinfo.getLogin();
    			log.setOwner(user);
    			//log.setMessage(accounttype+"("+user+")"  + "授权库ID:("+bank+"给单位"+station);
    			log.setMessage(accounttype+"("+user+")"  + "授权库:"+bank+"给单位"+station);
    			log.setTitle(user+ "库授权,"+userrealname+","+stationname);
             }
			auditLogRepository.save(log);
			
		*/}else if(target instanceof RuleInfo){/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			AuditLogInfo log = new AuditLogInfo();
			
			LOG.info("EntityAuditListener->touchForCreate->Auditable RuleInfo!!!");
			log.setOwner(user);
			log.setOperation("Create");
			log.setObject(GlobalConsts.T_NAME_RULE_INFO);
			log.setObjectId(((RuleInfo) target).getId());
			log.setObject_status(14);    
			log.setMessage(accounttype+"("+user+")"  + "增加库规则:"+((RuleInfo)target).getRuleName());
			log.setTitle(user+ "增加库规则,"+userrealname+","+stationname);
			auditLogRepository.save(log);
			
			
		*/}else if(target instanceof RedPerson) {/*
			
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();

			AuditLogInfo log = new AuditLogInfo();
			LOG.info("EntityAuditListener->touchForCreate->Auditable RedDetail!!!");
			log.setOwner(user);
			log.setOperation("Create");
			log.setObject(GlobalConsts.T_NAME_RED_PERSON);
			log.setObjectId(((RedPerson) target).getId());
			log.setObject_status(2001);
		
			log.setTitle(userrealname+"发布,"+stationname);
			//log.setMessage(accounttype+"("+user+")"  + "发布嫌疑人 ID:(" + log.getObject_id()+")"+((PersonDetail)target).getRealName());
			log.setMessage(accounttype+"("+user+")"  + "发布红名单 :" +((RedPerson)target).getName());
			auditLogRepository.save(log);
	
		
		*/} 
		
	}
	
	
	

	

	@PreUpdate
	public void touchForUpdated(InfoBase target) {
		LOG.info("EntityAuditListener->touchForUpdate with target:" + target.toString());
	
		if (target instanceof PersonDetail) {/*
			
			int fkType = ((PersonDetail) target).getFkType();
			String fk = "";
			if(fkType!=0){
			  fk = fkType+" 类反恐人员 ";  
			}
		    
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Update");
			
		
			if(!statusMap.containsKey(((PersonDetail) target).getId())) return;
			LOG.info("EntityAuditListener->touchForUpdate->Auditable PersonDetail!!!");
		
		
			log.setObject(GlobalConsts.T_NAME_PERSON_DETAIL);
			log.setObjectId(((PersonDetail) target).getId());

			// MQTT通知类型
			int mqttType = 0;
			
			// 状态未变则属于信息更新
			if (statusMap.get(((PersonDetail) target).getId()).getStatus() == ((PersonDetail) target).getStatus()
					&&statusMap.get(((PersonDetail) target).getId()).getImportant() == ((PersonDetail) target).getImportant()
					&&statusMap.get(((PersonDetail) target).getId()).getArrest() == ((PersonDetail) target).getArrest()) {
				
				PersonDetail pd = (PersonDetail) target;
				PersonDetail oldPd = statusMap.get(pd.getId());
	
				if(CommonUtil.ObjectEquals(oldPd.getRealName(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getRealGender(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getCid(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getAddress(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getCrimeType(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getCrimeAddress(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getDescription(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getStarttime().getTime(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getEndtime().getTime(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getPhotoData(),pd.getRealName())&&
						CommonUtil.ObjectEquals(oldPd.getRuleId(),pd.getRealName())){
					return;
				}
				log.setObject_status(5);
				if(((PersonDetail) target).getType()==0){
					log.setTitle(log.getOwner()+"编辑"+fk+"黑名单信息,"+userrealname+","+stationname);
					log.setMessage(accounttype+"("+log.getOwner()+")"  + "编辑"+fk+"黑名单信息 :" +((PersonDetail)target).getRealName());	
				}else if(((PersonDetail) target).getType()==1){
					log.setTitle(log.getOwner()+"编辑白名单信息,"+userrealname+","+stationname);
					log.setMessage(accounttype+"("+log.getOwner()+")"  + "编辑白名单信息 :" +((PersonDetail)target).getRealName());
					log.setObject_status(1005);
				}
				//log.setTitle(log.getOwner()+"编辑嫌疑人信息,"+userrealname+","+stationname);
				//log.setMessage(accounttype+"("+log.getOwner()+")"  + "编辑嫌疑人信息 :" +((PersonDetail)target).getRealName());
				
			} else {
				// 布控状态发生改变
				if (statusMap.get(((PersonDetail) target).getId()).getStatus() != ((PersonDetail) target).getStatus()) {
					if (((PersonDetail) target).getStatus() == 0) {
						log.setObject_status(2);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+fk+"黑名单人员取消布控,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消布控"+fk+"黑名单人员 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"白名单人员取消布控,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消布控白名单人员:" +((PersonDetail)target).getRealName());
							log.setObject_status(1002);
						}
						//log.setTitle(log.getOwner()+"嫌疑人过期,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"过期嫌疑人 :" +((PersonDetail)target).getRealName());
						
					}
					if (((PersonDetail) target).getStatus() == 1) {
						log.setObject_status(1);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"重新布控"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"布控"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"重新布控白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"布控白名单:" +((PersonDetail)target).getRealName());
							log.setObject_status(1001);
						}
						//log.setTitle(log.getOwner()+"重新布控嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"布控嫌疑人 :" +((PersonDetail)target).getRealName());
						
					}
					if (((PersonDetail) target).getStatus() == 2) {
						log.setObject_status(4);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"已删除"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"已删除"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"已删除白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"已删除白名单 :" +((PersonDetail)target).getRealName());
							log.setObject_status(1004);
						}
						//log.setTitle(log.getOwner()+"已删除嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"已删除嫌疑人 :" +((PersonDetail)target).getRealName());
						
					}
				}
				
				// 重点状态发生改变
				if (statusMap.get(((PersonDetail) target).getId()).getImportant() != ((PersonDetail) target).getImportant()) {
					if (((PersonDetail) target).getImportant() == 1) {
						log.setObject_status(9);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"设置重点"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"设置重点"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"设置重点白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"设置重点白名单 :" +((PersonDetail)target).getRealName());
							log.setObject_status(1009);
						}
						//log.setTitle(log.getOwner()+"设置重点嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"设置重点嫌疑人 :" +((PersonDetail)target).getRealName());
						
						mqttType = GlobalConsts.MAKE_A_IMPORTANT;
					}
					if (((PersonDetail) target).getImportant() == 0) {
						log.setObject_status(10);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"取消重点"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消重点"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"取消重点白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消重点白名单 :" +((PersonDetail)target).getRealName());
							log.setObject_status(1010);
						}
						//log.setTitle(log.getOwner()+"取消重点嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消重点嫌疑人 :" +((PersonDetail)target).getRealName());
						
						mqttType = GlobalConsts.CANCEL_A_IMPORTANT;
					}
					// 重点指令与取消重点指令Mqtt通知所有客户端
					setMqtt((PersonDetail) target, mqttType);
				}
				
				// 抓捕状态发生改变
				if (statusMap.get(((PersonDetail) target).getId()).getArrest() != ((PersonDetail) target).getArrest()) {
					if (((PersonDetail) target).getArrest() == 0) {
						log.setObject_status(7);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"取消抓捕"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消抓捕"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"取消抓捕白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消抓捕白名单 :" +((PersonDetail)target).getRealName());
							log.setObject_status(1007);
						}
						//log.setTitle(log.getOwner()+"取消抓捕嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"取消抓捕嫌疑人 :" +((PersonDetail)target).getRealName());
						
						mqttType = GlobalConsts.CANCEL_A_ARREST;
					}
					if (((PersonDetail) target).getArrest() == 1) {
						log.setObject_status(6);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"发起抓捕"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"发起抓捕"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"发起抓捕白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"发起抓捕白名单 :" +((PersonDetail)target).getRealName());
							log.setObject_status(1006);
						}
						//log.setTitle(log.getOwner()+"发起抓捕嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"发起抓捕嫌疑人 :" +((PersonDetail)target).getRealName());
						
						mqttType = GlobalConsts.MAKE_A_ARREST;
					}
					if (((PersonDetail) target).getArrest() == 2) {
						log.setObject_status(8);
						if(((PersonDetail) target).getType()==0){
							log.setTitle(log.getOwner()+"已抓捕"+fk+"黑名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"已抓捕"+fk+"黑名单 :" +((PersonDetail)target).getRealName());
						}else if(((PersonDetail) target).getType()==1){
							log.setTitle(log.getOwner()+"已抓捕白名单,"+userrealname+","+stationname);
							log.setMessage(accounttype+"("+log.getOwner()+")"  +"已抓捕白名单:" +((PersonDetail)target).getRealName());
							log.setObject_status(1008);
						}
						//log.setTitle(log.getOwner()+"已抓捕嫌疑人,"+userrealname+","+stationname);
						//log.setMessage(accounttype+"("+log.getOwner()+")"  +"已抓捕嫌疑人 :" +((PersonDetail)target).getRealName());
						
					}
					// 抓捕指令与取消抓捕指令Mqtt通知所有客户端
					if(((PersonDetail) target).getArrest()!=2) {
						setMqtt((PersonDetail) target, mqttType);
					}
				}
			}
			auditLogRepository.save(log);
	
		
		*/}else if (target instanceof UserInfo) {/*
		
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Update");
			
			//得先知道修改的是对象的什么属性   单位还得换成名称  账号类型还得根据rule转化
			String changes="";
			UserInfo newinfo=(UserInfo) target;
			UserInfo oldinfo=UserStatusMap.get(newinfo.getId());
			
			if(null == oldinfo){
				return;
			}
			
			if(!newinfo.getLogin().equals(oldinfo.getLogin())){
				changes="更新账号"+oldinfo.getLogin()+"为"+newinfo.getLogin();
			}
			
			if(!newinfo.getName().equals(oldinfo.getName())){
				
				changes=changes+" 更新用户姓名"+oldinfo.getName()+"为"+newinfo.getName();
				
			}
			
			
			if(!newinfo.getPassword().equals(oldinfo.getPassword())){
				
				changes=changes+" 更新了用户密码";
				
			}
			
			if(newinfo.getPoliceStationId()!=oldinfo.getPoliceStationId()){

			    String oldStationName = "";
			    String newStationName = "";
				if (oldinfo.getPoliceStationId() != null)
					oldStationName=policestationDao.findOne(oldinfo.getPoliceStationId()).getStationName();   //旧单位名称
				if (newinfo.getPoliceStationId() != null)
					newStationName=policestationDao.findOne(newinfo.getPoliceStationId()).getStationName();   //新单位名称

                if (newinfo.getPoliceStationId() == null) {
                	changes = changes + " 解绑了用户";
				} else if (oldinfo.getPoliceStationId() == null) {
                	changes = changes + " 绑定了用户单位为 " + newStationName;
				} else {
					changes = changes + " 更新了用户用户单位" + oldStationName + "为" + newStationName;
				}
			}

			 if(!newinfo.getPost().equals(oldinfo.getPost())){
				
				changes=changes+" 更新了用户职称"+oldinfo.getPost()+"为"+newinfo.getPost();
			
			}
			Map<String, String> roleNameInfo = new HashMap<>();
			if (userService.isRoleTypeModified(oldinfo.getRoleId(), newinfo.getRoleId(), roleNameInfo)) {
			 	changes += " 更新了用户角色 " + roleNameInfo.get("oldType") + "为" + roleNameInfo.get("newType");
			}

			Map<String, String> resNameInfo = new HashMap<>();
			if (userService.isResourcesModified(oldinfo.getRoleId(), newinfo.getRoleId(), resNameInfo)) {
				changes += " 更新了用户功能权限[" + resNameInfo.get("oldRes") + "]为[" + resNameInfo.get("newRes") + "]";
			}
			 //操作账号等修改用户访问时限也得记录哦
		    if(newinfo.getRoleId()>2){
		    	
		    	if(newinfo.getStartTime().compareTo(oldinfo.getStartTime())!=0){
		    		String oldStarttime=oldinfo.getStartTime()+"";   
					//String newStarttime=newinfo.getStartTime()+""; 
					String newStarttime=DateUtil.formatDate(newinfo.getStartTime(), "yyyy-MM-dd hh:mm:ss");
					changes=changes+" 更新了用户访问时限 开始时间"+oldStarttime+"变为"+newStarttime;
		    	}
				if(newinfo.getEndTime().compareTo(oldinfo.getEndTime())!=0){
					String oldEndtime=oldinfo.getEndTime()+"";   
					//String newEndtime=newinfo.getEndTime()+"";  
					String newEndtime=DateUtil.formatDate(newinfo.getEndTime(), "yyyy-MM-dd hh:mm:ss");
					changes=changes+" 更新了用户访问时限  截止时间时间 "+oldEndtime+"变为"+newEndtime;
				}				
			}
				
            LOG.info("EntityAuditListener->touchForUpdate->Auditable UserInfo!!!");
			
			log.setObject(GlobalConsts.T_NAME_USER);
			log.setObjectId(((UserInfo) target).getId());
			log.setObject_status(12);
			log.setTitle(log.getOwner()+"已更新用户,"+userrealname+","+stationname);
			//log.setMessage(accounttype+"("+log.getOwner()+")"  + "已更新用户 ID:"+((UserInfo)target).getId()+" "+changes);
			log.setMessage(accounttype+"("+log.getOwner()+")"  + "已更新用户 "+((UserInfo)target).getLogin()+" "+changes);
			auditLogRepository.save(log);
			
		*/}else if(target instanceof PoliceStation){

		}else if(target instanceof BlackBank){/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Update");
			
			
			String changes="";
			BlackBank newinfo=(BlackBank) target;
			BlackBank oldinfo=BlackBankStatusMap.get(newinfo.getId());
			
			if(null == oldinfo){
				return;
			}
			
			if(!newinfo.getBankName().equals(oldinfo.getBankName())){
				
				changes="更新库名称"+oldinfo.getBankName()+"为"+newinfo.getBankName();
			}
			
			if(!newinfo.getBankDescription().equals(oldinfo.getBankDescription())){
				
				changes=changes+" 更新库描述 "+oldinfo.getBankDescription()+"为"+newinfo.getBankDescription();
				
			}
			
			
	        LOG.info("EntityAuditListener->touchForUpdate->Auditable BlackBank!!!");
			
			log.setObject(GlobalConsts.T_NAME_BLACK_BANK);
			log.setObjectId(((BlackBank) target).getId());
			log.setObject_status(14);
			log.setTitle(log.getOwner()+"已更新库,"+userrealname+","+stationname);
		    log.setMessage(accounttype+"("+log.getOwner()+")"  + "已更新库 :"+changes);
			auditLogRepository.save(log);
			
		*/}else if(target instanceof RuleInfo){/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Update");
			
			
			String changes="";
			RuleInfo newinfo=(RuleInfo) target;
			RuleInfo oldinfo=RuleInfoStatusMap.get(newinfo.getId());
			
			if(null == oldinfo){
				return;
			}
			
			if(!newinfo.getRuleName().equals(oldinfo.getRuleName())){
				
				changes="更新库规则名称"+oldinfo.getRuleName()+"为"+newinfo.getRuleName();
			}
			
			if(!newinfo.getRuleDescription().equals(oldinfo.getRuleDescription())){
				
				changes=changes+" 更新库规则描述 "+oldinfo.getRuleDescription()+"为"+newinfo.getRuleDescription();
				
			}
			
           if(!newinfo.getThresholds().equals(oldinfo.getThresholds())){
				
				changes=changes+" 更新库规则thresholds "+oldinfo.getThresholds()+"为"+newinfo.getThresholds();
				
			}
           
           if(!newinfo.getTypes().equals(oldinfo.getTypes())){
				
				changes=changes+" 更新库规则种类 "+oldinfo.getTypes()+"为"+newinfo.getTypes();
				
			}
			
			
	        LOG.info("EntityAuditListener->touchForUpdate->Auditable RuleInfo!!!");
			
			log.setObject(GlobalConsts.T_NAME_RULE_INFO);
			log.setObjectId(((RuleInfo) target).getId());
			log.setObject_status(14);
			log.setTitle(log.getOwner()+"已更新库规则,"+userrealname+","+stationname);
		    log.setMessage(accounttype+"("+log.getOwner()+")"  + "已更新库规则:"+changes);
			auditLogRepository.save(log);
			
		*/}else if(target instanceof RedPerson){/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
			
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Update");
			
			
			String changes="";
			RedPerson newinfo=(RedPerson) target;
			RedPerson oldinfo=RedPersonStatusMap.get(newinfo.getId());
			if(null == oldinfo){
				return;
			}
			if(!newinfo.getName().equals(oldinfo.getName())){
				
				changes="更新红名单用户姓名 "+oldinfo.getName()+"为"+newinfo.getName();
			}
			
			if(!newinfo.getSex().equals(oldinfo.getSex())){
				
				changes=changes+" 更新红名单用户性别 "+oldinfo.getSex()+"为"+newinfo.getSex();
				
			}
			
          if(!newinfo.getRemarks().equals(oldinfo.getRemarks())){
				
				changes=changes+" 更新红名单用户备注 "+oldinfo.getRemarks()+"为"+newinfo.getRemarks();
				
			}
          if(!newinfo.getPolicePhone().equals(oldinfo.getPolicePhone())){
              
              changes=changes+" 更新红名单用户警信号 "+oldinfo.getPolicePhone()+"为"+newinfo.getPolicePhone();
              
          }
         if(!newinfo.getFaceUrl().equals(oldinfo.getFaceUrl())){
              
              changes=changes+" 更新红名单用户头像 "+oldinfo.getFaceUrl()+"为"+newinfo.getFaceUrl();
              
          }
          
			
	        LOG.info("EntityAuditListener->touchForUpdate->Auditable RedPerson!!!");
			
			log.setObject(GlobalConsts.T_NAME_RULE_INFO);
			log.setObjectId(((RedPerson) target).getId());
			log.setObject_status(2002);
			log.setTitle(log.getOwner()+"已更新红名单用户,"+userrealname+","+stationname);
		    log.setMessage(accounttype+"("+log.getOwner()+")"  + "已更新红名单用户:"+changes);
			auditLogRepository.save(log);
			
		*/}
		
		
		
		
	}

	//DB level DELETE
	@PostRemove
	public void touchForDelete(InfoBase target) {
		//        target.setUpdated(new Date());
		LOG.info("EntityAuditListener->touchForDelete with target:" + target.toString());

		if (target instanceof PersonDetail) {/*
			
	          int fkType = ((PersonDetail) target).getFkType();
	            String fk = "";
	            if(fkType!=0){
	              fk = fkType+" 类反恐人员 ";  
	            }
		    
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");
			
			LOG.info("EntityAuditListener->touchForDelete->Auditable PersonDetail!!!");
	
			log.setObject(GlobalConsts.T_NAME_PERSON_DETAIL);
			log.setObjectId(((PersonDetail) target).getId());
			log.setObject_status(4);
			
			if(((PersonDetail) target).getType()==0){
				log.setTitle(log.getOwner()+"已删除"+fk+"黑名单人员,"+userrealname+","+stationname);
			    log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除"+fk+"黑名单人员 :"+((PersonDetail)target).getRealName());
			}else if(((PersonDetail) target).getType()==1){
				log.setTitle(log.getOwner()+"已删除白名单人员,"+userrealname+","+stationname);
			    log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除白名单人员:"+((PersonDetail)target).getRealName());
			    log.setObject_status(1004);
			}
			
			//log.setTitle(log.getOwner()+"已删除嫌疑人,"+userrealname+","+stationname);
		    //log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除嫌疑人 :"+((PersonDetail)target).getRealName());
			auditLogRepository.save(log);
		*/}else if (target instanceof UserInfo) {/*
			
			UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");
		
			LOG.info("EntityAuditListener->touchForDelete->Auditable UserInfo!!!");
			
			log.setObject(GlobalConsts.T_NAME_USER);
			log.setObjectId(((UserInfo) target).getId());
			log.setObject_status(12);
			log.setTitle(log.getOwner()+"已删除用户,"+userrealname+","+stationname);
			log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除用户:"+((UserInfo)target).getName());
			auditLogRepository.save(log);
		*/}else if (target instanceof PoliceStation) {/*
			
			UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");
		
			LOG.info("EntityAuditListener->touchForDelete->Auditable PoliceStation!!!");
			
			log.setObject(GlobalConsts.T_NAME_POLICE_STATION);
			log.setObjectId(((PoliceStation) target).getId());
			log.setObject_status(13);
			log.setTitle(log.getOwner()+"已删除单位,"+userrealname+","+stationname);
			log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除单位 :"+((PoliceStation)target).getStationName());
			auditLogRepository.save(log);
		*/}else if (target instanceof BlackBank) {/*
			
			UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");
			
			LOG.info("EntityAuditListener->touchForDelete->Auditable BlackBank!!!");
			
			log.setObject(GlobalConsts.T_NAME_BLACK_BANK);
			log.setObjectId(((BlackBank) target).getId());
			log.setObject_status(14);
			log.setTitle(log.getOwner()+"已删除库,"+userrealname+","+stationname);
		    log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除库:"+((BlackBank)target).getBankName());
			auditLogRepository.save(log);
		
		*/}else if(target instanceof PoliceStationAuthority){/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");

			LOG.info("EntityAuditListener->touchForCreate->Auditable PoliceStationAuthority!!!");
		
			
			log.setObject(GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY);
			log.setObjectId(((PoliceStationAuthority) target).getId());
			log.setObject_status(14);    
			String station=policestationDao.findOne(((PoliceStationAuthority) target).getStationId()).getStationName();   //单位名称
			if(null ==blackbankDao.findOne(((PoliceStationAuthority) target).getBankId())) return;
			String bank=blackbankDao.findOne(((PoliceStationAuthority) target).getBankId()).getBankName();   //库
			log.setMessage(accounttype+"("+log.getOwner()+")"  + "取消给单位"+station+"的库:"+bank+"授权");
			log.setTitle(log.getOwner()+ "取消库授权,"+userrealname+","+stationname);
			auditLogRepository.save(log);
			
		*/}else if(target instanceof RuleInfo){/*
			
			UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");

			LOG.info("EntityAuditListener->touchForCreate->Auditable RuleInfo!!!");
		
			
			log.setObject(GlobalConsts.T_NAME_RULE_INFO);
			log.setObjectId(((RuleInfo) target).getId());
			log.setObject_status(14);    
			log.setMessage(accounttype+"("+log.getOwner()+")"  + "删除库规则"+((RuleInfo)target).getRuleName());
			log.setTitle(log.getOwner()+ "删除库规则,"+userrealname+","+stationname);
			auditLogRepository.save(log);
			
		*/}else if (target instanceof RedPerson) {/*
			
			 UserInfo userinfo=CurUserInfoUtil.getUserInfo();
			 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
			 
			 Long uid=userinfo.getId();
			 Long policeStationId =userinfo.getPoliceStationId();
			 String stationname=policestationDao.findOne(policeStationId).getStationName();   //单位名称
			 String userrealname=userinfo.getName();
			 String accounttype=roleinfo.getCnName();
			 String user =userinfo.getLogin();
		
			 AuditLogInfo log = new AuditLogInfo();
			 log.setOwner(user);
			 log.setOperation("Delete");
		
			LOG.info("EntityAuditListener->touchForDelete->Auditable RedPerson!!!");
			
			log.setObject(GlobalConsts.T_NAME_RED_PERSON);
			log.setObjectId(((RedPerson) target).getId());
			log.setObject_status(2003);
			log.setTitle(log.getOwner()+"已删除红名单用户,"+userrealname+","+stationname);
			log.setMessage(accounttype+"("+log.getOwner()+")"  + "已删除红名单用户:"+((RedPerson)target).getName());
			auditLogRepository.save(log);
		*/} 
		
		
		
		
	}


	@SuppressWarnings("static-access")
    public void setMqtt(PersonDetail person, int mqttType) {
		try {
			ifMqttClient = new IfMqttClient();
			ifMqttClient.connect();
			MqttMessage mqttMessage = new MqttMessage();
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JtsModule());
			MessageDto message = new MessageDto(person.getId(), person.getBankId(), new Date(), mqttType);
			String applyTaskDtoJsonStr = mapper.writeValueAsString(message);
			mqttMessage.setPayload(applyTaskDtoJsonStr.getBytes("UTF-8"));
			ifMqttClient.publish(MqttTopicNames.Message.getValue(), mqttMessage);
			ifMqttClient.getClient().disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
