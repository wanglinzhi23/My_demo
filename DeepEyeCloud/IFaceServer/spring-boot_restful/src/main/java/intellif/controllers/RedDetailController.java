/**
 *
 */
package intellif.controllers;

import intellif.audit.EntityAuditListener;
import intellif.common.Constants;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.controllers.FileUploadController.ImageSize;
import intellif.dao.BlackDetailDao;
import intellif.dao.CidDetailDao;
import intellif.dao.CidInfoDao;
import intellif.dao.ExcelRecordDao;
import intellif.dao.IFaceConfigDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.JuZhuInfoDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.OtherInfoDao;
import intellif.dao.PersonRedDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.RedCheckDao;
import intellif.dao.RedDetailDao;
import intellif.dao.RedForceDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.RedPerson;
import intellif.database.entity.UserInfo;
import intellif.dto.JsonObject;
import intellif.dto.RedDto;
import intellif.dto.RedParamDto;
import intellif.dto.SearchReasonDto;
import intellif.dto.UploadZipMessage;
import intellif.enums.IFaceSdkTypes;
import intellif.enums.MqttTopicNames;
import intellif.excel.PersonRedXLS;
import intellif.exception.MsgException;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ERedListIoctrlType;
import intellif.ifaas.E_FACE_EXTRACT_TYPE;
import intellif.ifaas.T_IF_FACERECT;
import intellif.ifaas.T_MulAlgFeatureExtReq;
import intellif.ifaas.T_MulAlgFeatureExtRsp;
import intellif.ifaas.T_OneAlgFeatureExtRsp;
import intellif.service.BlackFeatureServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.RedDetailServiceItf;
import intellif.service.RedFeatureServiceItf;
import intellif.settings.ServerSetting;
import intellif.settings.ThreadSetting;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CollectionUtil;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.DateUtil;
import intellif.utils.FileUploadUtil;
import intellif.utils.FileUtil;
import intellif.utils.ImageInfoHelper;
import intellif.utils.ImageUtil;
import intellif.utils.JinxinUtil;
import intellif.utils.MqttUtil;
import intellif.utils.StringUtil;
import intellif.validate.AnnotationValidator;
import intellif.validate.ValidateResult;
import intellif.validate.ValidateUtil;
import intellif.database.entity.CidDetail;
import intellif.database.entity.ExcelProcessInfo;
import intellif.database.entity.ExcelRecord;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.IFaceConfig;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.ImportThreadsParams;
import intellif.database.entity.JuZhuDetail;
import intellif.database.entity.MarkInfo;
import intellif.database.entity.OtherDetail;
import intellif.database.entity.RedCheckRecord;
import intellif.database.entity.RedDetail;
import intellif.database.entity.RedForceRecord;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blogspot.na5cent.exom.ExOM;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class RedDetailController.</h1>
 * The RedDetailController which serves request of the form /black/detail and returns a JSON object representing an instance of RedDetail.
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
@RequestMapping(GlobalConsts.R_ID_RED)
public class RedDetailController {
//    private static final int DEFAULT_PAGE_SIZE = 100;
    //
    private static Logger LOG = LogManager.getLogger(RedDetailController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;
 
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private RedDetailDao redDetailDao;
    @Autowired
    private PersonRedDao redPersonDao;
    @Autowired
    private IFaceConfigDao ifaceConfigDao;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private RedDetailServiceItf redService;
    @Autowired
	private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
	private ExcelRecordDao recordDao;
    @Autowired
	private RedForceDao forceDao;
    @Autowired
	private RedCheckDao checkDao;
    @Autowired
	private FaceServiceItf faceService;
    @Autowired
	private PoliceStationDao policestationDao;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedFeatureServiceItf featureService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BlackDetailDao blackDetailDao;
    @Autowired
    private JuZhuDetailDao juZhuDetailRepository;
    @Autowired
    private OtherDetailDao otherDetailRepository;
    @Autowired
    private CidDetailDao cidDetailRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the red detail info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid RedDto redDto) throws Exception {
    	try{
    	String imageIds = redDto.getImageIds();
        if(null== imageIds || imageIds.length()<= 0){
        	return new JsonObject("请先上传红名单照片",1001);
        }
    	 boolean status = ValidateUtil.validateResult(redDto);
        if(!status){
        	return new JsonObject("参数格式错误",1001);
        }
        RedPerson rp = new RedPerson(redDto);	
        rp = redPersonDao.save(rp);
        RedDetail resp = null;
        
        for (String imageId : redDto.getImageIds().split(",")) {
        	  try {
        	   RedDetail redDetail = new RedDetail();
        	   redDetail.setFromPersonId(rp.getId());
        	   redDetail.setFromImageId(Long.valueOf(imageId));
        	   String faceUrl_ori =((ImageInfo)_imageServiceItf.findById(Long.valueOf(imageId))).getFaceUri();
              // String faceUrl_store = ImageSettings.getStoreRemoteUrl() + new File(FileUtil.getStoreFaceUri(faceUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
               redDetail.setFaceUrl(faceUrl_ori);
                resp = redDetailDao.save(redDetail);
                rp.setFaceUrl(faceUrl_ori);
                redPersonDao.save(rp);
               this.redService.updateFaceFeature(resp);
               } catch (Exception e) {
                   LOG.error("save redDetail error,imageId:"+imageId+" error:",e);
                   if(null != resp){
                	   this.redDetailDao.delete(resp.getId());
                	   this.redPersonDao.delete(rp.getId());
                   }
                   return new JsonObject("通信故障，请联系管理员！", 1002);
               }
               
               //通知给C++引擎
              ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_ADD.getValue(), resp.getId(), 0);
        }
        
        rp.setFaceUrl(this.redDetailDao.findByFromPersonId(rp.getId()).get(0).getFaceUrl());
        this.redPersonDao.save(rp);
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	}catch(Exception e){
    	LOG.error("create red person error,e:",e);
    	return new JsonObject("系统故障，请联系管理员！", 1002);
    	}
    }
    @RequestMapping(value = "/detail/person/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the red detail is successfully delete or not.")
    public JsonObject getDetailByPerson(@PathVariable("id") long id) throws Exception { 	
    	List<RedDetail> rList = redDetailDao.findByFromPersonId(id);
        return new JsonObject(rList);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the red detail is successfully delete or not.")
    public JsonObject deleteRedPerson(@PathVariable("id") long id) throws Exception { 	
    	List<RedDetail> rList = redDetailDao.findByFromPersonId(id);
    	if(null != rList){
    		for(RedDetail item:rList){
    			long rId = item.getId();
    			redDetailDao.delete(rId);
    			  //通知给C++引擎
                ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_DEL.getValue(), rId, 0);
    		}
    		
    	     List<Long> idList = rList.stream().map(item -> item.getId()).collect(Collectors.toList());
    	     featureService.deleteByFaceIds(idList);
    	}
    	redPersonDao.delete(id);
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the red detail is successfully delete or not.")
    public JsonObject getRedsByPersonId(@PathVariable("id") long id) throws Exception { 	
    	RedPerson redPerson = redPersonDao.findOne(id);
        return new JsonObject(redPerson);
    }
    @RequestMapping(value = "/person/update", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the red detail is successfully delete or not.")
    public JsonObject updateRedPersonInfo(@RequestBody @Valid RedDto redDto) throws Exception { 
    	try{
    		boolean status = ValidateUtil.validateResult(redDto);
            if(!status){
            	return new JsonObject("参数格式错误",1001);
            }
       	 
        RedPerson find =redPersonDao.findOne(redDto.getId()); 
       	List<Long> newList = new ArrayList<Long>();
       	if(null != redDto.getImageIds() && redDto.getImageIds().trim().length()>0){
       		for (String imageId : redDto.getImageIds().split(",")) {
       			newList.add(Long.valueOf(imageId));
       		}
       
        List<BigInteger> bList = redDetailDao.findRedDetailImageIdsByPersonId(redDto.getId());
        List<Long> curList = bList.stream().map(x->x.longValue()).collect(Collectors.toList());
        List<Long> removeIdList = CollectionUtil.remove(curList, newList);
       	List<Long> retainIdList = CollectionUtil.mixed(curList, newList);
       	retainIdList = CollectionUtil.remove(newList, retainIdList);
       	List<Long> detailList = new ArrayList<Long>();
       	for(long item : removeIdList){
       	    List<RedDetail> rList = redDetailDao.findByFromImageId(item);
       		redDetailDao.deleteByFromImageId(new Long(item).longValue());
       	    ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_DEL.getValue(), rList.get(0).getId(), 0);
       	    detailList.add(rList.get(0).getId());
       	}
       	
        featureService.deleteByFaceIds(detailList);
       	
       	
       	RedDetail rd = null;
       	for(long rId : retainIdList){
       	   RedDetail redDetail = new RedDetail();
         	   redDetail.setFromPersonId(redDto.getId());
         	   redDetail.setFromImageId(rId);
         	   String faceUrl_ori =((ImageInfo)_imageServiceItf.findById(Long.valueOf(rId))).getFaceUri();
              redDetail.setFaceUrl(faceUrl_ori);
              rd = redDetailDao.save(redDetail);
             this.redService.updateFaceFeature(rd);
             
             //通知给C++引擎
             ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_ADD.getValue(), rd.getId(), 0);
     
       	}
       	//更新红名单头像
       	List<RedDetail> dList = this.redDetailDao.findByFromPersonId(redDto.getId());
       	EntityAuditListener.RedPersonStatusMap.put(find.getId(),find.clone());
       	if(null != dList && !dList.isEmpty()){
       		find.setFaceUrl(dList.get(0).getFaceUrl());
       	}else{
       		find.setFaceUrl(null);
       	}
    	}
       	find.updateRedPerson(redDto);
        return new JsonObject(this.redPersonDao.save(find));
    	}catch(Exception e){
    		LOG.error("update red person error,e:",e);
    		 return new JsonObject("系统故障，请联系管理员！", 1002);
    		
    	}
    }
    
    @RequestMapping(value = "/display", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST",value = "分页显示红名单列表")
    public JsonObject displayBlackBank(@RequestBody @Valid RedDto redDto){
        JsonObject jo = null;
        try{
            jo = redService.findRedListByPage(redDto);
        }catch(Exception e){
            LOG.error("get red check records error:",e);
            jo = new JsonObject("系统故障，请联系管理员！", 1002);
        }
          return jo;
    }
    @RequestMapping(value = "/switch", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "查看红名单开关状态")
    public JsonObject getSwitch(){
    	List<IFaceConfig> switchList = (List<IFaceConfig>) ifaceConfigDao.findByConKey(GlobalConsts.IFACE_CONFIG_RED);
    	IFaceConfig rSwitch = null;
		if(null == switchList || switchList.isEmpty()){
			IFaceConfig rs = new IFaceConfig(GlobalConsts.IFACE_CONFIG_RED,0,"红名单开关");
			rSwitch = ifaceConfigDao.save(rs);
		}else{
			rSwitch = switchList.get(0);
		}
		GlobalConsts.redConfig= rSwitch;
		 return new JsonObject(rSwitch);
    	
    }

    @RequestMapping(value = "/switch/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the red detail info is successfully created or not.")
    public JsonObject switchRed(@PathVariable("type") long type) throws Exception {
    	 int result = 0;
    	if(type == 1){
    		//switch on
    		  result = ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_SWITCHON.getValue(), 0, 0);
    	}else{
    		 result = ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_SWITCHOFF.getValue(), 0, 0);
    	}
    	
    		if(type == 1){
    			GlobalConsts.redConfig = new IFaceConfig(GlobalConsts.IFACE_CONFIG_RED,1,"红名单开关");
    			saveSwitchToDB(1);
    		}else{
    			GlobalConsts.redConfig = new IFaceConfig(GlobalConsts.IFACE_CONFIG_RED,0,"红名单开关");
    			saveSwitchToDB(0);
    		}
    		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	
    }
    
    @RequestMapping(value = "/force/records", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST",value = "分页显示红名单强制搜索列表")
    public JsonObject displayMistakeRecords(@RequestBody @Valid RedParamDto redDto){
       return new JsonObject(forceDao.findByTimes(redDto.getStartTime(), redDto.getEndTime(),(redDto.getPage()-1)*redDto.getPageSize(),redDto.getPageSize()));
    }
    @RequestMapping(value = "/check/records", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST",value = "分页显示红名单审核列表")
    public JsonObject displayCheckRecords(@RequestBody @Valid RedParamDto redDto){
        JsonObject jo = null;
        try{
            jo = redService.findRedCheckByPage(redDto);
        }catch(Exception e){
            LOG.error("get red check records error:",e);
            jo = new JsonObject("系统故障，请联系管理员！", 1002);
        }
    	  return jo;
    }
    @RequestMapping(value = "/check/records/user", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST",value = "分页显示当前用户名下红名单审核列表")
    public JsonObject displayCheckRecordsUser(@RequestBody @Valid RedParamDto redDto){
        JsonObject jo = null;
    	UserInfo userInfo = CurUserInfoUtil.getUserInfo();
    	redDto.setUserName(userInfo.getLogin());
          try{
              jo = redService.findRedCheckByPage(redDto);
          }catch(Exception e){
              LOG.error("get red check records user error:",e);
              jo = new JsonObject("系统故障，请联系管理员！", 1002);
          }
          return jo;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/check/result/{result}/name/{name}/id/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "request by jinxin user")
    public String updateCheckRecords(@PathVariable("result") String result,@PathVariable("name") String name,@PathVariable("id") String id){
       String resultStr = null;
       RedCheckRecord rcr = null;
       boolean isProcessed = false;
       String fResult = null;
       String dateStr = null;
       String thanksStr = null;
        try{
               LOG.info("receive messge from jinxin id:"+id+",result:"+result);
                rcr = checkDao.findOne(Long.parseLong(id));
               if(null == rcr){
                   return new String("记录不存在!");
               }else{
                   String cResult = rcr.getResult();
                   if(!"未审核".equals(cResult)){
                       isProcessed = true;
                   }
                   dateStr = DateUtil.getformatDate(new Date());
                   redService.updateRedCheckFromJinxin(id, name, result,dateStr);
                   rcr = checkDao.findOne(Long.parseLong(id));
                   if(isProcessed){
                       if("误报".equals(rcr.getResult())){
                           fResult = "此照片不是我本人";
                           thanksStr = "谢谢您的确认!";
                       }else{
                           fResult = "此照片是我本人";
                           thanksStr = "谢谢您的确认,管理员将核实违规操作情况!";
                       }
                   }else{
                       if("1".equals(result)){
                           fResult = "此照片是我本人";
                           thanksStr = "谢谢您的确认,管理员将核实违规操作情况!";
                       }else if("2".equals(result)){
                           fResult = "此照片不是我本人";
                           thanksStr = "谢谢您的确认!";
                       }else{
                           fResult = "未审核";
                       }
                   }
               }
           }catch(Exception e){
               LOG.error("update check result from jinxin error;id:"+id+",e:",e);
               return new String("系统错误!");
           }
        if(isProcessed){
            //报警已处理
            resultStr = "此红名单预警已被处理,处理结果为:"+fResult+","+System.getProperty("line.separator")+"处理时间:"+DateUtil.getformatDate(rcr.getUpdated())+". "+thanksStr;
        }else{
            resultStr = "本次处理结果为:"+fResult+","+System.getProperty("line.separator")+"处理时间:"+dateStr+". "+thanksStr;
              
        }
        return new String(resultStr);
    }
    
    @RequestMapping(value = "/check/records/mycheck", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST",value = "查找比中我的记录")
    public JsonObject displayMyCheckRecords(@RequestBody @Valid RedParamDto redDto){
        JsonObject jo = null;
        UserInfo userInfo = CurUserInfoUtil.getUserInfo();
        UserInfo ui = userDao.findOne(userInfo.getId());
        redDto.setPolicePhone(ui.getLogin());
           redDto.setUserName(userInfo.getLogin());
           try{
               jo = redService.findUserCheckRecords(redDto);
           }catch(Exception e){
               LOG.error("get red check records mycheck error:",e);
               jo = new JsonObject("系统错误", 1002);
           }
             return jo;
    }
    @RequestMapping(value = "/check/record/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT",value = "查看单个红名单审核")
    public JsonObject displayCheckRecordsUser(@PathVariable("id") long id,@RequestBody @Valid RedParamDto redDto){
    	RedCheckRecord rcr = checkDao.findOne(id);
    	if(null == rcr){
    	 return new JsonObject(null);
    	}
    	UserInfo userInfo = CurUserInfoUtil.getUserInfo();
    	rcr.setCheckPerson(userInfo.getLogin());
    	rcr.updateRedCheck(redDto);
       return new JsonObject(checkDao.save(rcr));
    }
    
    @RequestMapping(value = "/force/record/add", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST",value = "添加强制搜索记录")
    public JsonObject addForceRecord(@RequestBody @Valid RedParamDto redDto){
    	try{
    		String reasonStr = null;
    		UserInfo userInfo = CurUserInfoUtil.getUserInfo();
    		long sId = Long.parseLong(redDto.getsId());    	
    		long rId = Long.parseLong(redDto.getrId());    	
    		RedDetail rd = redDetailDao.findOne(rId);
    		String rUrl = rd.getFaceUrl();    
    		String cmpPerson = redPersonDao.findOne(rd.getFromPersonId()).getName()+","+rUrl;
    		List<FaceInfo> fList = this.faceService.findByFromImageId(sId);
    		String sUrl = fList.get(0).getImageData();
    		long fId = fList.get(0).getId();
    		SearchReasonDto srd = GlobalConsts.searchReasonMap.get(userInfo.getId());
    		if(null != srd){
    			reasonStr = srd.getReasonDetail();
    		}
    		Long policeStationId = userInfo.getPoliceStationId();
    		String stationname = policestationDao.findOne(policeStationId).getStationName(); // 单位名称
    		String levelStr = this.roleRepository.findOne(userInfo.getRoleId()).getCnName();
    		String message = levelStr+userInfo.getLogin()+"检索了图片信息,"+sUrl;
    		RedForceRecord rfr = new RedForceRecord(message,userInfo.getLogin(),cmpPerson,stationname,reasonStr,fId,rId);
    		RedForceRecord resp = forceDao.save(rfr);
    		return new JsonObject(resp);
    	}catch(Exception e){
    		LOG.error("save red force log error,e:",e);
    	    return new JsonObject("系统故障，请联系管理员！", 1002);
    	}
    }
    
    private class SearchDataInfo{
        private long id;
        private String url;
        
        public SearchDataInfo(long id,String url){
            this.id = id;
            this.url = url;
        }
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        
    }
    
    private SearchDataInfo getDataByIdAndType(long id,int dataType){
                SearchDataInfo sdi = null;
        try {
                switch(dataType) {
                case 0: {
                   BlackDetail bd = blackDetailDao.findOne(id);
                   sdi = new SearchDataInfo(bd.getId(),bd.getImageData());
                   break;
                }
                case 1:
                case 2:{
                   FaceInfo fi = this.faceService.findOne(id);
                   sdi = new SearchDataInfo(fi.getId(),fi.getImageData());
                   break;
                }
                case 3: {
                    CidDetail cd = cidDetailRepository.findOne(id);
                    sdi = new SearchDataInfo(cd.getId(),cd.getImageData());
                    break;
                }
                case 4: {
                    JuZhuDetail jd = juZhuDetailRepository.findOne(id);
                    sdi = new SearchDataInfo(jd.getId(),jd.getImageData());
                    break;
                }
                case 5:
                case 6:
                default: {
                    OtherDetail od = otherDetailRepository.findOne(id);
                    sdi = new SearchDataInfo(od.getId(),od.getImageData());
                    break;               
                }
                }
            
        }catch (Exception e) {
            LOG.error("red record process, source id :"+id+" datatype:"+dataType +" 数据不存在！", e);
            throw new MsgException(Constants.error_face_null);
        }
        return sdi;
        
    }
    
    
    
    @RequestMapping(value = "/check/record/add", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST",value = "添加强制搜索记录")
    public JsonObject addCheckRecord(@RequestBody @Valid RedParamDto redDto){
    	try{
    		UserInfo userInfo = CurUserInfoUtil.getUserInfo();
    		long sId = Long.parseLong(redDto.getsId());    	
    		long rId = Long.parseLong(redDto.getrId());    	
    		RedDetail rd = redDetailDao.findOne(rId);
    		String rUrl = rd.getFaceUrl();    
    		String cmpPerson = redPersonDao.findOne(rd.getFromPersonId()).getName()+","+rUrl;
    		String sUrl = null;
    		int dataType;
    		long fId;
    		   if(GlobalConsts.search_type_upload == redDto.getSearchType()){
                   List<FaceInfo> fList = this.faceService.findByFromImageId(sId);
                  FaceInfo fi = fList.get(0);
                  sUrl = fi.getImageData();
                  fId = fi.getId();
                  dataType = 1;
               }else{
                   SearchDataInfo sdi = getDataByIdAndType(sId,redDto.getType());
                   sUrl = sdi.getUrl();
                   fId = sdi.getId();
                   dataType = redDto.getType();
               }
    		Long policeStationId = userInfo.getPoliceStationId();
    		String stationname = policestationDao.findOne(policeStationId).getStationName(); // 单位名称
    		String levelStr = this.roleRepository.findOne(userInfo.getRoleId()).getCnName();
    		String message = levelStr+userInfo.getLogin()+"申请检索,"+sUrl;
    		RedCheckRecord rcr = new RedCheckRecord(message,userInfo.getLogin(),cmpPerson,stationname,GlobalConsts.RED_CHECK_RESULT_WAIT,fId,rId,dataType);
    		RedCheckRecord resp = checkDao.save(rcr);
    		//发送警信给警员
    		String phone = redPersonDao.findOne(rd.getFromPersonId()).getPolicePhone();
    		JinxinThread thread = new JinxinThread(resp.getId(),phone,sUrl);
    		thread.start();
    		return new JsonObject(resp);
    	}catch(Exception e){
    		LOG.error("save red check log error,e:",e);
    	    return new JsonObject("系统故障，请联系管理员！", 1002);
    	}
    }
    
    private class JinxinThread extends Thread{
        private long id;
        private String phone;
        private String sUrl;
        public JinxinThread(long id,String phone,String sUrl){
            this.id = id;
            this.phone = phone;
            this.sUrl = sUrl;
        }
       
        @Override
        public void run() {
                JinxinUtil.sendJinxinMessage(id, phone, sUrl);
        }
        
    }
    @RequestMapping(value = "/check/record/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "查询单条红名单审核记录")
    public JsonObject getCheckRecord(@PathVariable("id") long id){
    	try{
    			RedCheckRecord rcr = checkDao.findOne(id);
    			if(null == rcr){
    				return new JsonObject(null);	
    			}
    			
    			String rUrl = rcr.getCmpPerson().split(",")[1].trim();
    			String sUrl = rcr.getMessage().split(",")[1].trim();
    			
        		rcr.setrUrl(rUrl);
        		rcr.setsUrl(sUrl);
        		return new JsonObject(rcr);
    	}catch(Exception e){
    		LOG.error("get red check log error,e:",e);
    	    return new JsonObject("系统故障，请联系管理员！", 1002);
    	}
    }
    
    
 	@RequestMapping(method = RequestMethod.POST, value = "/zip/key/{key}", consumes = MediaType.MULTIPART_FORM_DATA)
 	@ApiOperation(httpMethod = "POST", value = "Response a string describing invoice' picture is successfully uploaded or not.")
 	public @ResponseBody JsonObject handleSingleZipFileUpload(@RequestParam(value = "file") MultipartFile file, @PathVariable("key") int key) throws Throwable {
 		JsonObject _unzipOutput = null;
 		if (!file.isEmpty()) {
 			_unzipOutput = this.unZipIt(file, key);
 		} else {
 			LOG.error("You failed to upload " + file.getName() + " because the file was empty.");
 		}
 		return new JsonObject(_unzipOutput);
 	}
 	@RequestMapping(method = RequestMethod.POST, value = "/zip/progress/{key}")
	@ApiOperation(httpMethod = "POST", value = "上传图片显示进度")
	public @ResponseBody JsonObject handleProcessZipUpload(@PathVariable("key") int key) {
		return new JsonObject(GlobalConsts.fileUploadMap.get(key));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/zip/cancel/{key}")
	@ApiOperation(httpMethod = "POST", value = "取消导入")
	public @ResponseBody JsonObject cancelZipUpload(@PathVariable("key") int key) {
		ExcelProcessInfo process = GlobalConsts.fileUploadMap.get(key);
		process.setImportState(false);
		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/zip/upload/eDownload/{key}")
	@ApiOperation(httpMethod = "POST", value = "导出批量布控错误图片")
	public @ResponseBody JsonObject handleDownloadErrorImages(@PathVariable("key") int key) {
		String returnPath = null;
		String randPath = (String) GlobalConsts.fileUploadMap.get(key).getDetailMap().get("random");
		try {
			returnPath = FileUtil.compressZip(randPath, "errorImage",propertiesBean.getIsJar());
		} catch (Exception e) {
			LOG.error("zip compress error,randPath:" + randPath, e);
			return new JsonObject(e.getMessage(), 1001);
		}
		return new JsonObject(returnPath);
	}

	/**
	 * Unzip it
	 *
	 * @param file
	 * input zip file
	 * @param key
	 *  random num
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public JsonObject unZipIt(MultipartFile file, int key) throws Throwable {
		UploadZipMessage zMessage = new UploadZipMessage();// 记录上传ZIP执行结果和错误信息
		Map<String, String> suffixMap = new HashMap<String, String>();// 记录文件后辍名
		String errorMessage = null;// 记录错误信息
		boolean xlsRecord = false;// xls解析标记
		String fullFileName = null;
		long userId = 0;
		String xlsName = null;
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserInfo ui = (UserInfo) auth.getPrincipal();
			if (null != ui) {
				userId = ui.getId();
			}
			CurUserInfoUtil.setAuth(auth);
			LOG.info("RedController,login user id:" + userId);
			byte[] bytes = file.getBytes();
			String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
			String randomStr = String.valueOf(Math.round(Math.random() * 1000000));
			final String fileNameAppendix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "_"
					+ randomStr;

			fullFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix + "." + fileExt;

			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullFileName)));
			stream.write(bytes);
			stream.close();
			LOG.info("RedController,Upload (zip)file success." + fullFileName);

			// create output directory is not exists
			File folder = new File(
					FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + fileNameAppendix + "/validate");
			FileUtil.checkFileExist(folder);

			List<PersonRedXLS> xlsItems = null;// xls数据总和
			List<PersonRedXLS> filterList = null;// xls过滤空xls后集合
			File zFile = new File(fullFileName);
			ZipFile zip = new ZipFile(zFile);
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				if (ze.isDirectory()) {
					return new JsonObject("ZIP文件格式不正确", 1001);
				} else {
					InputStream is = null;
					FileOutputStream fos = null;
					try {
						if (!ze.getName().startsWith("_") && !ze.getName().startsWith(".")) {
							String fileName = ze.getName();
							String[] sStr = fileName.split("\\.");
							suffixMap.put(sStr[0], sStr[1]);

							is = zip.getInputStream(ze);
							File newFile = new File(FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator
									+ fileNameAppendix + File.separator + fileName);
							// new File(newFile.getParent()).mkdirs();
							FileUtil.writeStreamToFile(is, newFile);
							String abName = newFile.getAbsoluteFile().getName();
							if (abName.contains(Constants.xls_NAME_SUFFIX_ZIP)) {
								if (xlsRecord) {
									errorMessage = "存在多个excel文件";
									return new JsonObject(errorMessage, 1002);
								}
								xlsRecord = true;
								xlsName = abName;
								xlsItems = ExOM.mapFromExcel(newFile).toObjectOf(PersonRedXLS.class).map();
							}
						}
					} catch (Exception e) {
						LOG.error(e.getMessage());
						return new JsonObject(e.getMessage(), 1001);
					}
				}
			}
			if (!xlsRecord) {
				return new JsonObject("不存在excel文件", 1001);
			}

			// 图片处理完了再处理xls
			// 初始化校验工厂
			try {
				Map<Integer, List<ValidateResult>> validateMap = new HashMap<Integer, List<ValidateResult>>();
				String path = FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + fileNameAppendix
						+ File.separator;
				int a = 0;

				filterList = filterNullxlsitem(xlsItems);
				LOG.info("save zip filter complete size:" + filterList.size() + " key:" + key);
				for (PersonRedXLS item : filterList) {
					a++;
					List<ValidateResult> result = AnnotationValidator.validate(item, path, suffixMap);
					validateMap.put(a, result);
				}

				Map<Integer, List<ValidateResult>> returnMap = new HashMap<Integer, List<ValidateResult>>();
				int total = FileUploadUtil.processexcelValidateResult(validateMap, returnMap, path + "validate/excel.txt", a);

				if (total > 50) {
					// 错误超过50条
					Map<String, Object> rMap = new HashMap<String, Object>();
					rMap.put("total", total);
					String txtPath = FileUtil.compressZip(fileNameAppendix, "validate",propertiesBean.getIsJar());
					rMap.put("url", txtPath);
					return new JsonObject(rMap, 2000);
				} else if (total > 0 && total <= 50) {
					return new JsonObject(returnMap, 2001); // 错误小于50条
				}
			} catch (Exception e) {
				LOG.error("validate error:", e);
				return new JsonObject("校验出现异常", 1004);
			}

			// start import
			ExcelProcessInfo processInfo = new ExcelProcessInfo(GlobalConsts.process_red);
			processInfo.setTotalSize(filterList.size());
			processInfo.getDetailMap().put("random", fileNameAppendix);
			GlobalConsts.fileUploadMap.put(key, processInfo);
			int count = 0;
			ThreadPoolExecutor threadPool = new ThreadPoolExecutor(ThreadSetting.getBlackThreadsNum(), 24, 1,
					TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE),
					new ThreadPoolExecutor.CallerRunsPolicy());
			List<PersonRedXLS> tList = new ArrayList<PersonRedXLS>();

			for (PersonRedXLS item : filterList) {
				count++;
				tList.add(item);
				if (count % 10 == 0 || count == filterList.size()) {
					int num = count;
					List<Object> tempList = new ArrayList<Object>();
					tempList.addAll(tList);
					ImportThreadsParams params = new ImportThreadsParams(tempList, suffixMap, ui,
							fileNameAppendix, key, num, xlsName);
					ExcelImportThread thread = new ExcelImportThread(params);
					threadPool.submit(thread);
					tList.clear();
				}
				// processUZipImages(item, fileNameAppendix,
				// suffixMap,bankId,key);
			}

			while (true) {
				Thread.sleep(1000);
				if (threadPool.getActiveCount() == 0) {
					LOG.info("all  red import thread finish");
					long total = processInfo.getTotalSize();
					long success = processInfo.getSuccessNum();
					processInfo.setFailedNum(total - success);
					processInfo.setImportState(false);
					break;// 所有线程执行完
				}
			}

			LOG.info("unZipIt Done!");
		} catch (Exception ex) {
			LOG.error("unZip error:", ex);
			zMessage.getErrorList().add("其它：错误日志:" + ex.getMessage());
			return new JsonObject("布控出现异常", 1005);
		} finally {
			try {
				ExcelProcessInfo pInfo = GlobalConsts.fileUploadMap.get(key);
				if (null != pInfo) {
					zMessage.setUserId(userId);
					zMessage.setSucNum(Integer.parseInt(String.valueOf(pInfo.getSuccessNum())));
					zMessage.setFailNum(Integer.parseInt(String.valueOf(pInfo.getFailedNum())));
					MqttUtil.setMqtt(zMessage,MqttTopicNames.Message.getValue());
					// 将批量布控的完成情况记录到t_user_business_api表中
					MarkInfo newMarkInfo = new MarkInfo();
					// List<MarkInfo> oldMarkInfoList =
					// markInfoDao.findByUserId(userId);
					// MarkInfo oldMarkInfo = oldMarkInfoList.get(0);
					// newMarkInfo.setUserId(userId);
					// String oldS = oldMarkInfo.getInfo();
					// String newS = oldS.substring(0,
					// oldS.length()-1)+",batchMessage:{id:"+userId+",success:"+pInfo.getSuccessNum()+",fail:"+pInfo.getFailedNum()+"}}";
					// newMarkInfo.setInfo(newS);
					// markInfoDao.save(newMarkInfo);

					JSONObject jsStr = null;
					String oldS = EntityAuditListener.markInfoMap.get(userId);
					if (oldS != null) {
						jsStr = JSONObject.fromObject(oldS);
					} else if (oldS == null) {
						jsStr = JSONObject.fromObject("{\"login\":true}");
					}
					jsStr.put("batchMessage", "success:" + pInfo.getSuccessNum() + ",fail:" + pInfo.getFailedNum());
					String info = jsStr.toString();

					String newS = info;
					EntityAuditListener.markInfoMap.put(userId, newS);
					Log.info("批量布控完成操作的记录： {} ", newS);

				}
			} catch (Exception e) {
				Log.info("批量布控完成操作的记录异常", e);
			}

		}

		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	}
	
	public class ExcelImportThread implements Runnable {
		private ImportThreadsParams params;

		public ExcelImportThread() {

		}

		public ExcelImportThread(ImportThreadsParams params) {
			this.params = params;
		}

		@Override
		public void run() {
			LOG.info("red thread running size:" + params.getCount() + " key:" + params.getKey() + " threadname:"
					+ Thread.currentThread().getName());
			try {
				for (Object item : params.getXlsList()) {
					PersonRedXLS pb = (PersonRedXLS) item;
					ExcelProcessInfo process = GlobalConsts.fileUploadMap.get(params.getKey());
					if (process.isImportState()) {
						processUZipImages(pb, params);
					}
				}
			} catch (Exception e) {
				LOG.error("red  run thread error threadname:" + Thread.currentThread().getName() + " error:", e);
			}

			// 记录导入状态
			ExcelRecord er = new ExcelRecord(params.getExcelName(), params.getDirPath(), params.getCount(),
					params.getUi().getId());
			recordDao.save(er);
		}

		private String formatImage(String source, String formatStr) throws Exception {
			String format4dbBase = FilenameUtils.getBaseName(source) + "_format" + "." + formatStr;// FilenameUtils.getExtension(source),always
																									// keep
																									// JPG
			String format4db = FileUtil.getUploads(propertiesBean.getIsJar()) + format4dbBase;
			String formatFullPath = format4db;
			String formatFullPathSrc = source;
			ConvertCmd cmd = new ConvertCmd();
			File thumbnailFile = new File(formatFullPath);
			if (!thumbnailFile.exists()) {
				IMOperation op = new IMOperation();
				op.addImage(formatFullPathSrc);
				op.format(formatStr);
				op.addImage(formatFullPath);
				cmd.run(op);
				LOG.info("ImageMagick success result(with format):" + formatFullPath);
			}
			return format4dbBase;
		}

		private Map<String, String> imageFileOperation(MultipartFile file) {
			LOG.info("imageFileOperation with file:" + file.toString());
			Map<String, String> _imageMagickOutput = new HashMap<String, String>();
			String dbFileName = null;
			String fullFileName = null;
			try {
				byte[] bytes = file.getBytes();
				String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
				LOG.info("fileExt:" + fileExt);
				String randomStr = String.valueOf(Math.round(Math.random() * 1000000));
				String fileNameAppendix
				// = "temp" + "." + fileExt;
						= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "_" + randomStr + "."
								+ fileExt;
				LOG.info("fileNameAppendix:" + fileNameAppendix);
				dbFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix;
				LOG.info("dbFileName:" + dbFileName);
				fullFileName = dbFileName;
				LOG.info("imageFileOperation with fullFieldName:" + fullFileName);

				LOG.info("{} begin get angle", file.getOriginalFilename());
				File f = new File(fullFileName);
				int angle = ImageUtil.getAngle(file.getInputStream());
				LOG.info("{} angle is {}", file.getOriginalFilename(), angle);
				if (angle != 0) {
					BufferedImage img = ImageUtil.rotateImage(bytes, angle, file.getOriginalFilename());
					try {
						ImageIO.write(img, "jpg", f);
						img.flush();
					} catch (IOException e) {
						LOG.info("{} after rotateImage write ImageIO exception:", file.getOriginalFilename());
					}
				} else {
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
					stream.write(bytes);
					stream.close();
				}

				// shi test 代替format操作
				/*
				 * String ff[] = fullFileName.split("\\."); String formatName =
				 * ff[0]+"_format.jpg"; BufferedOutputStream stream1 = new
				 * BufferedOutputStream(new FileOutputStream(new
				 * File(formatName))); stream1.write(bytes); stream1.close();
				 */
				// shi test 代替format操作
				LOG.info("Upload (image)file success." + fullFileName);
				String format4dbBase = this.formatImage(fullFileName, "jpg");
				_imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);
				return _imageMagickOutput;
			} catch (Exception e) {
				LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
			}
			return _imageMagickOutput;
		}

		private ImageInfo imageFileHandler(MultipartFile file) throws Exception {
			LOG.info("imageFileHandler with file:" + file.toString());
			Map<String, String> _imageMagickOutput = this.imageFileOperation(file);
			ImageInfo imageInfoResp = new ImageInfo();
			String fileName = _imageMagickOutput.get(ImageSize.ori.toString());
			String imageUrl = ImageInfoHelper.getRemoteImageUrl(fileName, propertiesBean.getIsJar());
			ImageInfo imageInfo = new ImageInfo();
			imageInfo.setUri(imageUrl);
			imageInfo.setTime(new Date());
			String faceUri = ImageInfoHelper.getRemoteFaceUrl(imageUrl, propertiesBean.getIsJar());
			imageInfo.setFaceUri(faceUri);
			imageInfoResp = _imageServiceItf.save(imageInfo);
			LOG.info("ImageMagick output success: " + imageInfoResp);
			return imageInfoResp;
		}

		@SuppressWarnings("unchecked")
		private boolean processRedDetail(ImportThreadsParams params, RedPerson person,
				MultipartFile multipartFile, String sourceName, PersonRedXLS item) {
			ExcelProcessInfo pi = GlobalConsts.fileUploadMap.get(params.getKey());
			long imageId = 0;
			RedDetail redDetailResp = null;
			ImageInfo imageInfoResp = null;
			try {
				String ePath = FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + params.getDirPath()
						+ File.separator + "errorImage/";

				try {
					imageInfoResp = this.imageFileHandler(multipartFile);
				} catch (Exception e) {
					LOG.error("imageFileHandler error:", e);
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					if (imageInfoResp != null) {
						recordNotNormalPic(ePath + "未知原因", imageInfoResp.getUri(), descDir);
					}
					pi.getDetailMap().put(4, ((Integer) (pi.getDetailMap().get(4))) + 1);
					throw new Exception("图片格式化异常");
				}
				imageId = imageInfoResp.getId();
				// 条件判断图片是否只有一个人脸
				IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
				List<T_IF_FACERECT> faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(),-1);

				if (faceList == null) {
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					recordNotNormalPic(ePath + "未知原因", imageInfoResp.getUri(), descDir);
					pi.getDetailMap().put(4, ((Integer) (pi.getDetailMap().get(4))) + 1);
					throw new Exception("图片处理引擎异常");
				} else if (faceList.size() == 0) {
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					recordNotNormalPic(ePath + "无人脸", imageInfoResp.getUri(), descDir);
					pi.getDetailMap().put(1, ((Integer) (pi.getDetailMap().get(1))) + 1);
					throw new Exception("图片不存在人脸");
				} else if (faceList.size() > 1) {
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					recordNotNormalPic(ePath + "多个人脸", imageInfoResp.getUri(), descDir);
					pi.getDetailMap().put(2, ((Integer) (pi.getDetailMap().get(2))) + 1);
					throw new Exception("图片人脸数大于1");
				}else {
					imageInfoResp.setFaces(1);
				}
				_imageServiceItf.save(imageInfoResp);
				// 条件判断图片是否只有一个人脸

				RedDetail redDetail = new RedDetail();
				redDetail.setFromPersonId(person.getId());
				redDetail.setFromImageId(imageId);
				String faceUrl = imageInfoResp.getFaceUri();
				redDetail.setFaceUrl(faceUrl);
				redDetailResp = redDetailDao.save(redDetail);
				// 调用C++
				boolean updateFaceFeatureResult = redService.updateFaceFeature(redDetailResp);
				if (!updateFaceFeatureResult) {
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					recordNotNormalPic(ePath + "其它原因", imageInfoResp.getUri(), descDir);
					pi.getDetailMap().put(4, ((Integer) (pi.getDetailMap().get(4))) + 1);
					throw new Exception("update face feature error");
				}
			       ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_REDLIST.getValue(), ERedListIoctrlType.REDLIST_IOCTRL_ADD.getValue(), redDetailResp.getId(), 0);
			} catch (Throwable e) {
				pi.incrementFailedImgNumWithLock();
				if (null != redDetailResp) {
					redDetailDao.delete(redDetailResp.getId());
				}
				LOG.error("process redDetail error:", e);
				return false;
			}
			pi.incrementSuccessImgNumWithLock();
			return true;
		}


		private void recordNotNormalPic(String folder, String url, String descDir) {
			try {
				File file = new File(folder);
				FileUtil.checkFileExist(file);
				FileUtil.copyUrl(url, folder + descDir);
			} catch (Exception e) {
				LOG.error("record not normal pic error, url:" + url, e);
			}

		}

		private void processUZipImages(PersonRedXLS item, ImportThreadsParams params) {

			ExcelProcessInfo pi = GlobalConsts.fileUploadMap.get(params.getKey());
			// RedPerson person = processRedPerson(item,
			// params.getBankId(),params.getKey(),params.getUi());
			RedPerson person = processRedPerson(item, params);
			LOG.info("save redPerson complete key:" + params.getKey() + " item name:" + item.getName());
			if (null != person) {
				for (int i = 1; i < 5; i++) {
					String iName = item.getImageName(i);
					if (iName != null && iName.toString().length() != 0) {
						pi.setImageTotal(pi.getImageTotal() + 1);
						MultipartFile multipartFile = createFileFromExcelName(params.getExtMap(), params.getDirPath(),
								iName);
						LOG.info("create multipartFile complete red person name:" + person.getName() + " imageName:"
								+ iName + " key:" + params.getKey());
						// processRedDetail(params.getExtMap(),person,multipartFile,params.getKey(),params.getDirPath(),iName);
						processRedDetail(params, person, multipartFile, iName, item);
					}
				}
				
				List<RedDetail> redList = redDetailDao.findByFromPersonId(person.getId());
				if (null == redList || redList.isEmpty()) {
					pi.incrementFailedNumWithLock();
					redPersonDao.delete(person.getId());
				} else {
					person.setFaceUrl(redList.get(0).getFaceUrl());//更新红名单人头像
					redPersonDao.save(person);
					pi.incrementSuccessNumWithLock();
				}
				// this.auditLogRepository.deleteByObjectAndObjectId(GlobalConsts.T_NAME_PERSON_DETAIL,
				// id); // 为什么之前要把这条日志删了呢
			}
		}

		private MultipartFile createFileFromExcelName(Map<String, String> extMap, String dirName, String name) {
			if (!StringUtils.isEmpty(name)) {
				try {
					byte[] content = null;
					String fm = null;

					String suffix = extMap.get(name);
					if (null != suffix) {
						Path path = Paths.get(FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + dirName
								+ File.separator + name + "." + suffix);
						try {
							content = Files.readAllBytes(path);
							fm = "." + suffix;
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						for (String format : GlobalConsts.picFormatList) {
							Path path = Paths.get(FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator
									+ dirName + File.separator + name + format);
							try {
								content = Files.readAllBytes(path);
								fm = format;
								break;
							} catch (Exception e) {
								continue;
							}
						}
					}

					File imgFile = new File(FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + dirName
							+ File.separator + name + fm);
					System.out.println("imgFile:" + imgFile.getAbsolutePath());

					String tName = "temp.jpg";
					String originalFileName = imgFile.getName();
					String contentType = "image/jpeg";

					MultipartFile multipartFile = new MockMultipartFile(tName, originalFileName, contentType, content);
					return multipartFile;
				} catch (Exception e) {
					LOG.error("create multipartfile error,name:" + name, e);

					return null;
				}
			}
			return null;
		}

		private RedPerson processRedPerson(PersonRedXLS item, ImportThreadsParams params) {
			LOG.info("PersonRedXLS:" + item.toString());
			RedPerson redPersonResp = null;
			try {
				RedPerson redPerson = new RedPerson();
				redPerson.setSex(item.getGender());
				redPerson.setName(item.getName());
				redPerson.setPolicePhone(item.getPolicePhone());
				redPerson.setRemarks(item.getRemark());
				redPersonResp = redPersonDao.save(redPerson);
				return redPersonResp;

			} catch (Exception e) {
				LOG.error(" save persondetail error", e);
				ExcelProcessInfo pi = GlobalConsts.fileUploadMap.get(params.getKey());
				pi.setFailedNum(pi.getFailedNum() + 1);
				if (null != redPersonResp && 0 != redPersonResp.getId()) {
					redPersonDao.delete(redPersonResp.getId());
				}
				return null;
			}
		}

	}
	private List<PersonRedXLS> filterNullxlsitem(List<PersonRedXLS> sourceList) {
		List<PersonRedXLS> returnList = new ArrayList<PersonRedXLS>();
		if (sourceList != null && !sourceList.isEmpty()) {
			for (PersonRedXLS item : sourceList) {
				if (item.isExist()) {
					returnList.add(item);
				}
			}
		}
		return returnList;
	}
    
    private void saveSwitchToDB(int state){
    	List<IFaceConfig> switchList = (List<IFaceConfig>) ifaceConfigDao.findByConKey(GlobalConsts.IFACE_CONFIG_RED);
		if(null == switchList || switchList.isEmpty()){
			IFaceConfig rs = new IFaceConfig(GlobalConsts.IFACE_CONFIG_RED,state,"红名单开关");
			ifaceConfigDao.save(rs);
		}else{
			IFaceConfig rs = switchList.get(0);
			rs.setConValue(state);
			ifaceConfigDao.save(rs);
		}
		LOG.info("红名单开关设置状态为："+state);
    }
}