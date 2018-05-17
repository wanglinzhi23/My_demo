package intellif.controllers;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.CidDetailDao;
import intellif.dao.CidInfoDao;
import intellif.dao.ImageInfoDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.JuZhuInfoDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.OtherInfoDao;
import intellif.database.entity.UserInfo;
import intellif.dto.CidInfoDto;
import intellif.dto.JsonObject;
import intellif.dto.PersonDto;
import intellif.dto.StaticIInfoDto;
import intellif.enums.IFaceSdkTypes;
import intellif.enums.SourceTypes;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.ESurveilIoctrlType;
import intellif.ifaas.E_FACE_EXTRACT_TYPE;
import intellif.ifaas.T_IF_FACERECT;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.settings.ImageSettings;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.FileUtil;
import intellif.utils.IFaceSDKUtil;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.CidDetail;
import intellif.database.entity.CidInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.JuZhuDetail;
import intellif.database.entity.JuZhuInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.OtherDetail;
import intellif.database.entity.OtherInfo;
import intellif.database.entity.PersonDetail;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.soap.providers.com.Log;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_POLICE_CLOUD)
public class PoliceCloudController {

    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(PoliceCloudController.class);

    @Autowired
    private CidInfoDao cidInfoRepository;
    @Autowired
    private CidDetailDao cidDetailRepository;
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private JuZhuDetailDao juzhuDetailRepository;
    @Autowired
    private JuZhuInfoDao juzhuInfoRepository;
    @Autowired
    private OtherDetailDao otherDetailRepository;
    @Autowired
    private OtherInfoDao otherInfoRepository;
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;

//    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
//    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the server info is successfully created or not.")
//    public JsonObject create(@RequestBody @Valid ServerInfo serverInfo) {
//        return new JsonObject(_serverInfoDao.save(serverInfo));
//    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取全部身份证信息")
    public JsonObject listInfo() {
        return new JsonObject(this.cidInfoRepository.findAll());
    }
    
    @RequestMapping(value = "/info/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "分页获取身份证信息")
    public JsonObject findLastInfo(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
        return new JsonObject(this.cidInfoRepository.findLast((page - 1) * pageSize, pageSize));
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取全部身份证人脸信息")
    public JsonObject listDetail() {
        return new JsonObject(this.cidDetailRepository.findAll());
    }
    
    @RequestMapping(value = "/detail/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "分页获取身份证人脸信息")
    public JsonObject findLastDetail(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize) {
        return new JsonObject(this.cidDetailRepository.findLast((page - 1) * pageSize, pageSize));
    }

    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id户籍人员信息")
    public JsonObject getInfo(@PathVariable("id") long id) {
        return new JsonObject(this.cidInfoRepository.findOne(id));
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id户籍人员人脸信息")
    public JsonObject getDetail(@PathVariable("id") long id) {
        return new JsonObject(this.cidDetailRepository.findOne(id));
    }

    @RequestMapping(value = "/other/info/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id其他（在逃、警综）人员信息")
    public JsonObject getOtherInfo(@PathVariable("id") long id) {
        return new JsonObject(this.otherInfoRepository.findOne(id));
    }

    @RequestMapping(value = "/other/detail/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定id其他（在逃、警综）人员人脸信息")
    public JsonObject getOtherDetail(@PathVariable("id") long id) {
        return new JsonObject(this.otherDetailRepository.findOne(id));
    }

    @RequestMapping(value = "/detail/{type}/info/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取指定Info类型Id（在逃、警综）人员人脸信息")
    public JsonObject getOtherDetailByInfo(@PathVariable("type") int type, @PathVariable("id") long id) {
    	switch (type) {
		case GlobalConsts.CID_INFO_TYPE:
	        return new JsonObject(this.cidDetailRepository.findByFromCidId(id));
		case GlobalConsts.JUZHU_INFO_TYPE:
	        return new JsonObject(this.juzhuDetailRepository.findByFromCidId(id));
		default:
	        return new JsonObject(this.otherDetailRepository.findByFromCidId(id));
		}
    }

    //@RequestMapping(value = "/info/{id}/face/{faceid}", method = RequestMethod.POST)
    @RequestMapping(value = "/info/id/{id}/type/{type}/faceid/{faceid}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "户籍信息 增加人脸图片")
    public JsonObject addDetailToCid(@PathVariable("id") long id, @PathVariable("type") long type, @PathVariable("faceid") long faceid) {
    	if(type==3){
    		CidDetail detail = new CidDetail();
        	detail.setZplxmc("VIP");
        	detail.setFromCidId(id);
        	detail.setIndexed(-1);
        	detail.setFromImageId(faceService.findOne(faceid).getFromImageId());
        	
        	FaceInfo face = faceService.findOne(faceid);
        	if(null == face) return new JsonObject("人脸信息不存在!", 1001);
        	detail.setImageData(face.getImageData()+"?vip");
            return new JsonObject(this.cidDetailRepository.save(detail));
    	}else if(type==4){
    		JuZhuDetail detail = new JuZhuDetail();
        	detail.setZplxmc("VIP");
        	detail.setFromCidId(id);
        	detail.setIndexed(-1);
        	detail.setFromImageId(faceService.findOne(faceid).getFromImageId());
        	
        	FaceInfo face = faceService.findOne(faceid);
        	if(null == face) return new JsonObject("人脸信息不存在!", 1001);
        	detail.setImageData(face.getImageData()+"?vip");
            return new JsonObject(this.juzhuDetailRepository.save(detail));
    	}
		 return new JsonObject("请输入type类型！", 1001);
    }
    
    @RequestMapping(value = "/judge/detail/type/{type}/id/{id}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "审核人脸图片允许加入户籍信息")
    public JsonObject judge(@PathVariable("type") long type,@PathVariable("id") long id) {
    	if(type==3){
    		CidDetail detail = cidDetailRepository.findOne(id);
        	try{
        		IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
            	List<T_IF_FACERECT> faceRectFeatures = ifaceSdkTarget.processFaceDetectExtract(detail.getImageData(), detail.getId(), E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_CIDDETAIL.getValue());
            	if (null == faceRectFeatures || faceRectFeatures.size() == 0) {
            		return new JsonObject("没有找到人脸!", 1001);
            	}
        	} catch (Exception e) {
        		e.printStackTrace();
        		return new JsonObject("无法连接上人脸引擎服务器!", 1001);
        	}
        	return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	}else if(type==4){
    		JuZhuDetail detail = juzhuDetailRepository.findOne(id);
        	try{
        		IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
            	List<T_IF_FACERECT> faceRectFeatures = ifaceSdkTarget.processFaceDetectExtract(detail.getImageData(), detail.getId(), E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_JUNZHUDETAIL.getValue());
            	if (null == faceRectFeatures || faceRectFeatures.size() == 0) {
            		return new JsonObject("没有找到人脸!", 1001);
            	}
        	} catch (Exception e) {
        		e.printStackTrace();
        		return new JsonObject("无法连接上人脸引擎服务器!", 1001);
        	}
        	return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	}
    	return new JsonObject("请输入type类型！", 1001);
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  server info is successfully updated or not.")
//    public JsonObject update(@PathVariable("id") long id, @RequestBody @Valid ServerInfo serverInfo) {
////		ServerInfo find = this._serverInfoDao.findOne(id);
//        serverInfo.setId(id);
//        return new JsonObject(this._serverInfoDao.save(serverInfo));
//    }

    @RequestMapping(value = "/detail/type/{type}/id/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除录入户籍人脸图片")
    public JsonObject delete(@PathVariable("type") int type,@PathVariable("id") long id) {
    	if(type==3){
    		CidDetail detail = cidDetailRepository.findOne(id);
        	if(!detail.getZplxmc().equals("VIP"))
        		return new JsonObject("非录入图片不允许删除!", 1001);
            this.cidDetailRepository.delete(id);
            return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	}else if(type==4){
    		JuZhuDetail detail = juzhuDetailRepository.findOne(id);
        	if(!detail.getZplxmc().equals("VIP"))
        		return new JsonObject("非录入图片不允许删除!", 1001);
            this.juzhuDetailRepository.delete(id);
            return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    	} else {
    		if(otherDetailRepository.findOne(id).getZplxmc().equals(String.valueOf(type))){
    			otherDetailRepository.delete(id);
    			try {
//					_solrDataServiceItf.deleteById(type, id+"");
					_solrDataServiceItf.deleteById(GlobalConsts.coreMap.get(type), id+"");
				} catch (Exception e) {
					LOG.error("删除静态库数据错误：", e);
					e.printStackTrace();
				}
                return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    		}
    	}
    	return new JsonObject("请输入type类型！", 1001);
    }

    @RequestMapping(value = "/detail/face/{type}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取全部待审核的身份证人脸信息")
    public JsonObject listDetailFace(@PathVariable("type") long type) {
    	if(type==3){
    		List<CidInfoDto> cidInfoDtoList = new ArrayList<CidInfoDto>();
        	List<CidDetail> detailList = this.cidDetailRepository.findByIndexedAndZplxmc(-1, "VIP");
            List<Long> ids = new ArrayList<Long>();
            for(CidDetail detail : detailList) {
            	ids.add(detail.getFromCidId());
            }
            
            if(ids.size()==0) return new JsonObject(cidInfoDtoList);
        	List<CidInfo> cidList = cidInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));
        	Map<Long, CidInfo> cidMap = new HashMap<Long, CidInfo> ();
        	for(CidInfo cid : cidList) {
        		cidMap.put(cid.getId(), cid);
        	}
        	
        	for(CidDetail detail : detailList) {
    			CidInfoDto cidInfoDto = new CidInfoDto(cidMap.get(detail.getFromCidId()));
    			cidInfoDto.setFile(detail.getImageData());
    			cidInfoDto.setId(detail.getId());
    			cidInfoDtoList.add(cidInfoDto);
        	}
            return new JsonObject(cidInfoDtoList);
    	}else if(type==4){
    		List<CidInfoDto> juzhuInfoDtoList = new ArrayList<CidInfoDto>();
        	List<JuZhuDetail> detailList = this.juzhuDetailRepository.findByIndexedAndZplxmc(-1, "VIP");
            List<Long> ids = new ArrayList<Long>();
            for(JuZhuDetail detail : detailList) {
            	ids.add(detail.getFromCidId());
            }
            
            if(ids.size()==0) return new JsonObject(juzhuInfoDtoList);
        	List<JuZhuInfo> juzhuList = juzhuInfoRepository.findByIds(ids.toArray(new Long[ids.size()]));
        	Map<Long, JuZhuInfo> juzhuMap = new HashMap<Long, JuZhuInfo> ();
        	for(JuZhuInfo juzhu : juzhuList) {
        		juzhuMap.put(juzhu.getId(), juzhu);
        	}
        	
        	for(JuZhuDetail detail : detailList) {
    			CidInfoDto cidInfoDto = new CidInfoDto(juzhuMap.get(detail.getFromCidId()));
    			cidInfoDto.setFile(detail.getImageData());
    			cidInfoDto.setId(detail.getId());
    			juzhuInfoDtoList.add(cidInfoDto);
        	}
            return new JsonObject(juzhuInfoDtoList);
    	}
    	return new JsonObject("请输入type类型！", 1001);
    }
    
    //根据t_cid_detail的id 取大图  即 里面的fromimageid     8.12 
    @RequestMapping(value = "/image/type/{type}/id/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the image is successfully get or not.")
    public JsonObject getImageByDetailId(@PathVariable("id") long id,@PathVariable("type") int type) {
    	String imageurl="";
    	long fromimageid=0;
    	if(type==3){
    		if(this.cidDetailRepository.findOne(id)!=null){
    			fromimageid=this.cidDetailRepository.findOne(id).getFromImageId();
    		}else{
    			return new JsonObject("数据为空",1004);	
    		}
    		
    		if(_imageServiceItf.findById(fromimageid)!=null){
    			imageurl=((ImageInfo)_imageServiceItf.findById(fromimageid)).getUri();
    		}else{
    			return new JsonObject("数据为空",1004);	
    		}
    	}else if(type==4){
    		if(this.juzhuDetailRepository.findOne(id)!=null){
    			fromimageid=this.juzhuDetailRepository.findOne(id).getFromImageId();
    		}else{
    			return new JsonObject("数据为空",1004);	
    		}
    		if(_imageServiceItf.findById(fromimageid)!=null){
    			 imageurl=((ImageInfo)_imageServiceItf.findById(fromimageid)).getUri();
    		}else{
    			return new JsonObject("数据为空",1004);	
    		}
    	}
    	return new JsonObject(imageurl);
    }
    
    @RequestMapping(value = "/info", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "静态库录入")
    public JsonObject createPerson(@RequestBody @Valid StaticIInfoDto staticIInfoDto) throws Exception {
    	try{
    		if (null == staticIInfoDto.getImageIds() || "".equals(staticIInfoDto.getImageIds().trim()))
    			return new JsonObject("请先上传图片..", 1001);
    		if (null == staticIInfoDto.getType() || staticIInfoDto.getType()<5)
    			return new JsonObject("请选择静态库类型..", 1001);
    		OtherInfo other = new OtherInfo();
    		other.setCreated(new Date());
    		other.setGmsfhm(staticIInfoDto.getGmsfzhm());
    		other.setXb(staticIInfoDto.getGender()+"");
    		other.setAddr(staticIInfoDto.getAddr());
    		other.setOwner(((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName());
    		other.setType(staticIInfoDto.getType());
    		ImageInfo photo = _imageServiceItf.findById(Long.valueOf(staticIInfoDto.getImageIds().split(",")[0]));
    		if(null == photo) return new JsonObject("入库图片id不存在！", 1001);
    		String personUrl_ori =photo.getFaceUri();
    		String personUrl_store = ImageSettings.getStoreRemoteUrl() + new File(FileUtil.getStoreFaceUri(personUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
    		other.setPhoto(personUrl_store);
    		other = otherInfoRepository.save(other);
    		List<OtherDetail> otherDetailList = new ArrayList<OtherDetail>();
    		for (String imageId : staticIInfoDto.getImageIds().split(",")) {
    			OtherDetail otherDetail = new OtherDetail();
    			otherDetail.setFromImageId(Long.valueOf(imageId));
    			otherDetail.setFromCidId(other.getId());
    			otherDetail.setZplxmc(other.getType()+"");
    			ImageInfo image = _imageServiceItf.findById(otherDetail.getFromImageId());
    			String image_uri = image.getUri();
    			String faceUrl_ori =image.getFaceUri();
    			String faceUrl_store = ImageSettings.getStoreRemoteUrl() + new File(FileUtil.getStoreFaceUri(faceUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
    			otherDetail.setImageData(faceUrl_store);
    			OtherDetail resp = otherDetailRepository.save(otherDetail);
    			try{
    				IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
    				List<T_IF_FACERECT> faceRectFeatures = ifaceSdkTarget.processFaceDetectExtract(image_uri, resp.getId(), E_FACE_EXTRACT_TYPE.FACE_EXT_TYPE_OTHERDETAIL.getValue());
    				if (null == faceRectFeatures || faceRectFeatures.size() == 0) {
    					return new JsonObject("没有找到人脸！", 1001);
    				} else  if (faceRectFeatures.size() >= 1) {
    		            LOG.info("faceRectFeatures.get(0).FaceRect.Rect:" + faceRectFeatures.get(0).Rect.toString());
    		            String json = IFaceSDKUtil.cutImage(FileUtil.getStoreImageUri(image_uri, propertiesBean.getIsJar()).oriImageUri, FileUtil.getStoreImageUri(faceUrl_ori, propertiesBean.getIsJar()).oriImageUri, faceRectFeatures.get(0).Rect, propertiesBean.getIsJar());
    		            otherDetailRepository.updateJson(otherDetail.getId(), json);
    				}
    			} catch (Exception e) {
    				LOG.error("静态库录入错误", e);
    				otherInfoRepository.delete(other);
    				otherDetailRepository.delete(resp);
    				return new JsonObject("无法连接上人脸引擎服务器！", 1001);
    			}
    			otherDetailList.add(otherDetail);
    		}
    		return new JsonObject(otherDetailList);
    	}catch(Exception e){
    		e.printStackTrace();
			LOG.error("静态库录入错误", e);
    		return new JsonObject("静态库录入错误！", 1001);
    	}
    }
}
