package intellif.mqtt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.eventbus.Subscribe;

import intellif.dao.AreaDao;
import intellif.dao.CameraInfoDao;
import intellif.dao.PersonDetailDao;
import intellif.dto.MqttInfoDto;
import intellif.enums.AlarmStatus;
import intellif.events.MqttMessageEvent;
import intellif.fk.dao.FkPersonAttrDao;
import intellif.fk.settings.FKLoginSettings;
import intellif.fk.vo.FKPersonInfo;
import intellif.fk.vo.FkPersonAttr;
import intellif.service.AlarmServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.utils.HttpUtil;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.PersonDetail;
import net.sf.json.JSONObject;

@Component
@Configurable
public class MqttAlarmEBSubscriber {

    private static Logger LOG = LogManager
            .getLogger(MqttAlarmEBSubscriber.class);

    @Autowired
    private AlarmServiceItf alarmServiceItf;
    @Autowired
    private PersonDetailDao personDetailDao;
    @Autowired
    private FkPersonAttrDao fkPersonAttrDao;
    @Autowired
    private CameraInfoDao cameraInfoDao;
    @Autowired
    private FaceServiceItf faceServiceItf;
    @Autowired
    private ImageServiceItf imageServiceItf;
    @Autowired
    private AreaDao areaDao;
    
    public void setAlarmServiceItf(AlarmServiceItf alarmServiceItf) {
        this.alarmServiceItf = alarmServiceItf;
    }

    @Subscribe
    public void onEvent(MqttMessageEvent event) {
        // Handle the string passed on by the Event Bus
        LOG.info("onEvent:" + event);
        //
        String jsonString = event.getMessage();
        ObjectMapper objectMapper = new ObjectMapper();
        //
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 单引号处理
        objectMapper
                .configure(
                        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                        true);
        JsonNode node = null;
        MqttInfoDto mqttInfoDto = new MqttInfoDto();
        try {
            node = objectMapper.readTree(jsonString);
            LOG.info("objectMapper.readTree:" + node.toString());
        } catch (Exception e) {
            LOG.error(e.toString());
            return ; //解决下面的node空指针的隐患
        }
        
        int alarmType = Integer.valueOf(node.get("AlarmType").toString());
        if ( alarmType == -1) {
            return;
        }
        mqttInfoDto.setAlarmType(node.get("AlarmType").toString());  //find bugs  possible null pointer dereference in method on exception path
        mqttInfoDto.setBlackId(node.get("BlackId").toString());
        mqttInfoDto.setConfidence(node.get("Confidence").toString());
        mqttInfoDto.setFaceId(node.get("FaceId").toString());
        mqttInfoDto.setTaskId(node.get("TaskId").toString());
        mqttInfoDto.setTime(node.get("Time").toString());
        mqttInfoDto.setPersonId(node.get("PersonId").toString());
        LOG.info("readValueFromJson(Time):" + node.get("Time").toString());//
        //
        AlarmInfo alarm = new AlarmInfo();
        alarm.setTaskId(Long.valueOf(mqttInfoDto.getTaskId()));
        alarm.setBlackId(Long.valueOf(mqttInfoDto.getBlackId()));
        alarm.setFaceId(Long.valueOf(mqttInfoDto.getFaceId()));
        alarm.setConfidence(Double.valueOf(mqttInfoDto.getConfidence()));
        alarm.setLevel(Integer.parseInt(mqttInfoDto.getAlarmType()));
        DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");//2015-07-16 19:08:31
        Date dateTime = null;
        try {
            String rawDateTime = mqttInfoDto.getTime().replace("\"", "");
            LOG.info("raw(Time):" + rawDateTime);//
            dateTime = format.parse(rawDateTime);//Replace the "" characters.
            //
            LOG.info("SimpleDataFormat parsed:" + dateTime);//
        } catch (ParseException e) {
            LOG.error("", e);
        }
        alarm.setTime(dateTime);
        alarm.setStatus(AlarmStatus.IGNORE.getValue());
        
        LOG.info("alarmServiceItf(onEvent):" + alarmServiceItf.toString());
//        alarmServiceItf.getDao().save(alarm);
        //
       /* List<FKPersonInfo> personInfoList = getFKPersonInfo(mqttInfoDto);
        String loginUrl = FKLoginSettings.getLoginUrl();
        String tokenUrl = FKLoginSettings.getTokenUrl();
        String alarmUrl = FKLoginSettings.getAlarmUrl();
        String applicationId = FKLoginSettings.getApplicationId();
        
        HttpUtil.fkGet(loginUrl);
        String content =  HttpUtil.fkGet(tokenUrl);
        JSONObject jsonObject = JSONObject.fromObject(content);
        String token = "";
        if (jsonObject.getString("token") != null) {
            token = jsonObject.getString("token");
        }
        for(FKPersonInfo personInfo : personInfoList){
            String result = toString(personInfo,alarmUrl,token,applicationId);
            LOG.info(result);
        }*/
    }
    
   /* private String toString(FKPersonInfo personInfo,String url,String token,String applicationId){
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
        String result = HttpUtil.fkPost(url, HttpUtil.toString(map),token,applicationId);
        return result;
    }
    
    private List<FKPersonInfo> getFKPersonInfo(MqttInfoDto mqttInfoDto){
        CameraInfo cameraInfo = cameraInfoDao.findByTaskId(Long.valueOf(mqttInfoDto.getTaskId())).get(0);
        Area area = areaDao.findOne(cameraInfo.getStationId());
        FaceInfo face = this.faceServiceItf.findOne(Long.parseLong(mqttInfoDto.getFaceId()));
        ImageInfo imageInfo = null;
        if (null != face) {
           imageInfo = imageServiceItf.findById(face.getFromImageId());
        }
        FKPersonInfo fkPersonInfo = new FKPersonInfo();
        PersonDetail personDetail = personDetailDao.findOne(Long.parseLong(mqttInfoDto.getPersonId()));
        List<FkPersonAttr> fkPersonAttrList = fkPersonAttrDao.findByFromPersonId(Long.parseLong(mqttInfoDto.getPersonId()));
        List<FKPersonInfo> fkPersonInfoList = new ArrayList<>();
        for(FkPersonAttr fkPersonAttr : fkPersonAttrList){
            fkPersonInfo.setAddress(cameraInfo.getAddr());
            fkPersonInfo.setAreaCode(area.getAreaNo());
            fkPersonInfo.setBranchCode(String.valueOf(fkPersonAttr.getAreaId()));
            fkPersonInfo.setStationCode(String.valueOf(fkPersonAttr.getDistricId()));
            fkPersonInfo.setCertId(personDetail.getCid());
            fkPersonInfo.setFkType(String.valueOf(personDetail.getFkType()));
            fkPersonInfo.setName(personDetail.getRealName());
            fkPersonInfo.setNation(personDetail.getNation());
            fkPersonInfo.setStartDate(String.valueOf(face.getTime()));
            fkPersonInfo.setEndDate(String.valueOf(face.getTime()));
            fkPersonInfo.setPoliceCode(area.getAreaNo());
            fkPersonInfo.setOfficeCode(String.valueOf(area.getDistrictId()));
            String largerMap = imageInfo.getUri();
            String smallMap = face.getImageData();
            String lomgitude = "";
            String dimensionality = "";
            if (cameraInfo.getGeoString() != null) {
                String geoString = cameraInfo.getGeoString();
                geoString = geoString.substring(6, geoString.length()-1);
                lomgitude = geoString.split(" ")[0];
                dimensionality = geoString.split(" ")[1];
            }
            fkPersonInfo.setLargerMap(largerMap);
            fkPersonInfo.setSmallMap(smallMap);
            fkPersonInfo.setLomgitude(lomgitude);
            fkPersonInfo.setDimensionality(dimensionality);
            fkPersonInfoList.add(fkPersonInfo);
            
        }
        return fkPersonInfoList;
    }*/

}