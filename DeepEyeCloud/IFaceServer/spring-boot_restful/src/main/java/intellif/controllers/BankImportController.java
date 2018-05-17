package intellif.controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.controllers.FileUploadController.ImageSize;
import intellif.dao.AreaDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.CidDetailDao;
import intellif.dao.CrimeFriTypeDao;
import intellif.dao.CrimeSecTypeDao;
import intellif.dao.JuZhuDetailDao;
import intellif.dao.OtherDetailDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.UserInfo;
import intellif.dto.BankImportInfo;
import intellif.dto.JsonObject;
import intellif.dto.ProcessInfo;
import intellif.enums.IFaceSdkTypes;
import intellif.enums.SourceTypes;
import intellif.ifaas.EBListIoctrlType;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.ifaas.T_IF_FACERECT;
import intellif.ifaas.T_ProgressQueryRst;
import intellif.service.BlackDetailServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.SolrServerItf;
import intellif.service.UserServiceItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FileUtil;
import intellif.utils.ImageInfoHelper;
import intellif.utils.ImageUtil;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.BatchInsertDto;
import intellif.database.entity.BatchPram;
import intellif.database.entity.CameraAndBlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.CrimeFriType;
import intellif.database.entity.CrimeSecType;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.ImportBank;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@RestController
@RequestMapping(GlobalConsts.R_ID_BANKIMPORT)
public class BankImportController {

	private static Logger LOG = LogManager.getLogger(BankImportController.class);


	// ==============
	// PRIVATE FIELDS
	// ==============

	@Autowired
	private AreaDao areaRepository;

	@Autowired
	private PropertiesBean propertiesBean;

	@Autowired
	private CidDetailDao cidDetailRepository;
	
	@Autowired
	private JuZhuDetailDao juZhuDetailRepository;
	
	@Autowired
	private OtherDetailDao otherDetailRepository;

	@Autowired
	private PoliceStationDao _policeStationDao;

	@Autowired
	private CrimeSecTypeDao crimeSecTypeRepository;

	@Autowired
	private CrimeFriTypeDao crimeFriTypeRepository;

	@Autowired
	private PersonDetailServiceItf personDetailService;

	@Autowired
	private IFaceSdkServiceItf iFaceSdkServiceItf;

	@Autowired
	private ImageServiceItf _imageServiceItf;

	@Autowired
	private BlackDetailDao blackDetailDao;

	@Autowired
	private BlackDetailServiceItf blackDetailService;

	@Autowired
	private SolrServerItf _solrService;

	@Autowired
	private FaceServiceItf faceService;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private CameraAndBlackDetailDao _cameraAndBlackDetailRepository;

	@Autowired
	private PersonDetailServiceItf _personDetailService;

	@Autowired
	private CameraServiceItf cameraService;

	@Autowired
	private UserServiceItf _userService;

	@Autowired
	private PersonDetailDao _personDetailDao;

	@Autowired
	private BlackDetailDao _blackDetailDao;

	@Autowired
	private SolrDataServiceItf _solrDataServiceItf;

	@Autowired
	private IoContrlServiceItf ioContrlServiceItf;
	@Autowired
	private JuZhuDetailDao juzhuDao;
	@Autowired
	private CidDetailDao cidDao;
	@Autowired
	private OtherDetailDao oDao;
	@Autowired
    private BlackBankDao blackBankDao;
	@Autowired
    ZoneAuthorizeServiceItf zoneAuthorizeService;
	static ConcurrentHashMap<Long, BankImportInfo> dlMap = new ConcurrentHashMap<Long, BankImportInfo>();


	@RequestMapping(value = "/schedule", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "获取导入进度")
	public JsonObject getSchedule() {
		return processSchedule();
		}
	private JsonObject processSchedule(){
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		UserInfo user = (UserInfo) auth.getPrincipal();
		 BankImportInfo bii = dlMap.get(user.getId());
		   if(bii == null){
			   return new JsonObject("該用戶沒有進度", 1001);
		   }
		   
		   ProcessInfo pInfo = bii.getProcess();
		   if((null != pInfo) &&(pInfo.getTotalSize() <= pInfo.getFailedNum()+pInfo.getSuccessNum())){
			   //用户进度已经完成
			   return new JsonObject(pInfo);
		   }
		   
		   int id = (int) user.getId();
		   int type =  bii.getImportType(); //0 static 1 black
		   if(type == 0){
			   try {
				   int bankId = bii.getBankId();
				   List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
						   .getAllTarget();
				   long excuteNum = 0;
				   LOG.info("start get schedule from c++,userId:"+id);
				   for (IFaaServiceThriftClient target : targetList) {
					   T_ProgressQueryRst progress = target
							   .getFeatureUpdateState(GlobalConsts.bankScheduleMap.get(bankId),id);
					   if (null != progress) {
						   excuteNum = excuteNum + progress.getFinishItemCnt();
					   }else{
						   LOG.error("get pk schedule back null");
					   }
				   }
	
				   ProcessInfo pi = bii.getProcess();
				   if(pi != null && pi.getSuccessNum() < excuteNum){
					   pi.setSuccessNum(excuteNum);
					   LOG.info("end get schedule from c++,userId:"+id+",totalNum:"+pi.getTotalSize()+",sucNum:"+pi.getSuccessNum());
				   }
				   return new JsonObject(pi);
			   } catch (Exception e) {
				   LOG.error("get pk schedule error", e);
				   return new JsonObject("获取进度出错", 1001);
			   }
		   }else{
			   return new JsonObject(bii.getProcess());
		   }
		
	}

	private void clearPKBaseProcess(long userId) {
		if(null!=dlMap.get(userId)){
			BankImportInfo bii = dlMap.get(userId);
			dlMap.remove(userId);
			if(bii.getImportType() == 0){
				Integer tableType = GlobalConsts.bankThriftMap.get(bii.getBankId());
				if(null != tableType){
					LOG.info("clear engine schedule,userId:"+userId);
					try {
						List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
								.getAllTarget();
						for (IFaceSdkTarget target : targetList) {
							
							target.iface_engine_ioctrl(
									EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(),
									tableType.intValue(),
									0,
									userId, 0); 
						}
					} catch (Exception e) {
						LOG.error("clear pk base process error:", e);
					}
				}
			}
			
		}
	}
	
	private void resetStaticSchedule(long userId, int bankId) {

		Integer tableType = GlobalConsts.bankThriftMap.get(bankId);
		if (null != tableType) {
			LOG.info("reset engine schedule,userId:" + userId + ",bankId:"
					+ bankId);
			try {
				List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
						.getAllTarget();
				for (IFaceSdkTarget target : targetList) {
					target.iface_engine_ioctrl(
							EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL.getValue(),
							tableType.intValue(), 0, userId, 0);
				}
			} catch (Exception e) {
				LOG.error("reset pk base process error:", e);
			}
		}
	}
	
	
	

	private boolean checkRuanlianjie(String name, String dirPath) {
		String[] dirs = dirPath.split("/");
		String fileNamePart = dirs[dirs.length - 1] + name;
		String imageUrl = ImageInfoHelper
				.getPKPrefix(propertiesBean.getIsJar()) + "/" + fileNamePart;
		return FileUtil.checkUrlIsOrNotExist(imageUrl);
	}

	@RequestMapping(value = "/cancelimport", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "PK项目导入功能")
	public JsonObject cancelImport() {
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		UserInfo user = (UserInfo) auth.getPrincipal();
		BankImportInfo bii = dlMap.get(user.getId());
		bii.setImportState(false);
		clearPKBaseProcess(user.getId());
		
		//通知C++ stop
		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
				HttpStatus.OK));

	}
	private  void getFirstFile(File dir,List<String> returnist,BankImportInfo info){
		try{
			String path = URLDecoder.decode(dir.getPath(),"utf-8");
			File pathFile = new File(path);
			if(pathFile.isDirectory()){
				String[] nameList = pathFile.list();
				if (null != nameList) {
					for(String item : nameList){
						if(info.isFindState()){
							String tempStr = URLEncoder.encode(item, "utf-8");
							String encodeStr = tempStr.replace("+", "%20");
							getFirstFile(new File(dir.getPath()+"/"+encodeStr),returnist,info);
						}
					}
				}
			}else{
				String abDir = dir.getPath();
				info.setFindState(false);
				returnist.add(abDir);
			}
		}catch(Exception e){
			LOG.error("getFirstFile error",e);
		}
		
}
	
	private boolean isOrNotFinishImport(long id){
		processSchedule();//先获取一下最后进度
		BankImportInfo bii = dlMap.get(id);
		if(bii!=null){
			ProcessInfo info = bii.getProcess();
			return(info.getTotalSize() <= info.getFailedNum()+info.getSuccessNum());
		}else{
			return true;
		}
	}
	private boolean checkIfOrNotExistInTable(long id,int bankId){
		if(1 == bankId){
			if(null !=cidDao.findOne(id)){
				return true;
			}
		}else if(2 == bankId){
			if(null !=juzhuDao.findOne(id)){
				return true;
			}
		}else if(3 == bankId){
			if(null !=oDao.findOne(id)){
				return true;
			}
		}
		return false;
	}
	
	 
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/import/bank/{bankStr}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@ApiOperation(httpMethod = "POST", value = "库导入功能")
	public JsonObject processBankImport(
			@PathVariable("bankStr") String bankStr,
			@RequestParam("path") String path) throws IOException {
		LOG.info("start import bank data");
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		UserInfo user = (UserInfo) auth.getPrincipal();
		//初始化用户导入信息数据 需要判断 当前用户是否存在未完成的导入
		if(!isOrNotFinishImport(user.getId())){
			return new JsonObject("用户存在未完成的导入", 1002);
		}
		LOG.info("normal start import bank data,userId:"+user.getId());
		clearPKBaseProcess(user.getId());
		//importOwner = auth.getName();
		//importOwnerStationId = user.getPoliceStationId();
		CurUserInfoUtil.setAuth(auth);
        int bankType = 0;
        int bankId = 0;
		final String dirPath = path.replaceAll("\\\\", "/");
		String[] bankArr = bankStr.split("#");
		if("s".equals(bankArr[0])){
			bankType = 0;
			bankId = Integer.parseInt(bankArr[1]);
		}else if("b".equals(bankArr[0])){
			bankType = 1;
			bankId = Integer.parseInt(bankArr[1]);
		}
		File file = new File(dirPath);
		if (file.isDirectory()) {
			
			BankImportInfo info  = new BankImportInfo(user,bankType,bankId);
			info.getProcess().setInfo(path+"$$"+bankStr);
			dlMap.put(user.getId(), info);
			List<String> nList = new ArrayList<String>();
			String[] pathArray = dirPath.split("/");
			StringBuffer pathSB = new StringBuffer();
			for(String item: pathArray){
				if(null!=item && item.trim().length()>0){
					pathSB.append("/");
					String tempStr = URLEncoder.encode(item, "utf-8");
					String encodeStr = tempStr.replace("+", "%20");
					pathSB.append(encodeStr);
				}
			}
			String newPath = pathSB.toString();
			getFirstFile(new File(newPath),nList,info);
			if(!nList.isEmpty()){
				String lastName = nList.get(0).split(newPath)[1];
				// 检查地址软链接是否正常
				if (!checkRuanlianjie(lastName, newPath)) {
					return new JsonObject("该目录没有设置软链接 ", 1002);
				}
										
				if (bankType == 0) {
					BankImportInfo bImport = dlMap.get(user.getId());
					resetStaticSchedule(user.getId(), bImport.getBankId());//不管是否存在，保险删除对应库的进度
					
					String randomStr = CommonUtil.getFixLenthString(8);
					 long baseData = Long.parseLong(randomStr
							+ "000000000");
					 while(true){
						 //静态库表数据重复检查
						 boolean isFind = false;
						 isFind = checkIfOrNotExistInTable(baseData+1,bImport.getBankId());
						 if(isFind){
							 LOG.info("find random in database random:"+baseData+" ,bankId:"+bImport.getBankId());
							 randomStr = CommonUtil.getFixLenthString(8);
							 baseData = Long.parseLong(randomStr
									 + "000000000");
						 }else{
							 break;
						 }
					 }
					 
					 LOG.info("random data:" + baseData);
					 ProcessInfo pi = bImport.getProcess();
					 int num = 3000;
					getAllFilesAndProcess(new File(newPath),baseData,newPath,bImport,num);
					//当图处总数不是3000倍数时，处理最后取余图片数据
					long count = pi.getTotalSize()%num;
					if(count != 0){
						long startIndex = (pi.getTotalSize()/num)*num+1;
						long endIndex = pi.getTotalSize();
						List<String> nnList = bImport.getBaseDirNameMap().get(pi.getTotalSize()/num+1);
						ImportBaseThread baseThread = new ImportBaseThread(nnList, newPath, startIndex, endIndex, baseData,user.getId());
						bImport.getBlackThreadPool().submit(baseThread);
					}
					

				} else {
					try{
					 BankImportInfo bImport = dlMap.get(user.getId());
					 int num = 10;
					 long baseData = 0;
						String randomStr = String.valueOf(Math
								.round(Math.random() * 1000000));
						String imagedir = new SimpleDateFormat(
								"yyyy-MM-dd-HH-mm-ss-SSS")
								.format(new Date())
								+ "_" + randomStr;
					
		                 String imageDir =  FileUtil.getUploads(propertiesBean.getIsJar()) + File.separator + imagedir;
						   File folder = new File(imageDir);
				            if (!folder.exists()) {
				                folder.mkdir();
				            }
						
					getAllFilesAndProcess(new File(newPath),imageDir,newPath,bImport,num);
					//当图处总数不是10倍数时，处理最后取余图片数据
					ProcessInfo pi = bImport.getProcess();
					long count = pi.getTotalSize()%num;
					if(count != 0){
						long startIndex = (pi.getTotalSize()/num)*num+1;
						long endIndex = pi.getTotalSize();
						List<String> nnList = bImport.getBaseDirNameMap().get(pi.getTotalSize()/num+1);
					
						ImportBlackThread blackThread = new ImportBlackThread(nnList, startIndex,user.getId());
						bImport.getBlackThreadPool().submit(blackThread);
					}
					
					   while(true){
			            	Thread.sleep(1000);
			            	 BankImportInfo bit = dlMap.get(user.getId());
			            	if(bit!=null && bit.getBlackThreadPool().getActiveCount()==0)
			               {	
			            	 LOG.info("complete black import task,userId:"+user.getId());
			            	 long total = bit.getProcess().getTotalSize();
			            	long success = bit.getProcess().getSuccessNum();
			            	bImport.getProcess().setFailedNum(total-success);
			            	_personDetailService.noticeEngineUpdateBlackDatas(user.getId());
			            	 break;//所有线程执行完
			               }
			            }
					
					}catch(Throwable e){
						LOG.error("process black import error",e);
					}
					
					
				}
			} else {
				return new JsonObject("该目录不存在图片", 1001);
			}

		} else {
			return new JsonObject("该目录不存在", 1001);
		}
		return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE,
				HttpStatus.OK));
	}

	
	public class ImportBlackThread implements Runnable{
		private List<String> nameList;
		private long userId;
		private long startIndex;
		public ImportBlackThread(List<String> nList,long sIndex,long userId){
			this.nameList = nList;
			this.userId = userId;
			this.startIndex = sIndex;
		}
		@Override
		public void run() {
			// 每个线程处理3000条数据
			LOG.info("black import thread start,thread name:"
					+ Thread.currentThread().getName()+" userId:"+userId);
		
			BankImportInfo bii = dlMap.get(userId);
			
			for (int i = 0; i < nameList.size(); i++) {
				LOG.info("pk black import thread start,thread name:"
						+ Thread.currentThread().getName());
				if (bii.isImportState()) {
					String blackDir = nameList.get(i);
					try {
						/*boolean state = StringUtil
								.isChineseChar(blackDir);
						if (state) {
							blackDir = StringUtil
									.filterChineseWord(blackDir);
						}	*/				
						String fName = blackDir.split("\\.")[0];
						String[] nameStr = fName.split("/");
						String name = nameStr[nameStr.length - 1];
						String decodeName = URLDecoder.decode(name,"utf-8");
						String decodeDir = URLDecoder.decode(blackDir,"utf-8");
		        	     processBlack(decodeName,decodeDir,userId);
				}catch(Throwable e){
						LOG.error("process black error",e);
						ProcessInfo pi = dlMap.get(userId).getProcess();
//						pi.setFailedNum(pi.getFailedNum()+1);
						pi.incrementFailedNumWithLock();
					}
			}
			
		}
			
			//很重要 处理完后 图片名称内存数据clear
			long num = this.startIndex/3000+1;
			bii.getBaseDirNameMap().remove(num);
		}
		
		private void processBlack(String blackName,String imageDir,long userId){

			PersonDetail personDetailResp = null;
			BlackDetail  blackDetailResp = null;
			String excStr = null;
			try {
				// 处理与疑犯表（t_person_detail）相关逻辑
				PersonDetail personDetail = new PersonDetail(
						true);
				personDetail.setSex("男");
				personDetail.setRealName(blackName);
				personDetail.setAddress("");
				personDetail.setCid("");
				personDetail.setCrimeAddress("");
				personDetail.setDescription("");
				personDetail.setNation("");
				BankImportInfo bii = dlMap.get(this.userId);
				personDetail.setBankId(bii.getBankId());
				BlackBank bank = blackBankDao.findOne(personDetail.getBankId());
		        personDetail.setType(bank.getListType());//不同类型的黑白名单库对应不同类型的黑白名单人员
				
				Calendar c = Calendar.getInstance();
				personDetail.setStarttime(c.getTime());
				c.add(Calendar.DATE, 365 * 30);
				personDetail.setEndtime(c.getTime());

				 
				personDetail.setOwner(dlMap.get(userId).getUser().getName());
				PoliceStation ps = _policeStationDao
						.findOne(dlMap.get(userId).getUser().getPoliceStationId());
				if (null != ps) {
					personDetail.setOwnerStation(ps
							.getStationName());
				}
				personDetail.setStatus(1);

				List<CrimeSecType> typeList = crimeSecTypeRepository
						.findAll();
				if (null == typeList || typeList.isEmpty()) {
					// 数据库无任何二级犯罪类型
					CrimeFriType friType = new CrimeFriType();
					friType.setFullName("莫须有");
					CrimeFriType resFri = crimeFriTypeRepository
							.save(friType);
					CrimeSecType secType = new CrimeSecType();
					secType.setFriId(resFri.getId());
					secType.setName("莫须有1");
					CrimeSecType resSec = crimeSecTypeRepository
							.save(secType);
					personDetail.setCrimeType(resSec
							.getId());
				} else {
					personDetail.setCrimeType(typeList.get(
							0).getId());
				}
				personDetailResp = (PersonDetail) personDetailService
						.save(personDetail);
				
			    MultipartFile multipartFile = ImageUtil.createMultFile("temp.jpg","image/jpeg", imageDir);
			    ImageInfo imageInfoResp = imageFileHandler(multipartFile);
    	       long  imageId = imageInfoResp.getId();
   	         personDetailResp.setPhotoData(imageInfoResp.getFaceUri());
   	         personDetailService.save(personDetailResp);
   	        //处理black_detail表相关逻辑
   	         
   	         //条件判断图片是否只有一个人脸
   	         IFaceSdkTarget ifaceSdkTarget = iFaceSdkServiceItf.getTarget(IFaceSdkTypes.THRIFT);
   	         List<T_IF_FACERECT> faceList  = ifaceSdkTarget.image_detect_extract(imageInfoResp.getUri(), imageInfoResp.getId());
   	            
   	         if(CollectionUtils.isEmpty(faceList)){
   	        	   excStr = "人脸引擎出错";
  	                imageInfoResp.setFaces(faceList.size());
  	                LOG.error(excStr);
  	                throw new Exception(excStr);
   	         } else if(faceList.size() != 1){
   	                excStr = "xls解析：图片解析出错(图片内容没有人脸或多个人脸)";
   	                imageInfoResp.setFaces(faceList.size());
   	                LOG.error(excStr);
   	                throw new Exception(excStr);
   	              }else if(faceList.size()==1 && faceList.get(0).forbiden>=1){
   	            	   excStr = "xls解析：图片解析出错(图片人脸在红名单内)，图片名称:";
   	            	   imageInfoResp.setFaces(-1);
      	                LOG.error(excStr);
      	                throw new Exception(excStr);
   	              }else{
   	            	  imageInfoResp.setFaces(1);
   	              }
   	             _imageServiceItf.save(imageInfoResp);
   	        //条件判断图片是否只有一个人脸
   	         
   	           BlackDetail blackDetail = new BlackDetail();
               blackDetail.setFromPersonId(personDetailResp.getId());
               blackDetail.setFromImageId(imageId);
               blackDetail.setBlackDescription("");
               blackDetail.setBankId(bii.getBankId());
               String faceUrl = imageInfoResp.getFaceUri();
               blackDetail.setImageData(faceUrl);
               //
                blackDetailResp = blackDetailDao.save(blackDetail);
               //调用C++
               boolean updateFaceFeatureResult = blackDetailService.updateFaceFeature(blackDetailResp,null);
               if(!updateFaceFeatureResult){
               	throw new Exception("update feature error");
               }
               
                  //布控设置                           
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
                        if(!CollectionUtils.isEmpty(cList)){
                            BatchInsertDto bid = new BatchInsertDto(cList);
                            jdbcTemplate.batchUpdate(bid.getInsertSql(),bid.getInsertSetter());
                        }
                        }
                    //区域布控
                    List<Object> aList = new ArrayList<Object>();
                    List<Long> areaSet = uAreaDataMap.get("area");
                    if(!CollectionUtils.isEmpty(areaSet)){
                        for(Long aId : areaSet){
                            aList.add(new AreaAndBlackDetail(aId, blackDetailResp.getId()));
                        }
                        if(!CollectionUtils.isEmpty(aList)){
                            BatchInsertDto bid1 = new BatchInsertDto(aList);
                            jdbcTemplate.batchUpdate(bid1.getInsertSql(),bid1.getInsertSetter());
                        }
                        }
                   
          
//                _solrDataServiceItf.addBlackDetail(GlobalConsts.BLACK_INFO_TYPE, blackDetailResp);
                _solrDataServiceItf.addBlackDetail(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE), blackDetailResp);
                if(null != dlMap.get(userId)){
                	ProcessInfo pi = dlMap.get(userId).getProcess();
//                	pi.setSuccessNum(pi.getSuccessNum()+1);
                	pi.incrementSuccessNumWithLock();
                }
               
   		   }catch(Exception e){
   			   LOG.error("process black  final exception",e);
   			   if(blackDetailResp != null){
   				blackDetailDao.delete(blackDetailResp.getId());
   			   }
   			   if(personDetailResp != null){
   			    personDetailService.delete(personDetailResp.getId());
   			   }
   			 if(null != dlMap.get(userId)){
   				ProcessInfo pi = dlMap.get(userId).getProcess();
//   				pi.setFailedNum(pi.getFailedNum()+1);
   				pi.incrementFailedNumWithLock();
             }
   		
   		   }
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
		   
		   private Map<String, String> imageFileOperation(MultipartFile file) throws Exception{
		        LOG.info("imageFileOperation with file:" + file.toString());
		        
		        Map<String, String> _imageMagickOutput = new HashMap<String, String>();
		        String dbFileName = null;
		        String fullFileName = null;
		        
		            byte[] bytes = file.getBytes();
		            String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
		            LOG.info("fileExt:" + fileExt);
		    		String randomStr = String.valueOf(Math
							.round(Math.random() * 1000000));
		            String fileNameAppendix
		                    // = "temp" + "." + fileExt;
		                    = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) +"_"+randomStr+ "." + fileExt;
		            LOG.info("fileNameAppendix:" + fileNameAppendix);
		            dbFileName = FileUtil.getUploads(propertiesBean.getIsJar()) + fileNameAppendix;
		            LOG.info("dbFileName:" + dbFileName);
		            fullFileName = dbFileName;
		            LOG.info("imageFileOperation with fullFieldName:" + fullFileName);

		            LOG.info("{} begin get angle", file.getOriginalFilename());
		            File f = new File(fullFileName);
		            int angle = ImageUtil.getAngle(file.getInputStream());
		            LOG.info("{} angle is {}", file.getOriginalFilename(), angle);
		            if(angle!=0){
		            	BufferedImage img = ImageUtil.rotateImage(bytes, angle, file.getOriginalFilename());
		                 try {
		                     ImageIO.write(img, "jpg", f);
		                     img.flush();
		                 } catch (IOException e) {
		                     LOG.info("{} after rotateImage write ImageIO exception:", file.getOriginalFilename());
		                 }   
		            }else{
		            	BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
		            	stream.write(bytes);
		            	stream.close();
		            }
		   
		            LOG.info("Upload (image)file success." + fullFileName);
		            String format4dbBase = this.formatImage(fullFileName, "jpg");
		            _imageMagickOutput.put(ImageSize.ori.toString(), format4dbBase);
		            return _imageMagickOutput;
		    }
		   @SuppressWarnings("unused")
		    private String formatImage(String source, String formatStr)
		            throws Exception {
		        String format4dbBase = FilenameUtils.getBaseName(source) + "_format" + "." + formatStr;//FilenameUtils.getExtension(source),always keep JPG
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
		
	}
	
	
	public class ImportBaseThread implements Runnable{
		private List<String> nameList;
		private String dirPath;
		private long startIndex;
		private long endIndex;
		private long baseData;
		private long userId;
		public ImportBaseThread(List<String> nList, String dirPath, long sIndex, long eIndex, Object bData,long userId){
			this.startIndex = sIndex;
			this.endIndex = eIndex;
			this.nameList = nList;
			this.baseData = (long) bData;
			this.dirPath = dirPath;
			this.userId = userId;
		}

		@Override
		public void run() {
			// 每个线程处理3000条数据
			LOG.info("pk base import thread start,thread name:"
					+ Thread.currentThread().getName()+" userId:"+userId);
		
			BankImportInfo bii = dlMap.get(userId);
			int bankId = bii.getBankId();
			List<ImportBank> bankList = new ArrayList<ImportBank>();
			
			for (int i = 0; i < nameList.size(); i++) {
				if (bii.isImportState()) {
					try {
						String dirName = nameList.get(i);
					/*	boolean state = StringUtil
								.isChineseChar(dirName);
						if (state) {
							dirName = StringUtil
									.filterChineseWord(dirName);
						}*/
						String[] dirs = dirPath.split("/");
						String lastName = dirName
								.split(dirPath)[1];
						String fileNamePart = dirs[dirs.length - 1]
								+ lastName;
						String imageUrl = ImageInfoHelper
								.getPKPrefix(propertiesBean
										.getIsJar())
								+ "/" + fileNamePart;
						
						long cid = baseData + startIndex+i;
						// 基础库导入 导入到static表
						//saveStaticBank(imageUrl,cid,bankId);
						String fName = lastName.split("\\.")[0];
						String[] nameStr = fName.split("/");
						String sName = nameStr[nameStr.length - 1];
						String decodeName = URLDecoder.decode(sName,"utf-8");
                        bankList.add(new ImportBank(imageUrl, cid,bankId,decodeName));
					} catch (Exception e) {
						LOG.error(
								"thread excute import base error:",
								e);
					}
					
				
				}

			}
			// notice c++;
			try {
			    if (bii.isImportState()){
			        saveStaticBank(bankList,bankId);
			    }
			    long stIndex = baseData + startIndex;
			    long enIndex = baseData + endIndex;
				List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
						.getAllTarget();
				Random ran = new Random();
				int aa = ran.nextInt(targetList.size());
				IFaceSdkTarget target = targetList.get(aa);
				LOG.info("select engine index:" + aa
						+ " start Index:" + stIndex
						+ " end Index:" + enIndex +"userId:"+userId);
				target.iface_engine_ioctrl(
						EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL
								.getValue(),
						GlobalConsts.bankThriftMap.get(bii.getBankId()),
						userId, stIndex,
								enIndex);
			} catch (Exception e) {
				LOG.error("notice c++ pk base error", e);
			}
			
			//很重要 处理完后 图片名称内存数据clear
			long num = this.startIndex/3000+1;
			bii.getBaseDirNameMap().remove(num);
			
		}
		
	
		
		/**
		 * 批量插入数据库
		 * @param bankList
		 * @param bankType
		 */
		private void saveStaticBank(List<ImportBank> bankList,int bankType){
			
		    BatchPram pram = new BatchPram(bankList, bankType);
			jdbcTemplate.batchUpdate(pram.getInsertDetailSql(),pram.getDetailSetter());
			jdbcTemplate.batchUpdate(pram.getInsertInfoSql(),pram.getInfoSetter());
				
			
		}
	
	}
	/**
	 * 扫描文件并分发线程处理
	 * @param dir
	 * @param dirList
	 */
	@SuppressWarnings("unchecked")
	public  void getAllFilesAndProcess(File dir,Object baseData,String baseDir,BankImportInfo bImport,int num){
		
		try{
			String path = URLDecoder.decode(dir.getPath(),"utf-8");
			File pathFile = new File(path);
			if(pathFile.isDirectory()){
				String[] nameList = pathFile.list();
				if (null != nameList) {
					for(String item : nameList){
						String tempStr = URLEncoder.encode(item, "utf-8");
						String encodeStr = tempStr.replace("+", "%20");
						getAllFilesAndProcess(new File(dir.getPath()+"/"+encodeStr),baseData,baseDir,bImport,num);
					}
				}
			}else{				
				String abDir = dir.getPath();
				ProcessInfo pi = bImport.getProcess();
				ConcurrentHashMap<Long,List<String>> baseDirNameMap = bImport.getBaseDirNameMap();
				pi.setTotalSize(pi.getTotalSize()+1);
				if((pi.getTotalSize()%num) == 1){
					//需要记录数据到list中
					LOG.info("new list store image name,current Num:"+pi.getTotalSize()+"userId:"+bImport.getUser().getId());
					long index = pi.getTotalSize()/num;
					/*if(null == baseDirNameMap.get(index+1)){
						List<String> nlist = new ArrayList<String>();
						baseDirNameMap.put(index+1, nlist);
					}*/
					List<String> nlist = new ArrayList<String>();   //修改上处findbugs 上处在并发环境中复合操作是不安全的
					baseDirNameMap.putIfAbsent(index+1, nlist);
					
					List<String> nnList = (List<String>)baseDirNameMap.get(index+1);
					nnList.add(abDir);
				}
				else if((pi.getTotalSize()%num) == 0){
					//需要创建线程来导入数据到数据库中
					LOG.info("create Thread process image list ,current Num:"+pi.getTotalSize()+"userId:"+bImport.getUser().getId());
				long endIndex = pi.getTotalSize();
				long startIndex = endIndex - (num-1);	
				List<String> nnList = (List<String>)baseDirNameMap.get(pi.getTotalSize()/num);
				nnList.add(abDir);
				
				if(bImport.getImportType() != 1){
					ImportBaseThread baseThread = new ImportBaseThread(nnList, baseDir, startIndex, endIndex, baseData,bImport.getUser().getId());
					bImport.getBlackThreadPool().submit(baseThread);
				}else{
					ImportBlackThread blackThread = new ImportBlackThread(nnList, startIndex,bImport.getUser().getId());
					bImport.getBlackThreadPool().submit(blackThread);
				}
				dir = null;
			}else{
				List<String> nnList = (List<String>)baseDirNameMap.get(pi.getTotalSize()/num+1);
				nnList.add(abDir);
			}
		}
		}catch(Exception e){
			LOG.error("count and process pk base size error",e);
		}
	}
}