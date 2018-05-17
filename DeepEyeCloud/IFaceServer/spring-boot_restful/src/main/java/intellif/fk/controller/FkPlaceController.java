package intellif.fk.controller;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.AreaDao;
import intellif.dao.AuditLogDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraInfoDao;
import intellif.dao.CidDetailDao;
import intellif.dao.CidInfoDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.JuZhuInfoDao;
import intellif.dao.OtherCameraDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.OtherInfoDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.ResidentAreaDao;
import intellif.dao.SearchReasonDao;
import intellif.dao.SearchRecordDao;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OtherCameraInfo;
import intellif.dto.JsonObject;
import intellif.dto.QueryFaceDto;
import intellif.fk.dao.FkPlaceCameraDao;
import intellif.fk.dao.FkPlaceDao;
import intellif.fk.dto.FindFkPlaceDto;
import intellif.fk.vo.FkPlace;
import intellif.fk.vo.FkPlaceCamera;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.SolrServerItf;
import intellif.service.StaticBankServiceItf;
import intellif.service.UserServiceItf;
import intellif.database.entity.FaceInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(GlobalConsts.R_ID_FK_PLACE)
public class FkPlaceController {

	public static final float DEFAULT_SCORE_THRESHOLD = 0.92F;

	private static final int DEFAULT_PAGE_SIZE = 40;
	private static Logger LOG = LogManager.getLogger(FkPlaceController.class);
	
	public static HashMap userSearchMap = new HashMap();

	@Autowired
	private FaceServiceItf _faceService;
	@Autowired
	private CameraServiceItf cameraService;
	@Autowired
	private SearchRecordDao _searchRecordDao;
	@Autowired
	private SolrServerItf _solrService;
	@Autowired
	private PropertiesBean propertiesBean;
	@Autowired
	private ImageServiceItf _imageServiceItf;
	@Autowired
	private BlackDetailDao blackDetailDao;
	@Autowired
	private CidInfoDao cidInfoRepository;
	@Autowired
	private JuZhuDetailDao juZhuDetailRepository;
	@Autowired
	private OtherDetailDao otherDetailRepository;
	@Autowired
	private CidDetailDao cidDetailRepository;
	@Autowired
	private JuZhuInfoDao juzhuInfoRepository;
	@Autowired
	private OtherInfoDao otherInfoRepository;
	@Autowired
	private ResidentAreaDao residentAreaRepository;
	@Autowired
	private StaticBankServiceItf staticBankServiceItf;
	@Autowired
	private FaceServiceItf faceService;
	@Autowired
	private PoliceStationDao policestationDao;
	@Autowired
	private AuditLogDao auditLogRepository;
	@Autowired
	private AreaDao areaRepository;
	@Autowired
	private SearchReasonDao reasonDao;
	@Autowired
	private UserServiceItf _userService;
	@Autowired
	private BlackBankDao bankDao;
	@Autowired
	private IFaceSdkServiceItf iFaceSdkServiceItf;
	@Autowired
	private ZoneAuthorizeServiceItf zoneAuthorizeService;
	@Autowired
    private FkPlaceDao fkPlaceDao;
	@Autowired
	private FkPlaceCameraDao fkPlaceCameraDao;
	@Autowired
	private CameraInfoDao cameraInfoDao;
	@Autowired
	private OtherCameraDao otherCameraDao;




	@RequestMapping(value = "/param/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "分页多参数获取所有station最近抓拍图片")
	public JsonObject listByParamsPage(@PathVariable("page") int page, @PathVariable("pagesize") int pagesize,
			@RequestBody @Valid QueryFaceDto faceQueryDto) {

		try {
			List<FaceInfo> faceList = this._faceService.findFkByCombinedParams(faceQueryDto, page, pagesize);
			return new JsonObject(faceList);
		} catch (Exception e) {
			LOG.error("get face by params error", e);
			return new JsonObject(e.getMessage(), 1001);
		}
	}
	
	
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "新建一个场所")
    public JsonObject create(@RequestBody @Valid FkPlace fkPlace) {
        //根据场所名称去重
        String placeName = fkPlace.getPlaceName();
        if(placeName==null||placeName.trim().equals("")){
            return new JsonObject("场所名称不能为空",1001);    
        }
        List<FkPlace> place = fkPlaceDao.getPlaceByNameExactly(placeName);
        if(place!=null&&place.size()>0){
            return new JsonObject("场所名称已存在，不能重复哦",1001);  
        }
        return new JsonObject(fkPlaceDao.save(fkPlace));
    }

    @RequestMapping(value = "/find",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "获取所有场所,支持根据名称和编号模糊查询")
    public JsonObject list(@RequestBody @Valid FindFkPlaceDto place) {
        
        String placeNameOrNo = place.getPlaceNameOrNo();     
        if(!StringUtils.isEmpty(placeNameOrNo)){
         return new JsonObject(fkPlaceDao.getPlaceByNameOrNo(placeNameOrNo));  
        }else{
        return new JsonObject(fkPlaceDao.findAll());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id的场所")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(fkPlaceDao.findOne(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "修改指定id场所信息")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid FkPlace fkPlace) throws Exception {
        FkPlace oldPlace = fkPlaceDao.findOne(id);
        fkPlace.setId(id);
        fkPlace.setCreated(oldPlace.getCreated());
        fkPlace.setUpdated(new Date());
        //根据场所名称去重
        String placeName = fkPlace.getPlaceName();
        if(placeName==null||placeName.trim().equals("")){
            return new JsonObject("场所名称不能为空",1001);    
        }
        List<FkPlace> place = fkPlaceDao.getPlaceByNameExactly(placeName);
        if(place!=null&&place.size()>0){
            return new JsonObject("场所名称已存在，不能重复哦",1001);  
        }
        return new JsonObject(fkPlaceDao.save(fkPlace));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除指定id的场所,及对应的摄像头关联关系")
    public JsonObject delete(@PathVariable("id") long id) {
        fkPlaceDao.delete(id);
        FkPlaceCamera fkPlaceAndCamera = fkPlaceCameraDao.findOne(id);
        if(fkPlaceAndCamera!=null){
            fkPlaceCameraDao.delete(fkPlaceAndCamera); 
        }       
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }
	
	
    @RequestMapping(value = "/camera",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "新增反恐场所与摄像头的对应关系，关联摄像头给指定的场所")
    public JsonObject placeAddCamera(@RequestBody @Valid FkPlaceCamera fkPlaceAndCamera) {     
       // 对cameraid进行存在性 及重复性 检验
        String[] cameraIds = fkPlaceAndCamera.getCameraIds().split(",");
        String checkedCameraIds = "";
        for(int i=0;i<cameraIds.length;i++){
            CameraInfo camera = cameraInfoDao.findOne(Long.valueOf(cameraIds[i]).longValue());
            OtherCameraInfo otherCamera = otherCameraDao.findOne(Long.valueOf(cameraIds[i]).longValue());
            if(camera==null&&otherCamera==null){
                return new JsonObject("摄像头列表校验不通过，请检查是否都存在",1001);  
            }else if(!checkedCameraIds.contains(cameraIds[i])){
                if(checkedCameraIds.trim().equals("")){
                    checkedCameraIds = cameraIds[i]; 
                }else{
                    checkedCameraIds = checkedCameraIds+","+cameraIds[i]; 
                }
               
            }
        }  
        fkPlaceAndCamera.setCameraIds(checkedCameraIds);
        return new JsonObject(fkPlaceCameraDao.save(fkPlaceAndCamera));
    }
	
	
    @RequestMapping(value = "/updatecamera/{placeId}",method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "更新反恐场所与摄像头的对应关系")
    public JsonObject placeDeleteCamera(@PathVariable("placeId") long placeId,@RequestBody @Valid FkPlaceCamera fkPlaceAndCamera) throws Exception {     
        fkPlaceAndCamera.setPlaceId(placeId);
        return new JsonObject(fkPlaceCameraDao.save(fkPlaceAndCamera));
    }
    
    @RequestMapping(value = "/camera/{placeId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id的场所下的摄像头列表")
    public JsonObject getPlaceCamera(@PathVariable("placeId") long placeId) {
        return new JsonObject(fkPlaceCameraDao.findOne(placeId));
    }
	

}
