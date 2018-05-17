package intellif.fk.controller;

import intellif.audit.EntityAuditListener;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.core.tree.Tree;
import intellif.dao.AlarmInfoDao;
import intellif.dao.AreaDao;
import intellif.dao.AuditLogDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.CameraAndBlackDetailDao;
import intellif.dao.CameraInfoDao;
import intellif.dao.CrimeFriTypeDao;
import intellif.dao.CrimeSecTypeDao;
import intellif.dao.DistrictDao;
import intellif.dao.IFaceConfigDao;
import intellif.dao.PersonDetailDao;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.RedDetailDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.fk.dao.FkBkBankDao;
import intellif.fk.dao.FkInstitutionCodeDao;
import intellif.fk.dao.FkPersonAttrDao;
import intellif.fk.dto.FindFkPersonDto;
import intellif.fk.dto.FkPersonDto;
import intellif.fk.dto.FkPersonIcCardDto;
import intellif.fk.dto.FkPersonResultDto;
import intellif.fk.service.FkPersonDetailService;
import intellif.fk.vo.FkBkBank;
import intellif.fk.vo.FkInstitutionCode;
import intellif.fk.vo.FkPersonAttr;
import intellif.service.BlackDetailServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.IoContrlServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.SolrServerItf;
import intellif.service.UserServiceItf;
import intellif.service.impl.PersonDetailServiceImpl;
import intellif.settings.ImageSettings;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FileUtil;
import intellif.utils.Pageable;
import intellif.database.entity.AreaAndBlackDetail;
import intellif.database.entity.BatchInsertDto;
import intellif.database.entity.CrimeFriType;
import intellif.database.entity.CrimeSecType;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.UserInfo;
import intellif.zoneauthorize.common.LocalCache;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_FK_PERSON_DETAIL)
public class FkPersonDetailController {

    private final static String format = "yyyy-MM-dd HH:mm:ss";
    private static Logger LOG = LogManager.getLogger(FkPersonDetailController.class);

    @Autowired
    private FkBkBankDao _fkBkBankDao;
    @Autowired
    private PersonDetailDao _personDetailDao;
    @Autowired
    private FkPersonAttrDao _fkPersonAttrDao;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeServiceItf;
    @Autowired
    private PersonDetailServiceItf _personDetailService;
    @Autowired
    private FkPersonDetailService fkPersonDetailService;
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private BlackDetailServiceItf _blackDetailService;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private CameraServiceItf cameraService;
    @Autowired
    private CameraAndBlackDetailDao _cameraAndBlackDetailRepository;
    @Autowired
    private SolrServerItf _solrService;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private PoliceStationDao _policeStationDao;
    @Autowired
    private IoContrlServiceItf ioContrlServiceItf;
    @Autowired
    private PropertiesBean propertiesBean;
    @Autowired
    private AuditLogDao auditLogRepository;
    @Autowired
    private UserDao userRepository;
    @Autowired
    private BlackBankDao blackbankDao;
    @Autowired
    private PersonDetailServiceImpl personDetailService;
    @Autowired
    private AlarmInfoDao _alarmInfoDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private RedDetailDao redDetailDao;
    @Autowired
    private IFaceConfigDao ifaceConfigDao;
    @Autowired
    private CrimeSecTypeDao crimeSecTypeRepository;
    @Autowired
    private CrimeFriTypeDao crimeFriTypeRepository;
    @Autowired
    private DistrictDao districtDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private FkInstitutionCodeDao fkInstitutionCodeDao;
    @Autowired
    private PoliceStationAuthorityDao policeStationAuthorityRepository;
    @Autowired
    private CameraInfoDao cameraInfoDao;


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the fk person detail is successfully created or not.")
    public JsonObject createFkPerson(@RequestBody @Valid FkPersonDto fkPersonDto) throws Exception {

        // boolean status = ValidateUtil.validateResult(fkPersonDto); //
        // 对象里有对身份证做校验      
        if (fkPersonDto.getCid() == null || fkPersonDto.getCid().trim().equals("")) {
            return new JsonObject("身份证号码不能为空", 1001);
        }
        if (StringUtils.isEmpty(fkPersonDto.getDistrictId())) {
            return new JsonObject("机构代码subInstitution不能为空", 1001);
        }
        if (StringUtils.isEmpty(fkPersonDto.getAreaId())&&!fkPersonDto.getDistrictId().equals("440300000000")) {
            return new JsonObject("机构代码localInstitution不能在上级区域不为市局时为空", 1001);
        }
        if (fkPersonDto.getRealName() == null || fkPersonDto.getRealName().trim().equals("")) {
            fkPersonDto.setRealName("fk_未知");
        }
        
        /*
         * if (!status) { return new JsonObject("身份证格式有问题或者号码不存在", 1001); }
         */
        
        UserInfo user = CurUserInfoUtil.getUserInfo();
        
        String subInstitutionCode = fkPersonDto.getDistrictId();
        String localInstitutionCode = fkPersonDto.getAreaId();
        
        int flag = 0; //0-反恐人员属于 分局-派出所这一具体类型    1-反恐人员直接属于深圳市局  2-反恐人员属于 深圳市局-分局
        
        if(subInstitutionCode.equals("440300000000")){
                if(StringUtils.isEmpty(localInstitutionCode)||localInstitutionCode.equals("440300000000")){
                    flag = 1;
                }else{
                    flag = 2; 
                }
            }                   
                     
        List<FkInstitutionCode> subInstitution = fkInstitutionCodeDao.findInstitionByCode(subInstitutionCode);
        List<FkInstitutionCode> localInstitution = fkInstitutionCodeDao.findInstitionByCode(localInstitutionCode);
           
        if (CollectionUtils.isEmpty(subInstitution)) {
            return new JsonObject("没有找到对应的district机构代码..", 1002);
        }
        if (CollectionUtils.isEmpty(localInstitution)&&flag!=1) {
            return new JsonObject("没有找到对应的area机构代码..", 1002);
        }

        if (flag!=1&&!localInstitution.get(0).getLSJG().equals(subInstitution.get(0).getJGDM())) {
            return new JsonObject("新增人员的所在分局districtid和区域areaid对应关系错误", 1001);
        }

       /* List<BigInteger> districtIdList = null;
        List<BigInteger> areaIdList = null;*/
        
       /* if(flag==0){
            districtIdList = districtDao.findDistrictIdByCode(subInstitutionCode);
            areaIdList = areaDao.findAreaIdByCode(localInstitutionCode); 
        }else if(flag==1){
            districtIdList = districtDao.findDistrictIdByCode(subInstitutionCode);
            areaIdList =  districtIdList;
        }else if(flag==2){
            districtIdList = districtDao.findDistrictIdByCode(subInstitutionCode);
            areaIdList =   districtDao.findDistrictIdByCode(subInstitutionCode);
        }
               
        if (CollectionUtils.isEmpty(districtIdList)||CollectionUtils.isEmpty(areaIdList)) {
            return new JsonObject("布控失败，反恐平台的分局和所对应到系统的区域为空，故找不到可布控摄像头", 1002);
        }*/
        
        // 查看该机构代码对应的反恐库是否存在了 不存在则新建
        String fkBankName = "fk_" + subInstitution.get(0).getJGMC() + "_" + localInstitution.get(0).getJGMC();
        List<BlackBank> bankList = blackbankDao.findByBankName(fkBankName);
        long savedBankId =0;
        if (CollectionUtils.isEmpty(bankList)) {
           savedBankId = creatFkBankByInstitutionCode(user.getPoliceStationId(),subInstitution.get(0).getJGDM(), localInstitution.get(0).getJGDM());
        }else{
            savedBankId = bankList.get(0).getId();
        }

        try {
            String imageIds = fkPersonDto.getImageIds();
            if (null == imageIds || "".equals(imageIds.trim())) {
                return new JsonObject("请先上传嫌疑人图片..", 1001);
            }
            PersonDetail personD = fkPersonDto.getPersonDetail();
            FkPersonAttr fkPersonAttr = fkPersonDto.getFkPersonAttr();

            personD.setBankId(savedBankId);
            // long fkBankId = blackbankDao.findFkBank().getId();
            /*String bankIdAutority = "," + this._userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE) + ",";
            if (bankIdAutority.indexOf("," + savedBankId + ",") >= 0) {
                personD.setBankId(savedBankId);
            } else {
                return new JsonObject("您没有权限对该反恐库新建嫌疑人！", 1001);
            }*/
            
            
            personD.setOwner(((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName());
            long ownerStationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());
            personD.setOwnerStation(_policeStationDao.findOne(ownerStationId).getStationName());
            personD.setType(0); // 反恐对象也属于黑名单人员
            personD.setArrest(0); // 和其他布控人员一样
            personD.setImportant(0);// 和其他布控人员一样
            if (fkPersonDto.getFkType() > 20 && fkPersonDto.getType() < 25) {
                personD.setStatus(1);
            } else if (fkPersonDto.getFkType() > 10 && fkPersonDto.getType() < 15) {
                personD.setStatus(0); // 查询库中新建不布控
            }
            personD.setRuleId(1);// 布控反恐人员 规则都默认给1

            if (fkPersonDto.getStime() == null || fkPersonDto.getStime().trim().equals("")) {
                personD.setStarttime(new Date());
            } else {
                personD.setStarttime(intellif.utils.DateUtil.getFormatDate(fkPersonDto.getStime(), format));
            }
            if (fkPersonDto.getEtime() == null || fkPersonDto.getEtime().trim().equals("")) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 365 * 30); // 默认布控年限30
                personD.setEndtime(c.getTime());
            } else {
                personD.setEndtime(intellif.utils.DateUtil.getFormatDate(fkPersonDto.getEtime(), format));
            }

            // 查看库中的反恐人员默认布控开始时间和结束时间为一个点
            if (fkPersonDto.getFkType() > 10 && fkPersonDto.getFkType() < 15) {
                personD.setStarttime(new Date());
                personD.setEndtime(new Date());
            } else {
                // 布控中结束时间不能比开始时间早的校验
                if (personD.getEndtime().before(personD.getStarttime())) {
                    return new JsonObject("布控结束时间不能早于布控开始时间！", 1001);
                }
            }

            String personUrl_ori = ((ImageInfo) _imageServiceItf.findById(Long.valueOf(fkPersonDto.getImageIds().split(",")[0]))).getFaceUri();
            String personUrl_store = ImageSettings.getStoreRemoteUrl()
                    + new File(FileUtil.getStoreFaceUri(personUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
            personD.setPhotoData(personUrl_store);
            // 反恐人员 犯罪类型 默认为 反恐-反恐人员 类别
            List<CrimeSecType> fkSecType = crimeSecTypeRepository.queryByText("反恐人员");
            if (fkSecType != null && fkSecType.size() != 0) {
                personD.setCrimeType(fkSecType.get(0).getId());
            } else {
                CrimeFriType crimeFriType = new CrimeFriType();
                crimeFriType.setFullName("反恐");
                crimeFriType.setShortName("反恐");
                CrimeFriType savedCrimeFriType = crimeFriTypeRepository.save(crimeFriType);
                CrimeSecType crimeSecType = new CrimeSecType();
                crimeSecType.setFriId(savedCrimeFriType.getId());
                crimeSecType.setName("反恐人员");
                CrimeSecType savedCrimeSecType = crimeSecTypeRepository.save(crimeSecType);
                personD.setCrimeType(savedCrimeSecType.getId());
            }

            PersonDetail personDSaved = (PersonDetail) personDetailService.save(personD);

            fkPersonAttr.setFromPersonId(personDSaved.getId());
            //适配 反恐一期的查询情况
            /*fkPersonAttr.setDistricId(districtIdList.get(0).longValue());
            fkPersonAttr.setAreaId(areaIdList.get(0).longValue());*/
            fkPersonAttr.setDistricId(0);
            fkPersonAttr.setAreaId(0);
            
            fkPersonAttr.setFkSubInstitutionCode(subInstitutionCode);
            fkPersonAttr.setFkLocalInstitutionCode(localInstitutionCode);

            _fkPersonAttrDao.save(fkPersonAttr);

           /* // 要根据fk_subInstitutionCode 转成areaIdlist  再转成得到cameraids 因为反恐平台不是直接传递的布控的摄像头列表
            List<TreeNode> cameraList = new ArrayList<TreeNode>();
            for(int i=0;i<areaIdList.size();i++){
                List<TreeNode> cList = null;
                if(flag==0){
                 cList = zoneAuthorizeServiceItf.offspring(Area.class, areaIdList.get(i).longValue(), CameraInfo.class);
                }else if(flag==1){
                 cList = zoneAuthorizeServiceItf.offspring(DistrictInfo.class, areaIdList.get(i).longValue(), CameraInfo.class);   
                }else if(flag==2){
                 cList = zoneAuthorizeServiceItf.offspring(DistrictInfo.class, areaIdList.get(i).longValue(), CameraInfo.class);   
                }
               
                cameraList.addAll(cList);
            }*/
            //布控库中的人员是直接 全区域布控   目前也就是 全龙岗区域
           /* List<BigInteger> districtId = districtDao.findDistrictIdByLocal(1);
            long localDistrictId = districtId.get(0).longValue();
            List<TreeNode> cameraList = new ArrayList<TreeNode>();
            if(null==LocalCache.tree){
                return new JsonObject("布控失败，localcache.tree中摄像头为空", 1002);
            }else{
                cameraList  =  LocalCache.tree.offspringList(DistrictInfo.class, localDistrictId, CameraInfo.class, false);
            }
            

            List<Long> cameraIds = new ArrayList<Long>();
            for (int i = 0; i < cameraList.size(); i++) {
                cameraIds.add(cameraList.get(i).getId());
            }*/
            //获取所有 区域（下面直接对应摄像头的区域 不能直接走tree缓存获取  那样取到的是全部区域 包含下面没有摄像头的区域）
            Set<Long> areaSet = new HashSet<Long>();
            areaSet = findAreaOfCameras();
            
            /*if (cameraIds.size() == 0) {
                this._personDetailDao.delete(personDSaved);
                this._fkPersonAttrDao.delete(fkPersonAttr);
                return new JsonObject("布控失败，布控区域下的摄像头为空", 1002);
            }*/

            for (String imageId : imageIds.split(",")) {
                BlackDetail blackDetail = new BlackDetail();
                blackDetail.setFromImageId(Long.valueOf(imageId));
                blackDetail.setFromPersonId(personDSaved.getId());
                String faceUrl_ori = ((ImageInfo) _imageServiceItf.findById(blackDetail.getFromImageId())).getFaceUri();
                String faceUrl_store = ImageSettings.getStoreRemoteUrl()
                        + new File(FileUtil.getStoreFaceUri(faceUrl_ori, propertiesBean.getIsJar()).storeImageUri).getName();
                blackDetail.setImageData(faceUrl_store);
                blackDetail.setBankId(personD.getBankId());
                BlackDetail resp = _blackDetailDao.save(blackDetail);

                try {
                    this._blackDetailService.updateFaceFeature(resp,null);
                } catch (Exception e) {
                    LOG.error("异常：", e);
                    StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
                    LOG.error("Class " + ste.getClassName() + " Method " + ste.getMethodName() + "：连接引擎服务器更新特征值失败！", e);
                    this._blackDetailDao.delete(resp);
                    personDetailService.delete(personDSaved.getId());
                    this._fkPersonAttrDao.delete(fkPersonAttr);
                    return new JsonObject("通信故障，请联系管理员！", 1002);
                }

                /*List<Object> cList = new ArrayList<Object>();
                for (long cId : cameraIds) {
                    cList.add(new CameraAndBlackDetail(cId, resp.getId()));
                }
                BatchInsertDto bid = new BatchInsertDto(cList);
                jdbcTemplate.batchUpdate(bid.getInsertSql(), bid.getInsertSetter());*/
                
                //修改为与1.4.4同步的 按区域布控 
                List<Object> aList = new ArrayList<Object>();
                if(!CollectionUtils.isEmpty(areaSet)){
                    for(Long aId : areaSet){
                        aList.add(new AreaAndBlackDetail(aId, resp.getId()));
                    }
                    BatchInsertDto bid1 = new BatchInsertDto(aList);
                    jdbcTemplate.batchUpdate(bid1.getInsertSql(),bid1.getInsertSetter());
                }

                try {
                    _solrDataServiceItf.addBlackDetail(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE), resp);
                } catch (Exception e) {
                    LOG.error("索引嫌疑人失败，失败id：" + resp.getId(), e);
                }
            }

            // 只对反恐-布控库中的人员进行布控 反恐-查询库中的人员不布控
            if (fkPersonDto.getFkType() > 20 && fkPersonDto.getType() < 25) {
                new Thread() {
                    @Override
                    public void run() {
                        _personDetailService.refreshPersonOfUpdate(personDSaved.getId());
                    }
                }.start();
            }
            String result = "PersonDetail's id:" + personDSaved.getId() + "   FkPersonAttr's id:" + fkPersonAttr.getId() + "   BlackDetail's count:"
                    + imageIds.split(",").length;
            return new JsonObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return new JsonObject("something wrong，contact the administrator please！", 1001);
        }

    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the fk person detail is successfully updated or not.")
    public JsonObject updateFkPerson(@PathVariable("id") long id, @RequestBody @Valid FkPersonDto fkPersonDto) throws Exception {

        /*String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
        if (authority.trim().length() == 0) {
            return new JsonObject("本单位没有库更新权限！", 1001);
        }*/
        /*if (fkPersonDto.getRealName() == null || fkPersonDto.getRealName().trim().equals("")) {
            return new JsonObject("反恐人员姓名不能为空", 1001);
        }*/
        
        if (fkPersonDto.getRealName() == null || fkPersonDto.getRealName().trim().equals("")) {
            fkPersonDto.setRealName("fk_未知");
        }       
        if (fkPersonDto.getCid() == null || fkPersonDto.getCid().trim().equals("")) {
            return new JsonObject("身份证号码不能为空", 1001);
        }
        try {
           // List<PersonDetail> findList = _personDetailDao.findOne(id, authority.split(","));
            
           // if (findList.size() > 0) {
               // PersonDetail find = findList.get(0);
                
                PersonDetail find =  personDetailService.findById(id);
                 
                // 以免entityauditlistener中的更新字段判断抛空指针异常
                find.setCrimeAddress("");
                find.setDescription("");
                find.setStime(find.getStarttime() + "");
                find.setEtime(find.getEndtime() + "");

                List<FkPersonAttr> fkAttr = _fkPersonAttrDao.findByFromPersonId(find.getId());
                EntityAuditListener.statusMap.put(find.getId(), find.clone());
                // EntityAuditListener.statusMap.put(fkAttr.getId(),
                // fkAttr.clone());
                PersonDetail newPerson = fkPersonDto.getPersonDetail();
                FkPersonAttr fkPersonAttr = fkPersonDto.getFkPersonAttr();

                find.update(newPerson);
                fkAttr.get(0).update(fkPersonAttr);
                fkAttr.get(0).setFromPersonId(find.getId());

                PersonDetail person = (PersonDetail) personDetailService.save(find);
                FkPersonAttr fk = _fkPersonAttrDao.save(fkAttr.get(0));

                String result = "update successful! PersonDetail's id:" + person.getId() + "   FkPersonAttr's id:" + fk.getId();
                return new JsonObject(result);
        /*    } else {
                return new JsonObject("对不起，您没有更新反恐库中人员的权限！", 1001);
            }*/
        } catch (Exception e) {
            LOG.error("change fk person layout status:", e);
            return new JsonObject("系统出现小问题,请稍后重试或者刷新浏览器", 1001);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the fk person detail is successfully delete or not.")
    public JsonObject deleteFkPerson(@PathVariable("id") long id) throws Exception {
        String authority = _userService.getAuthorityIds(GlobalConsts.UPDATE_AUTORITY_TYPE);
        if (authority.trim().length() == 0) {
            return new JsonObject("本单位没有库授权！", 1001);
        }
        String filterSql = " id = "+id+" bank_id in("+authority+")";
        List<PersonDetail> findList = personDetailService.findByFilter(filterSql);
        if (findList.size() <= 0) {
            return new JsonObject("该反恐人员已经被删除或没有权限！", 1001);
        }
        this._personDetailDao.delete(id);

        List<FkPersonAttr> fkPersonAttr = this._fkPersonAttrDao.findByFromPersonId(id);
        this._fkPersonAttrDao.delete(fkPersonAttr.get(0).getId());

        List<BlackDetail> blackList = this._blackDetailDao.findByFromPersonId(id);

        List<String> delList = new ArrayList<String>();
        for (BlackDetail black : blackList) {
            this._blackDetailDao.delete(black.getId());
            this._cameraAndBlackDetailRepository.deleteByBlackdetailId(black.getId());
            this._alarmInfoDao.deleteByBlackId(black.getId());
            delList.add("" + black.getId());
        }
        try {
            this._solrDataServiceItf.deleteById(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE), delList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 只对反恐-布控库中的人员进行取消布控 反恐-查询库中的人员本来就没布控 不用通知c++取消
        if (findList.get(0).getFkType() > 20 && findList.get(0).getType() < 25) {
            new Thread() {
                @Override
                public void run() {
                    _personDetailService.refreshPersonOfUpdate(id);
                }
            }.start();
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value = "/find/page/{page}/pagesize/{pagesize}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "find fk person")
    public JsonObject findFkPersonList(@PathVariable("page") int page, @PathVariable("pagesize") int pageSize,
            @RequestBody @Valid FindFkPersonDto findFkPersonDto) {

        long subInstitutionId = findFkPersonDto.getFkSubInstitutionId();
        long localInstitutionId = findFkPersonDto.getFkLocalInstitutionId();
        
        String subInstitutionCode = "";
        String localInstitutionCode = "";
        
        //对一期老数据的兼容 即subcode和localcode为空 根据districid和areaid判断的情况
        List<BigInteger> districtIdList = new ArrayList<BigInteger>();
        String districtId = "";
        List<BigInteger> areaIdList = new ArrayList<BigInteger>();
        String areaIds ="";
        
        if(subInstitutionId==0){
            if(localInstitutionId==0){
                //所有区域 相当于不过滤
            }else{
                //分局 - 所
                FkInstitutionCode localInstitution = fkInstitutionCodeDao.findOne(localInstitutionId);
                localInstitutionCode = localInstitution.getJGDM();
                areaIdList = areaDao.findAreaIdByCode(localInstitutionCode);
            }
        }else{
             if(localInstitutionId==0){
              //分局  
                 FkInstitutionCode subInstitution = fkInstitutionCodeDao.findOne(subInstitutionId);
                 subInstitutionCode = subInstitution.getJGDM();
                 districtIdList = districtDao.findDistrictIdByCode(subInstitutionCode);
            }else{
              //分局-所  
                FkInstitutionCode subInstitution = fkInstitutionCodeDao.findOne(subInstitutionId);
                FkInstitutionCode localInstitution = fkInstitutionCodeDao.findOne(localInstitutionId);
                subInstitutionCode = subInstitution.getJGDM();
                localInstitutionCode = localInstitution.getJGDM();
                districtIdList = districtDao.findDistrictIdByCode(subInstitutionCode);
                areaIdList = areaDao.findAreaIdByCode(localInstitutionCode);
            }
        }
             
        findFkPersonDto.setFkSubInstitutionCode(subInstitutionCode);
        findFkPersonDto.setFkLocalInstitutionCode(localInstitutionCode);
        
        findFkPersonDto.setDistrictIds(listToString(districtIdList));
        findFkPersonDto.setAreaIds(listToString(areaIdList));
        
        //人员查询接口根据人员所在库 依次进行权限过滤 
        List<FkPersonResultDto> listResult = this.fkPersonDetailService.findFkPerson(findFkPersonDto);
        if (listResult == null) {
            return new JsonObject("结果为空", 1001);
        }
        
        String bankIdAutority = "," +this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE) +","; 
        listResult = filterByFkBankAuthority(listResult,bankIdAutority);       
        if (listResult == null) {
            return new JsonObject("以过滤掉无权限库中人员", 1001);
        }
        int totalNum = listResult.size();
        Pageable<FkPersonResultDto> pageableResult = new Pageable<FkPersonResultDto>(listResult);
        pageableResult.setPageSize(pageSize);
        pageableResult.setPage(page);
        int maxPage = pageableResult.getMaxPages();

        return new JsonObject(pageableResult.getListForPage(), 0, maxPage, totalNum);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the fk person detail id is successfully get or not.")
    public JsonObject getFkPerson(@PathVariable("id") long id) {

        PersonDetail pd = personDetailService.findById(id);
        if (pd != null) {
            long fkBankId = pd.getBankId();
            String bankIdAutority = "," + this._userService.getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE) + ",";
            if (bankIdAutority.indexOf("," + fkBankId + ",") >= 0) {

                List<FkPersonAttr> fkPersonAttr = this._fkPersonAttrDao.findByFromPersonId(id);
                FkPersonDto fkPersonDto = new FkPersonDto(pd, fkPersonAttr.get(0));
                
                //为前端展示效果过滤    如果反恐人员直接属于市局 页面 所属分局 和 所属市局机构代码处 直接展示--  --       如果反恐人员直接属于 市局-分局   页面所属分局 和 所属市局  展示 分局  和  --
                String subInstitutionCode = fkPersonDto.getSubInstitutionCode();
                String localInstitutionCode = fkPersonDto.getLocalInstitutionCode();
                
                if(subInstitutionCode.equals("440300000000")&&(StringUtils.isEmpty(localInstitutionCode)||localInstitutionCode.equals("440300000000"))){
                    fkPersonDto.setSubInstitutionCode("");
                    fkPersonDto.setLocalInstitutionCode("");
                }else if(subInstitutionCode.equals("440300000000")&&!StringUtils.isEmpty(localInstitutionCode)&&!localInstitutionCode.equals("440300000000")){
                    fkPersonDto.setSubInstitutionCode(localInstitutionCode);
                    fkPersonDto.setLocalInstitutionCode("");
                }                
                return new JsonObject(fkPersonDto);

            } else {
                return new JsonObject("您没有权限查看反恐库嫌疑人！", 1001);
            }
        } else {
            return new JsonObject("数据已被删除或不存在！", 1001);
        }

    }

    @RequestMapping(value = "/updateIcCardOrMacAddress", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the fk person's iccard is successfully created or not.")
    public JsonObject updateIcCardOrMacAddress(@RequestBody @Valid FkPersonIcCardDto fkPersonIcCardDto) throws Exception {

        int operateObject = fkPersonIcCardDto.getOperateObject();
        int operateType = fkPersonIcCardDto.getOperateType();
        long personId = fkPersonIcCardDto.getPersonId();
        String newCode = fkPersonIcCardDto.getCode();
        String oldCode = "";
        String objectName = "";
        List<FkPersonAttr> fkPAttr = _fkPersonAttrDao.findByFromPersonId(personId);

        // object=0即操作icCard对象 object=1即操作macAddress对象
        if (operateObject == 0) {
            oldCode = fkPAttr.get(0).getIcCard();
            // objectName = "icCard";
            objectName = "交通卡";
        } else if (operateObject == 1) {
            oldCode = fkPAttr.get(0).getMacAddress();
            // objectName = "macAddress";
            objectName = "mac地址";
        } else {
            return new JsonObject("object对象只能为0或者1.0-交通卡,1-mac地址", 0);
        }

        if (operateType == 0) {

            // 最多只能有3个icCard的存储
            if (oldCode != null && !oldCode.trim().equals("") && oldCode.split(",").length >= 3) {
                return new JsonObject("该用户已经存储了3个" + objectName + ",请先删除再继续存储", 0);
            }
            // 如果获取到的最后一位是，说明已经存有了
            if (oldCode != null && !oldCode.trim().equals("")) {
                newCode = oldCode + "," + newCode;
            }
            try {
                if (operateObject == 0) {
                    _fkPersonAttrDao.updateIcCard(personId, newCode);
                } else if (operateObject == 1) {
                    _fkPersonAttrDao.updateMacAddress(personId, newCode);
                }
            } catch (Exception e) {
                return new JsonObject("添加" + objectName + "异常，请稍候再试！", 1001);
            }

            return new JsonObject("添加" + objectName + "成功", 0);

        } else if (operateType == 1) {

            if (oldCode != null && !oldCode.trim().equals("") && oldCode.indexOf(newCode) == -1) {
                return new JsonObject("您要删除的" + objectName + "不存在", 0);
            }
            // 逗号处理，如果要删除的是最后位置的iccard
            if (oldCode != null && !oldCode.trim().equals("") && oldCode.indexOf(newCode + ",") == -1) {
                oldCode = oldCode.replace(newCode, "");
            } else {
                oldCode = oldCode.replace(newCode + ",", "");
            }

            try {
                if (operateObject == 0) {
                    _fkPersonAttrDao.updateIcCard(personId, oldCode);
                } else if (operateObject == 1) {
                    _fkPersonAttrDao.updateMacAddress(personId, oldCode);
                }
            } catch (Exception e) {
                return new JsonObject("删除" + objectName + "异常，请稍候再试！", 1001);
            }
            return new JsonObject("删除" + objectName + "成功", 0);

        } else {
            return new JsonObject("操作type只能为0或者1.0-add,1-delete", 0);
        }

    }

    @RequestMapping(value = "/getIcCardOrMacAddress/type/{type}/personId/{personId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the fk person 's icCard or Mac Address  is successfully get or not.")
    public JsonObject getIcOrMacAddress(@PathVariable("type") int type, @PathVariable("personId") long personId) {

        String result = "";
        if (type == 1) {
            result = _fkPersonAttrDao.getIcCard(personId);
        } else if (type == 2) {
            result = _fkPersonAttrDao.getMacAddress(personId);
        } else {
            return new JsonObject("type值有误。type为1时查询该用户的交通卡.type为2时查询该用户的mac地址", 1001);
        }

        return new JsonObject(result);

    }

    // 获取库类型的数据字典列表
    @RequestMapping(value = "/fkBank/{type}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list of the fk bank.")
    public JsonObject getFkBkBankType(@PathVariable("type") int type) {

        return new JsonObject(getBkFkBank(type));

    }

    // 获取反恐库列表
    @RequestMapping(value = "/getFkBankId", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response the list of the fk bank.")
    public JsonObject getFkBankId() {

        List<BlackBank> fkBankList = blackbankDao.findFkBankList("fk_");
        return new JsonObject(fkBankList);

    }

    public ArrayList<FkBkBank> getBkFkBank(@PathVariable("type") int type) {

        // 0 1 2 分别对应 全部 查询库 布控库
        ArrayList<FkBkBank> fkBank = (ArrayList<FkBkBank>) this._fkBkBankDao.findAll();
        ArrayList<FkBkBank> fkCxOrBkBank = new ArrayList<FkBkBank>();

        for (int i = 0; i < fkBank.size(); i++) {
            FkBkBank fkbk = fkBank.get(i);

            if (type == 0) {
                return fkBank;
            } else if (type == 1) {
                if (fkbk.getBankno() > 10 && fkbk.getBankno() < 15) {
                    String bankName = fkbk.getFullName().split("-")[1];
                    fkbk.setFullName(bankName);
                    fkCxOrBkBank.add(fkbk);
                }
            } else if (type == 2) {
                if (fkbk.getBankno() > 20 && fkbk.getBankno() < 25) {
                    String bankName = fkbk.getFullName().split("-")[1];
                    fkbk.setFullName(bankName);
                    fkCxOrBkBank.add(fkbk);
                }
            }

        }
        return fkCxOrBkBank;

    }

    public long creatFkBankByInstitutionCode(long userPoliceStationId,String subInstitutionCode, String localInstitutionCode) {

        List<FkInstitutionCode> subInstitutionList = fkInstitutionCodeDao.findInstitionByCode(subInstitutionCode);
        if (CollectionUtils.isEmpty(subInstitutionList)) {
            return 0;
        }
        String subInstitutionName = subInstitutionList.get(0).getJGMC();
        List<FkInstitutionCode> localInstitutionList = fkInstitutionCodeDao.findInstitionByCode(localInstitutionCode);
        if (CollectionUtils.isEmpty(localInstitutionList)) {
            return 0;
        }
        String localInstitutionName = localInstitutionList.get(0).getJGMC();
        BlackBank fkBank = new BlackBank();
        fkBank.setBankName("fk_" + subInstitutionName + "_" + localInstitutionName);
        fkBank.setListType(0);
        fkBank.setBankDescription("反恐库");
        fkBank.setUrl("");
        fkBank.setStationId(CurUserInfoUtil.getUserInfo().getPoliceStationId());
        fkBank.setCreateUser(CurUserInfoUtil.getUserInfo().getLogin());
        BlackBank b = blackbankDao.save(fkBank);
             
       authorizeSubStaion(userPoliceStationId,b.getId(),subInstitutionName,localInstitutionName);

        return b.getId();

    }


    /*// 每新建一个库  依次遍历单位 授予库权限
    public void authorizeSubStaion(long userPoliceStationId,long bankId,String subInstitutionName,String localInstitutionName) {
   
        boolean flag = false;
        // 创建反恐库的账号所在单位不管符合分局所的概念与否 也应该赋予权限
        
        // 1.市局用户  直接授权  2.市局-非分局用户 直接授权  3.市局-分局  分局和库同 才授权  4.市局-分局-非所  分局和库同就授权   5.市局-分局-所  分局和所都与库同就授权
        List<UserInfo> allUsers = (List<UserInfo>) userRepository.findAll();
        for(int i=0;i<allUsers.size();i++){
            UserInfo user = allUsers.get(i);
            long stationId = user.getPoliceStationId();
            PoliceStation policeStation = _policeStationDao.findOne(stationId);
            String policeName = policeStation.getStationName();
            long parentId = policeStation.getParentId(); 
            PoliceStation parentStation = null; 
            String parentName = "";
            long grandParentId = 0;
            PoliceStation grandParentStation = null; 
            String grandParentName = "";
            if(parentId!=0){
                parentStation = _policeStationDao.findOne(parentId);
                parentName = parentStation.getStationName(); 
                grandParentId = parentStation.getParentId();
            }
            if(grandParentId!=0){
                grandParentStation = _policeStationDao.findOne(grandParentId);
                grandParentName = grandParentStation.getStationName();
            }         
            
            if(policeName.equals("深圳市公安局")||stationId==userPoliceStationId){
                flag = true;
            }else if(!parentName.isEmpty()&&!policeName.substring(policeName.length()-2, policeName.length()).equals("分局")&&parentName.equals("深圳市公安局")){
                flag = true;
            }else if(!parentName.isEmpty()&&policeName.substring(policeName.length()-2, policeName.length()).equals("分局")&&parentName.substring(parentName.length()-6, parentName.length()).equals("深圳市公安局")&&policeName.contains(subInstitutionName)){
                flag = true;
            }else if(!grandParentName.isEmpty()&&!policeName.substring(policeName.length()-3, policeName.length()).equals("派出所")&&parentName.substring(parentName.length()-2, parentName.length()).equals("分局")&&grandParentName.equals("深圳市公安局")&&parentName.contains(subInstitutionName)){
                flag = true;         
            }else if(!grandParentName.isEmpty()&&policeName.substring(policeName.length()-3, policeName.length()).equals("派出所")&&parentName.substring(parentName.length()-2, parentName.length()).equals("分局")&&grandParentName.equals("深圳市公安局")&&parentName.contains(subInstitutionName)&&policeName.contains(localInstitutionName)){
                flag = true;
            }
            List<PoliceStationAuthority> authority = policeStationAuthorityRepository.findByStationIdAndBankId(user.getPoliceStationId(), bankId);
            if(!authority.isEmpty()){
                flag = false;
            }
            if(flag){
                policeStationAuthorityRepository.save(new PoliceStationAuthority(user.getPoliceStationId(), bankId, GlobalConsts.CONTROL_AUTORITY_TYPE));
            }
            flag = false;
        }

    }*/
 
   
  // 每新建一个库  依次遍历单位 授予库权限
   public void authorizeSubStaion(long userPoliceStationId,long bankId,String subInstitutionName,String localInstitutionName) {
   
        boolean flag = false;
        boolean flagAuthority = false;
        // 创建反恐库的账号所在单位不管符合分局所的概念与否 也应该赋予权限
        
        // 1.市局单位  直接授权  2.市局-非分局单位  直接授权  3.市局-分局  分局和库同 才授权  4.市局-分局-非所  分局和库同就授权   5.市局-分局-所  分局和所都与库同就授权
        List<PoliceStation> allStations = (List<PoliceStation>) _policeStationDao.findAll();
        for(int i=0;i<allStations.size();i++){            
            long stationId = allStations.get(i).getId();            
            PoliceStation policeStation = _policeStationDao.findOne(stationId);
            String policeName = policeStation.getStationName();
            long parentId = policeStation.getParentId(); 
            PoliceStation parentStation = null; 
            String parentName = "";
            long grandParentId = 0;
            PoliceStation grandParentStation = null; 
            String grandParentName = "";
            try{
            if(parentId!=0){
                parentStation = _policeStationDao.findOne(parentId);
                parentName = parentStation.getStationName(); 
                grandParentId = parentStation.getParentId();
            }
            if(grandParentId!=0){
                grandParentStation = _policeStationDao.findOne(grandParentId);
                grandParentName = grandParentStation.getStationName();

            }              

                if(stationId==userPoliceStationId){
                    flagAuthority = true;
                }else if(policeName.equals("深圳市公安局")){
                    flag = true;
                }else if(!StringUtils.isEmpty(parentName)&&(policeName.length()>2)&&!policeName.substring(policeName.length()-2, policeName.length()).equals("分局")&&parentName.equals("深圳市公安局")){
                    flag = true;
                }else if(!StringUtils.isEmpty(parentName)&&(policeName.length()>2)&&policeName.substring(policeName.length()-2, policeName.length()).equals("分局")&&(parentName.length()>=6)&&parentName.substring(parentName.length()-6, parentName.length()).equals("深圳市公安局")&&(policeName.contains(subInstitutionName)||policeName.contains(localInstitutionName))){
                    flag = true;
                }else if(!StringUtils.isEmpty(grandParentName)&&(policeName.length()>3)&&!policeName.substring(policeName.length()-3, policeName.length()).equals("派出所")&&(parentName.length()>2)&&parentName.substring(parentName.length()-2, parentName.length()).equals("分局")&&grandParentName.equals("深圳市公安局")&&parentName.contains(subInstitutionName)){
                    flag = true;         
                }else if(!StringUtils.isEmpty(grandParentName)&&(policeName.length()>3)&&policeName.substring(policeName.length()-3, policeName.length()).equals("派出所")&&(parentName.length()>2)&&parentName.substring(parentName.length()-2, parentName.length()).equals("分局")&&grandParentName.equals("深圳市公安局")&&parentName.contains(subInstitutionName)&&policeName.contains(localInstitutionName)){
                    flag = true;
                }
                 }catch(Exception e){
                    System.out.println("fk_bank_authority exception:station_id"+stationId);
                }
            List<PoliceStationAuthority> authority = policeStationAuthorityRepository.findByStationIdAndBankId(stationId, bankId);
            if(!CollectionUtils.isEmpty(authority)){
                flag = false;
            }
            if(flag){
                policeStationAuthorityRepository.save(new PoliceStationAuthority(stationId, bankId, GlobalConsts.UPDATE_AUTORITY_TYPE));
            }
            if(flagAuthority){
                policeStationAuthorityRepository.save(new PoliceStationAuthority(stationId, bankId, GlobalConsts.CONTROL_AUTORITY_TYPE));
            }
            flag = false;
            flagAuthority = false;
        }

    }
   
   
    
    public List<FkPersonResultDto> filterByFkBankAuthority(List<FkPersonResultDto> fkPersonList,String bankIdAutority){
        
        List<FkPersonResultDto> list = new ArrayList<FkPersonResultDto>();
        for(int i=0;i<fkPersonList.size();i++){
            long bankId = fkPersonList.get(i).getBankId();
            if(bankIdAutority.indexOf("," + bankId + ",") >= 0) { 
                list.add(fkPersonList.get(i));
                }
        }
        return list;
    }
    
    public String listToString(List<BigInteger> list){
       String s = "";
       for(int i=0;i<list.size();i++){
           if(StringUtils.isEmpty(s)){
               s = s + list.get(i);
           }else{
               s = s + "," +list.get(i);
           }
       }
        return s;
    }
    
    public Set<Long> findAreaOfCameras(){
                                        
            Tree tree = LocalCache.tree;    
            Set<Long> cameraSet = tree.idSet(CameraInfo.class);  
            Set<Long> areaSet = new HashSet<Long>();
            for(Long camera : cameraSet){
               CameraInfo cameraInfo = tree.treeNode(CameraInfo.class, camera);
               areaSet.add(cameraInfo.getStationId());
            }
            return areaSet;
       
        
    } 

}