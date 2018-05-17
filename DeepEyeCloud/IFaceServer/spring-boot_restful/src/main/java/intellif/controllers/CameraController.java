package intellif.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;












import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.consts.RequestConsts;
import intellif.core.tree.Tree;
import intellif.dao.AreaAndBlackDetailDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.FaceCameraCountDao;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.AreaCameraDto;
import intellif.dto.CameraDto;
import intellif.dto.CameraIdListDto;
import intellif.dto.CameraQueryDto;
import intellif.dto.JsonObject;
import intellif.dto.MonitorAreaInfo;
import intellif.enums.SourceTypes;
import intellif.exception.MsgException;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.ESurveilIoctrlType;
import intellif.service.CameraServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.TaskServiceItf;
import intellif.settings.StreamMediaSettings;
import intellif.thrift.StreamMediaThriftClient;
import intellif.utils.CollectionUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.PageDto;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.service.ZoneAuthorizeCacheItf;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

/**
 * <h1>The Class CameraController.</h1>
 * The CameraController which serves request of the form /camera and returns a JSON object representing an instance of CameraInfo.
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
//@RequestMapping("/intellif/camera")
@RequestMapping(GlobalConsts.R_ID_CAMERA)
public class CameraController {

    // ==============
    // PRIVATE FIELDS
    // ==============

    private static Logger LOG = LogManager.getLogger(CameraController.class);

    // Autowire an object of type CameraInfoDao
   
    @Autowired
    private CameraServiceItf _cameraServiceItf;
    
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;

    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;

    @Autowired
    private TaskServiceItf taskServiceItf;
    @Autowired
    private FaceCameraCountDao faceCameraCountRepository;
    
    @Autowired
    CameraAndBlackDetailDao cameraAndBlackDao;
    
    @Autowired
    AreaAndBlackDetailDao areaAndBlackDao;
    
    @Autowired
	private BlackDetailDao _blackDetailDao;
    @Autowired
    private PersonDetailServiceItf _personDetailService;
    @Autowired
    private ZoneAuthorizeCacheItf zoneAuthorizeCache;
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the camera info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid CameraInfo cameraInfo) throws Exception {
        CameraInfo cameraInfoResp = null;
        try{
            cameraInfoResp = (CameraInfo) _cameraServiceItf.save(cameraInfo);
            Tree tree =  LocalCache.tree;
            tree.addTreeNode(CameraInfo.class,cameraInfoResp);
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),e.getErrorCode() ); 
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),RequestConsts.response_system_error); 
        }
        return new JsonObject(cameraInfoResp);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of camera info that is successfully get or not.")
    public JsonObject list() {
        return new JsonObject(this._cameraServiceItf.findAllCameraDto(null));
    }
    @RequestMapping(value = "/query",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "query camera info by params")
    public JsonObject queryByParams(@RequestBody @Valid CameraQueryDto cqd) {
        PageDto<CameraInfo> pageResult = new PageDto<CameraInfo>(new ArrayList<CameraInfo>());
        try{ 
            String areaIds = cqd.getAreaIds();
            String node = cqd.getNodeType();
          if(StringUtils.isNotBlank(areaIds)){
            zoneAuthorizeService.checkIds(Area.class,cqd.getAreaIds());
          }else{
              //如果不传areaIds则取用户授权区域数据
              List<Area> areaList = zoneAuthorizeService.findAll(Area.class,cqd.getUserId());
              List<Long> curcList = areaList.stream().map(s -> s.getId()).collect(Collectors.toList());
              areaIds = StringUtils.join(curcList, ",");
          }
          if(StringUtils.isNotBlank(areaIds)){          
              cqd.setAreaIds(areaIds);
              pageResult = _cameraServiceItf.queryUserCamerasByParams(cqd);        
          }
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }
        catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(pageResult.getData(), 0, pageResult.getMaxPages(), (int) pageResult.getCount());  
    }
    @RequestMapping(value = "/inStation", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取所有警局内摄像头")
    public JsonObject findByInStation() {
        return new JsonObject(this._cameraServiceItf.findInStation());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the camera info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this._cameraServiceItf.findOneCameraDto(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  camera info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid CameraInfo cameraInfo) throws Exception {
//		CameraInfo find = this._cameraInfoDao.findOne(id);
        CameraInfo cameraInfoResp = null;
        try{
            cameraInfo.setId(id);       
            cameraInfoResp = (CameraInfo) _cameraServiceItf.update(cameraInfo);
            Tree tree =  LocalCache.tree;
            tree.addTreeNode(CameraInfo.class,cameraInfoResp);
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),e.getErrorCode() ); 
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(),RequestConsts.response_system_error); 
        }
        return new JsonObject(cameraInfoResp);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the camera info is successfully delete or not.")
    public JsonObject delete(@PathVariable("id") long id) throws Exception {
        try{
            this._cameraServiceItf.delete(id);
            Tree tree =  LocalCache.tree;
            tree.deleteTreeNode(CameraInfo.class,id);
        }catch(MsgException e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), e.getErrorCode());
        }catch(Exception e){
            LOG.error("e:",e);
            return new JsonObject(e.getMessage(), RequestConsts.response_system_error);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
 
    }

    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取嫌疑人的布控摄像头")
    public JsonObject getCameraByPersonId(@PathVariable("id") long id) {
        List<CameraInfo> returnList = new ArrayList<CameraInfo>();
        List<Long> idList = new ArrayList<Long>();
       try{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo ui = (UserInfo) auth.getPrincipal();
        List<BigInteger> cList = cameraAndBlackDao.findCameraIdsByPersonId(id);
        List<Long> curcList = cList.stream().map(s -> s.longValue()).collect(Collectors.toList());
        List<BigInteger> aList = areaAndBlackDao.findAreaIdsByPersonId(id);
        
        Tree tree = LocalCache.tree;
        if(!CollectionUtils.isEmpty(aList)){
            for(BigInteger item : aList){
                Long val = item.longValue();
                List<Long> cameraList = tree.nextIds(Area.class, val);
                idList.addAll(cameraList);
            }
        }
        if(!CollectionUtils.isEmpty(cList)){
            idList.addAll(curcList);
        }
        idList = zoneAuthorizeService.filterIds(CameraInfo.class, idList, null);

        for(Long cId : idList){
          CameraInfo ci = tree.treeNodeWithOutTreeInfo(CameraInfo.class, cId);
          returnList.add(ci);
        }
       }catch(Exception e){
            LOG.error("getCameraByPersonId error, personId:"+id+",error:",e);
        }
        return new JsonObject(returnList);
        
    }
    
    @RequestMapping(value = "/ids/person/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取嫌疑人的布控区域和摄像头")
    public JsonObject getCameraIdsByPersonId(@PathVariable("id") long id) {
        List<MonitorAreaInfo> returnList = new ArrayList<MonitorAreaInfo>();
        try{
            
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo ui = (UserInfo) auth.getPrincipal();
        List<BigInteger> cList = cameraAndBlackDao.findCameraIdsByPersonId(id);
        List<Long> curcList = cList.stream().map(s -> s.longValue()).collect(Collectors.toList());
        List<BigInteger> aList = areaAndBlackDao.findAreaIdsByPersonId(id);
        
       if(null == GlobalConsts.userBukongMap.get(ui.getId())){
           _personDetailService.processUserAreaDataToMap(ui.getId());
       }
        Map<String,List<Long>> userMap = GlobalConsts.userBukongMap.get(ui.getId());
        List<Long> uAreaList = userMap.get("area");
        
        Tree tree = LocalCache.tree;
        if(!CollectionUtils.isEmpty(aList)){
            for(BigInteger item : aList){
                Long val = item.longValue();
                List<Long> cameraList = tree.nextIds(Area.class, val);
                MonitorAreaInfo mai = null;
                if(uAreaList.contains(val)){
                    //区域布控并且在当前login用户有该区域授权
                     mai = new MonitorAreaInfo(val, cameraList, RequestConsts.blackdetail_area_allselected);
                     returnList.add(mai);
                }else{
                    //区域布控但当前login用户没有该区域授权，只能摄像头过滤
                    cameraList = zoneAuthorizeService.filterIds(CameraInfo.class, cameraList, null);
                    if(!CollectionUtils.isEmpty(cameraList)){
                        mai = new MonitorAreaInfo(val, cameraList, RequestConsts.blackdetail_area_notallselected);
                        returnList.add(mai);
                    }
                }
            }
        }
        if(!CollectionUtils.isEmpty(cList)){
            curcList = zoneAuthorizeService.filterIds(CameraInfo.class, curcList, null);
            Map<Long,MonitorAreaInfo> aMap = new HashMap<Long,MonitorAreaInfo>();
            for(Long item : curcList){
                CameraInfo ci = (CameraInfo) tree.treeNodeWithOutTreeInfo(CameraInfo.class, item);
                Long areaId = ci.getStationId();
                MonitorAreaInfo mai = aMap.get(areaId);
                if(null == mai){
                    List<Long> tempList = new ArrayList<Long>();
                    tempList.add(item);
                    mai = new MonitorAreaInfo(areaId,tempList,RequestConsts.blackdetail_area_notallselected);
                    aMap.put(areaId, mai);
                }else{
                    mai.getCameraIds().add(item);
                }
            }
            
            Iterator<Long> iterator = aMap.keySet().iterator();
            while(iterator.hasNext()){
                long key = iterator.next();
                returnList.add(aMap.get(key));
            }
        }
        }catch(Exception e){
            LOG.error("getCameraIdsByPersonId error, personId:"+id+",error:",e);
        }
        return new JsonObject(returnList);
    }

    @RequestMapping(value = "/person/{id}/add/{stationId}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "对嫌疑人增加布控区域")
    public ResponseEntity<Boolean> addPersonToStation(@PathVariable("id") long id, @PathVariable("stationId") long stationId) throws Exception {
        List<CameraInfo> cameraList = _cameraServiceItf.findByStationId(stationId);
        _cameraServiceItf.addPersonToCamera(id, cameraList);
        for (CameraInfo camera : cameraList) {
            ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_ADD_PERSON.getValue(), SourceTypes.CAMERA.getValue(), camera.getId(), id);
        }
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @RequestMapping(value = "/person/change/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "对嫌疑人减少摄像头")
    public ResponseEntity<Boolean> changePersonFromCameras(@PathVariable("id") long id, @RequestBody @Valid AreaCameraDto areaDto) throws Exception {
        try{
    	List<BigInteger> cList = cameraAndBlackDao.findCameraIdsByPersonId(id);
    	List<BigInteger> aList = areaAndBlackDao.findAreaIdsByPersonId(id);
    	List<Long> curcList = cList.stream().map(s -> s.longValue()).collect(Collectors.toList());
    	List<Long> curaList = aList.stream().map(s -> s.longValue()).collect(Collectors.toList());
    	curcList = zoneAuthorizeService.filterIds(CameraInfo.class, curcList, null);
    	curaList = zoneAuthorizeService.filterIds(Area.class, curaList, null);
    	
    	Map<String,List<Long>> paramMap = _personDetailService.processParamDataToMap(areaDto.getAreaList());
        List<Long> modaList =  paramMap.get("area");
        List<Long> modcList =  paramMap.get("camera");
        
    	List<Long> removeCameraIdList = CollectionUtil.remove(curcList, modcList);
    	List<Long> retainCameraList = CollectionUtil.mixed(curcList, modcList);
    	retainCameraList = CollectionUtil.remove(modcList, retainCameraList);
    	
    	List<Long> removeAreaList = CollectionUtil.remove(curaList, modaList);
        List<Long> retainAreaList = CollectionUtil.mixed(curaList, modaList);
        retainAreaList = CollectionUtil.remove(modaList, retainAreaList);
    	
    	
    	
    	_cameraServiceItf.delPersonFromCameraAreaIds(id, removeCameraIdList,CameraAndBlackDetail.class); 
    	_cameraServiceItf.addPersonToCameraAreaIds(id, retainCameraList,CameraAndBlackDetail.class);
    	
    	_cameraServiceItf.delPersonFromCameraAreaIds(id, removeAreaList,AreaAndBlackDetail.class); 
        _cameraServiceItf.addPersonToCameraAreaIds(id, retainAreaList,AreaAndBlackDetail.class);
        
    	   new Thread(){
               @Override
               public void run() {
                   _personDetailService.refreshPersonOfUpdate(id);
               }
           }.start();
        }catch(Exception e){
        	LOG.error("change person cameras error:",e);
        	return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
        }
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }
    
   

    @RequestMapping(value = "/person/{id}/del/{stationId}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "对嫌疑人减少布控区域")
    public ResponseEntity<Boolean> delPersonFromStation(@PathVariable("id") long id, @PathVariable("stationId") long stationId) throws Exception {
        List<CameraInfo> cameraList = _cameraServiceItf.findByStationId(stationId);
        _cameraServiceItf.delPersonFromCamera(id, cameraList);
        for (CameraInfo camera : cameraList) {
            ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(), ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), SourceTypes.CAMERA.getValue(), camera.getId(), id);
        }
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }
 
    @RequestMapping(value = "/streammedia/camera/{id}/start/{start}/end/{end}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定摄像头指定时间段视频流")
    public JsonObject searchLastFaceAndHistory(@PathVariable("id") long id, @PathVariable("start") long start, @PathVariable("end") long end) throws IOException {
        try {
        	if(end - start>= 3600*1000) return new JsonObject("视频流时间段不得超过1小时！", 1001);
        	StreamMediaThriftClient client = StreamMediaThriftClient.getInstance(StreamMediaSettings.getIp(), StreamMediaSettings.getPort());
        	String result = client.processgetPlaybackLive(new Long(id).toString(), start, end);
            return new JsonObject(result);
        } catch (Exception e) {
         LOG.error("get stream media by camera error:",e);
         return new JsonObject(e.getMessage(), 1001);
        }
    }
    
    @RequestMapping(value = "/streammedia/camera/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定摄像头直播")
    public JsonObject searchLastFaceAndHistory(@PathVariable("id") long id) throws IOException {
        try {
        	StreamMediaThriftClient client = StreamMediaThriftClient.getInstance(StreamMediaSettings.getIp(), StreamMediaSettings.getPort());
        	String result = client.processGetLive(new Long(id).toString());
            return new JsonObject(result);
        } catch (Exception e) {
         LOG.error("get stream media by camera error:",e);
         return new JsonObject(e.getMessage(), 1001);
        }
    }

    @RequestMapping(value = "/statistic/source/{id}/date/{date}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定摄像头指定日期之后每天的采集统计")
    public JsonObject statisticByDateBySourceId(@PathVariable("id") long id, @PathVariable("date") String date) {
        return new JsonObject(this.faceCameraCountRepository.findBySourceIdByTime(id, date));
    }

    @RequestMapping(value = "/statistic/source/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定摄像头所有采集统计")
    public JsonObject statisticBySourceId(@PathVariable("id") long id) {
        return new JsonObject(this.faceCameraCountRepository.statisticBySourceId(id));
    }
    
    /**
     * 分页查询摄像头列表
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页查询摄像头列表")
    public JsonObject findAll(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @RequestBody CameraQueryDto cameraQueryDto) throws Exception {
        Page<CameraDto> cameraPage = _cameraServiceItf.findAll(cameraQueryDto, new PageRequest(page - 1, pageSize));
        JsonObject jsonObject = new JsonObject(cameraPage.getContent());
        jsonObject.setMaxPage(cameraPage.getTotalPages());
        jsonObject.setTotal(Long.valueOf(cameraPage.getTotalElements()).intValue());
        return jsonObject;
    }
    
    @RequestMapping(value = "/ids", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a list of cameras with requested ids")
    public JsonObject findByIds(@RequestBody @Valid CameraIdListDto cameraIdListDto) {
    	List<CameraDto> cameraList = _cameraServiceItf.findByIds(cameraIdListDto.getIdList());
    	return new JsonObject(cameraList);
    }
    
/*    @RequestMapping(value = "/authorize", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "查询用户的授权的摄像头")
    public JsonObject authorizeQuery(@RequestBody CameraQueryDto cameraQuery) {
    	List<CameraInfo> cameraList = _cameraServiceItf.authorizeQuery(cameraQuery);
    	return new JsonObject(cameraList);
    }*/
    
    @RequestMapping(value = "/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "查询摄像头列表")
    public JsonObject list(@RequestBody CameraQueryDto cameraQuery) {
        Page<CameraDto> page = _cameraServiceItf.findAll(cameraQuery, new PageRequest(0, Integer.MAX_VALUE));
        return new JsonObject(page.getContent());
    }
}
