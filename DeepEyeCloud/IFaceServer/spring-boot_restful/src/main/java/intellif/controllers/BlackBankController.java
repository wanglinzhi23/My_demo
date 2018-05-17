/**
 *
 */
package intellif.controllers;

import intellif.audit.EntityAuditListener;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.BlackBankDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.RoleDao;
import intellif.dao.TaskInfoDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.UserInfo;
import intellif.dto.BankDto;
import intellif.dto.BankInfoDto;
import intellif.dto.BlackBankInfoDto;
import intellif.dto.JsonObject;
import intellif.service.BlackBankServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.PoliceStationServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.DateUtil;
import intellif.utils.FileUtil;
import intellif.utils.ImageInfoHelper;
import intellif.database.entity.PoliceStationAuthority;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class BlackBankController.</h1>
 * The BlackBankController which serves request of the form /black/bank and returns a JSON object representing an instance of BlackBank.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc.
 * (see <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and static data storages),
 * while REST is a very-high-level API style (mostly for webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
//@RequestMapping("/intellif/black/bank")
@RequestMapping(GlobalConsts.R_ID_BLACK_BANK)
public class BlackBankController {
	
	// private static SimpleDateFormat dateFormatHMS = new SimpleDateFormat(GlobalConsts.YMDHMS);          ///find bugs  simpledateformat是非线程安全的 当做全局静态变量容易出错
	private final static String dateFormatHMS = "yyyy-MM-dd HH:mm:ss";
    private static Logger LOG = LogManager.getLogger(BlackBankController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============

    // Autowire an object of type BlackBankDao
    @Autowired
    private BlackBankDao _blackBankDao;
    @Autowired
    private TaskInfoDao _taskInfoDao;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private PersonDetailDao _personDetailDao;
    @Autowired
    private PoliceStationAuthorityDao policeStationAuthorityRepository;
    @Autowired
    private BlackBankDao balckBankAuthorityDao;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private PoliceStationServiceItf policeStationService;
    @Autowired
    private CameraServiceItf cameraService;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private BlackBankServiceItf bankService;
    @Autowired
    private PersonDetailServiceItf personDetailService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the black bank info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid BlackBank blackBank) {
    	String createUser = SecurityContextHolder.getContext().getAuthentication().getName();
    	blackBank.setCreateUser(createUser);
    	List<BlackBank> bankList = _blackBankDao.findByBankName(blackBank.getBankName());
    	if(bankList.size()>0) return new JsonObject("库名已存在！", 1001);
    	blackBank.setStationId(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
    	BlackBank resp = null;
    	try{
    		 resp = _blackBankDao.save(blackBank);
    	}catch(Exception e){
    		LOG.error("", e);
    	}
    	long stationId = (((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
    	long bankId = resp.getId();
    	policeStationAuthorityRepository.save(new PoliceStationAuthority(stationId, bankId, GlobalConsts.CONTROL_AUTORITY_TYPE));
    	
    	List<PoliceStation> forefathers = policeStationService.getForefathers(stationId);
    	for (PoliceStation forefather : forefathers) {
    	    _userService.createAuthorityOrIgnore(forefather.getId(), bankId);
    	}
    	return new JsonObject(resp);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of black bank info that is successfully get or not.")
    public JsonObject list() {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
    		return new JsonObject(this._blackBankDao.findAll(authority.split(",")));
    }
    
    
    @RequestMapping(value = "/blacktype/{blacktype}/type/{type}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of black bank info that is successfully get or not.")
    public JsonObject list(@PathVariable("blacktype") int blacktype,@PathVariable("type") int type) {
    	String authority = _userService.getAuthorityIds(type);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
    	if(blacktype==2){
    		return new JsonObject(this._blackBankDao.findAll(authority.split(",")));
    	}else{
    		return new JsonObject(this._blackBankDao.findAllByType(authority.split(","),blacktype));
    	}
    }
    
    
    //库管理中也添加根据库名的检索  根据type区分是本单位库还是可编辑库还是可查看库 ////////////// 
    @RequestMapping(value="/type/bankname",method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Response a list describing all of black bank info which have the same name.")
    public JsonObject listByName(@RequestBody @Valid BankDto bankDto) {
		String roleName = CurUserInfoUtil.getRoleInfo().getName();
		if (roleName.equals("SUPER_ADMIN") || roleName.equals("MIDDLE_ADMIN") || roleName.equals("ADMIN")) {
			
		    bankDto.setStartTime(DateUtil.checkDateStrSFM(bankDto.getStartTime(), " 00:00:00"));
		    bankDto.setEndTime(DateUtil.checkDateStrSFM(bankDto.getEndTime(), " 23:59:59"));
			String bankName = bankDto.getBankName();
			String authority = _userService.getAuthorityIdsByType(bankDto.getAuthorityType());
			if (authority.trim().length() == 0) {
				return new JsonObject("本单位没有库授权！", 1001);
			}
			List<BankInfoDto> banklist = null;
			long stationId = (((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
			banklist = bankService.findBankByCombinedConditions(stationId,bankDto);
			
			LOG.info("库管理 根据type区分是本单位库还是可编辑库还是可查询库的sql" + "SELECT * FROM  " + GlobalConsts.INTELLIF_BASE
					+ "." + GlobalConsts.T_NAME_BLACK_BANK + " WHERE id in (" + authority.toString()
					+ ") and bank_name like %" + bankName + "%");
			return new JsonObject(banklist);
		} else {
			return new JsonObject("对不起，您没有查询权限！", 1001);
		}
	}
    
    @RequestMapping(value="/music/upload/key/{key}",method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Response a list describing all of black bank info which have the same name.")
    public JsonObject fileUpload(@RequestPart(value = "file") @Valid @NotNull @NotBlank MultipartFile file,@PathVariable("key")int key){
    	String musicName = "";
    	String url = "";
    	if (!file.isEmpty()) {
    		uploadMusic(file, key);
    		musicName = GlobalConsts.musicNameMap.get(key);
    		url = GlobalConsts.musicUrlMap.get(key);
		}
    	BlackBankInfoDto infoDto = new BlackBankInfoDto();
    	infoDto.setMusicName(musicName);
    	infoDto.setMusicUrl(url);
    	return new JsonObject(infoDto);
    }
    
    public JsonObject uploadMusic(MultipartFile file,int key){
    	String fullName =  "";
        try {
			byte[] bytes = file.getBytes();
			String fileName = file.getOriginalFilename();
			fullName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileName;
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullName)));
            stream.write(bytes);
            stream.close();
            String url = ImageInfoHelper.getRemoteImageUrl(fileName, propertiesBean.getIsJar());
            GlobalConsts.musicNameMap.put(key, fileName);
            GlobalConsts.musicUrlMap.put(key, url);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value="/dispatch/switch/bank/{id}/status/{status}",method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Response whether the bank is or not switch success")
    public JsonObject updateBankPersonStatus(@PathVariable("id") int id,@PathVariable("status") int status) {
    	 try {
    		 if(status == 0){
    			 //库关闭布控
    			 Calendar calendar = Calendar.getInstance();  
    			 Date currentDate = calendar.getTime();
    			 _personDetailDao.updateBankPersonDispatch(currentDate, status, id);
    		 }else if(status == 1){ 
    			 //库打开布控
    			  Date fDate = DateUtil.getFormatDate("2050-01-01 00:00:00", dateFormatHMS);   /////find bugs 为了去掉下面的simpledateformat的使用
    			 //Date fDate = dateFormatHMS.parse("2050-01-01 00:00:00");
    			 _personDetailDao.updateBankPersonDispatch(fDate, status, id);
    		 }
    	 } catch (Exception e) {
 			LOG.error("update bank persons status error:",e);
 			return new JsonObject("更新库布控状态失败！", 1001);	
 		} 
    	 long userId = ((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    	 personDetailService.noticeEngineUpdateBlackDatas(userId);
    	 return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));	
    }
   
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the black bank info id is successfully get or not.")
//    public JsonObject get(@PathVariable("id") long id) {
//        return new JsonObject(this._blackBankDao.findOne(id));
//    }

//    @RequestMapping(value = "/type/{ids}", method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the black bank info id is successfully get or not.")
//    public JsonObject getType(@PathVariable("ids") String typeIds) {
//        List<BlackBank> bankList = new ArrayList<BlackBank>();
//        Set<Long> blackBankIdList = new HashSet<Long>();
//        String[] element = typeIds.split("_");
//        if (GlobalConsts.TASK_IDS_TYPE == Integer.valueOf(element[0])) {
//            List<Long> idList = new ArrayList<Long>();
//            for (String idStr : element[1].split(",")) {
//                idList.add(Long.valueOf(idStr));
//            }
//            Iterable<TaskInfo> taskList = _taskInfoDao.findAll(idList);
//            for (TaskInfo task : taskList) {
//                blackBankIdList.add(task.getBankId());
//            }
//            for (Long bankId : blackBankIdList) {
//                bankList.add(this._blackBankDao.findOne(bankId));
//            }
//        } else if (GlobalConsts.CAMERA_IDS_TYPE == Integer.valueOf(element[0])) {
//            for (String idStr : element[1].split(",")) {
//                List<TaskInfo> tempTaskList = this._taskInfoDao.findBySourceId(Long.valueOf(idStr));
//                for (TaskInfo task : tempTaskList) {
//                    blackBankIdList.add(task.getBankId());
//                }
//            }
//            for (Long bankId : blackBankIdList) {
//                bankList.add(this._blackBankDao.findOne(bankId));
//            }
//        }
//        return new JsonObject(bankList);
//    }

    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  black bank info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid BlackBank blackBank) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.CONTROL_AUTORITY_TYPE);
    	List<BlackBank> bankList = _blackBankDao.findByBankName(blackBank.getBankName());
    	String url = blackBank.getUrl();
    	
    	//if(bankList.size()>0) return new JsonObject("库名已存在！", 1001);     修改的时候 库名只是不能和其他已有的重复  和本身重复当然没关系 就相当于没改库名嘛
    	if(bankList.size()>0&&(bankList.get(0).getId()!=blackBank.getId())){
    		return new JsonObject("库名已存在！", 1001); 
    	}
    	
    	authority = "," + authority + ",";
    	if(authority.indexOf(","+id+",")<0) return new JsonObject("对不起，您没有修改权限！", 1001);
    	BlackBank find = balckBankAuthorityDao.findOne(id); 
        EntityAuditListener.BlackBankStatusMap.put(id, find.clone());
        blackBank.setId(id);
    	List<BlackBank> banklist = _blackBankDao.findById(id);
    	blackBank.setCreated(banklist.get(0).getCreated());
     	blackBank.setCreateUser(banklist.get(0).getCreateUser());
     	if(url!=null){
     		blackBank.setUrl(url);
     	}else{
     		blackBank.setUrl("0");
     	}
        return new JsonObject(this._blackBankDao.save(blackBank));
    }
    

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the black bank info is successfully delete or not.")
    public JsonObject delete(@PathVariable("id") long id) {
    	String authority = ","+_userService.getAuthorityIds(GlobalConsts.CONTROL_AUTORITY_TYPE)+",";
    	String filterSql = " bank_id = "+ id;
    	List<PersonDetail> personList = personDetailService.findByFilter(filterSql);
    	if(personList.size()>0) return new JsonObject("该库还有人员，无法进行删除！", 1001);
    	if(authority.indexOf(","+id+",")<0) return new JsonObject("对不起，您没有删除权限！", 1001);
    	BlackBank bank = this._blackBankDao.findOne(id);
        this._blackBankDao.delete(id);
        LOG.info(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLogin()+" delete BankId:" +id+" BankName:"+bank.getBankName());
        List<PoliceStationAuthority> policeStationAuthorityList = policeStationAuthorityRepository.findByBankId(id);
        this.policeStationAuthorityRepository.delete(policeStationAuthorityList);
        return new JsonObject(Boolean.TRUE);
    }


}

