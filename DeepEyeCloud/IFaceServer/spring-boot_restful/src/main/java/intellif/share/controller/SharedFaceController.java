package intellif.share.controller;

import intellif.audit.AuditServiceItf;
import intellif.common.Constants;
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
import intellif.dao.OtherAreaDao;
import intellif.dao.OtherCameraDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.OtherInfoDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.ResidentAreaDao;
import intellif.dao.SearchLogDao;
import intellif.dao.SearchReasonDao;
import intellif.dao.SearchRecordDao;
import intellif.dto.BlackFaceResultDto;
import intellif.dto.CameraNearFaceDto;
import intellif.dto.CidInfoDto;
import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceSearchStatisticDto;
import intellif.dto.HistorySearchOperationDetailDto;
import intellif.dto.JsonObject;
import intellif.dto.ProcessInfo;
import intellif.dto.QueryFaceDto;
import intellif.dto.SearchFaceDto;
import intellif.dto.SearchReasonDto;
import intellif.exception.MsgException;
import intellif.exception.RedException;
import intellif.fk.dao.FkPersonAttrDao;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.vo.FkPersonAttr;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.UserServiceItf;
import intellif.share.service.ShareDistrictAreaCameraServiceItf;
import intellif.share.service.ShareImageServiceItf;
import intellif.share.service.ShareSolrServerItf;
import intellif.share.service.SharedFaceServiceItf;
import intellif.utils.ApplicationResource;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.FileUtil;
import intellif.database.entity.CidDetail;
import intellif.database.entity.DistrictInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.JuZhuDetail;
import intellif.database.entity.OtherDetail;
import intellif.database.entity.ResidentArea;
import intellif.database.entity.SearchLogInfo;
import intellif.database.entity.SearchReason;
import intellif.database.entity.SearchRecord;
import intellif.database.entity.Area;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OtherArea;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class SharedFaceController.</h1>
 * The SharedFaceController which serves request
 * of the form /face and returns a JSON object representing
 * an instance of FaceInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc. (see
 * <a href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 *
 * @author Zheng Xiaodong
 * @version 1.0
 * @since 2017-03-08
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_SHARED_FACE)
public class SharedFaceController {

	public static final float DEFAULT_SCORE_THRESHOLD = 0.92F;
	private static final int DEFAULT_PAGE_SIZE = 40;
	private static Logger LOG = LogManager.getLogger(SharedFaceController.class);
	public static HashMap userSearchMap = new HashMap();

	@Autowired
	private FaceServiceItf _faceService;
	@Autowired
	private CameraInfoDao _cameraInfoDao;
	@Autowired
	private SearchRecordDao _searchRecordDao;
	@Autowired
	private ShareSolrServerItf _solrService;
	@Autowired
	private PropertiesBean propertiesBean;
	//@Autowired
	//private ImageServiceItf _imageServiceItf;
	@Autowired
	private ShareImageServiceItf shareImageService;
    @Autowired
    private SharedFaceServiceItf sharedFaceService;
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
	private SharedFaceServiceItf sharefaceService;
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
	ShareDistrictAreaCameraServiceItf dacService;
	@Autowired
	private OtherCameraDao _otherCameraDao;
	@Autowired
	private OtherAreaDao _otherAreaDao;
	@Autowired
	private AreaDao _areaDao;
	@Autowired
	private SolrDataServiceItf _solrDataServiceItf;
	@Autowired
	private AuditServiceItf _auditService;
	@Autowired
    private SearchLogDao searchLogDao;
    @Autowired
    private PersonDetailDao personDetailDao;
    @Autowired
    private FkPersonAttrDao fkPersonAttrDao;
    @Autowired
    private CameraServiceItf cameraService;
	
    @RequestMapping(value = "/param/page/{page}/pagesize/{pagesize}",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取所有摄像头最近抓拍图片, 包含所有行政区域的数据")
    public JsonObject listByParamsPage(@PathVariable("page") int page, @PathVariable("pagesize") int pagesize,
                                       @RequestBody @Valid QueryFaceDto faceQueryDto) {

        try {
            List<FaceInfo> faceList = this.sharedFaceService.findByCombinedParams(
                    faceQueryDto,-1, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

    @RequestMapping(value = "/param/district/{id}/page/{page}/pagesize/{pagesize}",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取某一行政区域所有摄像头最近抓拍图片")
    public JsonObject listByDistrictParamsPage(@PathVariable("id") int id,
                                               @PathVariable("page") int page,
                                               @PathVariable("pagesize") int pagesize,
                                               @RequestBody @Valid QueryFaceDto faceQueryDto) {
        try {
            List<FaceInfo> faceList = this.sharedFaceService.findByCombinedParams(faceQueryDto, 
		             id, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

    @RequestMapping(value = "/param/zone/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取某一区域所有摄像头最近抓拍图片")
    public JsonObject listByDistrictParamsPage(@PathVariable("page") int page, 
	                                           @PathVariable("pagesize") int pagesize,
                                               @RequestBody @Valid QueryFaceDto faceQueryDto) {
        try {
            List<FaceInfo> faceList = this.sharedFaceService.findByCombinedParams(faceQueryDto, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

    @RequestMapping(value = "/param/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取指定摄像头最近抓拍图片")
    public JsonObject findByParamCameraId(@PathVariable("id") long id, @PathVariable("page") int page,
                                          @PathVariable("pagesize") int pagesize,
                                          @RequestBody @Valid QueryFaceDto queryFaceDto) {
        try {
            queryFaceDto.setSourceId(id);
            List<FaceInfo> faceList = null;
            faceList = this.sharedFaceService.findByCombinedParams(queryFaceDto, -1, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params camera error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

	@RequestMapping(value = "/param/cameras/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "分页多参数获取指定摄像头列表最近抓拍图片")
	public JsonObject findByParamCameraId(@PathVariable("page") int page,
										  @PathVariable("pagesize") int pagesize,
										  @RequestBody @Valid QueryFaceDto queryFaceDto) {
		try {
			List<FaceInfo> faceList = null;
			faceList = this.sharedFaceService.findByMultipleCameras(queryFaceDto, page, pagesize);
			return new JsonObject(faceList);
		} catch (Exception e) {
			LOG.error("get face by multiple cameras error", e);
			return new JsonObject(e.getMessage(), 1001);
		}
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a string describing if the face info id is successfully get or not.")
	public JsonObject get(@PathVariable("id") long id) {
		return new JsonObject(this.sharefaceService.findOne(id));
	}

	@RequestMapping(value = "/statistic/day", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "按天统计最近七天人脸抓拍人次")
	public JsonObject statisticByDay() {
		return new JsonObject(this._faceService.statisticByDay());
	}


	@RequestMapping(value = "/page/{page}/time/{time}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "分页获取所有摄像头最近抓拍图片")
	public JsonObject listByPage(@PathVariable("page") int page, @PathVariable("time") String time,
			@PathVariable("pagesize") int pagesize) {
		return new JsonObject(this._faceService.findLast(page, pagesize,
				time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/zip/download/progress/{key}")
	@ApiOperation(httpMethod = "GET", value = "上传图片显示进度")
	public JsonObject handleProcessZipDownload(@PathVariable("key") int key) {
		return new JsonObject(GlobalConsts.downloadMap.get(key));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/cancelzip/{key}")
	@ApiOperation(httpMethod = "GET", value = "取消导出图片")
	public JsonObject cancelPollOut(@PathVariable("key") int key) {
		GlobalConsts.stateMap.put(key, false);
		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	}
	 
	private CameraInfo getCameraInfoByCameraId(long id){
	        List<String> filterList = new ArrayList<String>();
	        filterList.add("id = "+id);
	    	CameraInfo ci = (CameraInfo) cameraService.queryALLCameraInfoByConditions(filterList).get(0);
	    	return ci;
	    }
	
	private Area getStationByStationId(long id){
		Area area = _areaDao.findOne(id);
    	if(null == area){
    	OtherArea oArea = _otherAreaDao.findOne(id);
    	if(null != oArea){
    		area = new Area(oArea);
    	}
    	}
    	return area;
    }
	@RequestMapping(value = "/zip/camera/{key}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "zip导出指定camera下抓拍图片")
	public JsonObject createZipResultsByCamera(@RequestBody @Valid QueryFaceDto queryFaceDto,
			@PathVariable("key") int key) {
		try {
			int totalSize = 0;
			Map<String, List<ImageInfo>> cameraMap = new HashMap<String, List<ImageInfo>>();
			String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			int rand = CommonUtil.getRandomNumber(2);
			String randPath = userName + "_" + Calendar.getInstance().getTime().getTime() + "_" + rand;
			String[] sourceIds = queryFaceDto.getSourceIds();
			if (null == sourceIds || sourceIds.length == 0) {
				throw new Exception("请选择至少一个摄像头");
			}
			for (String idStr : sourceIds) {
				long id = Long.parseLong(idStr);
				queryFaceDto.setSourceId(id);
				List<ImageInfo> faceList = this.sharefaceService.findImageByCombinedParams(queryFaceDto, 1, 10001);
			
				if (null != faceList && !faceList.isEmpty()) 
				{
				 cameraMap.put(idStr, faceList);
				 totalSize = totalSize + faceList.size();
				 if (totalSize > 10000) {
				 	return new JsonObject("导出图片数目大于10000张，请重新设置过滤条件", 1002);
				}
				 }
			}

			ProcessInfo process = new ProcessInfo();
			process.setTotalSize(totalSize);
			GlobalConsts.downloadMap.put(key, process);
			GlobalConsts.stateMap.put(key, true);

			// save resultList
			Iterator<String> cameraIterator = cameraMap.keySet().iterator();
			boolean haveData = false;
			while (cameraIterator.hasNext()) {
				haveData = true;
				String idStr = cameraIterator.next();
				long id = Long.parseLong(idStr);
				List<ImageInfo> faceResultList = cameraMap.get(idStr);
				CameraInfo camera = getCameraInfoByCameraId(id);
				Area area = getStationByStationId(camera.getStationId());
				String appendPath = null;
				if (null != area) {
					appendPath = area.getAreaName() + "/" + camera.getName();
				} else {
					appendPath = camera.getName();
				}
				saveImageToDir(faceResultList, randPath, appendPath, process, key);
				if (!GlobalConsts.stateMap.get(key)) {
					GlobalConsts.stateMap.remove(key);
					return null;
				}
			}
            if(haveData){
            	compressZip(randPath);
            	String path = FileUtil.getZipHttpUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath
            			+ "/faceData.zip";
            	return new JsonObject(path);
            }else{
            	return new JsonObject("不存在导入数据", 1001);
            }
		} catch (Exception e) {
			LOG.error("export face error", e);
			return new JsonObject(e.getMessage(), 1001);
		}
	}

	@RequestMapping(value = "/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "分页获取指定摄像头最近抓拍图片")
	public JsonObject findByCameraId(@PathVariable("id") long id, @PathVariable("page") int page,
			@PathVariable("pagesize") int pagesize) {
		return new JsonObject(this._faceService.findBySourceId(id, page, pagesize));
	}

	@RequestMapping(value = "/station/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "获取指定派出所下所有摄像头最近抓拍图片")
	public JsonObject findByStationId(@PathVariable("id") long id) {
		List<CameraNearFaceDto> rsList = new ArrayList<CameraNearFaceDto>();
		List<String> filterList = new ArrayList<String>();
		filterList.add("station_id = "+id);
		List<CameraInfo> cameraList = this.cameraService.queryALLCameraInfoByConditions(filterList);
		if (cameraList.size() > 0) {
			for (CameraInfo camera : cameraList) {
				rsList.add(new CameraNearFaceDto(camera,
						this._faceService.findBySourceId(camera.getId(), 0, DEFAULT_PAGE_SIZE)));
			}
		}
		return new JsonObject(rsList);
	}

	@RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "Response a string describing if the face info image id is successfully get or not.")
	public JsonObject findByFromImageId(@PathVariable("id") long id) {
		return new JsonObject(this._faceService.findByFromImageId(id));
	}

	@RequestMapping(value = "/search/black/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "通过人脸检索检索相似嫌疑人")
	public JsonObject searchFaceInBankByBlackId(@PathVariable("id") long id,@PathVariable("page") int page,@PathVariable("pagesize") int pageSize) throws IOException {
		try {
			 List<FaceResultDto> faceList = _solrService.searchFaceByBlackId(id, DEFAULT_SCORE_THRESHOLD, GlobalConsts.BLACK_BANK_TYPE);
				if (faceList.size() == 0) {
					return new JsonObject("结果集为空", 1003);
				}
			 return new JsonObject(getPageList(faceList, page, pageSize));
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

	

	@RequestMapping(value = "/search/zip/camera/{key}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "ZIP导出指定cameras或静态库下搜索结果")
	public JsonObject createSearchZipCameraResults(@RequestBody @Valid SearchFaceDto searchFaceDto,
			@PathVariable("key") int key) {
		try {
			int totalSize = 0;
			ProcessInfo process = new ProcessInfo();
			GlobalConsts.downloadMap.put(key, process);
//			process.setFailedNum(totalSize - process.getSuccessNum());
			GlobalConsts.stateMap.put(key, true);
			Map<String, List<FaceResultDto>> cameraMap = new HashMap<String, List<FaceResultDto>>();
			Map<String, List<FaceResultDto>> bankMap = new HashMap<String, List<FaceResultDto>>();

			String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			int rand = CommonUtil.getRandomNumber(2);
			String randPath = userName + "_" + Calendar.getInstance().getTime().getTime() + "_" + rand;
			String[] sourceIds = searchFaceDto.getCameraIds();
			String[] bankIds = searchFaceDto.getBankIds();

			// 判断总数不能超过10000张
			if (null != sourceIds) {
				for (String idStr : sourceIds) {
					long id = Long.parseLong(idStr);
					searchFaceDto.setType(1);
					List<FaceResultDto> faceResultList = _solrService.getFaceByCameraId(searchFaceDto, id);
					if (null != faceResultList && !faceResultList.isEmpty()) {
						cameraMap.put(idStr, faceResultList);
						totalSize = totalSize + faceResultList.size();
						if (totalSize > 10000) {
							return new JsonObject("导出图片数目大于10000张，请重新设置过滤条件", 1002);
						}
					}
				}
			}
			if (null != bankIds) {
				for (String typeStr : bankIds) {
					int type = Integer.parseInt(typeStr);
					searchFaceDto.setType(type);
					List<FaceResultDto> faceResultList = _solrService.searchFaceByIdInBank(searchFaceDto);
					if (null != faceResultList && !faceResultList.isEmpty()) {
						bankMap.put(typeStr, faceResultList);
						totalSize = totalSize + faceResultList.size();
//						if (totalSize > 10000) {
//							return new JsonObject("导出图片数目大于10000张，请重新设置过滤条件", 1002);
//						}
					}
				}
			}

			process.setTotalSize(totalSize);
			
			// save resultList
			Iterator<String> cameraIterator = cameraMap.keySet().iterator();
			while (cameraIterator.hasNext()) {
				String idStr = cameraIterator.next();
				long id = Long.parseLong(idStr);
				List<FaceResultDto> faceResultList = cameraMap.get(idStr);
				CameraInfo camera = getCameraInfoByCameraId(id);
				Area area = getStationByStationId(camera.getStationId());
				String appendPath = null;
				if (null != area) {
					appendPath = area.getAreaName() + "/" + camera.getName();
				} else {
					appendPath = camera.getName();
				}
				saveSearchImageToDir(faceResultList, randPath, appendPath, process, key);
				if (!GlobalConsts.stateMap.get(key)) {
					GlobalConsts.stateMap.remove(key);
					return null;
				}
			}

			Iterator<String> bankIterator = bankMap.keySet().iterator();
			while (bankIterator.hasNext()) {
				String typeStr = bankIterator.next();
				int type = Integer.parseInt(typeStr);
				List<FaceResultDto> faceResultList = bankMap.get(typeStr);
				String coreName = GlobalConsts.kuMap.get(type);
				saveSearchImageToDir(faceResultList, randPath, coreName, process, key);
				if (!GlobalConsts.stateMap.get(key)) {
					GlobalConsts.stateMap.remove(key);
					return null;
				}
			}
			
			compressZip(randPath);
			String path = FileUtil.getZipHttpUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath
					+ "/faceData.zip";

            return new JsonObject(path);
        }catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            LOG.error("export face error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }


	
   /**
    * 搜索所有行政区域相似人脸
    * @param page
    * @param pageSize
    * @param searchFaceDto
    * @return
    * @throws IOException
    */
	@RequestMapping(value = "/search/face/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件所有行政区域进行人脸检索")
	public JsonObject searchFaceAllDistrictsByFaceId(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize,
			@RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
		try {
			Date startTime = new Date();
			String login = CurUserInfoUtil.getUserInfo().getLogin();
			List<FaceResultDto> faceResultList = _solrService.searchFaceByIdInBank(searchFaceDto);
            // 反恐库的搜索过滤
            int type = searchFaceDto.getType();
            if (type == 0 && searchFaceDto.getBankIds()[0].contains("fk-")) {               
                String nameOrCid = "";
                // searchFaceDto中的race字段用于反恐的情况 存储用户姓名或者身份证信息
                if (searchFaceDto.getRace() != null && !searchFaceDto.getRace().trim().equals("")) {
                    nameOrCid = searchFaceDto.getRace();
                }
                String[] fkType = searchFaceDto.getBankIds()[0].split("-");
                int[] fType = new int[fkType.length];
               
                for(int i=0;i<fkType.length-1;i++){
                    fType[i] = Integer.valueOf(fkType[i+1]).intValue();                   
                }                           
                List<FkPersonResultDto> fkResultList = filterByFktype(faceResultList, fType,nameOrCid);                           
                UserInfo userinfo = CurUserInfoUtil.getUserInfo();
                RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
                List resultPageList = getPageList(fkResultList, page, pageSize);
                Date endTime = new Date();
                long delyTime = endTime.getTime() - startTime.getTime();// 单位毫秒
                writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);
                return new JsonObject(resultPageList);               
                
            }

            UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();

			List resultPageList = getPageList(faceResultList, page, pageSize);
			Date endTime = new Date();
			long delyTime = endTime.getTime() - startTime.getTime();//单位毫秒
			
			writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);
			
			//搜索库的搜素结果得排除本身这一次的搜索记录
			if(searchFaceDto.getType()==10){
                String fids = searchFaceDto.getIds();
                if(fids.length()==0) {
                    String searchFaceUrl = getFaceUrlFromId(searchFaceDto.getFaceId(),Integer.valueOf(searchFaceDto.getDataType()));
                    Iterator<FaceResultDto> faceList = faceResultList.iterator(); 
                    while (faceList.hasNext()) { 
                        FaceResultDto faceNext = faceList.next();
                        HistorySearchOperationDetailDto historySearchOpe = _auditService.findSearchAuditDeatil(Long.parseLong(faceNext.getId()));
                        String searchUser = historySearchOpe.getOperator();
                        String imageDate = faceNext.getFile();
                        if(imageDate.equals(searchFaceUrl)&&searchUser.equals(login)){
                            faceList.remove();
                            System.out.println("搜索库结果统计中已过滤掉本次搜索图片");
                        }
                    }   
                } else {
                    for(int i = 0; i<fids.split(",").length; i++) {
                        String searchFaceUrl = getFaceUrlFromId(Long.valueOf(fids.split(",")[i]),Integer.valueOf(searchFaceDto.getDataType().split(",")[i]));
                        Iterator<FaceResultDto> faceList = faceResultList.iterator(); 
                        while (faceList.hasNext()) { 
                            FaceResultDto faceNext = faceList.next();
                            HistorySearchOperationDetailDto historySearchOpe = _auditService.findSearchAuditDeatil(Long.parseLong(faceNext.getId()));
                            String searchUser = historySearchOpe.getOperator();
                            String imageDate = faceNext.getFile();
                            if(imageDate.equals(searchFaceUrl)&&searchUser.equals(login)){
                                faceList.remove();
                                System.out.println("搜索库结果统计中已过滤掉本次搜索图片");
                            }
                        }
                    }
                }       
           }	
			return new JsonObject(resultPageList);
			//return new JsonObject(getPageList(faceResultList, page, pageSize));
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

    /**
     * 搜索指定行政区域相似人脸
     * 
     * @param page
     * @param pageSize
     * @param searchFaceDto
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/search/district/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件所有行政区域进行人脸检索")
    public JsonObject searchFaceDistrictByFaceId(@PathVariable("id") long id, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        List<FaceResultDto> returnList = new ArrayList<FaceResultDto>();
        try {
		Date startTime = new Date();
		List<FaceResultDto> faceResultList = _solrService
				.searchFaceByIdInBank(searchFaceDto);
		List<Long> dIdList = new ArrayList<Long>();
		dIdList.add(id);
        Set<Long> cIdList = dacService.findNodeIdsByNodeIds(dIdList,DistrictInfo.class,CameraInfo.class);
		if (null != cIdList && !cIdList.isEmpty() && null != faceResultList
				&& !faceResultList.isEmpty()) {

			List<Long> cList = cIdList.stream().map(s -> s.longValue()).collect(Collectors.toList());  
			
			int index = 0;
			if (page == 0) {
				page = 1;
			}
			for (FaceResultDto result : faceResultList) {
				long cId = result.getCamera();
				if (!cList.contains(cId)) {
					continue;
				}
				if ((index >= (page - 1) * pageSize)
						&& (index < page * pageSize)) {
					returnList.add(result);
				} else if (index >= page * pageSize) {
					break;
				}
				index++;
			}

			UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
			Date endTime = new Date();
			long delyTime = endTime.getTime() - startTime.getTime();
			writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);
            return new JsonObject(returnList);
            }
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        }catch (Exception e) {
            LOG.error("get seach data by district id error:", e);
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(returnList);
    }


	@RequestMapping(value = "/search/statistic", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中按摄像头分类进行人脸检索获取统计结果")
	public JsonObject searchFaceStatistic(@RequestBody @Valid SearchFaceDto searchFaceDto,
			@Context HttpServletRequest request) throws IOException {
		try {
			LOG.info("start statistis search");
			FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:" + searchFaceDto.getType() + " 开始统计!");
			FaceSearchStatisticDto faceSearchStatistic = _solrService.getFaceStatistic(searchFaceDto);
			FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:" + searchFaceDto.getType() + " 结束统计!");
			// 人脸检索记录日志
			try {
				int type = searchFaceDto.getType();
				if (type == 1) {
					String[] dataTypes = searchFaceDto.getDataType().split(",");
					String[] fids = searchFaceDto.getIds().split(",");
					for(int i = 0; i < dataTypes.length; i++) {
						long fid;
						if(fids.length<2) {
							fid = searchFaceDto.getFaceId();
						} else {
							fid = Long.valueOf(fids[i]);
						}
						String imageUrl = "";
						switch (Integer.parseInt(dataTypes[i])) {
						case 0: {
							imageUrl = blackDetailDao.findOne(fid).getImageData();
							break;
						}
						case 1: {
							imageUrl = sharefaceService.findOne(fid).getImageData();
							break;
						}
						case 2: {
							imageUrl = sharefaceService.findOne(fid).getImageData();
							break;
						}
						case 3: {
							imageUrl = cidDetailRepository.findOne(fid).getImageData();
							break;
						}
						case 4: {
							imageUrl = juZhuDetailRepository.findOne(fid).getImageData();
							break;
						}
						case 5: {
							imageUrl = otherDetailRepository.findOne(fid).getImageData();
							break;
						}
						case 6: {
							imageUrl = otherDetailRepository.findOne(fid).getImageData();
							break;
						}
						default: {
							imageUrl = otherDetailRepository.findOne(fid).getImageData();
							break;
						}
						}
						if (!"".equals(imageUrl)) {
							SearchRecord sr = new SearchRecord();
							sr.setFaceUrl(imageUrl);
							sr.setIp(request.getRemoteAddr());
							_searchRecordDao.save(sr);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("记录人脸检索操作日志出现错误" + e.getMessage());
			}
			// 人脸检索记录日志
			return new JsonObject(faceSearchStatistic);
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        }catch (Exception e) {
            e.printStackTrace();
            LOG.error("exception:", e);
            LOG.error("exception:" + e);

		}
		return new JsonObject(null);
	}

	@RequestMapping(value = "/search/face/camera/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中按摄像头分类进行人脸检索")
	public JsonObject searchFaceForCamera(@PathVariable("page") int page, @PathVariable("pagesize") int pagesize,
			@RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
		try {
			Date startTime = new Date();
			List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceId(searchFaceDto, 0, 2000000000);
			rsList = getPageList(rsList, page, pagesize);
			if(null != rsList) {
			    for(FaceResultByCameraDto rs : rsList) {
			        int size = rs.getFaceResult().size();
			        List<FaceResultDto> newRsList = rs.getFaceResult().subList(0, pagesize>size?size:pagesize);
			        rs.setFaceResult(newRsList);
			    }
			}
			
			UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
			Date endTime = new Date();
			long delyTime = endTime.getTime() - startTime.getTime();
			writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);
			
			return new JsonObject(rsList);
		} catch (MsgException e) {
			return new JsonObject(e.getMessage(), 1001);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonObject(null);
	}
	/**
	 * 支持1.2后搜索结果重点人员按库分类显示
	 * @param page
	 * @param pagesize
	 * @param searchFaceDto
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/search/face/black/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在重点人员库中检索")
	public JsonObject searchFaceForBlack(@PathVariable("page") int page, @PathVariable("pagesize") int pagesize,
			@RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
		try {
			Date startTime = new Date();
			//String authority = ","+_userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE)+",";
			List<BlackFaceResultDto> blackList = new ArrayList<BlackFaceResultDto>();
			List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceId(searchFaceDto, 0, 2000000000);
		    	FaceResultByCameraDto rs = rsList.get(0);
				List<FaceResultDto> newRsList = rs.getFaceResult();
				if(null != newRsList && !newRsList.isEmpty()){
					//newRsList = getPageList(newRsList, page, pagesize);
					int index = 0;
					if(page == 0){
						page = 1;
					}
					for(FaceResultDto item : newRsList){
						try{
							String bIdStr = item.getId();
							Long bId;
							if(bIdStr.indexOf("_") > 0){
							 bId = Long.parseLong(bIdStr.split("_")[1]);	
							}else{
							 bId = Long.parseLong(bIdStr);
							}
							BlackDetail bd = blackDetailDao.findOne(bId);
							if(null == bd){
								LOG.error("get black data error solr exist db not exist,bId:"+item.getId());
								continue;
							}
							long bankId = bd.getBankId();
						/*	if(authority.indexOf(","+bankId+",") < 0){
								LOG.info("get black data bank not authorization for this user,bankId:"+bankId);
								continue;
							}*/
							BlackBank bb = bankDao.findOne(bankId);
							if(bb == null || bb.getListType() == 1){
								LOG.info("get black data bank is not exist or is white data,bankId:"+bankId);
								continue;
							}
							BlackFaceResultDto bDto = new BlackFaceResultDto(item, bd.getBankId());
                            if((index >= (page-1)*pagesize) && (index < page*pagesize)){
                            	blackList.add(bDto);
							}else if(index >= page*pagesize){
								break;
							}
							index++;
						}catch(Exception e){
							LOG.error("get black data error bId:"+item.getId()+" error:",e);
						}
						
					}
				}
				
			Map<Long, List<BlackFaceResultDto>> detailMap = blackList.stream().collect(Collectors.groupingBy(BlackFaceResultDto::getBankId));
			UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
			Date endTime = new Date();
			long delyTime = endTime.getTime() - startTime.getTime();
			writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

			return new JsonObject(detailMap);
        }catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

	
	
	
	/**
	 * @author shixiaohua
	 * 区域图片搜索终极接口，可支持多人多个区域(district,area)搜索,支持两种返回结果(按纯图片返回和按摄像头列表返回)
	 * @param searchFaceDto 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/search/districtAreas", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "区域图片搜索终极接口")
    public JsonObject searchFaceByDistrictAreas(@RequestBody @Valid SearchFaceDto searchFaceDto)
            throws IOException {
        try {
            String nodeIds = searchFaceDto.getNodeIds();
            String[] nodeArr = nodeIds.split(",");
            List<Long> nodeIdList = new ArrayList<Long>();
            for(String item : nodeArr){
                nodeIdList.add(Long.parseLong(item));
            }
            String nodeType = searchFaceDto.getNodeType();
            int returnType = searchFaceDto.getReturnType();
            
           if(1 == returnType){
                   //返回类型为纯图片数据集合
                   List<FaceResultDto> returnList = new ArrayList<FaceResultDto>();
                   Date startTime = new Date();
                   List<FaceResultDto> faceResultList = _solrService
                           .searchFaceByIdInBank(searchFaceDto);
                   Set<Long> cIdList = null;
                   if(GlobalConsts.nodeType_district.equals(nodeType)){
                      cIdList = dacService.findNodeIdsByNodeIds(nodeIdList,DistrictInfo.class,CameraInfo.class);
                   }else if(GlobalConsts.nodeType_area.equals(nodeType)){
                      cIdList = dacService.findNodeIdsByNodeIds(nodeIdList,Area.class,CameraInfo.class);
                   }
                   
                   if (!CollectionUtils.isEmpty(cIdList) && !CollectionUtils.isEmpty(faceResultList)) {
                       List<Long> cList = cIdList.stream().map(s -> s.longValue()).collect(Collectors.toList());  
                       int index = 0;
                       int page = searchFaceDto.getPage();
                       int pageSize = searchFaceDto.getPageSize();
                       if (page == 0) {
                           page = 1;
                       }
                       for (FaceResultDto result : faceResultList) {
                           long cId = result.getCamera();
                           if (!cList.contains(cId)) {
                               continue;
                           }
                           if ((index >= (page - 1) * pageSize)
                                   && (index < page * pageSize)) {
                               returnList.add(result);
                           } else if (index >= page * pageSize) {
                               break;
                           }
                           index++;
                       }
                       UserInfo userinfo = CurUserInfoUtil.getUserInfo();
                       RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
                       Date endTime = new Date();
                       long delyTime = endTime.getTime() - startTime.getTime();
                       writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);
                       return new JsonObject(returnList);
                       
                   }
               }
           else if(2 == returnType){
               //返回按摄像头列表集合
               Set<Long> aIdList = null;
               if(GlobalConsts.nodeType_district.equals(nodeType)){
                   aIdList = dacService.findNodeIdsByNodeIds(nodeIdList,DistrictInfo.class,Area.class);
                }else if(GlobalConsts.nodeType_area.equals(nodeType)){
                   aIdList = dacService.findNodeIdsByNodeIds(nodeIdList,Area.class,Area.class);
                }
               if(!CollectionUtils.isEmpty(aIdList)){
                   Date startTime = new Date();
                   List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceIdAreas(searchFaceDto, aIdList, 2000000000);
                   int page = searchFaceDto.getPage();
                   int pageSize = searchFaceDto.getPageSize();
                   if (page == 0) {
                       page = 1;
                   }
                   rsList = getPageList(rsList, page, pageSize);
                   if(null != rsList) {
                       for(FaceResultByCameraDto rs : rsList) {
                           int size = rs.getFaceResult().size();
                           List<FaceResultDto> newRsList = rs.getFaceResult().subList(0, pageSize>size?size:pageSize);
                           rs.setFaceResult(newRsList);
                       }
                   }

                   UserInfo userinfo = CurUserInfoUtil.getUserInfo();
                   RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
                   Date endTime = new Date();
                   long delyTime = endTime.getTime() - startTime.getTime();
                   writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

                   return new JsonObject(rsList);
               }
           }
               
        }catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            LOG.error("searchFaceByDistrictAreas error nodeIds:"+searchFaceDto.getNodeIds()+",nodeType:"+
            searchFaceDto.getNodeType()+",returnType:"+searchFaceDto.getReturnType());
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(null);
    }
	
	
	@RequestMapping(value = "/search/station/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件获取指定派出所的人脸检索结果")
	public JsonObject searchFaceForCameraByStationId(@PathVariable("id") long id, @PathVariable("page") int page,
			@PathVariable("pagesize") int pagesize, @RequestBody @Valid SearchFaceDto searchFaceDto)
			throws IOException {
		try {
			Date startTime = new Date();
			List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceId(searchFaceDto, id, 2000000000);
			rsList = getPageList(rsList, page, pagesize);
			if(null != rsList) {
			    for(FaceResultByCameraDto rs : rsList) {
			        int size = rs.getFaceResult().size();
			        List<FaceResultDto> newRsList = rs.getFaceResult().subList(0, pagesize>size?size:pagesize);
			        rs.setFaceResult(newRsList);
			    }
			}

			UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
			Date endTime = new Date();
			long delyTime = endTime.getTime() - startTime.getTime();
			writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

			return new JsonObject(rsList);
        }catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }


	@RequestMapping(value = "/search/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件依据摄像头分页获取人脸检索结果")
	public JsonObject searchFaceByCameraId(@PathVariable("id") long id, @PathVariable("page") int page,
			@PathVariable("pagesize") int pagesize, @RequestBody @Valid SearchFaceDto searchFaceDto)
			throws IOException {
		try {
			Date startTime = new Date();
			List<FaceResultDto> faceResultList = _solrService.getFaceByCameraId(searchFaceDto, id);

			UserInfo userinfo = CurUserInfoUtil.getUserInfo();
			RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
			Date endTime = new Date();
			long delyTime = endTime.getTime() - startTime.getTime();
			writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

			return new JsonObject(getPageList(faceResultList, page, pagesize));

        }catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

	
	@RequestMapping(value = "/search/time/range", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "通过人脸检索条件获取人脸检索结果的时间范围")
	public JsonObject searchFaceTimeRange(@RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
		try {
			List<FaceResultDto> faceResultList = _solrService.searchFaceByIdInBank(searchFaceDto);
			List<Date> dataList = new ArrayList<Date>();
			if (faceResultList.size() == 0) {
				dataList.add(new Date());
				dataList.add(new Date());
			} else {
				dataList.add(faceResultList.get(faceResultList.size() - 1).getTime());
				dataList.add(faceResultList.get(0).getTime());
			}
            return new JsonObject(dataList);
        }catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonObject(null);
	}

	// @RequestMapping(value = "/search/again", method = RequestMethod.POST,
	// consumes = MediaType.APPLICATION_JSON)
	// @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件进行人脸再检索")
	// public JsonObject searchFaceInfoAgain(@RequestBody @Valid FaceSearchDto
	// faceSearchDto) throws IOException {
	// Date now = new Date();
	// int depth = 0;
	// Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
	// ArrayList<SimpleResult> allList = new ArrayList<SimpleResult>();
	// List<FaceInfoDto> facelist = new ArrayList<FaceInfoDto>();
	// IndexReader ir = DirectoryReader.open(FSDirectory.open(new
	// File("lireIndex")));
	// GenericFastImageSearcher searcher = (GenericFastImageSearcher)
	// ImageSearcherFactory.createIFACEFImageSearcher(1000);
	// LOG.info("索引数量:" + ir.numDocs());
	//
	// for (String face_id : faceSearchDto.getIds().split(",")) {
	// FaceInfo searchFace =
	// this.faceInfoDaoImpl.findOne(Long.valueOf(face_id));
	// LOG.info("待搜索人脸ID:" + searchFace.getId());
	// ImageSearchHits hits = searcher.search(searchFace.getFaceFeature(), ir,
	// faceSearchDto.getScoreThreshold(), indexMap);
	//
	// // --------------------------------
	// ArrayList<SimpleResult> rsList = hits.getResults();
	// for (int i = 0; i < rsList.size(); i++) {
	// indexMap.put(rsList.get(i).getIndexNumber(), 1);
	// }
	// allList.addAll(rsList);
	//
	// while (hits.length() > 0 && (depth < faceSearchDto.getDepth() ||
	// faceSearchDto.getDepth() < 0)) {
	// ArrayList<SimpleResult> tempList = new ArrayList<SimpleResult>();
	// for (int i = 0; i < hits.length(); i++) {
	// byte[] feature =
	// hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IFACEF).binaryValue().bytes;
	// ImageSearchHits rehits = searcher.search(feature, ir,
	// Float.valueOf(faceSearchDto.getScoreThreshold()), indexMap);
	// for (int j = 0; j < rehits.getResults().size(); j++) {
	// indexMap.put(rehits.getResults().get(j).getIndexNumber(), 1);
	// }
	// tempList.addAll(rehits.getResults());
	// }
	// hits = new SimpleImageSearchHits(tempList, 100);
	// allList.addAll(hits.getResults());
	// depth++;
	// }
	// }
	// // --------------------------------
	//
	// LOG.info("检索计算完成..");
	// if (allList.size() > 0) {
	// facelist = this._faceService.findByImageSearchHits(new
	// SimpleImageSearchHits(allList, 100), faceSearchDto.getStartTime(),
	// faceSearchDto.getEndTime());
	// LOG.info("查询 cost time:" + (new Date().getTime() - now.getTime()));
	// }
	// return new JsonObject(facelist);
	// }

	// @RequestMapping(value = "/query", method = RequestMethod.POST, consumes =
	// MediaType.APPLICATION_JSON)
	// @ApiOperation(httpMethod = "POST", value = "人脸查询")
	// public JsonObject queryFaceInfo(@RequestBody @Valid FaceQueryDto
	// faceSearchDto) throws Exception {
	// LOG.info("开始人脸查询..");
	// List<FaceQueryDto> facelist = null;
	// try {
	//
	// facelist = this._faceService.findByCombinedConditions(faceSearchDto);
	// } catch (Exception e) {
	// LOG.error("", e);
	// }
	// LOG.info("查询得到人脸数量:" + facelist.size());
	// return new JsonObject(facelist);
	// }

	@RequestMapping(value = "/search/face/area/{areaId}/page/{page}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "查找某个图片在指定常住人口区域的事件流")
	public JsonObject searchFacesByResidentAreaId(@PathVariable("page") int page, @PathVariable("areaId") long areaId,
			@RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
		List<FaceResultDto> returnList = new ArrayList<FaceResultDto>();
		try {
			ResidentArea area = residentAreaRepository.findOne(areaId);
			String cameraIds = area.getCameraIds();
			String[] ids = cameraIds.split(",");
			if (null != ids) {
				for (String item : ids) {
					long cameraId = Long.parseLong(item);
					List<FaceResultDto> rsList = _solrService.getFaceByCameraId(searchFaceDto, cameraId, page,
							DEFAULT_PAGE_SIZE);
					if (null != rsList) {
						returnList.addAll(rsList);
					}
				}
			}
			Collections.sort(returnList, new FaceResultDtoComparable("0"));
			return new JsonObject(returnList.subList((page - 1) * DEFAULT_PAGE_SIZE,
					returnList.size() > DEFAULT_PAGE_SIZE ? DEFAULT_PAGE_SIZE : returnList.size()));
		}catch (RedException e) {
            return new JsonObject(e, 1004);
        }catch (Exception e) {
			LOG.error("get resident person faces error", e);
			return new JsonObject(null);
		}

	}

	@RequestMapping(value = "/search/camera/{id}/ids/{ids}", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "获取摄像头最近抓拍一张人脸并检索指定ids的摄像头集内各检索一张最相似人脸")
	public JsonObject searchLastFaceAndHistory(@RequestParam("hours") int hours, @PathVariable("id") long id, @PathVariable("ids") String ids)
			throws IOException {
		try {
			// Map<Long, FaceResultDto> rsMap = new HashMap<Long,
			// FaceResultDto>();
			List<FaceResultDto> faceRsList = new ArrayList<FaceResultDto>();
			List<FaceInfo> faceList = this.sharefaceService.findBySourceId(id, 0, 1);
			if (null == faceList || faceList.size() == 0)
				return new JsonObject(null);
			FaceResultDto face = new FaceResultDto();
			face.setId(faceList.get(0).getId() + "");
			face.setCamera(id);
			face.setScore(1);
			// face.setTime(new
			// Date(faceList.get(0).getTime().getTime()+28800000));
			face.setTime(faceList.get(0).getTime());
			face.setType(1);
			// setBackgroundImage
			long fromImageId = faceList.get(0).getFromImageId();
			ImageInfo imageInfo = shareImageService.findById(fromImageId);
			face.setFile_bg(imageInfo.getUri());
			//
			face.setFile(faceList.get(0).getImageData());
			// rsMap.put(id, face);
			faceRsList.add(face);
			int k = 0;
			for (String cameraId : ids.split(",")) {
				List<FaceResultDto> rsList = _solrService.searchFaceByIdInCamera(faceList.get(0).getId(),
						Integer.valueOf(cameraId.split("_")[1]) / 100F, GlobalConsts.IN_CAMERA_TYPE,
						Long.valueOf(cameraId.split("_")[0]),Integer.valueOf(cameraId.split("_")[2]));
				// while (rsList.size() > 0 && new Date().getTime() -
				// rsList.get(0).getTime().getTime() > 36000000) {
				// rsList.remove(0);
				// }
				if (null == rsList || rsList.size() == 0) {
					// rsMap.put(Long.valueOf(cameraId), null);
					if (k == 0) {
						faceRsList.add(face);
					} else {
						faceRsList.add(null);
					}
				} else {
					// rsMap.put(Long.valueOf(cameraId), rsList.get(0));
					faceRsList.add(rsList.get(0));
				}
				k++;
			}
			return new JsonObject(faceRsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonObject(null);
	}

	

	/**
	 * 将搜索结果图片导出到指定目录
	 * 
	 * @param faceResultList
	 * @throws Exception
	 */
	private void saveSearchImageToDir(List<FaceResultDto> faceResultList, String randPath, String fileName,
			ProcessInfo process, int key) throws Exception {
		File file = new File(
				FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath + "/" + fileName + "/");
		FileUtil.checkFileExist(file);
		int j = 0;
		for (FaceResultDto item : faceResultList) {
			if (!GlobalConsts.stateMap.get(key)) return;
			boolean state = false;
			try {
				long imageId = 0;
				String id = item.getId();
				if (id.indexOf("_") != -1) {
					BlackDetail detail = this.blackDetailDao.findOne(Long.parseLong(id.substring(2, id.length())));
					imageId = detail.getFromImageId();
				} else {
					FaceInfo face = this.sharefaceService.findOne(Long.parseLong(item.getId()));
					imageId = face.getFromImageId();
				}
				ImageInfo ii = (ImageInfo) shareImageService.findById(imageId);
				String url = ii.getUri();
				/*if (!CommonUtil.checkImage(url)) {
					continue;
				}*/
				Date time = ii.getTime();
				String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);
				String fullFileName = FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath + "/"
						+ fileName + "/" + timeStr + "_" + j + ".jpg";
			
				
				//InputStream is = FileUtil.readStreamFromUri(url);
				boolean status = FileUtil.copyUrl(url, fullFileName);
				if(status){
					process.setSuccessNum(process.getSuccessNum() + 1);
				}else{
					process.setFailedNum(process.getFailedNum() + 1);
					FileUtil.deleteFile(new File(fullFileName), true);
					saveSmallImageToDir(item, randPath, "未知原因失败图片", timeStr, j);
					continue;
				}
				j++;

			} catch (Exception e) {
				LOG.error("download one image error,id:" + item.getId(), e);
				process.setFailedNum(process.getFailedNum() + 1);
			}

		}
	}
	
	private void saveSmallImageToDir(FaceResultDto item, String randPath, String folderName, String timeStr, int j) throws Exception {
		String fullFileName = FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath + "/"
				+ folderName + "/" + timeStr + "_" + j + ".jpg";
		 FileUtil.copyUrl(item.getFile(), fullFileName);
	}

	private void compressZip(String randPath) throws Exception {
		File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath + "/");
		FileUtil.deleteFile(file, true);
		FileUtil.checkFileExist(file);
		FileUtil.zipCompress(FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath,
				FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath + "/faceData.zip");
		File zipFile = new File(
				FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath + "/faceData.zip");
		if (!zipFile.exists()) {
			throw new Exception("压缩失败");
		}
	}

	private void saveImageToDir(List<ImageInfo> faceList, String randPath, String appendName, ProcessInfo process, int key)
			throws Exception {
		File file = new File(
				FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath + "/" + appendName + "/");
		FileUtil.checkFileExist(file);
		int j = 0;
		for (ImageInfo item : faceList) {
			if (!GlobalConsts.stateMap.get(key)) return;
			boolean state = false;
			try {
				String url = item.getUri();
				Date time = item.getTime();
				String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);
				String fullFileName = FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/image/" + randPath + "/"
						+ appendName + "/" + timeStr + "_" + j + ".jpg";
				//InputStream is = FileUtil.readStreamFromUri(url);
				boolean status = FileUtil.copyUrl(url, fullFileName);
				if(status){
					process.setSuccessNum(process.getSuccessNum() + 1);
				}else{
					process.setFailedNum(process.getFailedNum() + 1);
					FileUtil.deleteFile(new File(fullFileName), true);
				}
				j++;
			} catch (Exception e) {
				process.setFailedNum(process.getFailedNum() + 1);
				LOG.error("download one image error,id:" + item.getId(), e);
				continue;
			}
		}
	}

	public List getPageList(List list, int page, int pageSize) {
		int start = (page - 1) * pageSize;
		if (start < 0)
			start = 0;
		if (start > list.size() - 1)
			return null;
		int end = page * pageSize;
		if (end < 0)
			return null;
		if (end > list.size())
			end = list.size();
		return list.subList(start, end);
	}

	private void filterAge(List<FaceInfo> faceList, int age) {
		if (null != faceList) {
			for (int i = faceList.size() - 1; i >= 0; i--) {
				FaceInfo face = faceList.get(i);
				int resultAge = face.getAge();
				if (0 != age && 0 != resultAge && !CommonUtil.checkAge(age, resultAge)) {
					faceList.remove(i);
					continue;
				}
			}
		}
	}

	// 记录搜索操作
	public void writeSearchLog(UserInfo userinfo, RoleInfo roleinfo,
			SearchFaceDto searchFaceDto, long timeDelay) throws SQLException {

		Long uid = userinfo.getId();
		Long policeStationId = userinfo.getPoliceStationId();
		String stationname = policestationDao.findOne(policeStationId)
				.getStationName(); // 单位名称
		String userrealname = userinfo.getName();
		String accounttype = roleinfo.getCnName();
		String owner = userinfo.getLogin();

		// 记录检索的操作日志表
		LOG.info("EntityAuditListener->touchForCreate->Auditable search!!!");

		String ids = searchFaceDto.getIds(); // 先判断看是不是多张照片一起搜索的情况
		String datatypes = searchFaceDto.getDataType();
		ArrayList<Long> faceids = new ArrayList<Long>();
		ArrayList<String> faceurl = new ArrayList<String>();
		ArrayList<String> datatype = new ArrayList<String>();
		ArrayList<String> faceFeature = new ArrayList<String>();
		ArrayList<Integer> version = new ArrayList<Integer>();

		for (int j = 0; j < datatypes.split(",").length; j++) {

			datatype.add(datatypes.split(",")[j]);

		}

		if (ids.equals("")) {

			long faceid = searchFaceDto.getFaceId();
			faceids.add(faceid);

		} else {
			String f[] = ids.split(",");
			for (int j = 0; j < f.length; j++) {
				faceids.add(Long.parseLong(f[j]));
			}

		}

		try {
			List<SolrInputDocument> docsList = new ArrayList<SolrInputDocument>();
			HttpSolrClient solrServer = _solrDataServiceItf
					.getServer(GlobalConsts.coreMap
							.get(GlobalConsts.SEARCH_INFO_TYPE));

			for (int i = 0; i < faceids.size(); i++) {

				AuditLogInfo log = new AuditLogInfo();
				SearchLogInfo searchLog = new SearchLogInfo();

				long fid = (long) faceids.get(i);

				log.setOwner(owner);
				log.setOperation("search");
				log.setObject(datatype.get(i));
				log.setObjectId(fid);
                log.setObject_status(17);
				log.setTitle(log.getOwner() + "检索了图片信息," + userrealname + ","
						+ stationname);
				log.setTitle(log.getOwner() + "检索了图片信息," + userrealname + "," + stationname);
                searchLog.setCreated(new Date());
                searchLog.setResultCode(0);
                searchLog.setOwner(owner);
                searchLog.setTimeDelay(timeDelay);

				switch (Integer.parseInt(datatype.get(i).toString())) {

				case 0: {

					String authority = _userService
							.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
					if (authority.trim().length() == 0) {
						System.out.println("没权限查询黑名单库");
						return;
					}
					BlackDetail blackDetail = blackDetailDao.findOne(fid,
							authority.split(",")).get(0);
					faceFeature.add(blackDetail.getBase64FaceFeature());
					faceurl.add(blackDetail.getImageData());
					version.add(blackDetail.getVersion());
					break;
				}
				case 1: {
					FaceInfo faceInfo = _faceService.findOne(fid);
					faceFeature.add(faceInfo.getBase64FaceFeature());
					faceurl.add(faceInfo.getImageData());
					version.add(faceInfo.getVersion());
					break;
				}
				case 2: {
					FaceInfo faceInfo = _faceService.findOne(fid);
					faceFeature.add(faceInfo.getBase64FaceFeature());
					faceurl.add(faceInfo.getImageData());
					version.add(faceInfo.getVersion());
					break;
				}
				case 3: {
					CidDetail cidDetail = cidDetailRepository.findOne(fid);
					faceFeature.add(cidDetail.getBase64FaceFeature());
					faceurl.add(cidDetail.getImageData());
					version.add(cidDetail.getVersion());
					break;
				}
				case 4: {
					JuZhuDetail juzhuDetail = juZhuDetailRepository
							.findOne(fid);
					faceFeature.add(juzhuDetail.getBase64FaceFeature());
					faceurl.add(juzhuDetail.getImageData());
					version.add(juzhuDetail.getVersion());
					break;
				}
				case 5: {
					OtherDetail otherDetail = otherDetailRepository
							.findOne(fid);
					faceFeature.add(otherDetail.getBase64FaceFeature());
					faceurl.add(otherDetail.getImageData());
					version.add(otherDetail.getVersion());
					break;
				}
				case 6: {
					OtherDetail otherDetail = otherDetailRepository
							.findOne(fid);
					faceFeature.add(otherDetail.getBase64FaceFeature());
					faceurl.add(otherDetail.getImageData());
					version.add(otherDetail.getVersion());
					break;
				}
				default: {
					OtherDetail otherDetail = otherDetailRepository
							.findOne(fid);
					faceFeature.add(otherDetail.getBase64FaceFeature());
					faceurl.add(otherDetail.getImageData());
					version.add(otherDetail.getVersion());
					break;
				}
				}

				log.setMessage(accounttype + owner + "检索了图片信息，"
						+ faceurl.get(i));
				searchLog.setMessage("检索了图片信息，" + faceurl.get(i));

				// 把用户和对应的搜索图片存进map 不重复记日志

				// if(!userSearchMap.containsKey(userinfo.getLogin()+faceurl)){
				if (!userSearchMap.containsKey(userinfo.getLogin()
						+ faceurl.get(i))) {
					SearchReasonDto srd = GlobalConsts.searchReasonMap
							.get(userinfo.getId());
					if (null != srd) {
						SearchReason sr = reasonDao.findOne(srd.getReasonId());
						log.setFriDetail(null == sr ? "" : sr.getrName());
						log.setSecDetail(srd.getReasonDetail());
						auditLogRepository.save(log);
						searchLogDao.save(searchLog);
				
				   if(log.getId()==0){
                    	System.out.println("audit log id =0 非正常情况");
                    	LOG.info("audit log id =0 非正常情况 ");
                    	continue;
                    }
				   // 建索引
				   	byte[] feature = DatatypeConverter.parseBase64Binary(faceFeature.get(i));
					if (null == feature || feature.length != 724) continue;
					SolrInputDocument solrDoc = new SolrInputDocument();
					solrDoc.addField("id", log.getId());  //id取t_audit_log的id 不取faceid 因为faceid会重复   如果用camera字段记录faceid又需要另一个字段来记datatype  
					solrDoc.addField("time", log.getCreated());
					solrDoc.addField("file", faceurl.get(i));
					solrDoc.addField("type", 10);
					solrDoc.addField("feature", faceFeature.get(i));
					//solrDoc.addField("camera", other.getFromCidId());
					solrDoc.addField("version", version.get(i));
					docsList.add(solrDoc);
					
					solrServer.add(docsList);
					solrServer.commit(true, true, true);
					docsList.clear();
					LOG.info("搜索数据已经成功索引 ");
                    
				}
			}
			userSearchMap.put(userinfo.getLogin() + faceurl.get(i), i);			
		}

		
	} catch (Exception e) {
		e.printStackTrace();
	}

}
	
	
	 private String getFaceUrlFromId(long faceId, int datatype) throws Exception {
			String faceUrl = "";
			try {
				switch(datatype) {
				case 0: {
					String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
					if(authority.trim().length()==0){
		        		return faceUrl;
		        	}
					faceUrl = this.blackDetailDao.findOne(faceId, authority.split(",")).get(0).getImageData();
					break;
				}
				case 1: {
					faceUrl = this._faceService.findOne(faceId).getImageData();
					break;
				}
				case 2: {
					faceUrl = this._faceService.findOne(faceId).getImageData();
					break;
				}
				case 3: {
					faceUrl = this.cidDetailRepository.findOne(faceId).getImageData();
					break;
				}
				case 4: {
					faceUrl = this.juZhuDetailRepository.findOne(faceId).getImageData();
					break;
				}
				case 5: {
					faceUrl = this.otherDetailRepository.findOne(faceId).getImageData();
					break;
				}
				case 6: {
					faceUrl = this.otherDetailRepository.findOne(faceId).getImageData();
					break;
				}
				default: {
					faceUrl = this.otherDetailRepository.findOne(faceId).getImageData();
					break;
				}
				}
			}catch (Exception e) {
				LOG.error("faceId :"+faceId+" datatype:"+datatype +" 数据不存在！", e);
				throw new MsgException(Constants.error_face_null);
			}
			return faceUrl;
		}
	
	
	/**
	 * 同步批量把图片进行Base64编码
	 */
	public static void synImageBase64(Collection<? extends CidInfoDto> cidInfoCollection) {
		long currentTime = System.currentTimeMillis();

		// 如果cidInfoCollection为空，则直接返回
		if (null == cidInfoCollection || cidInfoCollection.isEmpty()) {
			return;
		}

		// 将图片Base64编码任务提交给线程池
		List<ForkJoinTask<?>> forkJoinTaskList = new ArrayList<ForkJoinTask<?>>();
		for (CidInfoDto cidInfo : cidInfoCollection) {
			forkJoinTaskList.add(ApplicationResource.THREAD_POOL.submit(new ImageBase64Runable(cidInfo)));
		}

		// 等待线程池运行完提交的任务
		for (ForkJoinTask<?> task : forkJoinTaskList) {
			task.join();
		}
		LOG.info(
				"xxxxxx synImageBase64 need {}ms, cidInfoCollection size is {}, pool size is {}, pool parallelism is {}",
				System.currentTimeMillis() - currentTime, cidInfoCollection.size(), ApplicationResource.THREAD_POOL.getPoolSize(),
				ApplicationResource.THREAD_POOL.getParallelism());
	}

	/**
	 * 将image进行Base64编码类
	 * 
	 * @author pengqirong
	 */
	private static class ImageBase64Runable implements Runnable {

		private CidInfoDto cidInfo = null;

		public ImageBase64Runable(CidInfoDto cidInfo) {
			super();
			this.cidInfo = cidInfo;
		}

		@Override
		public void run() {
			if (null != cidInfo) {
				long currentTime = System.currentTimeMillis();
				try {
					if (cidInfo.isNeedPhotoBase64()) {
						cidInfo.setImageBase64(cidInfo.getPhotoBase64());
					} else {
						cidInfo.setImageBase64(FileUtil.GetImageStr(cidInfo.getFile()));
					}
				} catch (Throwable e) {
					LOG.error("image to Base64 catch exception: ", e);
				}
				long needTime = System.currentTimeMillis() - currentTime;
				if (needTime >= 500) {
					LOG.info("xxxxxx image to Base64 need {}ms, file is {}, photo is {}, needPhotoBase64 is {}",
							System.currentTimeMillis() - currentTime, cidInfo.getFile(), cidInfo.getPhoto(),
							cidInfo.isNeedPhotoBase64());
				}
			}
		}
	};

    // 搜索重点人员库时 根据 反恐子类进行过滤
    public List<FkPersonResultDto> filterByFktype(List<FaceResultDto> faecList, int[] fkType, String nameOrCid) {
        Iterator<FaceResultDto> face = faecList.iterator();
        List<FkPersonResultDto> searchFkFaceList = new ArrayList<FkPersonResultDto>();
        while (face.hasNext()) {
            FaceResultDto faceNext = face.next();
            BlackDetail blackDetail = blackDetailDao.findById(Long.valueOf(faceNext.getId()).longValue()).get(0);
            long personDetailId = blackDetail.getFromPersonId();
            PersonDetail personDetail = personDetailDao.findOne(personDetailId);
            if (personDetail != null) {
                int fType = personDetail.getFkType();
                boolean flag = false;
                for (int i = 0; i < fkType.length; i++) {
                    if (fType == fkType[i]) {
                        flag = true;
                    }
                }
                if (flag) {
                    FkPersonResultDto fkFace = new FkPersonResultDto();
                    fkFace.setCid(personDetail.getCid());
                    fkFace.setId(personDetailId);
                    fkFace.setNation(personDetail.getNation());
                    fkFace.setPhotoData(personDetail.getPhotoData());
                    fkFace.setRealName(personDetail.getRealName());
                    fkFace.setBlackDetailId(faceNext.getId());
                    List<FkPersonAttr> fkPersonAttr = fkPersonAttrDao.findByFromPersonId(personDetailId);
                    fkFace.setRegisterAddress(fkPersonAttr.get(0).getRegisterAddress());
                    searchFkFaceList.add(fkFace);

                    if (!nameOrCid.trim().equals("")) {
                        if (personDetail.getCid().equals(nameOrCid) || personDetail.getRealName().equals(nameOrCid)) {
                            List<FkPersonResultDto> FkFace = new ArrayList<FkPersonResultDto>();
                            FkFace.add(fkFace);
                            return FkFace;
                        }
                    }
                }
            }

        }

        return searchFkFaceList;

    }
    
    @RequestMapping(value = "/dayun/param/cameras/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取指定摄像头列表最近抓拍图片")
    public JsonObject findByDayunParamCameraId(@PathVariable("page") int page,
                                          @PathVariable("pagesize") int pagesize,
                                          @RequestBody @Valid QueryFaceDto queryFaceDto) {
        try {
            List<FaceInfo> faceList = null;
            faceList = this.sharedFaceService.findByMultipleCamerasForDayun(queryFaceDto, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by multiple cameras error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }
}