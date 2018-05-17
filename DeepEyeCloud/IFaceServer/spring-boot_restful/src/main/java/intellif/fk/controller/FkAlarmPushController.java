package intellif.fk.controller;

import intellif.consts.GlobalConsts;
import intellif.dao.AlarmInfoDao;
import intellif.dao.AreaDao;
import intellif.dao.CameraInfoDao;
import intellif.dao.DistrictDao;
import intellif.dao.PersonDetailDao;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.dto.JsonObject;
import intellif.fk.dao.FkAlarmPushDao;
import intellif.fk.dao.FkInstitutionCodeDao;
import intellif.fk.dao.FkPersonAttrDao;
import intellif.fk.dto.FkAlarmPushDto;
import intellif.fk.settings.FKLoginSettings;
import intellif.fk.vo.FKPersonInfo;
import intellif.fk.vo.FkAlarmPushLog;
import intellif.fk.vo.FkInstitutionCode;
import intellif.fk.vo.FkPersonAttr;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.service.PersonDetailServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.HttpUtil;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.DistrictInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.TaskInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_FK_ALARM_PUSH)
public class FkAlarmPushController {

    private final static String format = "yyyy-MM-dd HH:mm:ss";
    private static Logger LOG = LogManager.getLogger(FkAlarmPushController.class);

    @Autowired
    private FkPersonAttrDao fkPersonAttrDao;
    @Autowired
    private CameraInfoDao cameraInfoDao;
    @Autowired
    private ImageServiceItf imageServiceItf;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private PersonDetailServiceItf personDetailService;
    @Autowired
    private FaceServiceItf faceServiceItf;
    @Autowired
    private AlarmInfoDao alarmInfoDao;
    @Autowired
    private FkAlarmPushDao fkAlarmPushDao;    
    @Autowired
    private DistrictDao districtDao;
    @Autowired
    private FkInstitutionCodeDao fkInstitutionCodeDao;
    

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the fk person detail is successfully created or not.")
    public JsonObject pushFkAlarm(@RequestBody @Valid FkAlarmPushDto fkAlarmPushDto) throws Exception {
        
        List<FKPersonInfo> fKPersonInfo = getFKPersonInfo(fkAlarmPushDto);
        if(CollectionUtils.isEmpty(fKPersonInfo)){
            return new JsonObject("反恐人员为空。。。",1001);
        }
        String imageUrl="";

        FKPersonInfo fkPerson = fKPersonInfo.get(0);
        imageUrl = fkPerson.getLargerMap();
        
        FkAlarmPushLog pushLog = new FkAlarmPushLog();
        pushLog.setImage(imageUrl);
        pushLog.setNotes(" alarmId:"+fkAlarmPushDto.getAlarmId()+" personId:"+fkAlarmPushDto.getPersonId()+" faceId:"+fkAlarmPushDto.getFaceId()+" notes:"+fkAlarmPushDto.getNotes());
        pushLog.setTime(new Date());
        pushLog.setUser(CurUserInfoUtil.getUserInfo().getLogin());
        
        try{
        String loginUrl = FKLoginSettings.getLoginUrl();
        String tokenUrl = FKLoginSettings.getTokenUrl();
        String alarmUrl = FKLoginSettings.getAlarmUrl();
        String applicationId = FKLoginSettings.getApplicationId();
        
        String content =  HttpUtil.fkGet(tokenUrl);
       
        JSONObject jsonObject = JSONObject.fromObject(content);
        String token = "";
        if (jsonObject.getString("token") != null) {
            token = jsonObject.getString("token");
        }      
        String result = toString(fkPerson,alarmUrl,token,applicationId);
        LOG.info(result);   
        
        }catch(Exception e){
            LOG.error(e.getStackTrace());
            pushLog.setResult("failed");
            fkAlarmPushDao.save(pushLog);
            return new JsonObject("推送失败.。。",1001); 
            
        }
             
        pushLog.setResult("success");
        fkAlarmPushDao.save(pushLog);
        return new JsonObject("已推送.。。",0);
    }

    
    private String toString(FKPersonInfo personInfo,String url,String token,String applicationId){
        Map<String, Object> map = new HashMap<>();
        map.put("certId", personInfo.getCertId());
        map.put("name", personInfo.getName());
        map.put("nation", personInfo.getNation());
        map.put("branchCode", personInfo.getBranchCode());
        map.put("stationCode", personInfo.getStationCode());
        map.put("fkType", personInfo.getFkType());
        map.put("largerMap", personInfo.getLargerMap());
        map.put("smallMap", personInfo.getSmallMap());
        map.put("areaCode", personInfo.getAreaCode());
        map.put("address", personInfo.getAddress());
        map.put("lomgitude", personInfo.getLomgitude());
        map.put("dimensionality", personInfo.getDimensionality());
        map.put("pliceCode", personInfo.getPoliceCode());
        map.put("officeCode", personInfo.getOfficeCode());
        map.put("startDate", personInfo.getStartDate());
        map.put("endDate", personInfo.getEndDate());
        map.put("noteInfo", personInfo.getNotes());
        map.put("nationality",personInfo.getNationality());
        map.put("rect", personInfo.getRect());
        String result = HttpUtil.fkPost(url, HttpUtil.toString(map),token,applicationId);
        return result;
    }
    
    private List<FKPersonInfo> getFKPersonInfo(FkAlarmPushDto fkAlarmPushDto){
        long alarmId = fkAlarmPushDto.getAlarmId();
        AlarmInfo alarm = alarmInfoDao.findOne(alarmId);
        long taskId = 0;
        if(alarm!=null){
            taskId = alarm.getTaskId();  
        }
        CameraInfo cameraInfo = cameraInfoDao.findByTaskId(taskId).get(0);
        Area area = areaDao.findOne(cameraInfo.getStationId());
        String areaName = area.getAreaName();
        DistrictInfo district = districtDao.findOne(area.getDistrictId());
        String districtName = district.getDistrictName();
        FaceInfo face = this.faceServiceItf.findOne(fkAlarmPushDto.getFaceId());
        String rect = face.getJson();
        ImageInfo imageInfo = null;
        if (null != face) {
           imageInfo = imageServiceItf.findById(face.getFromImageId());
        }
        FKPersonInfo fkPersonInfo = new FKPersonInfo();
        PersonDetail personDetail = (PersonDetail) personDetailService.findById(fkAlarmPushDto.getPersonId());
        List<FkPersonAttr> fkPersonAttrList = fkPersonAttrDao.findByFromPersonId(fkAlarmPushDto.getPersonId());
        List<FKPersonInfo> fkPersonInfoList = new ArrayList<>();
        for(FkPersonAttr fkPersonAttr : fkPersonAttrList){
            fkPersonInfo.setAddress(cameraInfo.getAddr());
            fkPersonInfo.setAreaCode(area.getAreaNo());
           // fkPersonInfo.setBranchCode(String.valueOf(fkPersonAttr.getAreaId()));
           // fkPersonInfo.setStationCode(String.valueOf(fkPersonAttr.getDistricId()));
            fkPersonInfo.setBranchCode(fkPersonAttr.getFkSubInstitutionCode());
            fkPersonInfo.setStationCode(fkPersonAttr.getFkLocalInstitutionCode());
            fkPersonInfo.setCertId(personDetail.getCid());
            fkPersonInfo.setFkType(String.valueOf(personDetail.getFkType()));
            fkPersonInfo.setName(personDetail.getRealName());
            fkPersonInfo.setNation(personDetail.getNation());
            fkPersonInfo.setStartDate(String.valueOf(face.getTime()));
            fkPersonInfo.setEndDate(String.valueOf(face.getTime()));
           /* fkPersonInfo.setPoliceCode(area.getAreaNo());
            fkPersonInfo.setOfficeCode(String.valueOf(area.getDistrictId()));*/
            List<FkInstitutionCode> alarmFaceSubInstitutionList = fkInstitutionCodeDao.findInstitionByName(districtName);
            List<FkInstitutionCode> alarmFaceLocalInstitutionList = fkInstitutionCodeDao.findInstitionByName(areaName);
            List<FkInstitutionCode> all = fkInstitutionCodeDao.findInstitionByName("深圳市公安局");
            if(CollectionUtils.isEmpty(all)){
                LOG.error("error:深圳市公安局机构代码为空");
                continue;
            }            
            if(CollectionUtils.isEmpty(alarmFaceSubInstitutionList)){
                fkPersonInfo.setPoliceCode(all.get(0).getJGDM());
            }else{
                fkPersonInfo.setPoliceCode(alarmFaceSubInstitutionList.get(0).getJGDM());
            }
            if(CollectionUtils.isEmpty(alarmFaceLocalInstitutionList)){
                fkPersonInfo.setOfficeCode(all.get(0).getJGDM());
            }else{
                fkPersonInfo.setOfficeCode(alarmFaceLocalInstitutionList.get(0).getJGDM());
            }
           
            String largerMap = imageInfo.getUri();
            String smallMap = face.getImageData();
            String lomgitude = "";
            String dimensionality = "";
            if (cameraInfo.getGeoString() != null&&!cameraInfo.getGeoString().trim().equals("")) {
                String geoString = cameraInfo.getGeoString();
                geoString = geoString.substring(6, geoString.length()-1);
                lomgitude = geoString.split(" ")[0];
                dimensionality = geoString.split(" ")[1];
            }
            fkPersonInfo.setLargerMap(largerMap);
            fkPersonInfo.setSmallMap(smallMap);
            fkPersonInfo.setLomgitude(lomgitude);
            fkPersonInfo.setDimensionality(dimensionality);
            fkPersonInfo.setRect(rect);
            
            fkPersonInfo.setNotes(fkAlarmPushDto.getNotes());
            fkPersonInfo.setNationality(fkPersonAttr.getNationality());
            
            fkPersonInfoList.add(fkPersonInfo);
            
        }
        return fkPersonInfoList;
    }
 

}