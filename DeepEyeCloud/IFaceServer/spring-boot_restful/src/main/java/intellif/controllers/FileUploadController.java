package intellif.controllers;

import intellif.audit.EntityAuditListener;
import intellif.common.Constants;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.CrimeSecTypeDao;
import intellif.dao.ExcelRecordDao;
import intellif.dao.IFaceConfigDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.RedDetailDao;
import intellif.dao.RuleInfoDao;
import intellif.dao.SearchLogDao;
import intellif.dao.ServerInfoDao;
import intellif.dao.TaskInfoDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.SearchLogInfo;
import intellif.database.entity.UserInfo;
import intellif.dto.ExcelUploadDetail;
import intellif.dto.ExcelUploadOverview;
import intellif.dto.JsonObject;
import intellif.dto.UploadZipMessage;
import intellif.enums.IFaceSdkTypes;
import intellif.enums.MqttTopicNames;
import intellif.enums.SourceTypes;
import intellif.excel.PersonBankXLS;
import intellif.excel.TaskInfoXLS;
import intellif.ifaas.EBListIoctrlType;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.ESurveilIoctrlType;
import intellif.ifaas.T_IF_FACERECT;
import intellif.service.BlackDetailServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.RedDetailServiceItf;
import intellif.service.SearchLogServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.RedDetailServiceImpl;
import intellif.settings.LongGangRedPersonSettings;
import intellif.settings.ThreadSetting;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CollectionUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FileUploadUtil;
import intellif.utils.FileUtil;
import intellif.utils.ImageInfoHelper;
import intellif.utils.ImageUtil;
import intellif.utils.MemcachedSpace;
import intellif.utils.MqttUtil;
import intellif.utils.StringUtil;
import intellif.validate.AnnotationValidator;
import intellif.validate.ValidateResult;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.BatchInsertDto;
import intellif.database.entity.BlackImportThreadsParams;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.database.entity.CrimeSecType;
import intellif.database.entity.ExcelProcessInfo;
import intellif.database.entity.ExcelRecord;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.IFaceConfig;
import intellif.database.entity.ImageDetail;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.MarkInfo;
import intellif.database.entity.RedDetail;
import intellif.database.entity.TaskInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
import org.hibernate.validator.constraints.NotBlank;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blogspot.na5cent.exom.ExOM;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class FileUploadController.</h1> The FileUploadController which
 * serves request of the form /image/upload and returns a JSON object
 * representing an instance of UploadInfo.
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
 * <b>Note:</b> CRUD is a set primitive operations (mostly for databases and
 * static data storages), while REST is a very-high-level API style (mostly for
 * webservices and other 'live' systems)..
 *
 * @author <a href="mailto:youngwelle@gmail.com">yangboz</a>
 * @version 1.0
 * @since 2015-03-31
 */
@RestController
// @RequestMapping(GlobalConsts.R_ID_UPLOAD)
// @see: http://spring.io/guides/gs/reactor-thumbnailer/
public class FileUploadController {
	//
	private static Logger LOG = LogManager.getLogger(FileUploadController.class);

	@Autowired
	private ImageServiceItf _imageServiceItf;
	@Autowired
	private PersonDetailServiceItf personDetailService;
	@Autowired
	private BlackDetailDao blackDetailDao;
	@Autowired
	private BlackBankDao blackBankDao;
	@Autowired
	private BlackDetailServiceItf blackDetailService;
	@Autowired
	private TaskInfoDao taskInfoDao;
	@Autowired
	private RuleInfoDao ruleInfoDao;
	@Autowired
	private ServerInfoDao serverInfoDao;
	@Autowired
	private CameraServiceItf cameraService;
	@Autowired
	private FaceServiceItf faceService;
	@Autowired
	private AlarmInfoDao alarmInfoDao;
	@Autowired
	private IFaceSdkServiceItf iFaceSdkServiceItf;
	@Autowired
	private PropertiesBean propertiesBean;
	@Autowired
	private CrimeSecTypeDao crimeSecTypeRepository;
	@Autowired
	private CameraAndBlackDetailDao _cameraAndBlackDetailRepository;
	@Autowired
	private IoContrlServiceItf ioContrlServiceItf;
	@Autowired
	private PoliceStationDao _policeStationDao;
	@Autowired
	private UserServiceItf _userService;
	@Autowired
	private SolrDataServiceItf _solrDataServiceItf;
	@Autowired
	private ExcelRecordDao recordDao;
	@Autowired
	private IFaceConfigDao ifaceConfigDao;
	@Autowired
	ZoneAuthorizeServiceItf zoneAuthorizeService;
	@Autowired
	private RedDetailDao redDetailDao;
    @Autowired
    private MemcachedSpace memcachedSpace;
    @Autowired
    private PersonDetailServiceItf _personDetailService;
    @Autowired
    private JdbcTemplate jdbcTemplate; 
    @Autowired
    SearchLogServiceItf logService;
    @Autowired
    RedDetailServiceImpl redDetailService;
	//url传输方式的增加 去服务器 下载图片并存储
	@RequestMapping(method = RequestMethod.POST, value = "intellif/image/upload/url/{face}", consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing picture is successfully uploaded or not with imageurl.")
	public JsonObject handleImageUrlUpload(@RequestBody String imageUrl,@PathVariable("face") Boolean face, @RequestParam("type") int type) throws Exception {
	       
	        String[] imageUrlList = imageUrl.split(",");  
	        ArrayList<String> resultList = new ArrayList<String>();
	        
	    for(int i=0;i<imageUrlList.length;i++){
	        
	        ImageInfo imageInfoResp = null;
            ImageDetail imageDetail = null;
            List<T_IF_FACERECT> faceList = null;
            if (imageUrlList.length>0) {
                imageInfoResp = (ImageInfo) imageUrlOperation(imageUrlList[i]);
            } else {
                LOG.error("You failed to upload Base64 String because the String was empty.");
                return new JsonObject("上传失败,文件为空!", 1001);
            }
           
            if (face) {
                try {
                    IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);

                    if (GlobalConsts.redConfig == null) {
                        getRedSwitch();
                    }
                    if (GlobalConsts.redConfig.getConValue() == 1) {
                        // 只有红名单开关为true时才进行红名单判断
                        if (type == 2) {
                            // 红名单上传图片 将imageId统一置为-1
                            faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), -1);
                        } else if (type == 1) {
                            // 布控上传
                            faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
                        } else {
                            // 检索上传
                            faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
                        }
                    } else {
                        faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
                    }
                    imageDetail = new ImageDetail(imageInfoResp);
                    imageDetail.setFaceList(faceList);
                    if (faceList.size() == 0 || faceList.size() > 1) {
                        imageDetail.setFaces(faceList.size());
                        imageInfoResp.setFaces(faceList.size());
                    } else {
                        int redState = faceList.get(0).forbiden;
                        if (redState >= 1) {
                            RedDetail rd = redDetailDao.findOne(new Long(redState).longValue());
                            if(null != rd){
                                imageDetail.setRedUri(rd.getFaceUrl());
                            }
                            imageDetail.setFaces(-1);// 红名单内
                            imageInfoResp.setFaces(-1);
                        } else {
                            imageDetail.setFaces(1);
                            imageInfoResp.setFaces(1);
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    LOG.error(e.getMessage());
                }
            } else {
                // Empty handler
            }
            ImageInfo imageinfo = this._imageServiceItf.save(imageInfoResp); 
            resultList.add(imageinfo.getId()+":"+imageinfo.getUri());
	    }
	       
	        return new JsonObject(resultList);
	    }
	
	
	@RequestMapping(method = RequestMethod.POST, value = "intellif/image/upload/{face}", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing picture is successfully uploaded or not with face detect option.")
	public @ResponseBody JsonObject handleSingleImageFileUpload(
			@RequestPart(value = "file") @Valid @NotNull @NotBlank MultipartFile file,
			@PathVariable("face") Boolean face, @RequestParam("type") int type

	) throws Exception {
		LOG.info("propertiesBean:" + propertiesBean.toString());
		SearchLogInfo searchLog;
		UserInfo user = CurUserInfoUtil.getUserInfo();

		ImageInfo imageInfoResp = null;
		ImageDetail imageDetail = null;
		List<T_IF_FACERECT> faceList = null;
		if (!file.isEmpty()) {
			imageInfoResp = imageFileHandler(file, type);
		} else {
			// 只针对检索上传图片失败的情况 记录检索失败日志
			if (type == 0) {
				searchLog = new SearchLogInfo();
				searchLog.setCreated(new Date());
				searchLog.setResultCode(1);
				searchLog.setMessage("上传文件为空" + file.getName());
				searchLog.setOwner(user.getLogin());
				logService.save(searchLog);
			}

			LOG.error("You failed to upload " + file.getName() + " because the file was empty.");
		}
		LOG.info("With image face detect? " + face);
		if (face) {
		    try {
			IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getCenterServer();
			if (null == ifaceSdkTarget) {
				return new JsonObject("没有可用引擎资源！", 1001);
			}
				if (LongGangRedPersonSettings.getRedPersonSearch().equals("false")) {
					if (GlobalConsts.redConfig == null) {
						getRedSwitch();
					}
					if (GlobalConsts.redConfig.getConValue() == 1) {
						// 只有红名单开关为true时才进行红名单判断
						if (type == 2) {
							// 红名单上传图片 将imageId统一置为-1
							faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), -1);
						} else if (type == 1) {
							// 布控上传
							faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
						} else {
							// 检索上传
							faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(),
									imageInfoResp.getId());
						}
					} else {
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
					}
					// 无人脸的错误记录也只针对检索上传的情况
					if (type == 0 && (faceList == null || faceList.size() == 0) && imageInfoResp.getUri() != null) {
						searchLog = new SearchLogInfo();
						searchLog.setCreated(new Date());
						searchLog.setResultCode(2);
						searchLog.setMessage("图片无人脸，" + imageInfoResp.getUri());
						searchLog.setOwner(user.getLogin());
						logService.save(searchLog);
					}

					imageDetail = new ImageDetail(imageInfoResp);
					imageDetail.setFaceList(faceList);
					if (faceList.size() == 0 || faceList.size() > 1) {
						imageDetail.setFaces(faceList.size());
						imageInfoResp.setFaces(faceList.size());
					} else {
						int redState = faceList.get(0).forbiden;
						if (redState >= 1) {
						      RedDetail rd = redDetailService.findById(new Long(redState).longValue());
						    if(null != rd){
						        imageDetail.setRedUri(rd.getFaceUrl());
						    }
							imageDetail.setFaces(-1);// 红名单内
							imageInfoResp.setFaces(-1);
						} else {
							imageDetail.setFaces(1);
							imageInfoResp.setFaces(1);
						}
					}
				} else {
					if (type == 2) {
						// 红名单上传图片 将imageId统一置为-1
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), -1);
					} else if (type == 1) {
						// 布控上传
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
					} else {
						// 检索上传
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
					}

					// 无人脸的错误记录也只针对检索上传的情况
					if (type == 0 && (faceList == null || faceList.size() == 0) && imageInfoResp.getUri() != null) {
						searchLog = new SearchLogInfo();
						searchLog.setCreated(new Date());
						searchLog.setResultCode(2);
						searchLog.setMessage("图片无人脸，" + imageInfoResp.getUri());
						searchLog.setOwner(user.getLogin());
						logService.save(searchLog);
					}

					imageDetail = new ImageDetail(imageInfoResp);
					imageDetail.setFaceList(faceList);
					if (faceList.size() == 0 || faceList.size() > 1) {
						imageDetail.setFaces(faceList.size());
						imageInfoResp.setFaces(faceList.size());
					} else {
						int redState = faceList.get(0).forbiden;
						if (redState >= 1) {
						    RedDetail rd = redDetailService.findById(new Long(redState).longValue());
                            if(null != rd){
                                imageDetail.setRedUri(rd.getFaceUrl());
                            }
							imageDetail.setFaces(-1);// 红名单内
							imageInfoResp.setFaces(-1);
						} else {
							imageDetail.setFaces(1);
							imageInfoResp.setFaces(1);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("Error : ", e);
			}
			this._imageServiceItf.save(imageInfoResp);
			
		/*	// 搜索流程优化，确定搜索原因之前获取待搜索人脸的特征值并缓存
			if(type == 0 || faceList.size() == 1) {
			    List<FaceInfo> faces = this.faceService.findByFromImageId(imageDetail.getId());
			    if(faces.size()==1) {
			        long faceId = faces.get(0).getId();
			        memcachedSpace.getFacefeatureFromId(Integer.valueOf(1)+":"+faceId, faceId, Integer.valueOf(1));
			    }
			}*/
			
			return new JsonObject(imageDetail);
		}

		return new JsonObject(imageInfoResp);
	}

	@RequestMapping(method = RequestMethod.POST, value = "intellif/image/upload/base64/{face}", consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing picture is successfully uploaded or not with face detect option.")
	public JsonObject handleSingleImageBase64Upload(@PathVariable("face") Boolean face, @RequestParam("type") int type,
			@RequestBody String fileData) throws Exception {
		// @Validated MultipartFileWrapper file, BindingResult result, Principal
		// principal){
		LOG.info("propertiesBean:" + propertiesBean.toString());
		ImageInfo imageInfoResp = null;
		ImageDetail imageDetail = null;
		List<T_IF_FACERECT> faceList = null;
		if (null != fileData && fileData.length() > 0) {
			imageInfoResp = imageBase64Handler(fileData);
		} else {
			LOG.error("You failed to upload Base64 String because the String was empty.");
			return new JsonObject("上传失败,文件为空!", 1001);
		}
		LOG.info("With image face detect? " + face);
		if (face) {
			try {
				IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);

				if (GlobalConsts.redConfig == null) {
					getRedSwitch();
				}
				if (GlobalConsts.redConfig.getConValue() == 1) {
					// 只有红名单开关为true时才进行红名单判断
					if (type == 2) {
						// 红名单上传图片 将imageId统一置为-1
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), -1);
					} else if (type == 1) {
						// 布控上传
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
					} else {
						// 检索上传
						faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
					}
				} else {
					faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
				}
				imageDetail = new ImageDetail(imageInfoResp);
				imageDetail.setFaceList(faceList);
				if (faceList.size() == 0 || faceList.size() > 1) {
					imageDetail.setFaces(faceList.size());
					imageInfoResp.setFaces(faceList.size());
				} else {
					int redState = faceList.get(0).forbiden;
					if (redState >= 1) {
					    RedDetail rd = redDetailDao.findOne(new Long(redState).longValue());
                        if(null != rd){
                            imageDetail.setRedUri(rd.getFaceUrl());
                        }
						imageDetail.setFaces(-1);// 红名单内
						imageInfoResp.setFaces(-1);
					} else {
						imageDetail.setFaces(1);
						imageInfoResp.setFaces(1);
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				LOG.error(e.getMessage());
			}
		} else {
			// Empty handler
		}
		this._imageServiceItf.save(imageInfoResp);
		return new JsonObject(imageDetail);
	}

	@RequestMapping(value = "intellif/image/upload/alarm/id/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "根据报警ID上传报警抓拍图片")
	public JsonObject uploadAlarmImage(@PathVariable("id") long id) throws IOException {
		ImageDetail imageDetail = null;
		List<T_IF_FACERECT> faceList = null;
		AlarmInfo alarm = this.alarmInfoDao.findOne(id);
		if (null == alarm)
			return new JsonObject(null);
		FaceInfo face = this.faceService.findOne(alarm.getFaceId());
		if (null != face) {
			ImageInfo image = this._imageServiceItf.findById(face.getFromImageId());
			try {
				IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
				faceList = ifaceSdkTarget.image_detect_extract(image.getUri(), image.getId());
				String faceUri = ImageInfoHelper.getRemoteFaceUrl(image.getUri(), propertiesBean.getIsJar());
				image.setFaceUri(faceUri);

				imageDetail = new ImageDetail(image);
				imageDetail.setFaceList(faceList);
				if (faceList.size() == 0 || faceList.size() > 1) {
					imageDetail.setFaces(faceList.size());
					image.setFaces(faceList.size());
				} else {
					int redState = faceList.get(0).forbiden;
					if (redState >= 1) {
					    RedDetail rd = redDetailDao.findOne(new Long(redState).longValue());
                        if(null != rd){
                            imageDetail.setRedUri(rd.getFaceUrl());
                        }
						imageDetail.setFaces(-1);// 红名单内
						image.setFaces(-1);
					} else {
						imageDetail.setFaces(1);
						image.setFaces(1);
					}
				}
			} catch (Exception e) {
				LOG.error("image upload error,e:",e);
			}
			this._imageServiceItf.save(image);
			return new JsonObject(imageDetail);
		}
		return new JsonObject(null);
	}

	@RequestMapping(value = "intellif/image/upload/face/id/{id}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "根据抓拍图片ID上传抓拍图片")
	public JsonObject uploadFaceImage(@PathVariable("id") long id) throws IOException {
		ImageDetail imageDetail = null;
		List<T_IF_FACERECT> faceList = null;
		FaceInfo face = this.faceService.findOne(id);
		if (null != face) {
			ImageInfo image = this._imageServiceItf.findById(face.getFromImageId());
			try {
				IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
				faceList = ifaceSdkTarget.image_detect_extract(image.getUri(), image.getId());
				String faceUri = ImageInfoHelper.getRemoteFaceUrl(image.getUri(), propertiesBean.getIsJar());
				image.setFaceUri(faceUri);

				imageDetail = new ImageDetail(image);
				imageDetail.setFaceList(faceList);
				if (faceList.size() == 0 || faceList.size() > 1) {
					imageDetail.setFaces(faceList.size());
					image.setFaces(faceList.size());
				} else {
					int redState = faceList.get(0).forbiden;
					if (redState >= 1) {
					    RedDetail rd = redDetailDao.findOne(new Long(redState).longValue());
                        if(null != rd){
                            imageDetail.setRedUri(rd.getFaceUrl());
                        }
						imageDetail.setFaces(-1);// 红名单内
						image.setFaces(-1);
					} else {
						imageDetail.setFaces(1);
						image.setFaces(1);
					}
				}
			} catch (Exception e) {
			    LOG.error("image upload error,e:",e);
			}
			this._imageServiceItf.save(image);
			return new JsonObject(imageDetail);
		}
		return new JsonObject(null);
	}

	// @see: https://spring.io/guides/gs/uploading-files/
	@RequestMapping(method = RequestMethod.POST, value = "intellif/zip/upload/{key}", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing invoice' picture is successfully uploaded or not.")
	public @ResponseBody JsonObject handleSingleZipFileUpload(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "bankId") Long bankId, @PathVariable("key") int key) throws Throwable {
		JsonObject _unzipOutput = null;
		if (!file.isEmpty()) {
			_unzipOutput = this.unZipIt(file, bankId, key);
		} else {
			LOG.error("You failed to upload " + file.getName() + " because the file was empty.");
		}
		return new JsonObject(_unzipOutput);
	}

	@RequestMapping(method = RequestMethod.POST, value = "intellif/zip/upload/progress/{key}")
	@ApiOperation(httpMethod = "POST", value = "上传图片显示进度")
	public @ResponseBody JsonObject handleProcessZipUpload(@PathVariable("key") int key) {
		return new JsonObject(GlobalConsts.fileUploadMap.get(key));
	}

	@RequestMapping(method = RequestMethod.POST, value = "intellif/zip/upload/cancel/{key}")
	@ApiOperation(httpMethod = "POST", value = "取消导入")
	public @ResponseBody JsonObject cancelZipUpload(@PathVariable("key") int key) {
		ExcelProcessInfo process = GlobalConsts.fileUploadMap.get(key);
		process.setImportState(false);
		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
	}

	@RequestMapping(method = RequestMethod.POST, value = "intellif/zip/upload/eDownload/{key}")
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



	// @see: https://spring.io/guides/gs/uploading-files/
	@RequestMapping(method = RequestMethod.POST, value = "intellif/excel/upload", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(httpMethod = "POST", value = "Response a string describing invoice' picture is successfully uploaded or not.")
	public @ResponseBody JsonObject handleSingleExcelFileUpload(@RequestParam(value = "file") MultipartFile file)
			throws Throwable {
		ExcelUploadOverview excelUploadResp = null;
		if (!file.isEmpty()) {
			excelUploadResp = excelFileHandler(file);
		} else {
			LOG.error("You failed to upload(excel) " + file.getName() + " because the file was empty.");
		}
		return new JsonObject(excelUploadResp);
	}

	private ImageInfo imageBase64Handler(String fileData) {
		LOG.info("imageBase64Handler with base64");
		// ImageMagick convert options; @see:
		// http://paxcel.net/blog/java-thumbnail-generator-imagescalar-vs-imagemagic/
		Map<String, String> _imageMagickOutput = this.imageBase64Operation(fileData);
		// Save to database.
		ImageInfo imageInfoResp = new ImageInfo();
		try {
			// Image resize operation.
			String fileName = _imageMagickOutput.get(ImageSize.ori.toString());
			String imageUrl = ImageInfoHelper.getRemoteImageUrl(fileName, propertiesBean.getIsJar());
			// Save to database.
			ImageInfo imageInfo = new ImageInfo();
			imageInfo.setUri(imageUrl);
			imageInfo.setTime(new Date());
			// Construct the faceUri;
			String faceUri = ImageInfoHelper.getRemoteFaceUrl(imageUrl, propertiesBean.getIsJar());
			imageInfo.setFaceUri(faceUri);
			// DB saving...
			// imageInfoResp = _imageServiceItf.save(imageInfo);
			imageInfoResp = _imageServiceItf.save(imageInfo);
			LOG.info("ImageMagick output success: " + imageInfoResp);
			// Return the result.
		} catch (Exception ex) {
			LOG.error(ex.toString());
		}
		return imageInfoResp;
	}

	private ExcelUploadOverview excelFileHandler(MultipartFile file) throws Throwable {
		ExcelUploadOverview results = new ExcelUploadOverview();
		// Excel file handler
		String dbFileName = null;
		String fullFileName = null;
		try {
			byte[] bytes = file.getBytes();
			String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
			String fileNameAppendix
			// = "temp" + "." + fileExt;
					= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "." + fileExt;

			dbFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix;
			fullFileName = dbFileName;

			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullFileName)));
			stream.write(bytes);
			stream.close();
			// System.out.println("Upload file success." + fullFileName);
			LOG.info("Upload (excel)file success." + fullFileName);
		} catch (Exception e) {
			// System.out.println("You failed to convert " + fullFileName + " =>
			// " + e.toString());
			LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
		}
		// Save to database.
		TaskInfo taskInfoResp = null;
		try {
			// Excel to object mapping:
			// File excelFile = new
			// ClassPathResource(newFile.getAbsoluteFile().getName())
			// .getFile();
			if (fullFileName == null) { // 解决下处的findbugs Null passed for
										// non-null parameter
				LOG.error("获取文件名失败");
				return null;
			}
			List<TaskInfoXLS> items = ExOM.mapFromExcel(new File(fullFileName)).toObjectOf(TaskInfoXLS.class).map();

			for (TaskInfoXLS item : items) {
				LOG.info("TaskInfoXLS:" + item.toString());
				// Save to database.
				TaskInfo taskInfo = new TaskInfo();
				int sizeOfBlackBank = blackBankDao.findByBankName(item.getBankName()).size();
				int sizeOfRuleInfo = ruleInfoDao.findByRuleName(item.getRuleName()).size();
				int sizeOfServerInfo = serverInfoDao.findByServerName(item.getServerName()).size();
				// Default as camera type.
				int indexOfSourceType = item.getSourceTypeInt();
				int indexOfDecodeMode = item.getDecodeModeNameInt();
				int sizeOfCameraInfo = cameraService.findByName(item.getSourceName()).size();
				System.out.println("sizeOfBlackBank:" + sizeOfBlackBank + ",sizeOfRuleInfo:" + sizeOfRuleInfo
						+ ",sizeOfServerInfo:" + sizeOfServerInfo + ",indexOfSourceType:" + indexOfSourceType
						+ ",sizeOfCameraInfo:" + sizeOfCameraInfo + ",indexOfDecodeMode:" + indexOfDecodeMode);
				if (sizeOfBlackBank >= 1 && sizeOfRuleInfo >= 1 && sizeOfServerInfo >= 1 && indexOfSourceType == 0
						&& sizeOfCameraInfo >= 1 && indexOfSourceType != -1) {
					long bankId = blackBankDao.findByBankName(item.getBankName()).get(0).getId();
					taskInfo.setBankId(bankId);
					long ruleId = ruleInfoDao.findByRuleName(item.getRuleName()).get(0).getId();
					taskInfo.setRuleId(ruleId);
					long serverId = serverInfoDao.findByServerName(item.getServerName()).get(0).getId();
					taskInfo.setServerId(serverId);
					//
					taskInfo.setSourceType(indexOfSourceType);
				     List<CameraInfo> list = cameraService.findByName(item.getSourceName());
				     long cameraId = list.get(0).getId();
					taskInfo.setSourceId(cameraId);
					taskInfo.setTaskName(item.getName());
					taskInfo.setDecodeType(indexOfDecodeMode);
					// DB saving...
					taskInfoResp = taskInfoDao.save(taskInfo);
					LOG.info("TaskInfoDao output success: " + taskInfoResp);
					//
					results.setNumOfSucc(results.getNumOfSucc() + 1);
					ExcelUploadDetail excelUploadDetail = new ExcelUploadDetail();
					excelUploadDetail.setTaskInfo(taskInfoResp);
					results.getDetails().add(excelUploadDetail);
				} else {
					results.setNumOfFail(results.getNumOfFail() + 1);
				}
			}
			// Return the result.
		} catch (Exception ex) {
			LOG.error(ex.toString());
		}
		return results;
	}

	//
	@SuppressWarnings("unused")
	private String thumbnailImage(int width, int height, String source) throws Exception {
		//
		String small4dbBase = FilenameUtils.getBaseName(source) + "_" + String.valueOf(width) + "x"
				+ String.valueOf(height) + "." + FilenameUtils.getExtension(source);
		String small4db = FileUtil.getUploads(propertiesBean.getIsJar()) + small4dbBase;
		String small = getClassPath() + small4db;
		// @see:
		// http://paxcel.net/blog/java-thumbnail-generator-imagescalar-vs-imagemagic/
		ConvertCmd cmd = new ConvertCmd();
		// cmd.setSearchPath("");
		File thumbnailFile = new File(small);
		if (!thumbnailFile.exists()) {
			IMOperation op = new IMOperation();
			op.addImage(source);
			op.thumbnail(width);
			op.addImage(small);
			cmd.run(op);
			LOG.info("ImageMagick success result:" + small);
		}
		return small4dbBase;
	}

	// @see:
	// http://www.concretepage.com/spring-4/spring-4-mvc-single-multiple-file-upload-example-with-tomcat
	// @RequestMapping(value = "intellif/file/uploads", method =
	// RequestMethod.POST)
	// @ApiOperation(httpMethod = "POST", value = "Response a string describing
	// invoice' pictures is successfully uploaded or not.")
	// public @ResponseBody List<JsonString>
	// handleMultiFileUpload(@RequestParam("name") String name,
	// @RequestParam("owner") String owner, @RequestParam("files")
	// MultipartFile[] files) {
	// ArrayList<JsonString> invoiceResps = new ArrayList<JsonString>();
	// {
	// }
	// ;
	// if (files != null && files.length > 0) {
	// for (int i = 0; i < files.length; i++) {
	// if (!files[i].isEmpty()) {
	// // ImageMagick convert options; @see:
	// //
	// http://paxcel.net/blog/java-thumbnail-generator-imagescalar-vs-imagemagic/
	// Map<String, String> _imageMagickOutput = this.fileOperation(files[i]);
	// // Save to database.
	// try {
	// // Image resize operation.
	// JsonString invoiceResp = new
	// JsonString(_imageMagickOutput.get(ImageSize.ori.toString()));
	// // Get image face feature
	//
	// // Save to database.
	// LOG.info("_invoiceDao save success.");
	// // Return the result.
	//
	// invoiceResps.add(invoiceResp);
	// } catch (Exception ex) {
	// LOG.error(ex.toString());
	// }
	// } else {
	// LOG.error("You failed to upload " + files.toString() + " because the file
	// was empty.");
	//
	// }
	// }
	// }
	// return invoiceResps;
	// }

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
					= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "." + fileExt;
			LOG.info("fileNameAppendix:" + fileNameAppendix);
			dbFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix;
			LOG.info("dbFileName:" + dbFileName);
			fullFileName = dbFileName;
			LOG.info("imageBase64Operation with fullFieldName:" + fullFileName);

			LOG.info("{} begin get angle", "base64 image");
			File f = new File(fullFileName);
			int angle = ImageUtil.getAngle(new ByteArrayInputStream(bytes));
			LOG.info("{} angle is {}", "base64 image", angle);
			if (angle != 0) {
				BufferedImage img = ImageUtil.rotateImage(bytes, angle, "base64 image");
				try {
					ImageIO.write(img, "jpg", f);
					img.flush();
				} catch (IOException e) {
					LOG.error("{} after rotateImage write ImageIO exception:", e);
				}
			} else {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
				stream.write(bytes);
				stream.close();
			}
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullFileName)));
			stream.write(bytes);
			stream.close();

			// shi test 代替format操作
			/*
			 * String ff[] = fullFileName.split("\\."); String formatName =
			 * ff[0]+"_format.jpg"; BufferedOutputStream stream1 = new
			 * BufferedOutputStream(new FileOutputStream(new File(formatName)));
			 * stream1.write(bytes); stream1.close();
			 */

			// shi test 代替format操作

			// System.out.println("Upload file success." + fullFileName);
			LOG.info("Upload (image)file success." + fullFileName);
			// ImageMagick convert options; @see:
			// http://paxcel.net/blog/java-thumbnail-generator-imagescalar-vs-imagemagic/
			String format4dbBase = this.formatImage(fullFileName, "jpg");
			_imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);
			// _imageMagickOutput.put(ImageSize.sml.toString(),
			// thumbnailImage(150, 150, fullFileName));
			// _imageMagickOutput.put(ImageSize.ico.toString(),
			// thumbnailImage(32, 32, fullFileName));
			return _imageMagickOutput;
		} catch (Exception e) {
			// System.out.println("You failed to convert " + fullFileName + " =>
			// " + e.toString());
			LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
		}
		return _imageMagickOutput;
	}

	// @Autowired
	// private FolderSetting folderSetting;

	public String getClassPath() {
		String classPath = this.getClass().getResource("/").getPath();
		return classPath;
	}



	private ValidateResult crimeValidate(PersonBankXLS item) {
		ValidateResult result = new ValidateResult();
		String typeStr[] = StringUtil.separateStr(item.getCrimeType(), "(", ")");
		if (null == typeStr || typeStr.length != 2) {
			result.setMessage("犯罪类型:犯罪类型格式错误");
			result.setCode(1001);
		} else {
			List<CrimeSecType> typeList = crimeSecTypeRepository.findCrimeSecByNames(typeStr[1].trim(),
					typeStr[0].trim());
			if (null == typeList || typeList.isEmpty()) {
				// 犯罪类型数据库查询出错
				result.setMessage("犯罪类型:系统无该犯罪类型");
				result.setCode(1001);
			}
		}
		return result;
	}

	/**
	 * Unzip it
	 *
	 * @param file
	 *            input zip file
	 * @param outputFolder
	 *            zip file output folder
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public JsonObject unZipIt(MultipartFile file, Long bankId, int key) throws Throwable {
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
			LOG.info("login user id:" + userId);
			byte[] bytes = file.getBytes();
			String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
			String randomStr = String.valueOf(Math.round(Math.random() * 1000000));
			final String fileNameAppendix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "_"
					+ randomStr;

			fullFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix + "." + fileExt;

			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullFileName)));
			stream.write(bytes);
			stream.close();
			LOG.info("Upload (zip)file success." + fullFileName);

			// create output directory is not exists
			File folder = new File(
					FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + fileNameAppendix + "/validate");
			FileUtil.checkFileExist(folder);

			List<PersonBankXLS> xlsItems = null;// xls数据总和
			List<PersonBankXLS> filterList = null;// xls过滤空xls后集合
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
								xlsItems = ExOM.mapFromExcel(newFile).toObjectOf(PersonBankXLS.class).map();
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
				for (PersonBankXLS item : filterList) {
					a++;
					List<ValidateResult> result = AnnotationValidator.validate(item, path, suffixMap);
					ValidateResult criValidate = crimeValidate(item);
					if (criValidate.getCode() != 0) {
						result.add(criValidate);
					}
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
			ExcelProcessInfo processInfo = new ExcelProcessInfo(GlobalConsts.process_Black);
			processInfo.setTotalSize(filterList.size());
			processInfo.getDetailMap().put("random", fileNameAppendix);
			GlobalConsts.fileUploadMap.put(key, processInfo);
			int count = 0;
			ThreadPoolExecutor threadPool = new ThreadPoolExecutor(ThreadSetting.getBlackThreadsNum(), 24, 1,
					TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE),
					new ThreadPoolExecutor.CallerRunsPolicy());
			List<PersonBankXLS> tList = new ArrayList<PersonBankXLS>();

			for (PersonBankXLS item : filterList) {
				count++;
				tList.add(item);
				if (count % 10 == 0 || count == filterList.size()) {
					int num = count;
					List<Object> tempList = new ArrayList<Object>();
					tempList.addAll(tList);
					BlackImportThreadsParams params = new BlackImportThreadsParams(tempList, suffixMap, ui,
							fileNameAppendix, key, num, xlsName,bankId);
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
					LOG.info("all black thread finish");
					long total = processInfo.getTotalSize();
					long success = processInfo.getSuccessNum();
					processInfo.setFailedNum(total - success);
					_personDetailService.noticeEngineUpdateBlackDatas(userId);
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

	private List<PersonBankXLS> filterNullxlsitem(List<PersonBankXLS> sourceList) {
		List<PersonBankXLS> returnList = new ArrayList<PersonBankXLS>();
		if (sourceList != null && !sourceList.isEmpty()) {
			for (PersonBankXLS item : sourceList) {
				if (item.isExist()) {
					returnList.add(item);
				}
			}
		}
		return returnList;
	}

 

	public class ExcelImportThread implements Runnable {
		private BlackImportThreadsParams params;

		public ExcelImportThread() {

		}

		public ExcelImportThread(BlackImportThreadsParams params) {
			this.params = params;
		}

		@Override
		public void run() {
			LOG.info("black thread running size:" + params.getCount() + " key:" + params.getKey() + " threadname:"
					+ Thread.currentThread().getName());
			try {
				for (Object item : params.getXlsList()) {
					PersonBankXLS pb = (PersonBankXLS) item;
					ExcelProcessInfo process = GlobalConsts.fileUploadMap.get(params.getKey());
					if (process.isImportState()) {
						processUZipImages(pb, params);
					}
				}
			} catch (Exception e) {
				LOG.error("black run thread error threadname:" + Thread.currentThread().getName() + " error:", e);
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
		private boolean processBlackDetail(BlackImportThreadsParams params, PersonDetail person,
				MultipartFile multipartFile, String sourceName, PersonBankXLS item) {
			ExcelProcessInfo pi = GlobalConsts.fileUploadMap.get(params.getKey());
			long imageId = 0;
			BlackDetail blackDetailResp = null;
			ImageInfo imageInfoResp = null;
			try {
				String ePath = FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + params.getDirPath()
						+ File.separator + "errorImage/";

				try {
					imageInfoResp = this.imageFileHandler(multipartFile);
				} catch (Exception e) {
					LOG.error("imageFileHandler error:", e);
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					// recordNotNormalPic(ePath+"未知原因",imageInfoResp.getUri(),descDir);
					// //// find bugs null pointer dereference value loaded from
					// imageinforesp
					if (imageInfoResp != null) {
						recordNotNormalPic(ePath + "未知原因", imageInfoResp.getUri(), descDir);
					}
					pi.getDetailMap().put(4, ((Integer) (pi.getDetailMap().get(4))) + 1);
					throw new Exception("图片格式化异常");
				}
				imageId = imageInfoResp.getId();

				// 条件判断图片是否只有一个人脸
				IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
				List<T_IF_FACERECT> faceList = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(),
						imageInfoResp.getId());

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
				} else if (faceList.size() == 1 && faceList.get(0).forbiden >= 1) {
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					recordNotNormalPic(ePath + "有红名单", imageInfoResp.getUri(), descDir);
					pi.getDetailMap().put(3, ((Integer) (pi.getDetailMap().get(3))) + 1);
					throw new Exception("图片在红名单内");
				} else {
					imageInfoResp.setFaces(1);
				}
				_imageServiceItf.save(imageInfoResp);
				// 条件判断图片是否只有一个人脸

				BlackDetail blackDetail = new BlackDetail();
				blackDetail.setFromPersonId(person.getId());
				blackDetail.setFromImageId(imageId);
				blackDetail.setBlackDescription(person.getDescription());
				blackDetail.setBankId(person.getBankId());
				// Pre-fill the face URL.
				String faceUrl = imageInfoResp.getFaceUri();
				blackDetail.setImageData(faceUrl);
				//
				blackDetailResp = blackDetailDao.save(blackDetail);
				// 调用C++
				boolean updateFaceFeatureResult = blackDetailService.updateFaceFeature(blackDetailResp,null);
				if (!updateFaceFeatureResult) {
					String descDir = File.separator + sourceName + "." + params.getExtMap().get(sourceName);
					recordNotNormalPic(ePath + "其它原因", imageInfoResp.getUri(), descDir);
					pi.getDetailMap().put(4, ((Integer) (pi.getDetailMap().get(4))) + 1);
					throw new Exception("update face feature error");
				}
				boolean isDispatch = item.isDispatch();

				if (isDispatch) {
				    Long userId = params.getUi().getId();
				    Map<String,List<Long>> uAreaDataMap = GlobalConsts.userBukongMap.get(userId);
				    if(CollectionUtils.isEmpty(uAreaDataMap)){
				        _personDetailService.processUserAreaDataToMap(userId);
				        uAreaDataMap = GlobalConsts.userBukongMap.get(userId);
				    }
				    
				    //摄像头布控
	                 List<Object> cList = new ArrayList<Object>();
	                 List<Long> cameraSet = uAreaDataMap.get("camera");
	                 if(!CollectionUtils.isEmpty(cameraSet)){
	                     for(Long cId : cameraSet){
	                         cList.add(new CameraAndBlackDetail(cId, blackDetailResp.getId()));
	                     }
	                     BatchInsertDto bid = new BatchInsertDto(cList);
	                     jdbcTemplate.batchUpdate(bid.getInsertSql(),bid.getInsertSetter());
	                 }
	                 //区域布控
	                 List<Object> aList = new ArrayList<Object>();
	                 List<Long> areaSet = uAreaDataMap.get("area");
	                 if(!CollectionUtils.isEmpty(areaSet)){
	                     for(Long aId : areaSet){
	                         aList.add(new AreaAndBlackDetail(aId, blackDetailResp.getId()));
                         }
	                     BatchInsertDto bid1 = new BatchInsertDto(aList);
	                     jdbcTemplate.batchUpdate(bid1.getInsertSql(),bid1.getInsertSetter());
	                 }
				    
				}

				// _solrDataServiceItf.addBlackDetail(GlobalConsts.BLACK_INFO_TYPE,
				// blackDetailResp);
				_solrDataServiceItf.addBlackDetail(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE),
						blackDetailResp);

				person.setPhotoData(imageInfoResp.getFaceUri());
				personDetailService.save(person);

			} catch (Throwable e) {
				pi.incrementFailedImgNumWithLock();
				if (null != blackDetailResp) {
					deleteBlackDetail(blackDetailResp.getId());
				}
				LOG.error("process blackDetail error:", e);
				return false;
			}
			pi.incrementSuccessImgNumWithLock();
			return true;
		}

		private void deleteBlackDetail(long id) {
			try {
				List<String> delList = new ArrayList<String>();
				blackDetailDao.delete(id);
				_cameraAndBlackDetailRepository.deleteByBlackdetailId(id);
				delList.add("" + id);
				// _solrDataServiceItf.deleteById(GlobalConsts.BLACK_INFO_TYPE,
				// delList);
				_solrDataServiceItf.deleteById(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE), delList);

			} catch (Exception e) {
				LOG.error("delete black detail information error,id:" + id + "error:", e);
			}
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

		private void processUZipImages(PersonBankXLS item, BlackImportThreadsParams params) {

			ExcelProcessInfo pi = GlobalConsts.fileUploadMap.get(params.getKey());
			// PersonDetail person = processPersonDetail(item,
			// params.getBankId(),params.getKey(),params.getUi());
			PersonDetail person = processPersonDetail(item, params);
			LOG.info("save personDetail complete key:" + params.getKey() + " item name:" + item.getName());
			if (null != person) {
				for (int i = 1; i < 5; i++) {
					String iName = item.getImageName(i);
					if (iName != null && iName.toString().length() != 0) {
						pi.setImageTotal(pi.getImageTotal() + 1);
						MultipartFile multipartFile = createFileFromExcelName(params.getExtMap(), params.getDirPath(),
								iName);
						LOG.info("create multipartFile complete person name:" + person.getRealName() + " imageName:"
								+ iName + " key:" + params.getKey());
						// processBlackDetail(params.getExtMap(),person,multipartFile,params.getKey(),params.getDirPath(),iName);
						processBlackDetail(params, person, multipartFile, iName, item);
					}
				}
				List<BlackDetail> blackList = blackDetailDao.findByFromPersonId(person.getId());
				if (null == blackList || blackList.isEmpty()) {
					pi.incrementFailedNumWithLock();
					personDetailService.delete(person.getId());
				} else {
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

		private PersonDetail processPersonDetail(PersonBankXLS item, BlackImportThreadsParams params) {
			LOG.info("PersonBankXLS:" + item.toString());
			PersonDetail personDetailResp = null;
			try {
				PersonDetail personDetail = new PersonDetail(true);
				personDetail.setSex(item.getGender());
				personDetail.setRealName(item.getName());
				personDetail.setAddress(item.getAddress());
				personDetail.setCid(item.getCid());
				personDetail.setCrimeAddress(item.getCrimeDes());
				personDetail.setDescription(item.getDescritpion());
				personDetail.setNation(item.getNation());
				personDetail.setBankId(params.getBankId());

				BlackBank bank = blackBankDao.findOne(personDetail.getBankId());
				personDetail.setType(bank.getListType());// 不同类型的黑白名单库对应不同类型的黑白名单人员
				Calendar c = Calendar.getInstance();
				personDetail.setStarttime(c.getTime());
				c.add(Calendar.DATE, 365 * 30);
				personDetail.setEndtime(c.getTime());
				personDetail.setOwner(params.getUi().getName());
				PoliceStation ps = _policeStationDao.findOne(params.getUi().getPoliceStationId());
				if (null != ps) {
					personDetail.setOwnerStation(ps.getStationName());
				}
				// 是否布控
				boolean isDispatch = item.isDispatch();
				if (isDispatch) {
					personDetail.setStatus(1);
				} else {
					personDetail.setStatus(0);
				}
				// 犯罪类型
				// String[] typeStr = StringUtil.separateStr("(出卖国家罪)
				// 侵犯国家财产","(",")");
				String typeStr[] = StringUtil.separateStr(item.getCrimeType(), "(", ")");
				List<CrimeSecType> typeList = crimeSecTypeRepository.findCrimeSecByNames(typeStr[1].trim(),
						typeStr[0].trim());
				personDetail.setCrimeType(typeList.get(0).getId());
				personDetailResp = (PersonDetail) personDetailService.save(personDetail);
				return personDetailResp;

			} catch (Exception e) {
				LOG.error(" save persondetail error", e);
				ExcelProcessInfo pi = GlobalConsts.fileUploadMap.get(params.getKey());
				pi.setFailedNum(pi.getFailedNum() + 1);
				if (null != personDetailResp && 0 != personDetailResp.getId()) {
					personDetailService.delete(personDetailResp.getId());
				}
				return null;
			}
		}

	}

	


	private void deletePersonById(long id) {

		String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
		if (authority.trim().length() == 0) {
			return;
		}
		String fSql = "id = " + id+" and bank_id in("+authority+")";
		List<PersonDetail> findList = personDetailService.findByFilter(fSql);
		if (findList.size() <= 0) {
			return;
		}
		blackDetailDao.delete(id);
		// this.auditLogRepository.deleteByObjectAndObjectId(GlobalConsts.T_NAME_PERSON_DETAIL,
		// id); // 为什么之前要把这条日志删了呢
		List<BlackDetail> blackList = blackDetailDao.findByFromPersonId(id);
		if (blackList.size() > 0) {
			List<CameraAndBlackDetail> list = this._cameraAndBlackDetailRepository
					.findByBlackdetailId(blackList.get(0).getId());
			if (list.size() > 0) {
				for (CameraAndBlackDetail cameraAndBlackDetail : list) {
					try {
						ioContrlServiceItf.ioContrlWith(EEnginIoctrlType.ENGIN_IOCTRL_SURVEIL.getValue(),
								ESurveilIoctrlType.SURVEIL_IOCTRL_DEL_PERSON.getValue(), SourceTypes.CAMERA.getValue(),
								cameraAndBlackDetail.getCameraId(), id);
					} catch (Exception e) {
						LOG.error("delete person detail error:", e);
					}
				}
			}
		}
		List<String> delList = new ArrayList<String>();
		for (BlackDetail black : blackList) {
			blackDetailDao.delete(black.getId());
			this._cameraAndBlackDetailRepository.deleteByBlackdetailId(black.getId());
			delList.add("" + black.getId());
		}
		try {
			// this._solrDataServiceItf.deleteById(GlobalConsts.BLACK_INFO_TYPE,
			// delList);
			this._solrDataServiceItf.deleteById(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE), delList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private String formatImage(String source, String formatStr) throws Exception {
		//
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

	// type 0-检索上传 1-布控上传
	private String formatImage(String source, String formatStr, int type) {
		String format4dbBase = FilenameUtils.getBaseName(source) + "_format" + "." + formatStr;// FilenameUtils.getExtension(source),always
																								// keep
																								// JPG
		try {
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
		} catch (Exception e) {
			LOG.info("ImageMagick format failed");
			// 图片格式不对的情况也得日志记录哦。只针对检索上传的情况
			UserInfo user = CurUserInfoUtil.getUserInfo();
			if (type == 0) {
				SearchLogInfo searchLog;
				searchLog = new SearchLogInfo();
				searchLog.setCreated(new Date());
				searchLog.setResultCode(1);
				searchLog.setMessage("上传文件格式不对," + source);
				searchLog.setOwner(user.getLogin());
				logService.save(searchLog);
			}
			return null;
		}
		return format4dbBase;
	}

	private ImageInfo imageFileHandler(MultipartFile file) {
		LOG.info("imageFileHandler with file:" + file.toString());
		Map<String, String> _imageMagickOutput = this.imageFileOperation(file);
		ImageInfo imageInfoResp = new ImageInfo();
		try {
			String fileName = _imageMagickOutput.get(ImageSize.ori.toString());
			String imageUrl = ImageInfoHelper.getRemoteImageUrl(fileName, propertiesBean.getIsJar());
			ImageInfo imageInfo = new ImageInfo();
			imageInfo.setUri(imageUrl);
			imageInfo.setTime(new Date());
			String faceUri = ImageInfoHelper.getRemoteFaceUrl(imageUrl, propertiesBean.getIsJar());
			imageInfo.setFaceUri(faceUri);
			imageInfoResp = _imageServiceItf.save(imageInfo);
			LOG.info("ImageMagick output success: " + imageInfoResp);
		} catch (Exception ex) {
			LOG.error(ex.toString());
		}
		return imageInfoResp;
	}

	// type值 区分 0-检索上传 1-布控上传
	private ImageInfo imageFileHandler(MultipartFile file, int type) {
		LOG.info("imageFileHandler with file:" + file.toString());
		Map<String, String> _imageMagickOutput = this.imageFileOperation(file, type);
		ImageInfo imageInfoResp = new ImageInfo();
		try {
			String fileName = _imageMagickOutput.get(ImageSize.ori.toString());
			String imageUrl = ImageInfoHelper.getRemoteImageUrl(fileName, propertiesBean.getIsJar());
			ImageInfo imageInfo = new ImageInfo();
			imageInfo.setUri(imageUrl);
			imageInfo.setTime(new Date());
			String faceUri = ImageInfoHelper.getRemoteFaceUrl(imageUrl, propertiesBean.getIsJar());
			imageInfo.setFaceUri(faceUri);
			imageInfoResp = _imageServiceItf.save(imageInfo);
			LOG.info("ImageMagick output success: " + imageInfoResp);
		} catch (Exception ex) {
			LOG.error(ex.toString());
		}
		return imageInfoResp;
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
			String fileNameAppendix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "."
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

			LOG.info("Upload (image)file success." + fullFileName);
			String format4dbBase = this.formatImage(fullFileName, "jpg");
			_imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);
			return _imageMagickOutput;
		} catch (Exception e) {
			LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
		}
		return _imageMagickOutput;
	}

	// type 0-检索上传的图片处理 1-布控上传的图片处理
	private Map<String, String> imageFileOperation(MultipartFile file, int type) {
		LOG.info("imageFileOperation with file:" + file.toString());
		Map<String, String> _imageMagickOutput = new HashMap<String, String>();
		String dbFileName = null;
		String fullFileName = null;
		try {
			byte[] bytes = file.getBytes();
			String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
			LOG.info("fileExt:" + fileExt);
			String fileNameAppendix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "."
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
			 * BufferedOutputStream(new FileOutputStream(new File(formatName)));
			 * stream1.write(bytes); stream1.close();
			 */
			// shi test 代替format操作

			LOG.info("Upload (image)file success." + fullFileName);
			String format4dbBase = this.formatImage(fullFileName, "jpg", type);
			_imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);
			return _imageMagickOutput;
		} catch (Exception e) {
			LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
		}
		return _imageMagickOutput;
	}

	// Enum for image size.
	enum ImageSize {
		ori, sml, ico
	}

	private void getRedSwitch() {
		List<IFaceConfig> switchList = (List<IFaceConfig>) ifaceConfigDao.findByConKey(GlobalConsts.IFACE_CONFIG_RED);
		IFaceConfig rSwitch = null;
		if (null == switchList || switchList.isEmpty()) {
			IFaceConfig rs = new IFaceConfig(GlobalConsts.IFACE_CONFIG_RED, 0, "红名单开关");
			rSwitch = ifaceConfigDao.save(rs);
		} else {
			rSwitch = switchList.get(0);
		}
		GlobalConsts.redConfig = rSwitch;
	}
   
   //根据图片url下载图片
   private ImageInfo imageUrlOperation(String fileUrl) {
       LOG.info("imageUrlOperation with Base64 String");

       Map<String, String> _imageMagickOutput = new HashMap<String, String>();
       String dbFileName = null;
       String fullFileName = null;
       try {          
           URL url = new URL(fileUrl);  
           DataInputStream dataInputStream = new DataInputStream(url.openStream());                                      
           byte[] bytes = input2byte(dataInputStream);
           String fileExt = "jpg";       
           String fileNameAppendix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "." + fileExt;          
           dbFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix;        
           fullFileName = dbFileName;                 
           File f = new File(fullFileName);
           int angle = ImageUtil.getAngle(new ByteArrayInputStream(bytes));
           if (angle != 0) {
               BufferedImage img = ImageUtil.rotateImage(bytes, angle, "base64 image");
               try {
                   ImageIO.write(img, "jpg", f);
                   img.flush();
               } catch (IOException e) {
                   LOG.error("{} after rotateImage write ImageIO exception:", e);
               }
           } else {
               BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
               stream.write(bytes);
               stream.close();
           }
           BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullFileName)));
           stream.write(bytes);
           stream.close();      
           LOG.info("Upload (image)file success." + fullFileName);        
           String format4dbBase = this.formatImage(fullFileName, "jpg");
           _imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);         
          
       } catch (Exception e) {      
           LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
       }
          
       ImageInfo imageInfoResp = new ImageInfo();
       try {
           // Image resize operation.
           String fileName = _imageMagickOutput.get(ImageSize.ori.toString());
           String imageUrl = ImageInfoHelper.getRemoteImageUrl(fileName, propertiesBean.getIsJar());
           // Save to database.
           ImageInfo imageInfo = new ImageInfo();
           imageInfo.setUri(imageUrl);
           imageInfo.setTime(new Date());
           // Construct the faceUri;
           String faceUri = ImageInfoHelper.getRemoteFaceUrl(imageUrl, propertiesBean.getIsJar());
           imageInfo.setFaceUri(faceUri);
           // DB saving...
           // imageInfoResp = _imageServiceItf.save(imageInfo);
           imageInfoResp = _imageServiceItf.save(imageInfo);
           LOG.info("ImageMagick output success: " + imageInfoResp);
           // Return the result.
       } catch (Exception ex) {
           LOG.error(ex.toString());
       }
       return imageInfoResp;
       
       
   }
   
       
       public static final byte[] input2byte(InputStream inStream)  
               throws IOException {  
           ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
           byte[] buff = new byte[100];  
           int rc = 0;  
           while ((rc = inStream.read(buff, 0, 100)) > 0) {  
               swapStream.write(buff, 0, rc);  
           }  
           byte[] in2b = swapStream.toByteArray();  
           return in2b;  
       }  
     
   
   
	
}