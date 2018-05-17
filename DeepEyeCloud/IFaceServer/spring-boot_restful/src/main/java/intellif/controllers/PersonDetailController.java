package intellif.controllers;

import intellif.audit.EntityAuditListener;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.AreaAndBlackDetailDao;
import intellif.dao.AuditLogDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.UserDao;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.BankDisplayInfo;
import intellif.dto.JsonObject;
import intellif.dto.PersonDto;
import intellif.dto.PersonFullDto;
import intellif.dto.PersonQueryDto;
import intellif.service.AlarmServiceItf;
import intellif.service.AreaAndBlackDetailServiceItf;
import intellif.service.AuditLogInfoServiceItf;
import intellif.service.BlackBankServiceItf;
import intellif.service.BlackDetailServiceItf;
import intellif.service.BlackFeatureServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.PoliceStationServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.SolrServerItf;
import intellif.service.UserAreaServiceItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.BlackBankServiceImpl;
import intellif.service.impl.PersonDetailServiceImpl;
import intellif.service.impl.PoliceStationServiceImpl;
import intellif.settings.ImageSettings;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FileUtil;
import intellif.utils.PageDto;
import intellif.database.entity.BatchInsertDto;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.exception.MsgException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class PersonDetailController.</h1>
 * The PersonDetailController which serves request of the form /person/detail and returns a JSON object representing an instance of PersonDetail.
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
//@RequestMapping("/intellif/person/detail")
@RequestMapping(GlobalConsts.R_ID_PERSON_DETAIL)
public class PersonDetailController {
    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(PersonDetailController.class);
    
//    private static final int DEFAULT_PAGE_SIZE = 40;

    // Auto wire an object of type PersonDetailDao
    @Autowired
    private PersonDetailDao _personDetailDao;
    @Autowired
    private PersonDetailServiceItf _personDetailService;
    /*@Autowired
    private ImageInfoDao _imageInfoDao;*/
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private BlackDetailServiceItf blackDetailService;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private CameraServiceItf cameraService;
    @Autowired
    private CameraAndBlackDetailDao _cameraAndBlackDetailRepository;
    @Autowired
    private AreaAndBlackDetailServiceItf areaAndBlackDetailService;
    @Autowired
    private SolrServerItf _solrService;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private PoliceStationServiceImpl policeStationService;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private UserDao userRepository;
    @Autowired
    private BlackBankServiceImpl blackBankService;
    @Autowired
    private PersonDetailServiceImpl personDetailService;
    @Autowired
    private AlarmServiceItf alarmService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BlackFeatureServiceItf featureService;
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    AuditLogInfoServiceItf auditLogService;
    @Autowired
    UserAreaServiceItf userAreaService;

    //PersonDetail related
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the person detail is successfully created or not.")

    public JsonObject createPerson(@RequestBody @Valid PersonDto personDto) throws Exception {
    	try{
        if (null == personDto.getImageIds() || "".equals(personDto.getImageIds().trim())){
        	return new JsonObject("请先上传嫌疑人图片..", 1001);
        }
        String owner = personDto.getOwner();
        if (owner == null || owner.equals("")) {
        	personDto.setOwner(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLogin());
		} else {
			personDto.setOwner("警务云" + owner);
		}
		long ownerStationId = (((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
		PoliceStation ps = policeStationService.findById(ownerStationId);
        personDto.setOwnerStation((policeStationService.findById(ownerStationId)).getStationName());
        String bankIdAutority = ","+this._userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE)+",";

         String stationId = personDto.getStationIds();
         if(!StringUtils.isNotBlank(stationId)){
             return new JsonObject("参数有误:没有门店ID", 1001);
         }
         String uSql = " user_id ="+CurUserInfoUtil.getUserInfo().getId()+" and area_id ="+stationId;
         
         if(CollectionUtils.isEmpty(userAreaService.findByFilter(uSql))){
             return new JsonObject("当前用户没有该门店权限", 1001);
         }
         
         String fSql = " station_id = "+stationId;
         List<BlackBank> bankList = blackBankService.findByFilter(fSql);
         if(CollectionUtils.isEmpty(bankList)){
             return new JsonObject("系统错误:找不到库", 1001);
         }
         personDto.setBankId(bankList.get(0).getId());
      
        PersonDetail newPerson = new PersonDetail(personDto);
        
        BlackBank bank = blackBankService.findById(personDto.getBankId());
        newPerson.setType(bank.getListType());//不同类型的黑白名单库对应不同类型的黑白名单人员
        
        PersonDetail personDetail = (PersonDetail) personDetailService.save(newPerson);
        
          // Map<String,List<Long>> paramMap = _personDetailService.processParamDataToMap(personDto.getAreaList());
               boolean isFirst = true;
           for (String imageId : personDto.getImageIds().split(",")) {
                 
                 BlackDetail blackDetail = new BlackDetail();
                 blackDetail.setFromPersonId(personDetail.getId());
                 blackDetail.setBankId(personDto.getBankId());
                 Long iId = 0l;
                 String copyUrl = null;
                 if(imageId.startsWith("face")){
                     //图片来自于face表小图
                    FaceInfo fi = faceService.findOne(Long.valueOf(imageId.substring(4)));
                    iId = fi.getFromImageId();
                    copyUrl = fi.getImageData();
                 }else{
                     iId = Long.valueOf(imageId);
                     ImageInfo imageInfo = (ImageInfo) this._imageServiceItf.findById(iId);                         //////////////////////////// v1.1.0
                     copyUrl = imageInfo.getUri();
                 }
                 blackDetail.setFromImageId(iId);
                 String faceUrl_ori =((ImageInfo)_imageServiceItf.findById(blackDetail.getFromImageId())).getFaceUri();
                 String faceUrl_store = ImageSettings.getStoreRemoteUrl() + new File(FileUtil.getStoreFaceUri(faceUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
                 blackDetail.setImageData(faceUrl_store);
                 if(isFirst){
                     newPerson.setPhotoData(faceUrl_store);
                     personDetailService.save(newPerson);
                 }
//                 personDetail.setPhotoData(faceUrl_store);
                 BlackDetail resp = (BlackDetail) blackDetailService.save(blackDetail);
                 
                 try {
                     blackDetailService.updateFaceFeature(resp,FileUtil.wrapProxyUrl(copyUrl));
                 } catch (Exception e) {
                 	LOG.error("异常：", e);
                 	StackTraceElement  ste = Thread.currentThread() .getStackTrace()[1];
                     LOG.error("Class "+ste.getClassName()+" Method "+ste.getMethodName()+"：连接引擎服务器更新特征值失败！", e);
                     blackDetailService.delete(resp.getId());
                     personDetailService.delete(personDetail.getId());
                     return new JsonObject("通信故障，请联系管理员！", 1002);
                 }
                  isFirst = false;           
                 //区域布控
             	 List<AreaAndBlackDetail> aList = new ArrayList<AreaAndBlackDetail>();
                 String sIds = personDto.getStationIds();
                 if(StringUtils.isNotBlank(sIds)){
                     String[] ss = sIds.split(",");
                     for(String aId : ss){
                         aList.add(new AreaAndBlackDetail(Long.parseLong(aId), resp.getId()));
                     }
                     areaAndBlackDetailService.batchSave(aList);
                 }
                
             }
		
        new Thread(){
            @Override
            public void run() {
                _personDetailService.refreshPersonOfUpdate(personDetail.getId());
            }
        }.start();
        return new JsonObject(personDetail);
		}catch(Exception e){
    		e.printStackTrace();
    		LOG.error(e.getMessage());
    		return null;
    	}
        
        
      
    }



    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the person detail id is successfully get or not.")
    public JsonObject getPerson(@PathVariable("id") long id) {
    	String authority = ","+_userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
    	PersonDetail pd = personDetailService.findById(id);
    	if(null == pd){
    		return new JsonObject("数据已被删除或不存在！", 1001);
    	}
    	long bankId = pd.getBankId();
    	if(authority.indexOf(","+bankId+",")>=0){
    		List<PersonDetail> pList = new ArrayList<PersonDetail>();
    		pList.add(pd);
    		return new JsonObject(pList);
    	}else{
    		return new JsonObject("无该人员库的查看权限！", 1001);
    	}
    }

    @RequestMapping(value = "/recorders/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "")
    public JsonObject updateRecoders(@PathVariable("id") long id) throws Exception {
        int similarSuspect = 0;
        int inStation = 0;
        int history = 0;
    	String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        String filterSql = "id = "+id+" AND bank_id in ("+ authority + ")";
        List<PersonDetail> findList = personDetailService.findByFilter(filterSql);
        if(findList.size()>0) {
        	PersonDetail find = findList.get(0);
        	EntityAuditListener.statusMap.put(find.getId(), find.clone());
        	String fSql = " from_person_id = "+id;
        	List<BlackDetail> blackList = blackDetailService.findByFilter(fSql);
        	try {
        		for (BlackDetail blackDetail : blackList) {
        			similarSuspect += (_solrService.searchFaceByBlackId(blackDetail.getId(), GlobalConsts.DEFAULT_SCORE_THRESHOLD, GlobalConsts.BLACK_BANK_TYPE).size() - 1);
        			inStation += _solrService.searchFaceByBlackId(blackDetail.getId(), GlobalConsts.DEFAULT_SCORE_THRESHOLD, GlobalConsts.IN_STATION_TYPE).size();
        			history += _solrService.searchFaceByBlackId(blackDetail.getId(), GlobalConsts.DEFAULT_SCORE_THRESHOLD, GlobalConsts.IN_CAMERA_TYPE).size();
        		}
        	} catch (Exception e) {
        		// TODO: handle exception
        	}
        	find.setSimilarSuspect(similarSuspect);
        	find.setInStation(inStation);
        	find.setHistory(history);
        	return new JsonObject(personDetailService.save(find));
        } else {
            return new JsonObject("对不起，您没有更新权限！", 1001);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  person detail is successfully updated or not.")
    public JsonObject updatePerson(@PathVariable("id") long id, @RequestBody @Valid PersonDetail personDetail) throws Exception {
    	String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
    	try{
    	    String filterSql = "id = "+id+" AND bank_id in ("+ authority + ")";
            List<PersonDetail> findList = personDetailService.findByFilter(filterSql);
        if(findList.size() > 0) {
        	PersonDetail find = findList.get(0);
        	EntityAuditListener.statusMap.put(find.getId(), find.clone());
        	boolean isUpdate = false;
        	if(personDetail.getRuleId() != -1 && personDetail.getRuleId() != find.getRuleId()){
        		isUpdate = true;
        	}
        	find.update(personDetail);
        	if(personDetail.getBankId()>0) {
        	    String fSql = " from_person_id = "+id;
                List<BlackDetail> blackList = blackDetailService.findByFilter(fSql);
        		for(BlackDetail black : blackList) {
        			black.setBankId(personDetail.getBankId());
        		}
        		blackDetailService.batchSave(blackList);
        	}
        	PersonDetail person = (PersonDetail) personDetailService.save(find);
            
        	if(isUpdate || _personDetailService.refreshPersonStatus(person)){
        	    new Thread(){
                    @Override
                    public void run() {
                        _personDetailService.refreshPersonOfUpdate(person.getId());
                    }
                }.start();
        	}
            return new JsonObject(person);
        } else {
            return new JsonObject("对不起，您没有修改权限！", 1001);
        }
    	}catch(Exception e){
    		LOG.error("change person layout status:",e);
    		return new JsonObject("系统出现小问题,请稍后重试或者刷新浏览器",1001);
    	}
//        List<CameraAndBlackDetail> cameraAndBlackDetailList = this._cameraAndBlackDetailRepository.findByBlackdetailId(id);
//        for (CameraAndBlackDetail cameraAndBlackDetail : cameraAndBlackDetailList) {
//            //iocontrol
//            ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue(), SourceTypes.CAMERA.getValue(), cameraAndBlackDetail.getCameraId(), id);
//        }
        //Engine has done the loop.
//        ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue(), id, 0, 0);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the person detail is successfully delete or not.")
    public JsonObject deletePerson(@PathVariable("id") long id) throws Exception {
    	try{
    	    _personDetailService.delete(id);
    	    new Thread(){
    	        @Override
    	        public void run() {
    	            _personDetailService.refreshPersonOfUpdate(id);
    	        }
    	    }.start();
    	    return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	}catch(MsgException e){
            return new JsonObject(e.getMessage(),e.getErrorCode()); 
        }catch(Exception e){
    	    return new JsonObject(e.getMessage(),RequestConsts.response_right_error); 
    	}
    }
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回入库嫌疑人总数")
    public JsonObject count() {
        return new JsonObject(this._personDetailDao.count());
    }

   /* @RequestMapping(value = "/bank/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据所属库获取嫌疑人集合")
    public JsonObject getPersonByBankId(@PathVariable("id") long id) {
    	String bankIdAutority = ","+this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
    	if(bankIdAutority.indexOf(","+id+",")<0)
    		return new JsonObject("您没有权限查看该库新建嫌疑人！", 1001);
    	return new JsonObject(this._personDetailService.findByBankId(id, ""));
    }
*/
    @RequestMapping(value = "/statistic/bank/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "统计指定库id的布控人数量")
    public JsonObject getPersonCountByBankId(@PathVariable("id") long id) {
    	String bankIdAutority = ","+this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
    	if(bankIdAutority.indexOf(","+id+",")<0)
    		return new JsonObject("您没有权限查看该库布控人数量！", 1001);
    	return new JsonObject(this._personDetailDao.countByBankId(id));
    }

    @RequestMapping(value = "/bank/{id}/ids/{ids}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "批量修改嫌疑人所属库")
    public JsonObject modifyBank(@PathVariable("id") long id, @PathVariable("ids") String ids) throws Exception {
    	String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
    	if((","+authority+",").indexOf(","+id+",")<0)
    		return new JsonObject("您没有权限修改嫌疑人进入该库！", 1001);
    	_personDetailDao.modifyBank(id, ids.split(","), authority.split(","));
    	_blackDetailDao.modifyBank(id, ids.split(","), authority.split(","));

      //  批量移库的操作日志记录
    	LOG.info("EntityAuditListener->touchForCreate->Auditable batch changes of black_detail's bank !!!");
    	
         UserInfo userinfo=CurUserInfoUtil.getUserInfo();
		 RoleInfo roleinfo=CurUserInfoUtil.getRoleInfo();
		 
		 Long uid=userinfo.getId();
		 Long policeStationId =userinfo.getPoliceStationId();
		 String stationname = policeStationService.findById(policeStationId).getStationName();   //单位名称
		 String userName=userinfo.getName();
		 String owner=userinfo.getLogin();
		 String accounttype=roleinfo.getCnName();
	
	    AuditLogInfo log = new AuditLogInfo();
		log.setOwner(owner);
		log.setOperation("batch changes of bank");
		log.setObject("");  //没有表咯
		log.setObjectId(uid);
		
		/*if(userinfo.){
			
		}else(){
			
		}*/
		
		//log.setObject_status(15);  //批量移库 也属于库信息的操作
    	log.setTitle(log.getOwner() + "进行了批量移库操作,"+userName+","+stationname);
    	String bankname=((BlackBank) blackBankService.findById(id)).getBankName();
    	String perosondetails="";  //是根据t_person_detail中 批量移库的
    	String[] persondetail=ids.split(",");
    	
    	String bankType = "";
        if(persondetail.length>0){
        	PersonDetail p = personDetailService.findById(Long.parseLong(persondetail[0]));
			if(p.getType()==0){
				
				bankType = "黑名单";
				log.setObject_status(15);
			}else if(p.getType()==1){
				
				bankType = "白名单";
				log.setObject_status(1000);
			}
        	
		}
    	
    	for(int i=0;i<persondetail.length;i++){
    	Long bid=Long.parseLong(persondetail[i]);	
    	PersonDetail bd = personDetailService.findById(bid);
    	perosondetails=perosondetails+bd.getRealName()+"、";
    		
    		
    	}
		log.setMessage(accounttype+userName+"进行了批量移库操作,把"+bankType+"："+perosondetails+"移入了库："+bankname);
		auditLogService.save(log);
    	
    	return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value = "/bank/display", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "根据所属库获取嫌疑人集合")
    public JsonObject getPersonByBankId(@RequestBody @Valid BankDisplayInfo bankDisplayInfo) {
        PageDto<PersonFullDto> pageResult = null;
        try{
            String bankIdAutority = ","+this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
            if(bankIdAutority.indexOf(","+bankDisplayInfo.getId()+",")<0){
                return new JsonObject("您没有权限查看该库新建嫌疑人！", 1001);
            }
            pageResult = this._personDetailService.findByBankId(bankDisplayInfo);       
            if(pageResult.getCount() == 0){
                return new JsonObject("结果集为空",1001);
            }
        }catch(Exception e){
            return new JsonObject(e.getMessage(), 1002);
        }
        return new JsonObject(pageResult.getData(), 0, pageResult.getMaxPages(), (int) pageResult.getCount());  
        
    }
    
    
  //v1.2.0 库名和入库时间联合查询黑名单用户的修改
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "按条件查询布控人员")
    public JsonObject getBlackPersonByBankIdAndTime(@RequestBody @Valid PersonQueryDto dto) {
        PageDto<PersonFullDto> pageResult = new PageDto<PersonFullDto>(new ArrayList<PersonFullDto>());
        try{
            pageResult = this._personDetailService.findByQueryParams(dto);		           
        }catch(MsgException e){
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch(Exception e){
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(pageResult.getData(), 0, pageResult.getMaxPages(), (int) pageResult.getCount());	
    }
    
    @RequestMapping(value = "/bank/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据所属库获取嫌疑人集合")
    public JsonObject getPersonByBankId(@PathVariable("id") long id, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
        BankDisplayInfo bankDisplayInfo = new BankDisplayInfo();
        bankDisplayInfo.setPage(page);
        bankDisplayInfo.setPageSize(pageSize);
        bankDisplayInfo.setId(id);
        PageDto<PersonFullDto> pageResult = null;
        try{
            String bankIdAutority = ","+this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
            if(bankIdAutority.indexOf(","+bankDisplayInfo.getId()+",")<0){
                return new JsonObject("您没有权限查看该库新建嫌疑人！", 1001);
            }
            pageResult = this._personDetailService.findByBankId(bankDisplayInfo);       
            if(pageResult.getCount() == 0){
                return new JsonObject("结果集为空",1001);
            }
        }catch(Exception e){
            return new JsonObject(e.getMessage(), 1002);
        }
        return new JsonObject(pageResult.getData(), 0, pageResult.getMaxPages(), (int) pageResult.getCount());  
    }

   /* @RequestMapping(value = "/bank/{id}/name/{name}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据所属库获取嫌疑人集合")
    public JsonObject getPersonByName(@PathVariable("id") long id, @PathVariable("name") String name) {
    	String bankIdAutority = ","+this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
    	if(bankIdAutority.indexOf(","+id+",")<0)
    		return new JsonObject("您没有权限查看该库新建嫌疑人！", 1001);
    	List<PersonFullDto> listResult = this._personDetailService.findByBankId(id, name);
        return new JsonObject(listResult);
    }*/
    
    @RequestMapping(value = "/black/authorize/count", method = RequestMethod.GET)
   	@ApiOperation(httpMethod = "GET", value = "返回当前用户授权黑名单人数")
   	public JsonObject countBlackAndAuthorize() {
       	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
       	if(authority.trim().length()==0){
       		return new JsonObject("本单位没有库授权！", 1001);
       	}
        Long totalCount =  _personDetailDao.countByBlackBankIds(authority.split(","));
        
   		return new JsonObject(totalCount);
   	}
}