/**
 *
 */
package intellif.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.soap.providers.com.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dao.BlackDetailDao;
import intellif.dao.ImageInfoDao;
import intellif.database.entity.BlackDetail;
import intellif.dto.JsonObject;
import intellif.dto.QueryFaceDto;
import intellif.enums.IFaceSdkTypes;
import intellif.ifaas.T_IF_FACERECT;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.ImageSettings;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.ImageInfoHelper;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import nu.xom.jaxen.NamedAccessNavigator;
import intellif.settings.ImageSettings;

/**
 * <h1>The Class ImageController.</h1>
 * The ImageController which serves request of the form /image and returns a JSON object representing an instance of ImageInfo.
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
//@RequestMapping("/intellif/image")
@RequestMapping(GlobalConsts.R_ID_IMAGE)
public class ImageController {
	private static Logger LOG = LogManager.getLogger(ImageController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============
   	 
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the image info is successfully created or not.")
    public JsonObject create(@RequestBody @Valid ImageInfo imageInfo) {
        return new JsonObject(_imageServiceItf.save(imageInfo));
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the image info id is successfully get or not.")
    public JsonObject get(@PathVariable("id") long id) {
        return new JsonObject(this._imageServiceItf.findById(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  image info is successfully updated or not.")
    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid ImageInfo imageInfo) {
//		ImageInfo find = this._imageInfoDao.findOne(id);
        imageInfo.setId(id);
        return new JsonObject(this._imageServiceItf.save(imageInfo));
    }

   /* @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the image info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") long id) {
        this._imageServiceItf.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }*/

    @RequestMapping(value = "/face/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据抓拍人脸ID获取大图图片信息")
    public JsonObject getByFaceId(@PathVariable("id") long id) {
        FaceInfo face = this.faceService.findOne(id);
        if (null != face) {
            return new JsonObject(this._imageServiceItf.findById(face.getFromImageId()));
        }
        return new JsonObject(null);
    }
    @RequestMapping(value = "/detect/image/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据大图获取所有小图信息")
    public JsonObject getAllFacesById(@PathVariable("id") long id) {
    	List<FaceInfo> fList = null;
    	try{
    		ImageInfo ii = _imageServiceItf.findById(id);
    		ImageInfo nii = new ImageInfo();
    		nii.setUri(ii.getUri());
    		nii.setTime(new Date());
    		ImageInfo  imageInfoResp = _imageServiceItf.save(nii);
    		IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
    		if(null == ifaceSdkTarget){
    			return new JsonObject("没有可用引擎资源！", 1001); 
    		}
    		 ifaceSdkTarget.image_detect_extract(ii.getUri(), imageInfoResp.getId());
    		 fList = this.faceService.findByFromImageId(imageInfoResp.getId());
    	}catch(Exception e){
    		LOG.error("get faces from big image error,e:",e);
    	}
        return new JsonObject(fList);
    }
    
    @RequestMapping(value = "/face/json/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据抓拍人脸ID获取大图图片信息和位置json")
    public JsonObject getJsonByFaceId(@PathVariable("id") long id) {
        FaceInfo face = this.faceService.findOne(id);
        String json = face.getJson();
    	ImageInfo image = (ImageInfo) this._imageServiceItf.findById(face.getFromImageId());
    	try {
			if (ImageSettings.isJsonSwitch()) {
				if (null == json | "".equals(json)) {
					IFaceSdkTarget iFaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
					json = iFaceSdkTarget.face_detect_rect(image.getUri(), id);
				}
				image.setJson(json);
				face.setJson(json);
				this.faceService.save(face);            
			}
		} catch (Exception e) {
			LOG.error("get image from face id:"+id+" error:",e);
		}
    	return new JsonObject(image);
    }
  
    
	@RequestMapping(value = "/black/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据抓拍人脸ID获取大图图片信息和位置信息json")
    public JsonObject getByBlackId(@PathVariable("id") long id) {
    	String authority = _userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE);
    	if(authority.trim().length()==0){
    		return new JsonObject("本单位没有库授权！", 1001);
    	}
        try {
        	List<BlackDetail> blackList = this._blackDetailDao.findOne(id, authority.split(","));
        	ImageInfo imageInfo = (ImageInfo) this._imageServiceItf.findById(blackList.get(0).getFromImageId());
        	String json = blackList.get(0).getJson();
			if (null == json | "".equals(json)) {
				IFaceSdkTarget iFaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
				json = iFaceSdkTarget.face_detect_rect(imageInfo.getUri(), id);
			}
			imageInfo.setJson(json);
			if (blackList.size()>0) {
				return new JsonObject(imageInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new JsonObject(null);
    }

    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据嫌疑人ID获取大图图片信息和位置信息json")
    public JsonObject getByPersonId(@PathVariable("id") long id) {
        try {
        	List<BlackDetail> black = this._blackDetailDao.findByFromPersonId(id);
        	ImageInfo imageInfo = (ImageInfo) this._imageServiceItf.findById(black.get(0).getFromImageId());
        	String json = black.get(0).getJson();
			if (null == json | "".equals(json)) {
	        	IFaceSdkTarget iFaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
				json = iFaceSdkTarget.face_detect_rect(imageInfo.getUri(), id);
			}
			imageInfo.setJson(json);
			//if (null != black && black.size() > 0) {   // find bugs nullcheck of value previously dereferenced
				return new JsonObject(imageInfo);  
			//}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new JsonObject(null);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "返回所有图片的总数")
    public JsonObject count() {
    	if(GlobalConsts.imageBaseCount == 0){
    		long total = this._imageServiceItf.count();
    		GlobalConsts.imageDayCount = this._imageServiceItf.countDay();
    		GlobalConsts.imageBaseCount = (long) (total-GlobalConsts.imageDayCount);
    		GlobalConsts.imageMinCount = 0;
    	}
    	return new JsonObject(GlobalConsts.imageBaseCount+GlobalConsts.getImageDayCount());
    }
    
    @RequestMapping(value = "/countSize", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "计算图片image和人脸文件大小")
    public JsonObject countSize() {
    	if(GlobalConsts.imageBaseCount == 0){
    		GlobalConsts.imageBaseCount = this._imageServiceItf.count();
    		GlobalConsts.imageDayCount = this._imageServiceItf.countDay();
    		GlobalConsts.imageDayCount = 0;
    		GlobalConsts.imageMinCount = 0;
    	}
    	if(GlobalConsts.faceBaseCount == 0){
    		long total = this.faceService.count();
    		GlobalConsts.faceDayCount = this.faceService.countToday();
    		GlobalConsts.faceBaseCount = total;
    		GlobalConsts.faceMinCount = 0;
    	}
    	long faceCount = GlobalConsts.faceBaseCount+GlobalConsts.getFaceDayCount();
    	long imageCount = GlobalConsts.imageBaseCount+GlobalConsts.getImageDayCount();
    	float returnSize = (imageCount*600+faceCount*20)/1024;
    	return new JsonObject(returnSize);
    	
    }
    
    // 给玛隆服装提供的接口
	@RequestMapping(value = "/param/camera/{id}/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "分页多参数获取指定摄像头最近抓拍图片")
	public JsonObject findByParamCameraId(@PathVariable("id") long id, @PathVariable("page") int page,
			@PathVariable("pagesize") int pagesize, @RequestBody @Valid QueryFaceDto queryFaceDto) {
		try {
			queryFaceDto.setSourceId(id);
			List<ImageInfo> imageList = null;
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
			imageList = this.faceService.findImageByCombinedParams(queryFaceDto, page, pagesize);
			return new JsonObject(imageList);
		} catch (Exception e) {
			LOG.error("catch exception: ", e);
			return new JsonObject(e.getMessage(), 1001);
		}
	}

}
