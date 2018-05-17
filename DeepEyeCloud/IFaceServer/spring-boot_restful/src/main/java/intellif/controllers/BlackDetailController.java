/**
 *
 */
package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.dao.AreaAndBlackDetailDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.ImageInfoDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.TaskInfoDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.PersonDetail;
import intellif.dto.BlackDetailDto;
import intellif.dto.JsonObject;
import intellif.ifaas.EBListIoctrlType;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ESurveilIoctrlType;
import intellif.service.BlackBankServiceItf;
import intellif.service.BlackDetailServiceItf;
import intellif.service.BlackFeatureServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.PersonDetailServiceImpl;
import intellif.settings.ImageSettings;
import intellif.utils.FileUtil;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.BatchInsertDto;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.TaskInfo;
import intellif.exception.MsgException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <h1>The Class BlackDetailController.</h1>
 * The BlackDetailController which serves request of the form /black/detail and returns a JSON object representing an instance of BlackDetail.
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
//@RequestMapping("/intellif/black/detail")
@RequestMapping(GlobalConsts.R_ID_BLACK_DETAIL)
public class BlackDetailController {
//    private static final int DEFAULT_PAGE_SIZE = 100;
    //
    private static Logger LOG = LogManager.getLogger(BlackDetailController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;
    // Auto wire an object of type BlackDetailDao
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private TaskInfoDao _taskInfoDao;
    @Autowired
    private BlackBankServiceItf _blackService;
   /* @Autowired
    private ImageInfoDao _imageInfoDao;*/
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private BlackDetailServiceItf _blackDetailService;
    @Autowired
    private CameraServiceItf _cameraService;
    @Autowired
    private CameraAndBlackDetailDao _cameraAndBlackDetailRepository;
    @Autowired
    private AreaAndBlackDetailDao _areaAndBlackDetailRepository;
    @Autowired
    private PersonDetailServiceImpl personDetailService;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private BlackBankDao bankDao;
    @Autowired
    private BlackFeatureServiceItf featureService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PersonDetailServiceItf _personDetailService;
    @Autowired
    private FaceServiceItf faceService;
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the black detail info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid BlackDetail blackDetail) throws Exception {
        // Pre-fill the face URL.
    	String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
    	String filterSql = " id = "+ blackDetail.getFromPersonId()+" bank_id in ("+authority+")";
    	List<PersonDetail>personList = personDetailService.findByFilter(filterSql);
    	if(personList.size()<=0) {
            return new JsonObject("对不起，您没有修改权限！", 1001);
    	}
    	blackDetail.setBankId(personList.get(0).getBankId());
            Long iId = 0l;
            String sourceUrl = null;
            if(GlobalConsts.FACE.equals(blackDetail.getPicType())){
                //图片来自于face表小图
               FaceInfo fi = faceService.findOne(blackDetail.getFromImageId());
               iId = fi.getFromImageId();
               sourceUrl = fi.getImageData();
            }else{
                iId = blackDetail.getFromImageId();
                ImageInfo imageInfo = (ImageInfo) this._imageServiceItf.findById(iId);                         //////////////////////////// v1.1.0
                sourceUrl = imageInfo.getUri();
            }
            blackDetail.setFromImageId(iId);
           // String faceUrl_ori = _imageInfoDao.findOne(blackDetail.getFromImageId()).getFaceUri();
        	 String faceUrl_ori = ((ImageInfo)this._imageServiceItf.findById(iId)).getFaceUri();                         //////////////////////////// v1.1.0
            String faceUrl_store = ImageSettings.getStoreRemoteUrl() + new File(FileUtil.getStoreFaceUri(faceUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
            blackDetail.setImageData(faceUrl_store);
        
        BlackDetail resp = _blackDetailDao.save(blackDetail);
        
        if (this._blackDetailService.updateFaceFeature(resp,sourceUrl)) {
           List<Long> cameraIdList = _cameraService.getCameraIdsByPersonId(resp.getFromPersonId());
            List<Object> cList = new ArrayList<Object>();
            for (Long cId : cameraIdList) {
                cList.add(new CameraAndBlackDetail(cId, resp.getId()));
            }
            if(!CollectionUtils.isEmpty(cList)){
                BatchInsertDto bid = new BatchInsertDto(cList);
                jdbcTemplate.batchUpdate(bid.getInsertSql(),bid.getInsertSetter());
            }
           
           List<Long> areaIdList = _cameraService.getAreaIdsByPersonId(resp.getFromPersonId());
           List<Object> aList = new ArrayList<Object>();
           for (Long aId : areaIdList) {
               aList.add(new AreaAndBlackDetail(aId, resp.getId()));
           }
           if(!CollectionUtils.isEmpty(aList)){
               BatchInsertDto aid = new BatchInsertDto(aList);
               jdbcTemplate.batchUpdate(aid.getInsertSql(),aid.getInsertSetter());
           }
          
        } else {
            //TODO:rollback the fail item.
        }
        
        try {
//            _solrDataServiceItf.addBlackDetail(GlobalConsts.BLACK_INFO_TYPE, resp);
            _solrDataServiceItf.addBlackDetail(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE), resp);
        } catch (Exception e) {
        	LOG.error("索引嫌疑人失败，失败id："+resp.getId(), e);
        }
        
        new Thread(){
            @Override
            public void run() {
                _personDetailService.refreshPersonOfUpdate(resp.getFromPersonId());
            }
        }.start();
        return new JsonObject(_blackDetailDao.findOne(resp.getId()));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of black detail info that is successfully get or not.")
    public JsonObject list() {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        return new JsonObject(this._blackDetailDao.findAll(authority.split(",")));
    }

    @RequestMapping(value = "/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "分页获取嫌疑人人脸图片")
    public JsonObject findLast(@PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        return new JsonObject(this._blackDetailDao.findLast((page - 1) * pagesize, pagesize, authority.split(",")));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the black detail info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        return new JsonObject(this._blackDetailDao.findOne(id, authority.split(",")));
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  black detail info is successfully updated or not.(接口已废置)")
//    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid BlackDetail blackDetail) throws Exception {
//        BlackDetail find = this._blackDetailDao.findOne(id);
//        blackDetail.setId(id);
//        blackDetail.setCreated(find.getCreated());
//        //Update the face URL.
////        String faceUrl = _imageInfoDao.findOne(blackDetail.getFromImageId()).getFaceUri();
////        blackDetail.setImageData(faceUrl);
////        // Pre-fill the face URL.
//        String faceUrl_ori = _imageInfoDao.findOne(blackDetail.getFromImageId()).getFaceUri();
//        String faceUrl_store = ImageSettings.getStoreRemoteUrl() + new File(FileUtil.getStoreFaceUri(faceUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
//        blackDetail.setImageData(faceUrl_store);
//
//        BlackDetail resp = _blackDetailDao.save(blackDetail);
//        if (this._blackDetailService.updateFaceFeature(resp)) {
//            //if bankId in task items.
//            long bankId = find.getBankId();
//            List<TaskInfo> taskInfos = this._taskInfoDao.findByBankId(bankId);
//            //
//            if (null != taskInfos && 0 < taskInfos.size()) {
//                ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(), EParamIoctrlType.PARAM_IOCTRL_BLACKLIST.getValue(), EBListIoctrlType.BLIST_IOCTRL_UPDATE.getValue(), bankId, 0);
//                //TODO:if black detail update.
////        this.iocontrolBlackDetailUpdateAtTask(resp.get);
//            }
//        } else {
//            //TODO:rollback the fail item.
//        }
//        return new JsonObject(_blackDetailDao.findOne(resp.getId()));
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the black detail info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) throws Exception {
    	String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
    	}
        long personId = this._blackDetailDao.findOne(id, authority.split(",")).get(0).getFromPersonId();
        if (this._blackDetailDao.findByFromPersonId(personId).size() <= 1)
            return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
        this._blackDetailDao.delete(id);
        try {
//            _solrDataServiceItf.deleteById(GlobalConsts.BLACK_INFO_TYPE, id+"");
            _solrDataServiceItf.deleteById(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE),  id + "");
        } catch (Exception e) {
        	LOG.error("删除嫌疑人索引失败，失败id："+id, e);
        }
        this._cameraAndBlackDetailRepository.deleteByBlackdetailId(id);
        
       List<Long> idList = new ArrayList<Long>();
       idList.add(id);
       featureService.deleteByFaceIds(idList);
        
        ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_UPDATE_PERSON.getValue(), personId, 0, 0);
        PersonDetail person = personDetailService.findById(personId);
        person.setPhotoData(this._blackDetailDao.findByFromPersonId(personId).get(0).getImageData());
        personDetailService.save(person);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    //
    @RequestMapping(value = "/bank/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the bank id queried black detai is successfully get or not.")
    public JsonObject getByBankId(@PathVariable("id") long id) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	authority = ","+authority+",";
    	if(authority.indexOf(","+id+",")>=0) {
    		return new JsonObject(this._blackDetailDao.findByBankId(id));
    	}
    	return new JsonObject(null);
    }

    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the person id queried black detai is successfully get or not.")
    public JsonObject getByPersonId(@PathVariable("id") long id) {
        try{
            return new JsonObject(_blackDetailService.getBlackDetailsByPerson(id));
        }catch(MsgException e){
            return new JsonObject(e.getMessage(),e.getErrorCode()); 
        }catch(Exception e){
            return new JsonObject(e.getMessage(),RequestConsts.response_right_error); 
        }
    }

    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the black detail is successfully created or not.", notes = "Oneway communication to Thrift socket server.")
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    public JsonObject findByCombinedConditions(@RequestBody @Valid BlackDetailDto blackDetailDto) {
        LOG.info("POST taskInfoDto:" + blackDetailDto.toString());
        List<BlackDetailDto> respblackDetailDto = _blackService.findByCombinedConditions(blackDetailDto);
        return new JsonObject(respblackDetailDto);
    }

    // FindBySourceId
//    @RequestMapping(value = "/source/{id}", method = RequestMethod.GET)
//    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the task related source id queried black detail is successfully get or not.")
//    public JsonObject findBySourceId(@PathVariable("id") int id) {
//        List<TaskInfo> taskInfos = _taskInfoDao.findBySourceId(id);
//        long bankId = 0;
//        if (!taskInfos.isEmpty()) {
//            TaskInfo tInfo = taskInfos.get(0);
//            bankId = tInfo.getBankId();
//        }
//        return new JsonObject(this._blackDetailDao.findByBankId(bankId));
//    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回入库嫌疑人的人脸总数")
    public JsonObject count() {
        return new JsonObject(this._blackDetailDao.count());
    }
    
    @ApiOperation(httpMethod = "GET", value = "重点人员按库分页显示")
    @RequestMapping(value = "/bank/page/{page}/pagesize/{pagesize}/personsize/{personsize}", method = RequestMethod.GET)
    public JsonObject findBlackByBanksPage(@PathVariable("page") int page,@PathVariable("pagesize") int pagesize,@PathVariable("personsize") int personsize) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	Map<Long, List<BlackDetail>> detailMap = null;
    	Map<Long,Long> countMap = null;
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        List<BlackBank> bankList = this.bankDao.findExistBDetailByPage(authority.split(","),(page-1)*pagesize,pagesize);
        if(null != bankList && !bankList.isEmpty()){
         detailMap = _blackDetailService.getBlackByBanksPage(bankList, personsize);
        }
        
        if(null != detailMap){
        	countMap = new HashMap<Long,Long>();
        	Iterator<Long> iterator = detailMap.keySet().iterator();
        	while(iterator.hasNext()){
        		Long id = iterator.next();
                String fSql = "bank_id = "+id;
        		Long count = personDetailService.countByFilter(fSql);
        		countMap.put(id, count);
        	}
        	returnMap.put("blackDetail", detailMap);
        	returnMap.put("countDetail", countMap);
        }
    	return new JsonObject(returnMap);
    }
    @ApiOperation(httpMethod = "GET", value = "分页获取某库重点人员")
    @RequestMapping(value = "/bank/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    public JsonObject findBlackByBankPage(@PathVariable("page") int page,@PathVariable("pagesize") int pagesize,@PathVariable("id") long id) {
    	List<BlackDetail> detailList = _blackDetailDao.findBankDetailPage((page-1)*pagesize, pagesize, id);
    	String fSql = "bank_id = "+id;
        Long count = personDetailService.countByFilter(fSql);
    	JsonObject jo = new JsonObject(detailList);
    	jo.setTotal(count.intValue());
    	return jo;
    }
}