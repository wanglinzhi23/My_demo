package intellif.controllers;

import intellif.audit.AuditServiceImpl;
import intellif.audit.AuditServiceItf;
import intellif.consts.GlobalConsts;
import intellif.dao.AuditLogDao;
import intellif.dao.CidDetailDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.PoliceCloudAuditLogDao;
import intellif.dao.RoleDao;
import intellif.dao.SearchErrCodeDao;
import intellif.dao.SearchLogDao;
import intellif.dao.UserDao;
import intellif.database.entity.UserInfo;
import intellif.dto.AuditLogInfoDto;
import intellif.dto.HistoryOperationDto;
import intellif.dto.HistorySearchOperationDto;
import intellif.dto.JsonObject;
import intellif.dto.PoliceCloudLogInfoDto;
import intellif.dto.ProcessInfo;
import intellif.service.AuditLogInfoServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.SolrServerItf;
import intellif.service.impl.AuditLogInfoServiceImpl;
import intellif.utils.GetHashCode;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.CidDetail;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.JuZhuDetail;
import intellif.database.entity.OtherDetail;
import intellif.database.entity.PoliceCloudAuditLogInfo;

import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class AuditLogController.</h1>
 * The AuditLogController which serves request of the form /audit/log and returns a JSON object representing an instance of AuditLog.
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
@RequestMapping(GlobalConsts.R_ID_AUDIT_LOG)
public class AuditLogController {
    // ==============
    // PRIVATE FIELDS
    // ==============
	 SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    @Autowired
    private AuditServiceItf _auditService;

    // Autowire an object of type AuditLogRepository
    @Autowired
    private AuditLogInfoServiceImpl auditService;
    
    @Autowired
    private PoliceCloudAuditLogDao policeCloudAuditLogDao;
    @Autowired
    private UserDao userRepository;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
	private FaceServiceItf faceService;
    @Autowired
    private CidDetailDao cidDetailRepository;
    @Autowired
    private JuZhuDetailDao juzhuDetailRepository;
    @Autowired
    private OtherDetailDao otherDetailRepository;
    @Autowired
	private SolrServerItf _solrService;
    @Autowired
	private FaceController faceController;
    @Autowired
	private SearchLogDao searchLogDao;
    @Autowired
   	private SearchErrCodeDao searchErrCodeDao;
    
    private static Logger LOG = LogManager.getLogger(AuditLogController.class);
    

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the audit log info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid AuditLogInfo auditLogInfo) {
        return new JsonObject(auditService.save(auditLogInfo));
    }

/*    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of audit log info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this.auditLogRepository.findAll());
    }*/

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the audit log info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.auditService.findById(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  audit log info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid AuditLogInfo auditLogInfo) {
//		ImageInfo find = this._imageInfoDao.findOne(id);
        auditLogInfo.setId(id);
        return new JsonObject(this.auditService.save(auditLogInfo));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the audit log info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this.auditService.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }
    
    
  //对于日志记录的条件查询   7.27    分页查询
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the auditlog is successfully searched or not.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/search/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject findByCombinedConditions(@RequestBody @Valid AuditLogInfoDto auditloginfodto, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     	Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
   	    UserInfo userinfo= userRepository.findOne(userid);
   	    String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
   	 
   	  if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
        ArrayList<HistoryOperationDto> auditDto = _auditService.findByCombinedConditions(auditloginfodto,page,pageSize);
        BigInteger biginteger=  AuditServiceImpl.hisopmaxpage;
        int maxpage=0;
        if(biginteger!=null){
        if(((biginteger.intValue())%pageSize)==0){
        maxpage=(biginteger.intValue())/pageSize;
        }else{
        maxpage=(biginteger.intValue())/pageSize+1;
        }
        }
        return new JsonObject(auditDto,0,maxpage);
        
       	}else {
		
		return new JsonObject("对不起，您没有修改权限！", 1001);
	} 
    	
    }
    
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the auditlog is successfully searched or not.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/search/user/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject findByUserCombinedConditions(@RequestBody @Valid AuditLogInfoDto auditloginfodto, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     	Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
   	    UserInfo userinfo= userRepository.findOne(userid);
   	    auditloginfodto.setOwner(userinfo.getLogin());
        ArrayList<HistoryOperationDto> auditDto = _auditService.findByCombinedConditions(auditloginfodto,page,pageSize);
        BigInteger biginteger=  AuditServiceImpl.hisopmaxpage;
        int maxpage=0;
        if(biginteger!=null){
        if(((biginteger.intValue())%pageSize)==0){
        maxpage=(biginteger.intValue())/pageSize;
        }else{
        maxpage=(biginteger.intValue())/pageSize+1;
        }
        }
        return new JsonObject(auditDto,0,maxpage);
    }
    
    private List<String> getFaceUrlAndFature(int dataType,long faceId) throws SQLException{
    	List<String> result = new ArrayList<String>();
    	String faceUrl = "";
    	String faceFature = "";
    	switch (dataType) {
    	case GlobalConsts.FACE_INFO_TYPE:
    	    FaceInfo face = faceService.findOne(faceId);
    		faceUrl = face.getImageData();
    		faceFature = face.getBase64FaceFeature();
    		break;
		case GlobalConsts.CID_INFO_TYPE:
            CidDetail cid = cidDetailRepository.findOne(faceId);
			faceUrl = cid.getImageData();
			faceFature = cid.getBase64FaceFeature();
			break;
		case GlobalConsts.JUZHU_INFO_TYPE:
            JuZhuDetail juzhu = juzhuDetailRepository.findOne(faceId);
			faceUrl = juzhu.getImageData();
			faceFature = juzhu.getBase64FaceFeature();
			break;
		default:
            OtherDetail other = otherDetailRepository.findOne(faceId);
			faceUrl = other.getImageData();
			faceFature = other.getBase64FaceFeature();
			break;
		}
    	result.add(faceUrl);
    	result.add(faceFature);
    	return result;
    }
    
    @RequestMapping(value = "/policecloud/search/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "警务云操作记录查询")
    public JsonObject findAuditLogByPolice(@RequestBody @Valid AuditLogInfoDto auditloginfodto, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userid = Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
		UserInfo userinfo = userRepository.findOne(userid);
		String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();

		if (roleName.equals("SUPER_ADMIN") || roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
			List<HistoryOperationDto> auditDto = _auditService.findLogByPoliceCloud(auditloginfodto, page,
					pageSize);
			BigInteger biginteger = AuditServiceImpl.hisopmaxpage;
			int maxpage = 0;
			if (biginteger != null) {
				if (((biginteger.intValue()) % pageSize) == 0) {
					maxpage = (biginteger.intValue()) / pageSize;
				} else {
					maxpage = (biginteger.intValue()) / pageSize + 1;
				}
			}
			return new JsonObject(auditDto, 0, maxpage);
		} else {
			return new JsonObject("对不起，您没有修改权限！", 1001);
		}
    }
    
    @RequestMapping(value = "/policecloud", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "警务云操作")
    public JsonObject findAuditLogByPolice(@RequestBody @Valid PoliceCloudLogInfoDto logInfoDto) throws SQLException{
    	String url = logInfoDto.getUrl();
    	String result = url.substring(url.indexOf("api"));
    	if (result.contains("camera")) {
			result = result.substring(0,result.indexOf("camera"));
		}
    	if (result.contains("page")) {
    		result = result.substring(0, result.indexOf("page"));
		} 
    	String realName = logInfoDto.getRealName();
    	String policeId = logInfoDto.getPoliceId();
    	long faceId = logInfoDto.getFaceId();
    	int dataType = logInfoDto.getDataType();
    	String faceUrl = "";
    	String faceFature = "";
    	PoliceCloudAuditLogInfo auditLogInfo = new PoliceCloudAuditLogInfo();
    	String time = String.valueOf(System.currentTimeMillis());
    	time = time.substring(0, time.length()-4);
    	auditLogInfo.setPoliceId(policeId);
    	auditLogInfo.setObject("");
    	if (result.equals(GlobalConsts.cloudSearch)) {
    		List<String> faceArr = getFaceUrlAndFature(dataType, faceId);
    		faceUrl = faceArr.get(0);
    		faceFature = faceArr.get(1);
    		auditLogInfo.setHashCode(new GetHashCode(Long.valueOf(time),policeId,faceFature).hashCode());
    		auditLogInfo.setObject_status(16);
    		auditLogInfo.setOperation("identity query");
    		auditLogInfo.setTitle("警号" + policeId + "进行了身份查询操作");
    		auditLogInfo.setMessage("警号" + policeId + "检索了图片信息 " + faceUrl);
		} else if (result.equals(GlobalConsts.faceSearch) || result.equals(GlobalConsts.faceSearch_c)) {
    		List<String> faceArr = getFaceUrlAndFature(dataType, faceId);
    		faceUrl = faceArr.get(0);
    		faceFature = faceArr.get(1);
    		auditLogInfo.setHashCode(new GetHashCode(Long.valueOf(time),policeId,faceFature).hashCode());
    		auditLogInfo.setObject_status(17);
    		auditLogInfo.setOperation("face search");
    		auditLogInfo.setTitle("警号" + policeId + "进行了人脸检索操作");
    		auditLogInfo.setMessage("警号" + policeId + "检索了图片信息 " + faceUrl);
		}else if (result.equals(GlobalConsts.personDetail)) {
			auditLogInfo.setHashCode(new GetHashCode(Long.valueOf(time),policeId,null).hashCode());
    		auditLogInfo.setObject_status(18);
    		auditLogInfo.setOperation("dispatched person");
    		auditLogInfo.setObject(GlobalConsts.T_NAME_PERSON_DETAIL);
    		auditLogInfo.setTitle("警号" + policeId + "进行了布控人员操作");
    		auditLogInfo.setMessage("警号" + policeId + "发布了嫌疑人:" + realName);
		} else {
			return new JsonObject("无效日志",1001);
		}
    	
    	try {
    		return new JsonObject(policeCloudAuditLogDao.save(auditLogInfo));
		} catch (Exception e) {
			LOG.error("audit save error!", e);
			return new JsonObject(e.getMessage(), 1001);
		}
    }

    
 
  //对于登录统计的查询   7.27     分页查询
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the login_information is successfully searched or not.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/search/login/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject loginformation(@RequestBody @Valid AuditLogInfoDto auditloginfodto, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     	Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
   	    UserInfo userinfo= userRepository.findOne(userid);
   	    String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
    	
   	 if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
    	 LinkedMap loginf= _auditService.findLoginInformation(auditloginfodto,page,pageSize);
    	 BigInteger biginteger= AuditServiceImpl.logusermaxpage;
    	 int maxpage=0;
    	 if(biginteger!=null){
    	 if(((biginteger.intValue())%pageSize)==0){
    	  maxpage=(biginteger.intValue())/pageSize;
    	 }else{
    	 maxpage=(biginteger.intValue())/pageSize+1;
    	       }
    	 }
        return new JsonObject(loginf,0,maxpage);
      }else {
		
		return new JsonObject("对不起，您没有修改权限！", 1001);
	} 
    	
    }
    
    
    
    //格式同步于 当前在线列表的 今日登陆列表  与操作记录里面的今日登陆记录返回对象有格式的差异   多了一个post职位和ip
    @RequestMapping(value = "/todaylogin/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "分页返回今日登陆用户情况")
    public JsonObject getonlinevisitednumbers(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize){
       
    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
    	 UserInfo userinfo= userRepository.findOne(userid);
    	 String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
    	 
    
    	
 			 
    	if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
 
    		AuditLogInfoDto alog=new AuditLogInfoDto();
    		alog.setStarttime(sqlDateFormat.format(new Date()));
    		alog.setEndtime(sqlDateFormat.format(new Date()));
    		_auditService.findLoginInformation(alog,page,pageSize);
    		
    		LinkedMap todayloginmap=AuditServiceImpl.loginmap2;
    		
    		 BigInteger biginteger= AuditServiceImpl.logusermaxpage;
        	 int maxpage=0;
        	 if(biginteger!=null){
        	 if(((biginteger.intValue())%pageSize)==0){
        	  maxpage=(biginteger.intValue())/pageSize;
        	 }else{
        	 maxpage=(biginteger.intValue())/pageSize+1;
        	       }
        	 }
    		
    		return new JsonObject(todayloginmap,0,maxpage);

    		
    		}else{
    		
    		return new JsonObject("对不起，您没有修改权限！", 1001);
    	} 
   	
 
    }

    
    //导出日志记录 
    //对于日志记录的条件查询   7.27    分页查询
    @ApiOperation(httpMethod = "POST", value = "export audit log.", notes = "Default as 2015~2017 time period.")
    @RequestMapping(value = "/exportexcel/key/{key}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject exportAuditExcel(@RequestBody @Valid AuditLogInfoDto auditloginfodto, @PathVariable("key") int key) {
    	
    	ProcessInfo exportProcess = new ProcessInfo();
    	exportProcess.setTotalSize(-1);
    	exportProcess.setSuccessNum(0);
    	exportProcess.setFailedNum(0);
		GlobalConsts.downloadAuditLogMap.put(key, exportProcess);
		GlobalConsts.downloadAuditLogMapStop.put(key, true);
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     	Long userid= Long.parseLong(((authentication.getPrincipal().toString().split(","))[0].split("="))[1]);
   	    UserInfo userinfo= userRepository.findOne(userid);
   	    String roleName = roleRepository.findOne(userinfo.getRoleId()).getName();
   	 
   	  if(roleName.equals("SUPER_ADMIN")||roleName.equals("ADMIN") || roleName.equals("MIDDLE_ADMIN")) {
       
   	    String result = _auditService.exportAuditExcel(auditloginfodto,key);
   	    
   	    if(result.equals("数据为空")){
   	    	
   	    	return new JsonObject("数据为空！", 1001);
   	    }
   	   
        return new JsonObject(result);
        
       	}else {
		
		return new JsonObject("对不起，您没有导出操作权限！", 1001);
	} 
    	
    }
    
    
    
    //停止导出日志
    @RequestMapping(value = "/exportexcel/stop/{key}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "停止导出日志操作")
    public JsonObject stopExporting(@PathVariable("key") int key) {
    	
    	GlobalConsts.downloadAuditLogMapStop.put(key, false);
        return new JsonObject("导出操作已停止");
        
    }
    
    //导出日志进度
    @RequestMapping(method = RequestMethod.GET, value = "/exportexcel/progress/{key}")
	@ApiOperation(httpMethod = "GET", value = "日志导出显示进度")
	public JsonObject handleProcessZipDownload(@PathVariable("key") int key) {
		return new JsonObject(GlobalConsts.downloadAuditLogMap.get(key));
	}
    
    
    
    //返回所有搜索图片的日志记录
    @RequestMapping(method = RequestMethod.GET, value = "/searchAudit/page/{page}/pagesize/{pagesize}")
    @ApiOperation(httpMethod = "GET", value = "seachAudit.")
    public JsonObject searchAudit(@PathVariable("page") int page, @PathVariable("pagesize")int pageSize) {
         
   	    List<HistorySearchOperationDto> searchAuditList = _auditService.findSearchAudit(page,pageSize);
      
        return new JsonObject(searchAuditList);     
    	
    }
   
    
  //返回指定图片搜索详情记录
    @RequestMapping(value = "/searchAuditDetail/auditId/{auditId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "seachAuditDetail.")
    public JsonObject getSearchAuditDetail(@PathVariable("auditId") long auditId) {

        return new JsonObject( _auditService.findSearchAuditDeatil(auditId));     
    	
    }
   
    
    
    //返回搜索时延以及失败详情日志记录
     @RequestMapping(value = "/searchDelayAndErr", method = RequestMethod.GET)
     @ApiOperation(httpMethod = "GET", value = "seachAuditDetail.")
     public JsonObject getSearchDelayAndErrLog() {

         return new JsonObject( searchLogDao.findAll());     
      	
      }
      
      
     //返回搜索失败错误码列表 
     @RequestMapping(value = "/searchErrCodeList", method = RequestMethod.GET)
     @ApiOperation(httpMethod = "GET", value = "seachErrCode.")
     public JsonObject getSearchErrCode() {

          return new JsonObject( searchErrCodeDao.findAll());     
        	
        }
    
    
    
}
