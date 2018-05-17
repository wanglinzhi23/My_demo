package intellif.controllers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.wordnik.swagger.annotations.ApiOperation;

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
import intellif.dao.IFaceConfigDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.JuZhuInfoDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.OtherInfoDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.RedDetailDao;
import intellif.dao.ResidentAreaDao;
import intellif.dao.RoleDao;
import intellif.dao.SearchLogDao;
import intellif.dao.SearchReasonDao;
import intellif.dao.SearchRecordDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.database.entity.Area;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.BlackFaceResultDto;
import intellif.dto.CameraCodeDto;
import intellif.dto.CameraIdListDto;
import intellif.dto.CameraNearFaceDto;
import intellif.dto.CidInfoDto;
import intellif.dto.FaceCompareDto;
import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceSearchStatisticDto;
import intellif.dto.HistorySearchOperationDetailDto;
import intellif.dto.HistorySearchOperationDto;
import intellif.dto.IdentityInfoDto;
import intellif.dto.JsonObject;
import intellif.dto.ProcessInfo;
import intellif.dto.QueryFaceDto;
import intellif.dto.SearchFaceDto;
import intellif.dto.SearchReasonDto;
import intellif.dto.StaticFaceSearchDto;
import intellif.dto.XinghuoQuery;
import intellif.enums.IFaceSdkTypes;
import intellif.exception.MsgException;
import intellif.exception.RedException;
import intellif.fk.dao.FkInstitutionCodeDao;
import intellif.fk.dao.FkPersonAttrDao;
import intellif.fk.dao.FkPlaceCameraDao;
import intellif.fk.dto.FindFkPersonDto;
import intellif.fk.dto.FindFkPlaceFaceDto;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.vo.FkInstitutionCode;
import intellif.fk.vo.FkPersonAttr;
import intellif.fk.vo.FkPlaceCamera;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ERedListIoctrlType;
import intellif.ifaas.T_IF_FACERECT;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.PoliceStationServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.SolrServerItf;
import intellif.service.StaticBankServiceItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.CameraServiceImpl;
import intellif.service.impl.PersonDetailServiceImpl;
import intellif.service.impl.PoliceStationServiceImpl;
import intellif.service.impl.SolrServerImpl;
import intellif.settings.XinYiSettings;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.ApplicationResource;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.DateUtil;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.FaceUtil;
import intellif.utils.FileUtil;
import intellif.utils.Pageable;
import intellif.utils.RegularExpressionValidator;
import intellif.utils.ImageInfoHelper;
import intellif.utils.RegularExpressionValidator;
import intellif.database.entity.CidDetail;
import intellif.database.entity.CidInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.IFaceConfig;
import intellif.database.entity.ImageDetail;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.JuZhuDetail;
import intellif.database.entity.JuZhuInfo;
import intellif.database.entity.OtherDetail;
import intellif.database.entity.OtherInfo;
import intellif.database.entity.RedDetail;
import intellif.database.entity.ResidentArea;
import intellif.database.entity.SearchLogInfo;
import intellif.database.entity.SearchReason;
import intellif.database.entity.SearchRecord;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * <h1>The Class FaceController.</h1> The FaceController which serves request of
 * the form /face and returns a JSON object representing an instance of
 * FaceInfo.
 * <ul>
 * <li>Create
 * <li>Read
 * <li>Update
 * <li>Delete
 * <li>Statistics
 * <li>Query
 * <li>Misc. (see <a
 * href="https://spring.io/guides/gs/actuator-service/">RESTful example</a>)
 * </ul>
 * <p/>
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and
 * static data storages), while REST is a very-high-level API style (mostly for
 * webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
// @RequestMapping("/intellif/face")
@RequestMapping(GlobalConsts.R_ID_FACE)
public class FaceController {

    public static final float DEFAULT_SCORE_THRESHOLD = 0.92F;
    // ==============
    // PRIVATE FIELDS
    // ==============
    // private static final int CLOUD_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_SIZE = 40;
    private static Logger LOG = LogManager.getLogger(FaceController.class);
    // Autowire an object of type FaceInfoDao
    public static HashMap userSearchMap = new HashMap();

    @Autowired
    private FaceServiceItf _faceService;
    @Autowired
    private CameraServiceImpl cameraService;
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
    private PoliceStationServiceImpl policeStationService;
    @Autowired
    private AreaDao areaRepository;
    @Autowired
    private SearchReasonDao reasonDao;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private BlackBankDao bankDao;
    @Autowired
    private FaceInfoDaoImpl faceDao;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    private SolrServerImpl solrServerImpl;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;
    @Autowired
    private SearchLogDao searchLogDao;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private IFaceConfigDao ifaceConfigDao;
    @Autowired
    private AuditServiceItf _auditService;
    @Autowired
    private CameraInfoDao cameraInfoDao;
    @Autowired
    private RedDetailDao redDetailDao;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private PersonDetailServiceImpl personDetailService;
    @Autowired
    private FkPersonAttrDao fkPersonAttrDao;
    @Autowired
    private FkPlaceCameraDao fkPlaceCameraDao;
    @Autowired
    private FkInstitutionCodeDao fkInstitutionCodeDao;
	    
// v1.2.1 face compare
@RequestMapping(value = "/compare", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
@ApiOperation(httpMethod = "POST", value = "人脸相似度比对")
public JsonObject faceCompare(@RequestBody @Valid FaceCompareDto faceCompareDto) {
    try {
        if (faceCompareDto.getFaceIdA() == 0 || faceCompareDto.getFaceIdB() == 0
                || faceCompareDto.getThreshold() == 0.0) {
            return new JsonObject("参数都是必填项", -1, new Boolean(false));
        }
        // 人脸如果属于红名单对象则也不能比对
		int resultA = solrServerImpl.checkFaceIsOrNotInRedDetails(faceCompareDto.getFaceIdA(),faceCompareDto.getAtype());
		int resultB = solrServerImpl.checkFaceIsOrNotInRedDetails(faceCompareDto.getFaceIdB(),faceCompareDto.getBtype());
		if (resultA > 0) {
			// throw new MsgException("第一张人脸在红名单中，禁止搜索！");
            return new JsonObject("第一张图片为红名单人员，不支持对比！", -5, new Boolean(false));
        }
        if (resultB > 0) {
            // throw new MsgException("第二张人脸在红名单中，禁止搜索！");
            return new JsonObject("第二张图片为红名单人员，不支持对比！", -5, new Boolean(false));
        }

        // 先要根据人脸id及type得到人脸特征值feature的byte字节数组
        List<Long> idList = new ArrayList<Long>();
        idList.add(faceCompareDto.getFaceIdA());
        idList.add(faceCompareDto.getFaceIdB());

        List<Integer> faceTypes = new ArrayList<Integer>();
        faceTypes.add(faceCompareDto.getAtype());
        faceTypes.add(faceCompareDto.getBtype());

        List<T> faces = new ArrayList<T>();
//      List<byte[]> faceFeatures = new ArrayList<byte[]>();
        List<String> images = new ArrayList<String>();

        for (int i = 0; i < faceTypes.size(); i++) {

            switch (faceTypes.get(i)) {
            case 0:
                if (blackDetailDao.findById(idList.get(i)) == null) {
                    return new JsonObject("请检查id,重点人员人脸库搜素无结果", -2, new Boolean(false));
                }
//              faceFeatures.add(blackDetailDao.findById(idList.get(i)).get(0).getFaceFeature());
                images.add(blackDetailDao.findById(idList.get(i)).get(0).getImageData());
                break;
            case 1:
            case 2:
                List<Long> fList = new ArrayList<Long>();
                fList.add(idList.get(i));
                if (faceDao.findByIds(fList) == null) {
                    return new JsonObject("请检查id,抓拍人脸库搜素无结果", -2, new Boolean(false));
                }
//              faceFeatures.add(faceDao.findByIds(fList).get(0).getFaceFeature());
                images.add(faceDao.findByIds(fList).get(0).getImageData());
                break;
            case 3:
                if (cidDetailRepository.findOne(idList.get(i)) == null) {
                    return new JsonObject("请检查id,户籍人员人脸库搜素无结果", -2, new Boolean(false));
                }
//              faceFeatures.add(cidDetailRepository.findOne(idList.get(i)).getFaceFeature());
                images.add(cidDetailRepository.findOne(idList.get(i)).getImageData());
                break;
            case 4:
                if (juZhuDetailRepository.findOne(idList.get(i)) == null) {
                    return new JsonObject("请检查id,居住人员人脸库搜素无结果", -2, new Boolean(false));
                }
//              faceFeatures.add(juZhuDetailRepository.findOne(idList.get(i)).getFaceFeature());
                images.add(juZhuDetailRepository.findOne(idList.get(i)).getImageData());
                break;
            case 5:
            case 6:
                if (otherDetailRepository.findOne(idList.get(i)) == null) {
                    return new JsonObject("请检查id,其他人员人脸库搜素无结果", -2, new Boolean(false));
                }
//              faceFeatures.add(otherDetailRepository.findOne(idList.get(i)).getFaceFeature());
                images.add(otherDetailRepository.findOne(idList.get(i)).getImageData());
                break;
            default:
                return new JsonObject("type值不在正确范围", -2, new Boolean(false));
            }
        }
//      if (faceFeatures.size() != 2) {
//          System.out.println("根据id找到的人脸张数" + faces.size());
//          return new JsonObject("请检查对应图片及id,检测到人脸张数不为2", -2, new Boolean(false));
//      }
        if (images.size() != 2) {
            System.out.println("根据id找到的人脸张数" + faces.size());
            return new JsonObject("请检查对应图片及id,检测到人脸张数不为2", -2, new Boolean(false));
        }
        

        IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
        float result = (float) ifaceSdkTarget.processFaceFeatureVerify(images.get(0), images.get(1));
        
//      // 再把两个byte数组转为float数组
//      float[] A = FaceUtil.byte2float(faceFeatures.get(0), 0, faceFeatures.get(0).length);
//      float[] B = FaceUtil.byte2float(faceFeatures.get(1), 0, faceFeatures.get(1).length);
//      float result = FaceUtil.isLike(A, B, faceCompareDto.getThreshold());
        Boolean compareResult = false;
        if (result != 0) {
            compareResult = Float.compare(result, faceCompareDto.getThreshold()) >= 0;
        }
        // 返回前端结果过滤 为负数的统一过滤为0
        if (result < 0) {
            result = 0;
        }
        return new JsonObject("threshold:" + result + ",result:" + compareResult);
    } catch (Exception e) {
        System.err.println("人脸比对异常：" + e.getStackTrace() + "。。。" + e.getMessage());
        return new JsonObject("系统异常,请稍后再试", -3, new Boolean(false));
    }
}

    // @RequestMapping(method = RequestMethod.POST, consumes =
    // MediaType.APPLICATION_JSON)
    // @ApiOperation(httpMethod = "POST", value = "Response a string describing
    // if the face info is successfully created or not.")
    // public JsonObject create(@RequestBody @Valid FaceInfo faceInfo) {
    // return new JsonObject(faceInfoDaoImpl.save(faceInfo));
    // }

    // @RequestMapping(method = RequestMethod.GET)
    // @ApiOperation(httpMethod = "GET", value = "Response a list describing all
    // of face info that is successfully get or not.")
    // public JsonObject list() {
    // return new JsonObject(this.faceInfoDaoImpl.findAll());
    // }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the face info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this.faceService.findOne(id));
    }

    // @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    // @ApiOperation(httpMethod = "PUT", value = "Response a string describing
    // if the face info is successfully updated or not.")
    // public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid
    // FaceInfo faceInfo) {
    // // FaceInfo find = this.faceInfoDaoImpl.findOne(id);
    // faceInfo.setId(id);
    // return new JsonObject(this.faceInfoDaoImpl.save(faceInfo));
    // }

    // @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    // @ApiOperation(httpMethod = "DELETE", value = "Response a string
    // describing if the face info is successfully delete or not.")
    // public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
    // this.faceService.delete(id);
    // return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    // }

    @RequestMapping(value = "/statistic/day", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "按天统计最近七天人脸抓拍人次")
    public JsonObject statisticByDay() {
        return new JsonObject(this._faceService.statisticByDay());
    }

    @RequestMapping(value = "/statistic/station/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "统计指定所的人脸抓拍数量")
    public JsonObject statisticByStation(@PathVariable("id") long id) {
        return new JsonObject(this.faceService.statisticByArea(id));
    }
    @RequestMapping(value = "/statistic/district/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "统计指定行政区域人脸抓拍数量")
    public JsonObject statisticByDistrictId(@PathVariable("id") long id) {
        return new JsonObject(this.faceService.statisticByDistict(id));
    }

    @RequestMapping(value = "/statistic/cameraIds", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "统计指定摄像头列表人脸抓拍数量")
    public JsonObject statisticByCameraIds( @RequestBody @Valid CameraIdListDto cameraIdListDto) {
        return new JsonObject(this.faceService.statisticByCameraIds(cameraIdListDto.getIdList()));
    }
    @RequestMapping(value = "/page/{page}/time/{time}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "分页获取所有摄像头最近抓拍图片")
    public JsonObject listByPage(@PathVariable("page") int page,
            @PathVariable("time") String time,
            @PathVariable("pagesize") int pagesize) {
        return new JsonObject(this.faceService.findLast(
                page,
                pagesize,
                time.substring(0, 4) + "-" + time.substring(4, 6) + "-"
                        + time.substring(6)));
    }

    @RequestMapping(value = "/param/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取所有摄像头最近抓拍图片")
    public JsonObject listByParamsPage(@PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize,
            @RequestBody @Valid QueryFaceDto faceQueryDto) {

        try {
            /*
             * List<FaceInfo> faceList = null; String race =
             * faceQueryDto.getRace(); if ("1".equals(race)) { if
             * (RaceUtil.getRaceList().isEmpty()) { faceList =
             * this._faceService.findByCombinedParams( faceQueryDto, (page - 1)
             * * DEFAULT_BIG_PAGE_SIZE, DEFAULT_BIG_PAGE_SIZE); } else { //
             * 从缓存获取 faceList = RaceUtil.getRaceListByPage((page - 1)
             * DEFAULT_BIG_PAGE_SIZE, DEFAULT_BIG_PAGE_SIZE); } } else {
             * faceList = this._faceService.findByCombinedParams(faceQueryDto,
             * (page - 1) * DEFAULT_BIG_PAGE_SIZE, DEFAULT_BIG_PAGE_SIZE); }
             */
            // 当只传入单个摄像头的时候 得判断这一个摄像头是否属于权限范围内
            if (0 != faceQueryDto.getSourceId()) {
                zoneAuthorizeService.checkIds(CameraInfo.class, faceQueryDto.getSourceId());
            }
            List<FaceInfo> faceList = this._faceService.findByCombinedParams(
                    faceQueryDto, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

    @RequestMapping(value = "/param/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取指定摄像头最近抓拍图片")
    public JsonObject findByParamCameraId(@PathVariable("id") long id, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize, @RequestBody @Valid QueryFaceDto queryFaceDto) {

        // 判断用户是否具有该摄像头的权限 有就直接抛出异常了
        zoneAuthorizeService.checkIds(CameraInfo.class, id);

        try {
            queryFaceDto.setSourceId(id);
            List<FaceInfo> faceList = null;
            /*
             * String race = queryFaceDto.getRace(); if ("1".equals(race)) { if
             * (RaceUtil.getRaceList().isEmpty()) { faceList =
             * this._faceService.findByCombinedParams( queryFaceDto, (page - 1)
             * * DEFAULT_BIG_PAGE_SIZE, DEFAULT_BIG_PAGE_SIZE); } else { //
             * 从缓存获取 faceList = RaceUtil.getCameraRaceListByPage((page - 1)
             * DEFAULT_BIG_PAGE_SIZE,
             * DEFAULT_BIG_PAGE_SIZE,queryFaceDto.getSourceId()); } } else {
             * faceList = this._faceService.findByCombinedParams(queryFaceDto,
             * (page - 1) * DEFAULT_BIG_PAGE_SIZE, DEFAULT_BIG_PAGE_SIZE); }
             */
            faceList = this._faceService.findByCombinedParams(queryFaceDto, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params camera error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }
    
    @RequestMapping(value = "/param/camera/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取指定摄像头code最近抓拍图片")
    public JsonObject findByParamCameraCode( @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize, @RequestBody @Valid XinghuoQuery queryFaceDto) {

        String code = queryFaceDto.getCode();
        if(code.trim().isEmpty()){
            return new JsonObject("摄像头code不能为空！");
        }
        List<CameraInfo> cameraInfos = cameraInfoDao.findByCode(code);
        if (cameraInfos == null || cameraInfos.isEmpty()) {
            return new JsonObject("该摄像头code不存在，请重新输入！");
        }
        CameraInfo cameraInfo = cameraInfos.get(0);
        
        long id = cameraInfo.getId();
        // 判断用户是否具有该摄像头的权限 有就直接抛出异常了
        if (0 != id) {
                zoneAuthorizeService.checkIds(CameraInfo.class, id);
            }

        try {
            queryFaceDto.setSourceId(id);
            List<FaceInfo> faceList = null;
            faceList = this._faceService.findByXinghuoCombinedParams(queryFaceDto, page, pagesize);
            CameraCodeDto cameraCodeDto = new CameraCodeDto(code, faceList);
            return new JsonObject(cameraCodeDto);
        } catch (Exception e) {
            LOG.error("get face by params camera error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
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
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
                HttpStatus.OK));
    }

    @RequestMapping(value = "/zip/camera/{key}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "zip导出指定camera下抓拍图片")    
    public JsonObject createZipResultsByCamera(@RequestBody @Valid QueryFaceDto queryFaceDto,@PathVariable("key") int key) {

        // 判断摄像头id列表中是否有权限外的摄像头 有就直接抛出异常了
        zoneAuthorizeService.checkIds(CameraInfo.class, queryFaceDto.getSourceIds());

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
                List<ImageInfo> faceList = this.faceService.findImageByCombinedParams(queryFaceDto, 1, 10001);

                if (null != faceList && !faceList.isEmpty()) {
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
            while (cameraIterator.hasNext()) {
                String idStr = cameraIterator.next();
                try {
                    long id = Long.parseLong(idStr);
                    List<ImageInfo> faceResultList = cameraMap.get(idStr);
                    CameraInfo camera = cameraService.findById(id);
                    Area area = this.areaRepository.findOne(camera.getStationId());
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
                } catch (Exception e) {
                    LOG.error("download image from camera error,cameraId:" + idStr + ",error:", e);
                }
            }

            compressZip(randPath);
            String path = FileUtil.getZipHttpUrl(propertiesBean.getIsJar()) + "export/zip/" + randPath
                    + "/faceData.zip";
            return new JsonObject(path);
        } catch (Exception e) {
            LOG.error("export face error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }


    @RequestMapping(value = "/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定摄像头最近抓拍图片")
    public JsonObject findByCameraId(@PathVariable("id") long id, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize) {

        // 判断用户是否具有该摄像头的权限 有就直接抛出异常了
        zoneAuthorizeService.checkIds(CameraInfo.class, id);

        return new JsonObject(this.faceService.findBySourceId(id, page, pagesize));
    }

    @RequestMapping(value = "/station/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定派出所下所有摄像头最近抓拍图片")
    public JsonObject findByStationId(@PathVariable("id") long id) {
        List<CameraNearFaceDto> rsList = new ArrayList<CameraNearFaceDto>();
        List<CameraInfo> cameraList = this.cameraService.findByStationId(id);
        if (cameraList.size() > 0) {
            for (CameraInfo camera : cameraList) {
                rsList.add(new CameraNearFaceDto(camera, this.faceService
                        .findBySourceId(camera.getId(), 0, DEFAULT_PAGE_SIZE)));
            }
        }
        return new JsonObject(rsList);
    }

    @RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the face info image id is successfully get or not.")
    public JsonObject findByFromImageId(@PathVariable("id") long id) {
        return new JsonObject(this.faceService.findByFromImageId(id));
    }

    @RequestMapping(value = "/search/black/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "通过人脸检索检索相似嫌疑人")
    public JsonObject searchFaceInBankByBlackId(@PathVariable("id") long id,
            @PathVariable("page") int page,
            @PathVariable("pagesize") int pageSize) throws IOException {
        try {
            List<FaceResultDto> faceList = _solrService.searchFaceByBlackId(id,
                    DEFAULT_SCORE_THRESHOLD, GlobalConsts.BLACK_BANK_TYPE);
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

    /*
     * @RequestMapping(value = "/search/zip", method = RequestMethod.POST,
     * consumes = MediaType.APPLICATION_JSON)
     * 
     * @ApiOperation(httpMethod = "POST", value = "ZIP导出特定条件下的抓拍图片") public
     * JsonObject createSearchZipResults(@RequestBody @Valid SearchFaceDto
     * searchFaceDto) { try{ String userName =
     * SecurityContextHolder.getContext().getAuthentication().getName(); File
     * file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar()) +
     * "export/image/"+userName+"/"); FileUtil.deleteFile(file,true);
     * FileUtil.checkFileExist(file); List<FaceResultDto> faceResultList =
     * _solrService.searchFaceByIdInBank(searchFaceDto); if
     * (faceResultList.size() > 5000) { return new
     * JsonObject("图片数目大于5000张，请重新设置过滤条件", 1002); }
     * saveSearchImageToDir(faceResultList,"cameras");
     * 
     * searchFaceDto.setType(2); List<FaceResultDto> faceResultList1 =
     * _solrService.searchFaceByIdInBank(searchFaceDto); if
     * (faceResultList1.size() > 5000) { return new
     * JsonObject("图片数目大于5000张，请重新设置过滤条件", 1002); }
     * saveSearchImageToDir(faceResultList1,"inStation");
     * 
     * searchFaceDto.setType(0); searchFaceDto.setStarttime("");
     * searchFaceDto.setEndtime(""); List<FaceResultDto> faceResultList2 =
     * _solrService.searchFaceByIdInBank(searchFaceDto); if
     * (faceResultList2.size() > 5000) { return new
     * JsonObject("图片数目大于5000张，请重新设置过滤条件", 1002); }
     * saveSearchImageToDir(faceResultList2,"black");
     * 
     * compressZip(userName); //String path =
     * FileUtil.getUploads(propertiesBean.getIsJar())+"export/zip/"+userName+
     * "/faceData.zip"; String path
     * =FileUtil.getZipHttpUrl(propertiesBean.getIsJar())+"export/zip/"+userName
     * +"/faceData.zip"; return new JsonObject(path); } catch (Exception e) {
     * LOG.error("export face error", e); return new JsonObject(e.getMessage(),
     * 1001); } }
     */

    @RequestMapping(value = "/search/zip/camera/{key}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "ZIP导出指定cameras或静态库下搜索结果")
    public JsonObject createSearchZipCameraResults(@RequestBody @Valid SearchFaceDto searchFaceDto,
            @PathVariable("key") int key) {

        // 判断摄像头id列表中是否有权限外的摄像头 有就直接抛出异常了
        zoneAuthorizeService.checkIds(CameraInfo.class, searchFaceDto.getCameraIds());

        try {
            int totalSize = 0;
            ProcessInfo process = new ProcessInfo();
            GlobalConsts.downloadMap.put(key, process);
            // process.setFailedNum(totalSize - process.getSuccessNum());
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
                         return new JsonObject("导出图片数目大于10000张，请重新设置过滤条件",
                         1002);
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
                        // if (totalSize > 10000) {
                        // return new JsonObject("导出图片数目大于10000张，请重新设置过滤条件",
                        // 1002);
                        // }
                    }
                }
            }

            process.setTotalSize(totalSize);

            // save resultList
            Iterator<String> cameraIterator = cameraMap.keySet().iterator();
            while (cameraIterator.hasNext()) {
                String idStr = cameraIterator.next();

                try {
                    long id = Long.parseLong(idStr);
                    List<FaceResultDto> faceResultList = cameraMap.get(idStr);
                    CameraInfo camera = cameraService.findById(id);
                    Area area = areaRepository.findOne(camera.getStationId());
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
                } catch (Exception e) {
                    LOG.error("download search image from camera error,cameraId:" + idStr + ",error:", e);

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
        }catch (Exception e) {
            LOG.error("export face error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }
    /*
     * @RequestMapping(value = "/search/zip/camera/{id}", method =
     * RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
     * 
     * @ApiOperation(httpMethod = "POST", value = "ZIP导出指定camera下搜索结果") public
     * JsonObject createSearchZipCameraResults(@PathVariable("id") long
     * id,@RequestBody @Valid SearchFaceDto searchFaceDto) { try { String
     * userName =
     * SecurityContextHolder.getContext().getAuthentication().getName();
     * CameraInfo camera = _cameraInfoDao.findOne(id); PoliceStation ps =
     * _policeStationDao.findOne(camera.getInStation()); String appendPath =
     * ps.getStationName()+"/"+camera.getName()+"/"; List<FaceResultDto>
     * faceResultList = _solrService.getFaceByCameraId(searchFaceDto, id); if
     * (null != faceResultList) { if (faceResultList.size() > 1000) { return new
     * JsonObject("摄像头("+camera.getName()+")图片数目大于1000张，请重新设置过滤条件", 1002); }
     * File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar()) +
     * "export/image/"+userName+"/"); FileUtil.deleteFile(file,true); //
     * FileUtil.checkFileExist(file);
     * saveSearchImageToDir(faceResultList,appendPath); >>>>>>>
     * ade35fecab3cc8865c4123367c5c9afcf6b3d231 } compressZip(userName); //
     * String path = //
     * FileUtil.getUploads(propertiesBean.getIsJar())+"export/zip/"+userName+
     * "/faceData.zip"; String path =
     * FileUtil.getZipHttpUrl(propertiesBean.getIsJar()) + "export/zip/" +
     * userName + "/faceData.zip"; return new JsonObject(path); } catch
     * (Exception e) { LOG.error("export face error", e); return new
     * JsonObject(e.getMessage(), 1001); } }
     */

    @SuppressWarnings({ "unused", "unchecked" })
    @RequestMapping(value = "/search/face/cloud/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中进行人脸检索")
    public JsonObject searchFaceInBankByFaceIdForCloud(@PathVariable("page") int page,
            @PathVariable("pagesize") int pageSize, @RequestBody @Valid SearchFaceDto searchFaceDto)
            throws IOException {
        long currentTime = System.currentTimeMillis(), startTime = currentTime;
        String login = CurUserInfoUtil.getUserInfo().getLogin();
        
        try {
            List<FaceResultDto> faceResultList = _solrService.searchFaceByIdInBank(searchFaceDto);
            LOG.info("xxxxxx searchFaceByIdInBank need {}ms, faceResultList size is {}, faceId is {}. type is {}",
                    System.currentTimeMillis() - currentTime, faceResultList.size(), searchFaceDto.getFaceId(),
                    searchFaceDto.getType());

            int total = faceResultList.size();
            if (total == 0) {

                return new JsonObject("结果集为空", 1003);
            }
            faceResultList = getPageList(faceResultList, page, pageSize);
            if(faceResultList==null){
                return new JsonObject(null);
            }           
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
            List<Long> ids = new ArrayList<Long>();
            for (FaceResultDto faceRs : faceResultList) {
                ids.add(faceRs.getCamera());
            }
            switch (searchFaceDto.getType()) {
            case 3: { // type为3 即 是警务云 中身份信息的搜素
                currentTime = System.currentTimeMillis();
                List<CidInfo> cidList = cidInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));
                System.err.println(cidList.get(0).getUpdated());

                LOG.info("xxxxxx findByIds need {}ms, cidList size is {}, faceId is {}. type is {}",
                        System.currentTimeMillis() - currentTime, cidList.size(), searchFaceDto.getFaceId(),
                        searchFaceDto.getType());
                Map<Long, CidInfo> cidMap = new HashMap<Long, CidInfo>();
                Map<String, Boolean> flagMap = new HashMap<String, Boolean>();
                for (CidInfo cid : cidList) {
                    cidMap.put(cid.getId(), cid);
                    System.err.println(cid.getUpdated());
                }
                List<CidInfoDto> cidInfoDtoList = new ArrayList<CidInfoDto>();
                for (int i = 0; i < faceResultList.size(); i++) {
                    FaceResultDto faceRs = faceResultList.get(i);
                    if (cidMap.containsKey(faceRs.getCamera())) {
                        if (i == 0 && faceRs.getFile().endsWith("?vip")) {
                            CidInfo info = cidMap.get(faceRs.getCamera());
                            if (null == info) {
                                continue;
                            }
                            CidInfoDto cidInfoDto = new CidInfoDto(info);
                            cidInfoDto.setDetailId(faceRs.getId());
                            cidInfoDto.setFile(faceRs.getFile());
                            if (faceResultList.size() <= 1) {
                                while (faceRs.getScore() > 0.94) {
                                    faceRs.setScore(faceRs.getScore() - 0.01F);
                                }
                                cidInfoDto.setScore(faceRs.getScore());
                            } else {
                                FaceResultDto secFaceRs = faceResultList.get(i + 1);
                                if (secFaceRs.getScore() < 0.92) {
                                    while (faceRs.getScore() > 0.93) {
                                        faceRs.setScore(faceRs.getScore() - 0.01F);
                                    }
                                    cidInfoDto.setScore(faceRs.getScore());
                                } else {
                                    cidInfoDto.setScore(secFaceRs.getScore() + (float) (0.02 * Math.random()));
                                }
                            }

                            // cidInfoDto.setImageBase64(cidInfoDto.getPhotoBase64());
                            cidInfoDto.setNeedPhotoBase64(true);
                            cidInfoDto.setFile(cidInfoDto.getPhoto());

                            flagMap.put(cidInfoDto.getPhoto(), true);
                            cidInfoDtoList.add(cidInfoDto);
                        } else {
                            CidInfoDto cidInfoDto = new CidInfoDto(cidMap.get(faceRs.getCamera()));
                            System.err.println(cidInfoDto.getUpdated());
                            System.err.println(cidMap.get(faceRs.getCamera()).getUpdated());

                            cidInfoDto.setDetailId(faceRs.getId());
                            cidInfoDto.setFile(faceRs.getFile());
                            cidInfoDto.setScore(faceRs.getScore());
                            if (faceRs.getFile().endsWith("?vip")) {
                                if (flagMap.containsKey(cidInfoDto.getPhoto()))
                                    continue;

                                // cidInfoDto.setImageBase64(cidInfoDto.getPhotoBase64());
                                cidInfoDto.setNeedPhotoBase64(true);
                                cidInfoDto.setFile(cidInfoDto.getPhoto());

                                flagMap.put(cidInfoDto.getPhoto(), true);
                            } else {
                                if (flagMap.containsKey(faceRs.getFile()))
                                    continue;

                                // cidInfoDto.setImageBase64(FileUtil.GetImageStr(faceRs.getFile()));

                                flagMap.put(faceRs.getFile(), true);
                            }
                            cidInfoDtoList.add(cidInfoDto);
                        }
                    }
                }
                // 利用多线程对图片进行Base64编码
                // synImageBase64(cidInfoDtoList);
                return new JsonObject(cidInfoDtoList, 0, 0, total);
            }
            case 4: { // type为4 即 是警务云 中居民证信息的搜素
                List<JuZhuInfo> juzhuList = juzhuInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));
                Map<Long, JuZhuInfo> juzhuMap = new HashMap<Long, JuZhuInfo>();
                Map<String, Boolean> flagMap = new HashMap<String, Boolean>();
                for (JuZhuInfo juzhuid : juzhuList) {
                    juzhuMap.put(juzhuid.getId(), juzhuid);
                }
                List<CidInfoDto> juzhuInfoDtoList = new ArrayList<CidInfoDto>();
                for (int i = 0; i < faceResultList.size(); i++) {
                    FaceResultDto faceRs = faceResultList.get(i);
                    if (juzhuMap.containsKey(faceRs.getCamera())) {
                        if (i == 0 && faceRs.getFile().endsWith("?vip")) {
                            JuZhuInfo info = juzhuMap.get(faceRs.getCamera());
                            if (null == info) {
                                continue;
                            }
                            CidInfoDto juzhuInfoDto = new CidInfoDto(info);
                            juzhuInfoDto.setDetailId(faceRs.getId());
                            juzhuInfoDto.setFile(faceRs.getFile());
                            if (faceResultList.size() <= 1) {
                                while (faceRs.getScore() > 0.94) {
                                    faceRs.setScore(faceRs.getScore() - 0.01F);
                                }
                                juzhuInfoDto.setScore(faceRs.getScore());
                            } else {
                                FaceResultDto secFaceRs = faceResultList.get(i + 1);
                                if (secFaceRs.getScore() < 0.92) {
                                    while (faceRs.getScore() > 0.93) {
                                        faceRs.setScore(faceRs.getScore() - 0.01F);
                                    }
                                    juzhuInfoDto.setScore(faceRs.getScore());
                                } else {
                                    juzhuInfoDto.setScore(secFaceRs.getScore() + (float) (0.02 * Math.random()));
                                }
                            }

                            // juzhuInfoDto.setImageBase64(juzhuInfoDto.getPhotoBase64());
                            juzhuInfoDto.setNeedPhotoBase64(true);
                            juzhuInfoDto.setFile(juzhuInfoDto.getPhoto());

                            flagMap.put(juzhuInfoDto.getPhoto(), true);
                            juzhuInfoDtoList.add(juzhuInfoDto);
                        } else {
                            JuZhuInfo info = juzhuMap.get(faceRs.getCamera());
                            if (null == info) {
                                continue;
                            }
                            CidInfoDto juzhuInfoDto = new CidInfoDto(info);
                            juzhuInfoDto.setDetailId(faceRs.getId());
                            juzhuInfoDto.setFile(faceRs.getFile());
                            juzhuInfoDto.setScore(faceRs.getScore());
                            if (faceRs.getFile().endsWith("?vip")) {
                                if (flagMap.containsKey(juzhuInfoDto.getPhoto()))
                                    continue;

                                // juzhuInfoDto.setImageBase64(juzhuInfoDto.getPhotoBase64());
                                juzhuInfoDto.setNeedPhotoBase64(true);
                                juzhuInfoDto.setFile(juzhuInfoDto.getPhoto());

                                flagMap.put(juzhuInfoDto.getPhoto(), true);
                            } else {
                                if (flagMap.containsKey(faceRs.getFile()))
                                    continue;

                                // juzhuInfoDto.setImageBase64(FileUtil.GetImageStr(faceRs.getFile()));

                                flagMap.put(faceRs.getFile(), true);
                            }
                            juzhuInfoDtoList.add(juzhuInfoDto);
                        }
                    }
                }
                // 利用多线程对图片进行Base64编码
                // synImageBase64(juzhuInfoDtoList);
                return new JsonObject(juzhuInfoDtoList, 0, 0, total);
            }
            case 5: {
                List<CidInfoDto> otherInfoDtoList = processOtherInfoList(ids,faceResultList,5); 
                return new JsonObject(otherInfoDtoList, 0, 0, total);
            }
            case 6: {                
                List<CidInfoDto> otherInfoDtoList = processOtherInfoList(ids,faceResultList,6);      
                return new JsonObject(otherInfoDtoList, 0, 0, total);
            }
            case 11: {
                LOG.info("mobile info type 11,search face,policeStationId:"+searchFaceDto.getPoliceStationId());
                List<CidInfoDto> otherInfoDtoList = processOtherInfoList(ids,faceResultList,11);
                return new JsonObject(otherInfoDtoList, 0, 0, total);
            }
            default: {
                return new JsonObject(faceResultList, 0, 0, total);
            }
            }
		} catch (RedException e) {
			return new JsonObject(e, 1004);
		} catch (Exception e) {
			e.printStackTrace();
			return new JsonObject(null);
        } finally {
            LOG.info("xxxxxx searchFaceInBankByFaceIdForCloud total need {}ms, faceId is {}. type is {}",
                    System.currentTimeMillis() - startTime, searchFaceDto.getFaceId(), searchFaceDto.getType());
        }

	}

    private List<CidInfoDto> processOtherInfoList(List<Long> ids, List<FaceResultDto> faceResultList,int type) {
        List<OtherInfo> otherList = otherInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));
        Map<Long, OtherInfo> otherMap = new HashMap<Long, OtherInfo>();
        for (OtherInfo other : otherList) {
            otherMap.put(other.getId(), other);
        }
        List<CidInfoDto> otherInfoDtoList = new ArrayList<CidInfoDto>();
        for (FaceResultDto faceRs : faceResultList) {
            OtherInfo info = otherMap.get(faceRs.getCamera());
            if (null == info) {
                continue;
            }
            
            CidInfoDto otherInfoDto = new CidInfoDto(info);
            if(GlobalConsts.MOBILE_INFO_TYPE == type){
                try{
                    String idStr = info.getExtendField4();
                    PoliceStation ps = policeStationService.findById(Long.parseLong(idStr));
                    otherInfoDto.setPoliceStationName(ps.getStationName());
                }catch(Exception e){
                    LOG.error("other info get policestation error,type:11,id:"+info.getId());
                }
            }
            otherInfoDto.setDetailId(faceRs.getId());
            otherInfoDto.setFile(faceRs.getFile());
            otherInfoDto.setScore(faceRs.getScore());
            // otherInfoDto.setImageBase64(FileUtil.GetImageStr(faceRs.getFile()));
            otherInfoDtoList.add(otherInfoDto);
        }
        // 利用多线程对图片进行Base64编码
        // synImageBase64(otherInfoDtoList);
        return otherInfoDtoList;

    }
    // ////////////1.2.4 静态库条件检索
    @RequestMapping(value = "/conditionSearch/staticFace/cloud/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过指定条件在指定静态库中进行人脸检索")
    public JsonObject conditionSearchStaticFaceInBankByFaceIdForCloud(@PathVariable("page") int page,
            @PathVariable("pagesize") int pageSize, @RequestBody @Valid StaticFaceSearchDto staticFaceSearchDto)
            throws IOException {
        if (staticFaceSearchDto.getSex() == null) {
            return new JsonObject("性别不能为空，0为全部 1为男 2为女", 1001);
        }
        if (staticFaceSearchDto.getName() == null && staticFaceSearchDto.getPhone() == null
                && staticFaceSearchDto.getClass() == null) {
            return new JsonObject("姓名、身份证、手机号至少一个不为空", 1001);
        }
        String phone = staticFaceSearchDto.getPhone();
        String idCard = staticFaceSearchDto.getIdCard();
        if (phone != null && !phone.equals("")) {
            if (!RegularExpressionValidator.isMobile(phone)) {
                return new JsonObject("请输入合法的手机号", 1001);
            }
        }
        if (idCard != null && !idCard.equals("")) {
            if (!RegularExpressionValidator.checkIDCard(idCard)) {
                return new JsonObject("请输入合法的身份证号", 1001);
            }
        }
        List resultList = staticBankServiceItf.findByCondition(staticFaceSearchDto, page, pageSize);
        return new JsonObject(resultList);
    }

    // ////////////1.2.4 静态库条件检索返回结果总数
    @RequestMapping(value = "/conditionSearchCount/staticFace/cloud", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "返回通过指定条件在指定静态库中进行人脸检索的结果集总数")
    public JsonObject conditionSearchCountInStaticBankForCloud(
            @RequestBody @Valid StaticFaceSearchDto staticFaceSearchDto) throws IOException {
        if (staticFaceSearchDto.getSex() == null) {
            return new JsonObject("性别不能为空，0为全部 1为女 2为男", 1001);
        }
        if (staticFaceSearchDto.getName() == null && staticFaceSearchDto.getPhone() == null
                && staticFaceSearchDto.getClass() == null) {
            return new JsonObject("姓名、身份证、手机号至少一个不为空", 1001);
        }
        BigInteger resultCount = staticBankServiceItf.CountByCondition(staticFaceSearchDto);
        return new JsonObject(resultCount);
    }


    @RequestMapping(value = "/search/face/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中进行人脸检索")
    public JsonObject searchFaceInBankByFaceId(@PathVariable("page") int page,
            @PathVariable("pagesize") int pageSize,
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
                if (!searchFaceDto.getRace().isEmpty()) {
                    nameOrCid = searchFaceDto.getRace();
                }
                String[] fkType = searchFaceDto.getBankIds()[0].split("-");
                int[] fType = new int[fkType.length-1];                            
                
                for (int i = 0; i < fkType.length - 1; i++) {
                    fType[i] = Integer.valueOf(fkType[i + 1]).intValue();
                }
                //搜索结果还要根据反恐人员区域过滤 参数cameraids 中存放 subInistitutionId-subinstitutionId,localInstitutionId-localInstitutionId  我还得根据id转化成code
                String[] subInstitutionIds = searchFaceDto.getCameraIds()[0].split("-");
                String[] localInstitutionIds = searchFaceDto.getCameraIds()[1].split("-");
                String[] subInstitutionCodes = changeIdToCode(subInstitutionIds);
                String[] localInstitutionCodes = changeIdToCode(localInstitutionIds);
                
                List<FkPersonResultDto> fkResultList = filterByFktype(faceResultList, fType, nameOrCid,subInstitutionCodes,localInstitutionCodes);
                //根据库权限对 反恐搜索结果过滤
                String bankIdAutority = "," +this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE) +","; 
                fkResultList = filterByFkBankAuthority(fkResultList,bankIdAutority);       
                
                UserInfo userinfo = CurUserInfoUtil.getUserInfo();
                RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
               // List resultPageList = getPageList(fkResultList, page, pageSize);
                Date endTime = new Date();
                long delyTime = endTime.getTime() - startTime.getTime();// 单位毫秒
                writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);
                
                Pageable<FkPersonResultDto> pageableResult = new Pageable<FkPersonResultDto>(fkResultList);
                pageableResult.setPageSize(pageSize);
                pageableResult.setPage(page);
                int totalNum = fkResultList.size();
                int maxPage = pageableResult.getMaxPages();
                return new JsonObject(pageableResult.getListForPage(),0, maxPage, totalNum);

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
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }
    
    @RequestMapping(value = "/search/face/attribute/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中进行人脸检索,并根据属性进行过滤")
    public JsonObject searchFaceInBankByAttribute(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        try {
            Date startTime = new Date();
            List<FaceResultDto> faceResultList = _solrService.searchFaceByAttribute(searchFaceDto);

            UserInfo userinfo = CurUserInfoUtil.getUserInfo();
            RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
            Date endTime = new Date();
            long delyTime = endTime.getTime() - startTime.getTime();//单位毫秒
            writeSearchLog(userinfo, roleinfo, searchFaceDto,delyTime);
            return new JsonObject(getPageList(faceResultList, page, pageSize));
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }


    @RequestMapping(value = "/search/statistic", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中按摄像头分类进行人脸检索获取统计结果")
    public JsonObject searchFaceStatistic(
            @RequestBody @Valid SearchFaceDto searchFaceDto,
            @Context HttpServletRequest request) throws IOException {
        try {
            LOG.info("start statistis search");
            FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:"
                    + searchFaceDto.getType() + " 开始统计!");
            FaceSearchStatisticDto faceSearchStatistic = _solrService
                    .getFaceStatistic(searchFaceDto);
            FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:"
                    + searchFaceDto.getType() + " 结束统计!");
            // 人脸检索记录日志
            try {
                int type = searchFaceDto.getType();
                if (type == 1) {
                    String[] dataTypes = searchFaceDto.getDataType().split(",");
                    String[] fids = searchFaceDto.getIds().split(",");
                    for (int i = 0; i < dataTypes.length; i++) {
                        long fid;
                        if (fids.length < 2) {
                            fid = searchFaceDto.getFaceId();
                        } else {
                            fid = Long.valueOf(fids[i]);
                        }
                        String imageUrl = "";
                        switch (Integer.parseInt(dataTypes[i])) {
                        case 0: {
                            imageUrl = blackDetailDao.findOne(fid)
                                    .getImageData();
                            break;
                        }
                        case 1: {
                            imageUrl = faceService.findOne(fid).getImageData();
                            break;
                        }
                        case 2: {
                            imageUrl = faceService.findOne(fid).getImageData();
                            break;
                        }
                        case 3: {
                            imageUrl = cidDetailRepository.findOne(fid)
                                    .getImageData();
                            break;
                        }
                        case 4: {
                            imageUrl = juZhuDetailRepository.findOne(fid)
                                    .getImageData();
                            break;
                        }
                        case 5: {
                            imageUrl = otherDetailRepository.findOne(fid)
                                    .getImageData();
                            break;
                        }
                        case 6: {
                            imageUrl = otherDetailRepository.findOne(fid)
                                    .getImageData();
                            break;
                        }
                        default: {
                            imageUrl = otherDetailRepository.findOne(fid)
                                    .getImageData();
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
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("exception:", e);
            LOG.error("exception:" + e);

        }
        return new JsonObject(null);
    }
    
    @RequestMapping(value = "/search/attribute/statistic", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中按摄像头分类进行人脸检索获取统计结果")
    public JsonObject searchFaceAttributeStatistic(@RequestBody @Valid SearchFaceDto searchFaceDto,
            @Context HttpServletRequest request) throws IOException {
        try {
            LOG.info("start statistis search");
            FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:" + searchFaceDto.getType() + " 开始统计!");
            FaceSearchStatisticDto faceSearchStatistic = _solrService.getFaceAttributeStatistic(searchFaceDto);
            FileUtil.log("id:" + searchFaceDto.getFaceId() + " type:" + searchFaceDto.getType() + " 结束统计!");
            // 人脸检索记录日志
            try {
                int type = searchFaceDto.getType();
                if (type == 1) {
                    String[] dataTypes = searchFaceDto.getDataType().split(",");
                    String[] fids = searchFaceDto.getIds().split(",");
                    for (int i = 0; i < dataTypes.length; i++) {
                        long fid;
                        if (fids.length < 2) {
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
                            imageUrl = faceService.findOne(fid).getImageData();
                            break;
                        }
                        case 2: {
                            imageUrl = faceService.findOne(fid).getImageData();
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
            return new JsonObject(e.getMessage(), 1001);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("exception:", e);
            LOG.error("exception:" + e);

        }
        return new JsonObject(null);
    }

    @RequestMapping(value = "/search/face/camera/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中按摄像头分类进行人脸检索")
    public JsonObject searchFaceForCamera(@PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        try {
            Date startTime = new Date();
            List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceId(
                    searchFaceDto, 0, 2000000000);
            rsList = getPageList(rsList, page, pagesize);
            if (null == rsList) {
                return new JsonObject(rsList);
            }

            for (FaceResultByCameraDto rs : rsList) {
                int size = rs.getFaceResult().size();
                List<FaceResultDto> newRsList = rs.getFaceResult().subList(0,
                        pagesize > size ? size : pagesize);
                rs.setFaceResult(newRsList);
            }

            UserInfo userinfo = CurUserInfoUtil.getUserInfo();
            RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
            Date endTime = new Date();
            long delyTime = endTime.getTime() - startTime.getTime();
            writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

            return new JsonObject(rsList);
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

    /**
     * 支持1.2后搜索结果重点人员按库分类显示
     * 
     * @param page
     * @param pagesize
     * @param searchFaceDto
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/search/face/black/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在重点人员库中检索")
    public JsonObject searchFaceForBlack(@PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        try {
            List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceId(
                    searchFaceDto, 0, 2000000000);

            if (CollectionUtils.isEmpty(rsList)) {
                return new JsonObject(new HashMap<Long, List<BlackFaceResultDto>>());
            }

            List<BlackFaceResultDto> blackList = faceService.parseBlackFaceResultList(page, pagesize, rsList);

            Map<Long, List<BlackFaceResultDto>> detailMap = blackList.stream()
                    .collect(
                            Collectors
                                    .groupingBy(BlackFaceResultDto::getBankId));
            UserInfo userinfo = CurUserInfoUtil.getUserInfo();
            RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();

            Date startTime = new Date();
            Date endTime = new Date();
            long delyTime = endTime.getTime() - startTime.getTime();

            writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

            return new JsonObject(detailMap);
        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

    @RequestMapping(value = "/search/station/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中获取指定派出所的人脸检索结果")
    public JsonObject searchFaceForCameraByStationId(
            @PathVariable("id") long id, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        try {

            Date startTime = new Date();

            List<FaceResultByCameraDto> rsList = _solrService.getFaceByFaceId(
                    searchFaceDto, id, 2000000000);
            rsList = getPageList(rsList, page, pagesize);
            for (FaceResultByCameraDto rs : rsList) {
                int size = rs.getFaceResult().size();
                List<FaceResultDto> newRsList = rs.getFaceResult().subList(0,
                        pagesize > size ? size : pagesize);
                rs.setFaceResult(newRsList);
            }

            UserInfo userinfo = CurUserInfoUtil.getUserInfo();
            RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();

            Date endTime = new Date();
            long delyTime = endTime.getTime() - startTime.getTime();
            writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

            return new JsonObject(rsList);
        } catch (RedException e) {
                return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }
    
    @RequestMapping(value = "/search/attribute/station/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件在指定类型库中获取指定派出所的人脸检索结果")
    public JsonObject searchFaceForCameraByAttributeStationId(
            @PathVariable("id") long id, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        try {

            Date startTime = new Date();

            List<FaceResultByCameraDto> rsList = _solrService.getFaceAttributeByFaceId(
                    searchFaceDto, id, 2000000000);
            rsList = getPageList(rsList, page, pagesize);
            for (FaceResultByCameraDto rs : rsList) {
                int size = rs.getFaceResult().size();
                List<FaceResultDto> newRsList = rs.getFaceResult().subList(0,
                        pagesize > size ? size : pagesize);
                rs.setFaceResult(newRsList);
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

    @RequestMapping(value = "/search/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件依据摄像头分页获取人脸检索结果")
    public JsonObject searchFaceByCameraId(@PathVariable("id") long id,
            @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {

        // 判断用户是否具有该摄像头的权限 有就直接抛出异常了
        zoneAuthorizeService.checkIds(CameraInfo.class, id);

        try {
            Date startTime = new Date();
            List<FaceResultDto> faceResultList = _solrService
                    .getFaceByCameraId(searchFaceDto, id);

            UserInfo userinfo = CurUserInfoUtil.getUserInfo();
            RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();

            Date endTime = new Date();
            long delyTime = endTime.getTime() - startTime.getTime();
            writeSearchLog(userinfo, roleinfo, searchFaceDto, delyTime);

            return new JsonObject(getPageList(faceResultList, page, pagesize));

        } catch (RedException e) {
            return new JsonObject(e, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject(null);
    }

    @RequestMapping(value = "/search/time/range", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "通过人脸检索条件获取人脸检索结果的时间范围")
    public JsonObject searchFaceTimeRange(
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        try {
            List<FaceResultDto> faceResultList = _solrService
                    .searchFaceByIdInBank(searchFaceDto);
            List<Date> dataList = new ArrayList<Date>();
            if (faceResultList.size() == 0) {
                dataList.add(new Date());
                dataList.add(new Date());
            } else {
                dataList.add(faceResultList.get(faceResultList.size() - 1)
                        .getTime());
                dataList.add(faceResultList.get(0).getTime());
            }
            return new JsonObject(dataList);
        } catch (RedException e) {
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
    public JsonObject searchFacesByResidentAreaId(
            @PathVariable("page") int page,
            @PathVariable("areaId") long areaId,
            @RequestBody @Valid SearchFaceDto searchFaceDto) throws IOException {
        List<FaceResultDto> returnList = new ArrayList<FaceResultDto>();
        try {
            ResidentArea area = residentAreaRepository.findOne(areaId);
            String cameraIds = area.getCameraIds();
            String[] ids = cameraIds.split(",");
            // 过滤成该区域下 用户有权限的摄像头id
            List<Long> authIds = new ArrayList();
            for (int i = 0; i < ids.length; i++) {
                authIds.add(Long.parseLong(ids[i]));
            }
            authIds = zoneAuthorizeService.filterIds(CameraInfo.class, authIds, null);

            /*
             * if (null != ids) { for (String item : ids) {
             */
            if (null != authIds) {
                for (Long item : authIds) {
                    long cameraId = item;
                    List<FaceResultDto> rsList = _solrService
                            .getFaceByCameraId(searchFaceDto, cameraId, page,
                                    DEFAULT_PAGE_SIZE);
                    if (null != rsList) {
                        returnList.addAll(rsList);
                    }
                }
            }
            Collections.sort(returnList, new FaceResultDtoComparable("0"));
            return new JsonObject(returnList.subList((page - 1)
                    * DEFAULT_PAGE_SIZE,
                    returnList.size() > DEFAULT_PAGE_SIZE ? DEFAULT_PAGE_SIZE
                            : returnList.size()));
        } catch (Exception e) {
            LOG.error("get resident person faces error", e);
            return new JsonObject(null);
        }

    }

    @RequestMapping(value = "/search/cid/{cid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "根据身份证号查询图片")
    public JsonObject searchFacesByCid(@PathVariable("cid") String cid) throws IOException {
        String faceFeature = "";
        IdentityInfoDto infoDto = new IdentityInfoDto();
        if (XinYiSettings.getXinyiSwitch().equals("true")) {
            String content = null;
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                String url = XinYiSettings.getIdentityQueryApiUrlBegin() + cid
                        + XinYiSettings.getIdentityQueryApiUrlEnd();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpGet);
                content = IOUtils.toString(response.getEntity().getContent());
            } catch (Exception e) {
                LOG.error("xinyi api query identity error", e);
                return new JsonObject("信义接口查询出错", 1001);
            }
            String SFZH = "";
            String XM = "";
            String XB = "";
            String ZZXZ = "";
            String CSRQ = "";
            JSONObject jsonObject = JSONObject.fromObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (Object object : jsonArray) {
                JSONObject jsonObject2 = JSONObject.fromObject(object);
                SFZH = jsonObject2.getString("SFZH");
                XM = jsonObject2.getString("XM");
                XB = jsonObject2.getString("XB");
                ZZXZ = jsonObject2.getString("ZZXZ");
                CSRQ = jsonObject2.getString("CSRQ");
                faceFeature = jsonObject2.getString("XP");
            }
            infoDto.setCSRQ(CSRQ);
            infoDto.setSFZH(SFZH);
            infoDto.setXB(XB);
            infoDto.setXM(XM);
            infoDto.setXP(faceFeature);
            infoDto.setZZXZ(ZZXZ);
            if (StringUtils.isBlank(faceFeature)) {
                return new JsonObject("该身份证号不存在，请重新输入。", 1002);
            }
        } else {
            List<CidInfo> cidInfoList = cidInfoRepository.findByCid(cid);
            if (cidInfoList.isEmpty()) {
                return new JsonObject("没有查到该身份证信息，请重新输入！", 1002);
            }
            CidInfo cidInfo = cidInfoList.get(0);
            CidDetail cidDetail = cidDetailRepository.findByFromCidId(cidInfo.getId()).get(0);
            try {
                faceFeature = cidDetail.getCidImageBase64();
            } catch (Exception e) {
                e.printStackTrace();
            }
            infoDto.setXM(cidInfo.getXm());
            infoDto.setSFZH(cidInfo.getGmsfhm());
            infoDto.setXB(cidInfo.getXb());
            infoDto.setZZXZ(cidInfo.getXjzdz());
            if (null == cidInfo.getCsrq()) {
                infoDto.setCSRQ("");
            } else {
                infoDto.setCSRQ(DateUtil.getDateString(cidInfo.getCsrq()));
            }
            infoDto.setXP(faceFeature);
            if (faceFeature == null) {
                return new JsonObject(infoDto);
            }
        }

        ImageInfo imageInfoResp = null;
        ImageDetail imageDetail = null;
        List<T_IF_FACERECT> faceList = null;
        if (null != faceFeature && faceFeature.length() > 0) {
            imageInfoResp = imageBase64Handler(faceFeature);
        } else {
            LOG.error("You failed to upload Base64 String because the String was empty.");
            return new JsonObject("上传失败,文件为空!", 1001);
        }
        try {
            IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
            faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
            imageDetail = new ImageDetail(imageInfoResp);
            imageDetail.setFaceList(faceList);
            if (faceList.size() == 0 || faceList.size() > 1) {
                imageInfoResp.setFaces(faceList.size());
            } else {
                int redState = faceList.get(0).forbiden;
                if (redState >= 1) {
                    RedDetail rd = redDetailDao.findOne(new Long(redState).longValue());
                    if(null != rd){
                        imageDetail.setRedUri(rd.getFaceUrl());
                    }
                    imageDetail.setFaces(-1);// 红名单内
                    infoDto.setImageDetail(imageDetail);
                    return new JsonObject(infoDto,1004);
                } else {
                    imageInfoResp.setFaces(1);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        this._imageServiceItf.save(imageInfoResp);
        infoDto.setImageInfo(imageInfoResp);
        return new JsonObject(infoDto);

    }

    private ImageInfo imageBase64Handler(String fileData) {
        LOG.info("imageBase64Handler with base64");
        Map<String, String> _imageMagickOutput = this
                .imageBase64Operation(fileData);
        ImageInfo imageInfoResp = new ImageInfo();
        try {
            String fileName = _imageMagickOutput.get(ImageSize.ori.toString());
            String imageUrl = ImageInfoHelper.getRemoteImageUrl(fileName,
                    propertiesBean.getIsJar());
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUri(imageUrl);
            imageInfo.setTime(new Date());
            String faceUri = ImageInfoHelper.getRemoteFaceUrl(imageUrl,
                    propertiesBean.getIsJar());
            imageInfo.setFaceUri(faceUri);
            imageInfoResp = _imageServiceItf.save(imageInfo);
            LOG.info("ImageMagick output success: " + imageInfoResp);
        } catch (Exception ex) {
            LOG.error(ex.toString());
        }
        return imageInfoResp;
    }

    private Map<String, String> imageBase64Operation(String fileData) {
        LOG.info("imageBase64Operation with Base64 String");

        Map<String, String> _imageMagickOutput = new HashMap<String, String>();
        String dbFileName = null;
        String fullFileName = null;
        try {
            byte[] bytes = DatatypeConverter.parseBase64Binary(fileData);
            String fileExt = "jpg";
            LOG.info("fileExt:" + fileExt);
            String fileNameAppendix
            // = "temp" + "." + fileExt;
            = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
                    .format(new Date()) + "." + fileExt;
            LOG.info("fileNameAppendix:" + fileNameAppendix);
            dbFileName = FileUtil.getUploads(propertiesBean.getIsJar())
                    + fileNameAppendix;
            LOG.info("dbFileName:" + dbFileName);
            fullFileName = dbFileName;
            LOG.info("imageBase64Operation with fullFieldName:" + fullFileName);

            LOG.info("{} begin get angle", "base64 image");
            File f = new File(fullFileName);
            int angle = getAngle(new ByteArrayInputStream(bytes));
            LOG.info("{} angle is {}", "base64 image", angle);
            if (angle != 0) {
                BufferedImage img = rotateImage(bytes, angle, "base64 image");
                try {
                    ImageIO.write(img, "jpg", f);
                    img.flush();
                } catch (IOException e) {
                    LOG.error("{} after rotateImage write ImageIO exception:",
                            e);
                }
            } else {
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(f));
                stream.write(bytes);
                stream.close();
            }
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File(fullFileName)));
            stream.write(bytes);
            stream.close();

            LOG.info("Upload (image)file success." + fullFileName);
            String format4dbBase = this.formatImage(fullFileName, "jpg");
            _imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);
            return _imageMagickOutput;
        } catch (Exception e) {
            LOG.error("You failed to convert " + fullFileName + " => "
                    + e.toString());
        }
        return _imageMagickOutput;
    }

    /**
     * 获取图片正确显示需要旋转的角度（顺时针）
     * 
     * @return
     */
    public int getAngle(InputStream input) {
        int angle = 0;
        Metadata metadata;
        try {
            metadata = JpegMetadataReader.readMetadata(input);
            Directory directory = metadata
                    .getDirectory(ExifIFD0Directory.class);
            if (directory != null
                    && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                // Exif信息中方向
                int orientation = directory
                        .getInt(ExifIFD0Directory.TAG_ORIENTATION);
                // 原图片的方向信息
                if (6 == orientation) {
                    // 6旋转90
                    angle = 90;
                } else if (3 == orientation) {
                    // 3旋转180
                    angle = 180;

                } else if (8 == orientation) {
                    // 8旋转90
                    angle = 270;
                }
            }
        } catch (Exception e) {
            LOG.error("getAngle Exception", e);
        }
        return angle;
    }

    /**
     * 旋转图片为指定角度
     * 
     * @param bufferedimage
     *            目标图像
     * @param degree
     *            旋转角度
     * @return
     */
    public BufferedImage rotateImage(byte[] bytes, final int degree,
            String filename) {
        BufferedImage bufferedImage;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes); // 将b作为输入流；
        BufferedImage img = null;
        try {
            bufferedImage = ImageIO.read(in);
            int w = bufferedImage.getWidth();
            int h = bufferedImage.getHeight();
            int type = bufferedImage.getColorModel().getTransparency();
            Graphics2D graphics2d;
            (graphics2d = (img = new BufferedImage(w, h, type))
                    .createGraphics()).setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
            graphics2d.drawImage(bufferedImage, 0, 0, null);
            graphics2d.dispose();
            return img;
        } catch (IOException e) {
            LOG.error("{} rotateImage exception", filename, e);
        }
        return img;
    }

    enum ImageSize {
        ori, sml, ico
    }

    private String formatImage(String source, String formatStr)
            throws Exception {
        //
        String format4dbBase = FilenameUtils.getBaseName(source) + "_format"
                + "." + formatStr;// FilenameUtils.getExtension(source),always
        String format4db = FileUtil.getUploads(propertiesBean.getIsJar())
                + format4dbBase;
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
            LOG.info("ImageMagick success result(with format):"
                    + formatFullPath);
        }
        return format4dbBase;
    }

    @RequestMapping(value = "/search/camera/{id}/ids/{ids}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取摄像头最近抓拍一张人脸并检索指定ids的摄像头集内各检索一张最相似人脸")
    public JsonObject searchLastFaceAndHistory(@PathVariable("id") long id, @PathVariable("ids") String ids)
            throws IOException {

        // 判断用户是否具有该摄像头的权限 有就直接抛出异常了  ids的格式 2_92_2,8_92_2,9_92_2   cameraid_相似度  所以得解析出cameraid
        //cameraAuthorizeConsume.checkCameraId(id);
        String[] appointIds = ids.split(",");  
        String[] cameraIds = new String[appointIds.length];
        for(int i=0;i<appointIds.length;i++){
            cameraIds[i]=appointIds[i].split("_")[0];
        }
        zoneAuthorizeService.checkIds(CameraInfo.class, cameraIds);

        try {
            // Map<Long, FaceResultDto> rsMap = new HashMap<Long,
            // FaceResultDto>();
            List<FaceResultDto> faceRsList = new ArrayList<FaceResultDto>();
            List<FaceInfo> faceList = this.faceService.findBySourceId(id, 0, 1);
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
            ImageInfo imageInfo = _imageServiceItf.findById(fromImageId);
            face.setFile_bg(imageInfo.getUri());
            //
            face.setFile(faceList.get(0).getImageData());
            // rsMap.put(id, face);
            faceRsList.add(face);
            int k = 0;
            for (String cameraId : ids.split(",")) {
                List<FaceResultDto> rsList = _solrService.searchFaceByIdInCamera(faceList.get(0).getId(),
                        Integer.valueOf(cameraId.split("_")[1]) / 100F, GlobalConsts.IN_CAMERA_TYPE,
                        Long.valueOf(cameraId.split("_")[0]), Integer.valueOf(cameraId.split("_")[2]));
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
    
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回抓拍人脸总数")
    public JsonObject count() {
        if (GlobalConsts.faceBaseCount == 0) {
            long total = this.faceService.count();
            GlobalConsts.faceDayCount = this.faceService.countToday();
            GlobalConsts.faceBaseCount = total;
            GlobalConsts.faceMinCount = 0;
        }
        // return new JsonObject(this.faceInfoDaoImpl.count());
        return new JsonObject(GlobalConsts.faceBaseCount
                + GlobalConsts.getFaceDayCount());
    }

    @RequestMapping(value = "/count/today", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回今天抓拍人脸总数")
    public JsonObject countToday() {
        // return new JsonObject(this.faceInfoDaoImpl.countToday());
        if (GlobalConsts.faceBaseCount == 0) {
            long total = this.faceService.count();
            GlobalConsts.faceDayCount = this.faceService.countToday();
            GlobalConsts.faceBaseCount = total;
            GlobalConsts.faceMinCount = 0;
        }
        return new JsonObject(GlobalConsts.getFaceDayCount());
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除特定face图片")
    public JsonObject deleteByFaceId(@PathVariable("id") long id) {
        try{
            UserInfo ui = CurUserInfoUtil.getUserInfo();
            String roleName = roleRepository.findOne(ui.getRoleId()).getName();     
            if (!roleName.equals(GlobalConsts.SUPER_ADMIN)){
                return new JsonObject("当前用户无权操作删除图片!", 1002);
            }
            _faceService.deleteFaceById(id);
            return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
                    HttpStatus.OK));
        }catch(Exception e){
           LOG.error("delete face error id:"+id+",error:",e); 
           return new JsonObject("图片删除失败", 1001);
        }
    }

    @RequestMapping(value = "/rate", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回最近每秒抓拍人脸数")
    public JsonObject rate() {
        return new JsonObject(GlobalConsts.faceMinCount / 60.00F);
    }

    /**
     * 将搜索结果图片导出到指定目录
     * 
     * @param faceResultList
     * @throws Exception
     */
    private void saveSearchImageToDir(List<FaceResultDto> faceResultList,
            String randPath, String fileName, ProcessInfo process, int key)
            throws Exception {
        File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
                + "export/image/" + randPath + "/" + fileName + "/");
        FileUtil.checkFileExist(file);
        int j = 0;
        for (FaceResultDto item : faceResultList) {
            if (!GlobalConsts.stateMap.get(key))
                return;
            boolean state = false;
            try {
                long imageId = 0;
                String id = item.getId();
                if (id.indexOf("_") != -1) {
                    BlackDetail detail = this.blackDetailDao.findOne(Long
                            .parseLong(id.substring(2, id.length())));
                    imageId = detail.getFromImageId();
                } else {
                    FaceInfo face = this.faceService.findOne(Long
                            .parseLong(item.getId()));
                    imageId = face.getFromImageId();
                }
                ImageInfo ii = (ImageInfo) _imageServiceItf.findById(imageId);
                String url = ii.getUri();
                /*
                 * if (!CommonUtil.checkImage(url)) { continue; }
                 */
                Date time = ii.getTime();
                String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
                        .format(time);
                String fullFileName = FileUtil.getZipUrl(propertiesBean
                        .getIsJar())
                        + "export/image/"
                        + randPath
                        + "/"
                        + fileName + "/" + timeStr + "_" + j + ".jpg";

                // InputStream is = FileUtil.readStreamFromUri(url);
                boolean status = FileUtil.copyUrl(url, fullFileName);
                if (status) {
                    process.setSuccessNum(process.getSuccessNum() + 1);
                } else {
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

    private void saveSmallImageToDir(FaceResultDto item, String randPath,
            String folderName, String timeStr, int j) throws Exception {
        String fullFileName = FileUtil.getZipUrl(propertiesBean.getIsJar())
                + "export/image/" + randPath + "/" + folderName + "/" + timeStr
                + "_" + j + ".jpg";
        FileUtil.copyUrl(item.getFile(), fullFileName);
    }

    private void compressZip(String randPath) throws Exception {
        File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
                + "export/zip/" + randPath + "/");
        FileUtil.deleteFile(file, true);
        FileUtil.checkFileExist(file);
        FileUtil.zipCompress(FileUtil.getZipUrl(propertiesBean.getIsJar())
                + "export/image/" + randPath,
                FileUtil.getZipUrl(propertiesBean.getIsJar()) + "export/zip/"
                        + randPath + "/faceData.zip");
        File zipFile = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
                + "export/zip/" + randPath + "/faceData.zip");
        if (!zipFile.exists()) {
            throw new Exception("压缩失败");
        }
    }

    private void saveImageToDir(List<ImageInfo> faceList, String randPath,
            String appendName, ProcessInfo process, int key) throws Exception {
        File file = new File(FileUtil.getZipUrl(propertiesBean.getIsJar())
                + "export/image/" + randPath + "/" + appendName + "/");
        FileUtil.checkFileExist(file);
        int j = 0;
        for (ImageInfo item : faceList) {
            if (!GlobalConsts.stateMap.get(key))
                return;
            boolean state = false;
            try {
                String url = item.getUri();
                Date time = item.getTime();
                String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
                        .format(time);
                String fullFileName = FileUtil.getZipUrl(propertiesBean
                        .getIsJar())
                        + "export/image/"
                        + randPath
                        + "/"
                        + appendName + "/" + timeStr + "_" + j + ".jpg";
                // InputStream is = FileUtil.readStreamFromUri(url);
                boolean status = FileUtil.copyUrl(url, fullFileName);
                if (status) {
                    process.setSuccessNum(process.getSuccessNum() + 1);
                } else {
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
                if (0 != age && 0 != resultAge
                        && !CommonUtil.checkAge(age, resultAge)) {
                    faceList.remove(i);
                    continue;
                }
            }
        }
    }

    // 记录搜索操作
        public void writeSearchLog(UserInfo userinfo, RoleInfo roleinfo, SearchFaceDto searchFaceDto, long timeDelay) throws SQLException {

                Long uid = userinfo.getId();
            Long policeStationId = userinfo.getPoliceStationId();
            String stationname = policestationDao.findOne(policeStationId).getStationName(); // 单位名称
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
                        .getServer(GlobalConsts.coreMap.get(GlobalConsts.SEARCH_INFO_TYPE));

                for (int i = 0; i < faceids.size(); i++) {

                    AuditLogInfo log = new AuditLogInfo();
                                SearchLogInfo searchLog = new SearchLogInfo();

                    long fid = (long) faceids.get(i);
                    
                    log.setOwner(owner);
                    log.setOperation("search");
                    log.setObject(datatype.get(i));
                    log.setObjectId(fid);
//                  if (checkFaceIsOrNotInRedDetails(fid)) {
//                      log.setObject_status(2000);
//                  } else {
//                  }
                    log.setObject_status(17);
                    log.setTitle(log.getOwner() + "检索了图片信息," + userrealname + "," + stationname);
                    searchLog.setCreated(new Date());
                    searchLog.setResultCode(0);
                    searchLog.setOwner(owner);
                    searchLog.setTimeDelay(timeDelay);

                    switch (Integer.parseInt(datatype.get(i).toString())) {

                    case 0: {
                        String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
                        if (authority.trim().length() == 0) {
                            System.out.println("没权限查询黑名单库");
                            return;
                        }
                        BlackDetail blackDetail = blackDetailDao.findOne(fid, authority.split(",")).get(0);
                        faceFeature.add(blackDetail.getBase64FaceFeature());
                        faceurl.add(blackDetail.getImageData());
                        version.add(blackDetail.getVersion());
                        break;
                    }
                    case 1: {
                        FaceInfo faceInfo = faceService.findOne(fid);
                        faceFeature.add(faceInfo.getBase64FaceFeature());
                        faceurl.add(faceInfo.getImageData());
                        version.add(faceInfo.getVersion());
                        break;
                    }
                    case 2: {
                        FaceInfo faceInfo = faceService.findOne(fid);
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
                        JuZhuDetail juzhuDetail = juZhuDetailRepository.findOne(fid);
                        faceFeature.add(juzhuDetail.getBase64FaceFeature());
                        faceurl.add(juzhuDetail.getImageData());
                        version.add(juzhuDetail.getVersion());
                        break;
                    }
                    case 5: {
                        OtherDetail otherDetail = otherDetailRepository.findOne(fid);
                        faceFeature.add(otherDetail.getBase64FaceFeature());
                        faceurl.add(otherDetail.getImageData());
                        version.add(otherDetail.getVersion());
                        break;
                    }
                    case 6: {
                        OtherDetail otherDetail = otherDetailRepository.findOne(fid);
                        faceFeature.add(otherDetail.getBase64FaceFeature());
                        faceurl.add(otherDetail.getImageData());
                        version.add(otherDetail.getVersion());
                        break;
                    }
                    default: {
                        OtherDetail otherDetail = otherDetailRepository.findOne(fid);
                        faceFeature.add(otherDetail.getBase64FaceFeature());
                        faceurl.add(otherDetail.getImageData());
                        version.add(otherDetail.getVersion());
                        break;
                    }
                    }

                    log.setMessage(accounttype + owner + "检索了图片信息，" + faceurl.get(i));
                                    searchLog.setMessage("检索了图片信息，" + faceurl.get(i));

                    // 把用户和对应的搜索图片存进map 不重复记日志

                    // if(!userSearchMap.containsKey(userinfo.getLogin()+faceurl)){
                    if (!userSearchMap.containsKey(userinfo.getLogin() + faceurl.get(i))) {
                        SearchReasonDto srd = GlobalConsts.searchReasonMap.get(userinfo.getId());
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

    /**
     * 同步批量把图片进行Base64编码
     */
    public static void synImageBase64(
            Collection<? extends CidInfoDto> cidInfoCollection) {
        long currentTime = System.currentTimeMillis();

        // 如果cidInfoCollection为空，则直接返回
        if (null == cidInfoCollection || cidInfoCollection.isEmpty()) {
            return;
        }

        // 将图片Base64编码任务提交给线程池
        List<ForkJoinTask<?>> forkJoinTaskList = new ArrayList<ForkJoinTask<?>>();
        for (CidInfoDto cidInfo : cidInfoCollection) {
            forkJoinTaskList.add(ApplicationResource.THREAD_POOL
                    .submit(new ImageBase64Runable(cidInfo)));
        }

        // 等待线程池运行完提交的任务
        for (ForkJoinTask<?> task : forkJoinTaskList) {
            task.join();
        }
        LOG.info(
                "xxxxxx synImageBase64 need {}ms, cidInfoCollection size is {}, pool size is {}, pool parallelism is {}",
                System.currentTimeMillis() - currentTime,
                cidInfoCollection.size(),
                ApplicationResource.THREAD_POOL.getPoolSize(),
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
                        cidInfo.setImageBase64(FileUtil.GetImageStr(cidInfo
                                .getFile()));
                    }
                } catch (Throwable e) {
                    LOG.error("image to Base64 catch exception: ", e);
                }
                long needTime = System.currentTimeMillis() - currentTime;
                if (needTime >= 500) {
                    LOG.info(
                            "xxxxxx image to Base64 need {}ms, file is {}, photo is {}, needPhotoBase64 is {}",
                            System.currentTimeMillis() - currentTime,
                            cidInfo.getFile(), cidInfo.getPhoto(),
                            cidInfo.isNeedPhotoBase64());
                }
            }
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
                    faceUrl = this.faceService.findOne(faceId).getImageData();
                    break;
                }
                case 2: {
                    faceUrl = this.faceService.findOne(faceId).getImageData();
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

    // 搜索重点人员库时 根据 反恐子类  以及反恐区域   进行过滤
    public List<FkPersonResultDto> filterByFktype(List<FaceResultDto> faecList, int[] fkType, String nameOrCid,String subInstitutions[],String localInstitutions[]) {
        Iterator<FaceResultDto> face = faecList.iterator();
        List<FkPersonResultDto> searchFkFaceList = new ArrayList<FkPersonResultDto>();
        while (face.hasNext()) {
            FaceResultDto faceNext = face.next();
            List<BlackDetail> blackList = blackDetailDao.findById(Long.valueOf(faceNext.getId()).longValue());
            BlackDetail blackDetail = null;
            if(blackList!=null&&blackList.size()!=0){
            blackDetail = blackList.get(0); 
            }else{
               // return searchFkFaceList;   
                continue;
            }
            
            long personDetailId = blackDetail.getFromPersonId();
            PersonDetail personDetail = personDetailService.findById(personDetailId);
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
                    LOG.info("filterByFktype personId:"+personDetailId);
                    List<FkPersonAttr> fkPersonAttr = fkPersonAttrDao.findByFromPersonId(personDetailId);
                    if(fkPersonAttr!=null && !fkPersonAttr.isEmpty()){                                        
                    fkFace.setRegisterAddress(fkPersonAttr.get(0).getRegisterAddress());  
                   
                    //补充反恐人员直接属于   1.市局  2.市局-分局   的情况                  
                    String subCode = fkPersonAttr.get(0).getFkSubInstitutionCode();
                    String localCode = fkPersonAttr.get(0).getFkLocalInstitutionCode();
                    List subList = Arrays.asList(subInstitutions);
                    List localList = Arrays.asList(localInstitutions);
                    boolean addFlag = false;
                    //反恐人员直接属于市局
                    if((!StringUtils.isEmpty(subCode)&&subCode.equals("440300000000"))&&(!StringUtils.isEmpty(localCode)&&localCode.equals("440300000000"))){
                       addFlag = true;
                    }                                    
                    if((subList.contains(subCode)&&localList.contains(localCode))||(subList.contains(localCode)&&localList.size()>1)||(addFlag&&subList.size()>1&&localList.size()>1)){
                        if (!nameOrCid.trim().equals("")) {                          
                               if (personDetail.getCid().equals(nameOrCid) || personDetail.getRealName().equals(nameOrCid)) {                                
                                   searchFkFaceList.add(fkFace);                       
                               }
                               
                           }else{
                               searchFkFaceList.add(fkFace);   
                           }
                        
                    }
                    
                }else{
                    LOG.info("fkPersonAttr can not find, personId:"+personDetailId); 
                }
 }
            }

        }

        return searchFkFaceList;

    }
    
    @RequestMapping(value = "/fk/param/place/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "分页多参数获取指定反恐场所最近抓拍图片")
    public JsonObject findByParamFkPlaceId(@PathVariable("id") long id, @PathVariable("page") int page,
            @PathVariable("pagesize") int pagesize, @RequestBody @Valid FindFkPlaceFaceDto findFkPlaceFaceDto) {

        FkPlaceCamera fkPlaceCamera = fkPlaceCameraDao.findOne(id);
        String cameraIds = fkPlaceCamera.getCameraIds();
        List<String> cIds =java.util.Arrays.asList(cameraIds.split(","));
        List<Long> cids = new ArrayList<Long>();
        for(int i=0;i<cIds.size();i++){
            cids.add(Long.valueOf(cIds.get(i)).longValue());
        }
        
        //场所下的每一个摄像头 根据用户权限过滤  有权限的才能展示 
        List<Long>  cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class, cids, null);
        String newCameraIds = org.apache.commons.lang.StringUtils.join(cameraIdList.toArray(),","); 
        findFkPlaceFaceDto.setCameraIds(newCameraIds);         
        try {           
            List<FaceInfo> faceList = null;       
            faceList = this._faceService.findByFkPlace(findFkPlaceFaceDto, page, pagesize);
            return new JsonObject(faceList);
        } catch (Exception e) {
            LOG.error("get face by params camera error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }
    
    
    public String[] changeIdToCode(String[] institutionIds){
      String[] institutionCodes = new String[institutionIds.length];
      
      for(int i=0;i<institutionIds.length;i++){         
          
          FkInstitutionCode institution = fkInstitutionCodeDao.findOne(Long.valueOf(institutionIds[i]).longValue());
               
          String institutionCode = "";
          
          if(institution!=null){
              institutionCode = institution.getJGDM();
             
          }
           institutionCodes[i] = institutionCode;
      }
      
      return institutionCodes;
        
    }

     public List<FkPersonResultDto> filterByFkBankAuthority(List<FkPersonResultDto> fkPersonList,String bankIdAutority){
        
        List<FkPersonResultDto> list = new ArrayList<FkPersonResultDto>();
        for(int i=0;i<fkPersonList.size();i++){
            long personDetailId = fkPersonList.get(i).getId();
            PersonDetail p = personDetailService.findById(personDetailId);
            if(bankIdAutority.indexOf("," + p.getBankId() + ",") >= 0) { 
                list.add(fkPersonList.get(i));
                }
        }
        return list;
    }
    
}
