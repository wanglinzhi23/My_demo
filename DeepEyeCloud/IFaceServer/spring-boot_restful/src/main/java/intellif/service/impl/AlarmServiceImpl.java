package intellif.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import intellif.service.FaceServiceItf;
import intellif.utils.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import intellif.consts.GlobalConsts;
import intellif.dao.PoliceStationAuthorityDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.database.dao.AlarmInfoDao;
import intellif.database.dao.PersonDetailDao;
import intellif.database.dao.impl.AlarmInfoDaoImpl;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.UserInfo;
import intellif.dto.AlarmInfoDto;
import intellif.dto.AlarmQueryDto;
import intellif.dto.AlarmStatisticByStationDto;
import intellif.dto.AlarmStatisticDto;
import intellif.dto.CameraDto;
import intellif.dto.EventsByStationIdKey;
import intellif.dto.QueryInfoDto;
import intellif.fk.dao.FkBkBankDao;
import intellif.fk.dao.FkInstitutionCodeDao;
import intellif.fk.vo.FkBkBank;
import intellif.fk.vo.FkInstitutionCode;
import intellif.service.AlarmServiceItf;
import intellif.service.CameraServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.PerformParamSetting;
import intellif.database.entity.AlarmImageInfo;
import intellif.database.entity.EventInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.PoliceStationAuthority;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@Service
public class AlarmServiceImpl  extends AbstractCommonServiceImpl<AlarmInfo> implements AlarmServiceItf<AlarmInfo> {

    private static Logger LOG = LogManager.getLogger(AlarmServiceImpl.class);

    @PersistenceContext
    EntityManager entityManager;
    //
    @Autowired
    AlarmInfoDao alarmInfoDao;
    @Autowired
    PersonDetailDao personDetailDao;
    @Autowired
    UserServiceItf _userService;

    @Autowired
    PoliceStationAuthorityDao policeStationAuthorityRepository;

    @Autowired
    FaceInfoDaoImpl faceInfoDao;

    @Autowired
    ImageInfoDaoImpl imageInfoDao;

    @Autowired
    CameraServiceItf cameraServiceItf;

    @Autowired
    ZoneAuthorizeServiceItf zoneAuthorizeService;

    @Autowired
    private FkBkBankDao _fkBkBankDao;
    
    @Autowired
    private FkInstitutionCodeDao fkInstitutionCodeDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FaceServiceItf faceService;

    @PostConstruct
    public void init() {
        System.out.println("AlarmServiceImpl from @service");
    }

    // @PersistenceContext
    // EntityManager em;
    // @Autowired
    // public AlarmServiceImpl(AlarmRepository alarmRepository)
    // {
    // this.alarmRepository = alarmRepository;
    // }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlarmInfoDto> findByCombinedConditions(AlarmInfoDto alarmInfoDto) {
        List<AlarmInfoDto> resp = null;
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("county");
        cameraFields.add("name");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "b");
        String cameraStatement = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, "AND b.id in");
        try {
            String sqlString = "SELECT a.id, a.level, a.confidence,a.face_id as alarm_face_id,  b.county, b.name as camera_name, g.real_name as black_name,"
                    + " d.bank_name as bank_name,d.list_type, a.time as alarm_time, a.status as alarm_status, c.image_data as black_image_data,c.from_image_id as black_image_id,"
                    + " g.description as black_description, g.type, f.task_name, 0 as face_image_data,0 as face_big_image_uri,0 as black_big_image_uri "
                    + "FROM "
                    + GlobalConsts.INTELLIF_BASE
                    + "."
                    + GlobalConsts.T_NAME_ALARM_INFO
                    + " a, "
                    + cameraSql
                    + ", "
                    + GlobalConsts.INTELLIF_BASE
                    + "."
                    + GlobalConsts.T_NAME_BLACK_DETAIL
                    + " c, "
                    + GlobalConsts.INTELLIF_BASE
                    + "."
                    + GlobalConsts.T_NAME_BLACK_BANK
                    + " d, "
                    + GlobalConsts.INTELLIF_BASE
                    + "."
                    + GlobalConsts.T_NAME_TASK_INFO
                    + " f, "
                    + GlobalConsts.INTELLIF_BASE
                    + "."
                    + GlobalConsts.T_NAME_PERSON_DETAIL
                    + " g "
                    + "WHERE a.task_id = f.id and a.black_id = c.id and c.bank_id = d.id "
                    + "and c.from_person_id = g.id and f.source_id = b.id and g.is_urgent = 0 ";

            sqlString = _userService.processAuthority(sqlString);
            // support for findOne.
            if (0 < alarmInfoDto.getId()) {
                sqlString += "AND a.id=" + alarmInfoDto.getId();
            } else {

                if (!"".equals(alarmInfoDto.getTaskName())) {
                    sqlString += "AND f.task_name LIKE '%" + alarmInfoDto.getTaskName() + "%' ";
                }
                if (!"".equals(alarmInfoDto.getBlackName())) {
                    sqlString += "AND g.real_name LIKE '%" + alarmInfoDto.getBlackName() + "%' ";
                }
                if (null != alarmInfoDto.getCameraName() && !"".equals(alarmInfoDto.getCameraName())) {
                    sqlString += "AND b.name ='" + alarmInfoDto.getCameraName() + "' ";
                }
                if (null != alarmInfoDto.getCounty() && !"".equals(alarmInfoDto.getCounty())) {
                    sqlString += "AND b.county = '" + alarmInfoDto.getCounty() + "' ";
                }
                if (!"全部".equals(alarmInfoDto.getBankName()) && !"".equals(alarmInfoDto.getBankName())) {
                    sqlString += "AND d.bank_name" + "" + "" + "" + " = '" + alarmInfoDto.getBankName() + "' ";
                }
                if (alarmInfoDto.getAlarmStatus() < 3) {
                    sqlString += "AND a.status = " + alarmInfoDto.getAlarmStatus() + " ";
                }
                if (alarmInfoDto.getLevel() < 3) {
                    sqlString += "AND a.level = " + alarmInfoDto.getLevel() + " ";
                }
                String startTime = alarmInfoDto.getStartTime();
                String endTime = alarmInfoDto.getEndTime();

                if (null != startTime && !"".equals(startTime) && null != endTime && !"".equals(endTime)) {
                    sqlString += "AND a.time >= '" + startTime + "' AND a.time <'" + endTime + "' ";
                }
                sqlString += cameraStatement;
                sqlString += " ORDER BY a.time DESC";

            }

            // @see:
            // http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist

            Query query = this.entityManager.createNativeQuery(sqlString, AlarmInfoDto.class);
            resp = (ArrayList<AlarmInfoDto>) query.getResultList();
            // 根据alarmFaceId和blackImageId查询对应face table url路径
            updateFaceInfoToAlarmInfo(resp);

        } catch (Exception e) {
            LOG.error("findByCombinedConditions method error:", e);
        } finally {
            entityManager.close();
        }

        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlarmStatisticDto> findByCountAndBetweenWeek()// group in a day
    {
        List<AlarmStatisticDto> resp = null;
        //
        String sqlString = "SELECT a.id,a.level,DATE_FORMAT(a.time, '%Y-%m-%d') AS TIME,COUNT(*) AS alarms FROM "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_ALARM_INFO
                + " a "
                + "WHERE a.time BETWEEN DATE_ADD(CURDATE(), INTERVAL -6 DAY) AND DATE_ADD(CURDATE(), INTERVAL 1 DAY) GROUP BY a.level,DATE_FORMAT(a.time, '%Y-%m-%d')";
        // @see:
        // http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, AlarmStatisticDto.class);
            resp = (ArrayList<AlarmStatisticDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("findByCountAndBetweenWeek method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlarmStatisticByStationDto> statisticByPoliceStation()// group
                                                                      // in a
                                                                      // day
    {   
        List<AlarmStatisticByStationDto> resp = null;
        String sqlString = "SELECT d.id ,d.area_name,COUNT(*) AS num FROM "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_ALARM_INFO
                + " a, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_TASK_INFO
                + " b, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_CAMERA_INFO
                + " c, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_AREA
                + " d "
                + "WHERE a.time BETWEEN DATE_ADD(CURDATE(), INTERVAL -6 DAY) AND DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND a.task_id = b.id AND b.source_id = c.id AND c.station_id = d.id GROUP BY d.id;";
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, AlarmStatisticByStationDto.class);
            resp = (ArrayList<AlarmStatisticByStationDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("statisticByPoliceStation method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlarmInfoDto> findByBlackDetailId(long id) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("name");
        cameraFields.add("county");
        cameraFields.add("id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "b");
        List<AlarmInfoDto> resp = null;
        String cameraStatement = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, " and b.id in");
        String sqlString = "SELECT a.id, a.level, a.confidence,a.face_id as alarm_face_id, b.county, b.name as camera_name,"
                + " g.real_name as black_name, g.type, d.bank_name as bank_name,d.list_type, a.time as alarm_time,"
                + " a.status as alarm_status, c.image_data as black_image_data,c.from_image_id as black_image_id,"
                + " 0 as face_image_data, 0 as face_big_image_uri, 0 as black_big_image_uri, " + "c.black_description, f.task_name " + "FROM "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_ALARM_INFO
                + " a, "          
                + cameraSql
                + ", "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_BLACK_DETAIL
                + " c, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_BLACK_BANK
                + " d, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_TASK_INFO
                + " f, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_PERSON_DETAIL
                + " g "
                + "WHERE a.task_id = f.id and a.black_id = c.id and c.id = "
                + id
                + " and c.bank_id = d.id and c.from_person_id = g.id"
                + " and f.source_id = b.id" + cameraStatement + " ORDER BY a.time DESC";

        sqlString = _userService.processAuthority(sqlString);
        // @see:
        // http://stackoverflow.com/questions/1091489/converting-an-untyped-arraylist-to-a-typed-arraylist
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, AlarmInfoDto.class);
            resp = (ArrayList<AlarmInfoDto>) query.getResultList();

            // 根据alarmFaceId和blackImageId查询对应face table url路径
            updateFaceInfoToAlarmInfo(resp);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @Override
    public AlarmInfoDao getDao() {
        return this.alarmInfoDao;
    }
    public long userId() {
        UserInfo userInfo = CurUserInfoUtil.getUserInfo();
        Validate.notNull(userInfo, "获取登录信息失败！请您重新登录！");
        Long userId = userInfo.getId();
        Validate.notNull(userId, "获取登录信息失败！请您重新登录！");
        return userId;
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<EventInfo> findEventsByPersonId(QueryInfoDto dto) {
        List<EventInfo> resp = null;
        int page = dto.getPage();
        int pageSize = dto.getPageSize();
        float confidence = dto.getThreshold();
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("station_id");
        cameraFields.add("addr");
        cameraFields.add("geo_string");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "f");
       
        String cameraIdStr = zoneAuthorizeService.ucsqlManipulateThread(CameraInfo.class, "and f.id in",dto.getUserId());
        String sql1 = "SELECT id,status,send,face_id, person_id,confidence,image_data,scene,camera_id,camera_name,area_id,geo_string,address,time FROM ("
                + "SELECT a.id, a.status,a.send,a.face_id,b.from_person_id person_id, a.confidence confidence, 0 as image_data, 0 as scene,f.id camera_id,"
                + " f.name camera_name, f.station_id area_id, f.geo_string ,f.addr as address, a.time time FROM "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_ALARM_INFO
                + " a, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_BLACK_DETAIL
                + " b, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_TASK_INFO
                + " e, "
                +cameraSql+" where a.status in ("+dto.getStatus()+") and a.confidence >= "
                + confidence
                + " and a.black_id = b.id and a.task_id = e.id and e.source_id = f.id " + cameraIdStr + " and b.from_person_id = " + dto.getPersonId() 

                +") a order by time desc LIMIT " + (page - 1) * pageSize + "," + pageSize + "";

        //sql1 = _userService.processAuthorityByThread(sql1,userId);

        String sqlString = sql1 ;

        try {  
            resp = alarmInfoDao.getPersonEventListBySql(sqlString);
            updateFaceInfoToEventInfo(resp);
        } catch (Exception e) {
            LOG.error("findEventByPersonId  method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventInfo> findEventsByPersonIdCameraIds(long id, String cameraids, float threshold, int page, int pageSize) {
        List<EventInfo> resp = null;
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("station_id");
        cameraFields.add("geo_string");
        cameraFields.add("addr");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "f");
        List<Long> cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class, Arrays.asList(cameraids.split(",")).stream().map(m -> Long.valueOf(m))
                .collect(Collectors.toList()), null);
        String cameraIdstr = String.join(",", cameraIdList.stream().map(m -> String.valueOf(m)).toArray(String[]::new));

        String sql1 = "SELECT id,send,face_id, person_id,confidence,image_data,scene,camera_id,camera_name,area_id,geo_string,address,time FROM ("
                + "SELECT a.id, a.send,a.face_id,b.from_person_id person_id, a.confidence confidence, 0 as image_data, 0 as scene,f.id camera_id,"
                + " f.name camera_name, f.station_id area_id, f.geo_string ,f.addr as address, a.time time FROM "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_ALARM_INFO
                + " a, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_BLACK_DETAIL
                + " b, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_TASK_INFO
                + " e, "
                + cameraSql
                + " where a.confidence >= "
                + threshold
                + " and a.black_id = b.id and a.task_id = e.id and e.source_id = f.id and f.id in ("
                + cameraIdstr
                + ") and b.from_person_id = "
                + id
                + " union ";

        String sql2 = "SELECT a.id+10000000000, 0,0,a.object_id person_id, -1 confidence, a.title image_data, \"\" scene, -1 camera_id, "
                + "a.object_status camera_name, \"\" area_id, \"\" geo_string, " + "a.created time FROM " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_AUDIT_LOG + " a where a.object = '" + GlobalConsts.T_NAME_PERSON_DETAIL + "' and a.object_id = " + id
                + ") a order by time desc LIMIT " + (page - 1) * pageSize + "," + pageSize + "";

        sql1 = _userService.processAuthority(sql1);
        // sql2= _userService.processAuthority(sql2);

        String sqlString = sql1 + sql2;

        try {
            Query query = this.entityManager.createNativeQuery(sqlString, EventInfo.class);
            resp = (ArrayList<EventInfo>) query.getResultList();
            updateFaceInfoToEventInfo(resp);
        } catch (Exception e) {
            LOG.error("findEventByPersonId  method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @Override
    public List<EventInfo> findEventsByPersonIdList(long[] idlist, String cameraids, float threshold, int page, int pageSize) {
        List<EventInfo> resp = null;

        StringBuilder idStrBuilder = new StringBuilder();
        for (long id : idlist) {
            idStrBuilder.append(id);
            idStrBuilder.append(",");
        }
        String idStr = idStrBuilder.toString();
        if (idStr != null && idStr.length() > 0) {
            idStr = idStr.substring(0, idStr.length() - 1);
        }
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("station_id");
        cameraFields.add("geo_string");
        cameraFields.add("addr");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "f");
        List<Long> cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class, Arrays.asList(cameraids.split(",")).stream().map(m -> Long.valueOf(m))
                .collect(Collectors.toList()), null);
        String cameraIdstr = String.join(",", cameraIdList.stream().map(m -> String.valueOf(m)).toArray(String[]::new));

        String sql1 = "SELECT id,send,face_id, person_id,confidence,image_data,scene,camera_id,camera_name,area_id,geo_string,address,time FROM ("
                + "SELECT a.id, a.send,a.face_id,b.from_person_id person_id, a.confidence confidence, 0 as image_data, 0 as scene,f.id camera_id,"
                + " f.name camera_name, f.station_id area_id, f.geo_string ,f.addr as address a.time time FROM "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_ALARM_INFO
                + " a, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_BLACK_DETAIL
                + " b, "
                + GlobalConsts.INTELLIF_BASE
                + "."
                + GlobalConsts.T_NAME_TASK_INFO
                + " e, "
                + cameraSql + " where a.confidence >= " + threshold

                + " and a.black_id = b.id and a.task_id = e.id and f.id in (" + cameraIdstr + ") and e.source_id = f.id" +

                " and b.from_person_id in (" + idStr + ") union ";

        String sql2 = "SELECT a.id+10000000000, 0,0,a.object_id person_id, -1 confidence, a.title image_data, \"\" scene, -1 camera_id, "
                + "a.object_status camera_name, \"\" area_id, \"\" geo_string, " + "a.created time FROM " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_AUDIT_LOG + " a where a.object = '" + GlobalConsts.T_NAME_PERSON_DETAIL + "' and a.object_id in (" + idStr
                + ")) a order by time desc LIMIT " + (page - 1) * pageSize + "," + pageSize + "";

        sql1 = _userService.processAuthority(sql1);
        // sql2= _userService.processAuthority(sql2);

        String sqlString = sql1 + sql2;

        try {
            Query query = this.entityManager.createNativeQuery(sqlString, EventInfo.class);
            resp = (ArrayList<EventInfo>) query.getResultList();
            updateFaceInfoToEventInfo(resp);
        } catch (Exception e) {
            LOG.error("findEventsByPersonIdList  method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventInfo> findEventsByPersonIdAndCameras(AlarmQueryDto alarmQueryDto) {
        List<EventInfo> resp = null;
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("station_id");
        cameraFields.add("geo_string");
        cameraFields.add("addr");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "f");

        String sqlString = "SELECT a.id, a.send, a.status, a.face_id, b.from_person_id person_id, a.confidence confidence,"
                + " 0 as image_data,0 as scene,f.id camera_id, f.name camera_name, f.station_id area_id, f.geo_string ,f.addr as address, a.time time FROM "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " a, " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " e, "
                + cameraSql + " where a.confidence >= " + alarmQueryDto.getThreshold()
                + " and a.black_id = b.id and a.task_id = e.id " + "and e.source_id = f.id and b.from_person_id = " + alarmQueryDto.getId() + " ";

        if (null != alarmQueryDto.getCameraIds() && !"".equals(alarmQueryDto.getCameraIds())) {
            List<Long> cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class,
                    Arrays.asList(alarmQueryDto.getCameraIds().split(",")).stream().map(m -> Long.valueOf(m)).collect(Collectors.toList()), null);
            String cameraIdstr = String.join(",", cameraIdList.stream().map(m -> String.valueOf(m)).toArray(String[]::new));
            sqlString += "and f.id in (" + cameraIdstr + ") ";
        }
        if (null != alarmQueryDto.getStartTime() && !"".equals(alarmQueryDto.getStartTime())) {
            sqlString += "and a.time >= '" + alarmQueryDto.getStartTime() + "' ";
            String cameraIdStr = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, " and f.id in");
            sqlString += cameraIdStr;
        }
        if (null != alarmQueryDto.getEndTime() && !"".equals(alarmQueryDto.getEndTime())) {
            sqlString += "and a.time <= '" + alarmQueryDto.getEndTime() + "' ";
            String cameraIdStr = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, " and f.id in");
            sqlString += cameraIdStr;
        }

        sqlString += "order by time desc LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + "," + alarmQueryDto.getPageSize() + "";

        sqlString = _userService.processAuthority(sqlString);

        try {
            Query query = this.entityManager.createNativeQuery(sqlString, EventInfo.class);
            resp = (ArrayList<EventInfo>) query.getResultList();
            updateFaceInfoToEventInfo(resp);
        } catch (Exception e) {
            LOG.error("findEventsByPersonIdAndCameras method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonByCameraId(String ids, int type, double threshold, int page, int pageSize) {
        // List<PersonDetailAlarmDate> resp = null;
        List<PersonDetail> resp = null;
        /*
         * if (type == 2) {//获取全部黑白名单 sqlString =
         * "SELECT b.from_person_id, c.* FROM " +
         * GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_ALARM_INFO +
         * " a, " +
         * GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_BLACK_DETAIL +
         * " b, " +
         * GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_PERSON_DETAIL +
         * " c, " + GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_TASK_INFO
         * + " d where a.black_id = b.id and b.from_person_id = c.id " +
         * "and a.task_id = d.id and d.source_id in (" + ids +
         * ") group by b.from_person_id order by max(a.time) desc LIMIT " +
         * (page - 1) * pageSize + "," + (pageSize * 3) + ""; } else { sqlString
         * = "SELECT b.from_person_id, c.* FROM " +
         * GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_ALARM_INFO +
         * " a, " +
         * GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_BLACK_DETAIL +
         * " b, " +
         * GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_PERSON_DETAIL +
         * " c, " + GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_TASK_INFO
         * + " d where a.black_id = b.id and c.type = " + type +
         * " and b.from_person_id = c.id " +
         * "and a.task_id = d.id and d.source_id in (" + ids +
         * ") group by b.from_person_id order by max(a.time) desc LIMIT " +
         * (page - 1) * pageSize + "," + (pageSize * 3) + ""; }
         */

        // sqlString = _userService.processAuthority(sqlString);
        long stationId = (((UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPoliceStationId());

        List<Long> cameraIdList = zoneAuthorizeService.filterIds(CameraInfo.class,
                Arrays.asList(ids.split(",")).stream().map(m -> Long.valueOf(m)).collect(Collectors.toList()), null);
        String cameraIdstr = String.join(",", cameraIdList.stream().map(m -> String.valueOf(m)).toArray(String[]::new));
        // String[] idArray = ids.split(",");
        // List<String> sqlStringList = new ArrayList<>();
        // for (int i = 0; i < idArray.length; i++) {
        // String id = idArray[i];
        // String sqlString = "";
        // if(type == 2){
        // sqlString =
        // "SELECT f.from_person_id, f.time, c.* FROM (SELECT b.from_person_id, e.time FROM (SELECT a.black_id,  max(a.time) as time FROM "
        // + GlobalConsts.T_NAME_ALARM_INFO + " a, "
        // + GlobalConsts.T_NAME_TASK_INFO + " d WHERE d.source_id in (" + id +
        // ") AND a.task_id = d.id GROUP BY a.black_id order by max(a.time) desc) e, "
        // + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " +
        // GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY +
        // " g where g.station_id = " + stationId +
        // " and b.bank_id = g.bank_id "
        // + "and b.id = e.black_id) f, " + GlobalConsts.T_NAME_PERSON_DETAIL +
        // " c where f.from_person_id = c.id group by f.from_person_id order by max(f.time) desc LIMIT "
        // + (page - 1) * pageSize + "," + (pageSize * 3) + "";
        // }else{
        // sqlString =
        // "SELECT f.from_person_id, f.time, c.* FROM (SELECT b.from_person_id, e.time FROM (SELECT a.black_id,  max(a.time) as time FROM "
        // + GlobalConsts.T_NAME_ALARM_INFO + " a, "
        // + GlobalConsts.T_NAME_TASK_INFO + " d WHERE d.source_id in (" + id +
        // ") AND a.task_id = d.id GROUP BY a.black_id order by max(a.time) desc) e, "
        // + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " +
        // GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY +
        // " g where g.station_id = " + stationId +
        // " and b.bank_id = g.bank_id "
        // + "and b.id = e.black_id) f, " + GlobalConsts.T_NAME_PERSON_DETAIL +
        // " c where f.from_person_id = c.id and c.type = " + type
        // +" group by f.from_person_id order by max(f.time) desc LIMIT "
        // + (page - 1) * pageSize + "," + (pageSize * 3) + "";
        // }
        // sqlStringList.add(sqlString);
        // }
        String sqlString = null;
        if (type == 2) {
            sqlString = "SELECT f.from_person_id, c.* FROM (SELECT b.from_person_id, e.atime FROM (SELECT a.black_id,  max(a.time) as atime FROM "
                    + GlobalConsts.T_NAME_ALARM_INFO + " a, " + GlobalConsts.T_NAME_TASK_INFO + " d WHERE d.source_id in (" + cameraIdstr
                    + ") AND a.task_id = d.id AND a.confidence >= " + String.valueOf(threshold) + " GROUP BY a.black_id order by max(a.time) desc) e, "
                    + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY + " g where g.station_id = " + stationId
                    + " and b.bank_id = g.bank_id " + "and b.id = e.black_id) f, " + GlobalConsts.T_NAME_PERSON_DETAIL
                    + " c where f.from_person_id = c.id and c.is_urgent = 0 group by f.from_person_id order by max(f.atime) desc LIMIT " + (page - 1)
                    * pageSize + "," + (pageSize) + "";
        } else {
            sqlString = "SELECT f.from_person_id, c.* FROM (SELECT b.from_person_id, e.time FROM (SELECT a.black_id,  max(a.time) as time FROM "
                    + GlobalConsts.T_NAME_ALARM_INFO + " a, " + GlobalConsts.T_NAME_TASK_INFO + " d WHERE d.source_id in (" + cameraIdstr
                    + ") AND a.task_id = d.id AND a.confidence >= " + String.valueOf(threshold) + " GROUP BY a.black_id order by max(a.time) desc) e, "
                    + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY + " g where g.station_id = " + stationId
                    + " and b.bank_id = g.bank_id " + "and b.id = e.black_id) f, " + GlobalConsts.T_NAME_PERSON_DETAIL
                    + " c where f.from_person_id = c.id and c.is_urgent = 0 and c.type = " + type
                    + " group by f.from_person_id order by max(f.time) desc LIMIT " + (page - 1) * pageSize + "," + (pageSize) + "";
        }
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, PersonDetail.class);
            resp = (ArrayList<PersonDetail>) query.getResultList();
        } catch (Exception e) {
            LOG.error("findAlarmPersonByCameraId  ids:" + ids + " type:" + type + " method error:", e);
        } finally {
            entityManager.close();
        }

        // List<PersonDetailAlarmDate> resultList =
        // Collections.synchronizedList(new ArrayList<>());
        // List<Future<?>> tasklist = new ArrayList<>();
        // for (String sql : sqlStringList) {
        // tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
        // try {
        // Query query = this.entityManager.createNativeQuery(sql,
        // PersonDetailAlarmDate.class);
        // List<PersonDetailAlarmDate> result =
        // (ArrayList<PersonDetailAlarmDate>) query.getResultList();
        // resultList.addAll(result);
        // } catch (Exception e) {
        // LOG.error("findAlarmPersonByCameraId method error:",e);
        // } finally {
        // entityManager.close();
        // }
        // }));
        // }
        // tasklist.forEach(FunctionUtil::waitTillThreadFinish);
        // List<PersonDetailAlarmDate> preresp = new ArrayList<>();
        // Map<Long, List<PersonDetailAlarmDate>> personDetailListMap =
        // resultList.stream().collect(Collectors.groupingBy(PersonDetailAlarmDate::getId));
        // for (Map.Entry<Long, List<PersonDetailAlarmDate>> entry :
        // personDetailListMap.entrySet()) {
        // preresp.add(entry.getValue().stream().max((m, n) ->
        // m.getTime().compareTo(n.getTime())).get());
        // }
        // resp = preresp.stream().sorted((m, n) ->
        // n.getTime().compareTo(m.getTime())).limit(pageSize *
        // 3).collect(Collectors.toList());
        //
        // List<PersonDetail> response = null;
        // if (resp != null) {
        // response = new ArrayList<>();
        // for (PersonDetailAlarmDate pdad : resp) {
        // response.add(new PersonDetail(pdad));
        // }
        // }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonByCameraIdConfidence(String ids,EventsByStationIdKey key) {
        List<PersonDetail> resp = null;

        String sqlstr = "select id-1000000 from " + GlobalConsts.T_NAME_ALARM_INFO + " order by id desc limit 1";
       
        long startId = 0;
        try {
            List<Long> idList = null;
            idList = alarmInfoDao.getListLongBySql(sqlstr);
            if(!CollectionUtils.isEmpty(idList)){
               startId = idList.get(0);
               if(startId < 0){
                   startId = 0;
               }
            }
        } catch (Exception e) {
            LOG.error("findAlarmPersonByCameraIdConfidence get alarm id error:", e);
        }


        long userId = key.getUserId();
        String uFilterSql = " ";
        if( 0 != userId){
            uFilterSql = " and c.owner = '"+((UserInfo)_userService.findById(userId)).getLogin()+"' ";
        }

        String sqlString = null;
       
            sqlString = "SELECT f.from_person_id, c.* FROM (SELECT b.from_person_id, e.atime FROM (SELECT a.black_id,  max(a.time) as atime FROM "
                    + GlobalConsts.T_NAME_ALARM_INFO + " a, " + GlobalConsts.T_NAME_TASK_INFO + " d WHERE d.source_id in (" + ids
                    + ") AND a.task_id = d.id and a.id > " + String.valueOf(startId) + " and a.status in ("+key.getStatus()+") and a.confidence >= " + String.valueOf(key.getThreshold())
                    + " GROUP BY a.black_id order by max(a.time) desc) e, " + GlobalConsts.T_NAME_BLACK_DETAIL + " b, "
                    + GlobalConsts.T_NAME_BLACK_BANK + " g where g.station_id in(" + key.getIds() + ") and b.bank_id = g.id "
                    + "and b.id = e.black_id) f, " + GlobalConsts.T_NAME_PERSON_DETAIL
                    + " c  where f.from_person_id = c.id "+uFilterSql+" group by f.from_person_id order by max(f.atime) desc LIMIT "
                    + (key.getPage() - 1) * key.getPagesize() + "," + (key.getPagesize()) + "";
      
        try {
            resp = personDetailDao.findObjectBySql(sqlString);
        } catch (Exception e) {
            LOG.error("findAlarmPersonByCameraIdConfidence method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonByBankId(long id, float threshold, int page, int pageSize) {
        
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "e");
        
        String countBankSql = "select count(1) from "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL+" WHERE bank_id = "+id;
        Long bCount = jdbcTemplate.queryForObject(countBankSql, Long.class);   
        List<PersonDetail> resultList = new ArrayList<PersonDetail>();    
        String countAlarmSql = "select count(1) from "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO+" WHERE confidence >= "+threshold;
        Long count = jdbcTemplate.queryForObject(countAlarmSql, Long.class); 
        long cNum = 80*bCount+count; //计算公式
        if(cNum > PerformParamSetting.getScanCount() * 10000 && bCount > PerformParamSetting.getBankPersonNum()){
             //查询扫瞄数据过多，报警分批查询
            int num = 0;
            int size = resultList.size();
            int selectCount = PerformParamSetting.getSelectAlarmNum();
            while(size < pageSize && num*selectCount < count){
                String sqlStringA = "SELECT a.confidence,a.time,b.from_person_id, c.* FROM " 
                        +"(select task_id,black_id,time,confidence from "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO+"  WHERE confidence >= "+threshold+"  order by time desc limit "+num*selectCount+","+(num+1)*selectCount+")a LEFT JOIN "
                        +GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL+" b  on a.black_id = b.id LEFT JOIN "
                        +GlobalConsts.INTELLIF_BASE + "."+ GlobalConsts.T_NAME_PERSON_DETAIL+" c on b.from_person_id = c.id  LEFT JOIN "
                        +GlobalConsts.INTELLIF_BASE + "."+ GlobalConsts.T_NAME_TASK_INFO+" d on a.task_id = d.id LEFT JOIN "
                        +cameraSql+" on d.source_id = e.id "
                        +"where  c.is_urgent = 0 and c.bank_id = "+ id +" group by b.from_person_id ORDER BY max(a.time) desc limit "+(page-1)*pageSize+size+","+pageSize;
               
               try {
                   Query query = this.entityManager.createNativeQuery(sqlStringA, PersonDetail.class);
                   List<PersonDetail> resp = (ArrayList<PersonDetail>) query.getResultList();
                   if(null != resp && !resp.isEmpty()){
                       resultList.addAll(resp);
                   }
               } catch (Exception e) {
                   LOG.error("findAlarmPersonByBankId  A method error:", e);
               } finally {
                   entityManager.close();
               }
               num++;
               size = resultList.size();
            }
        }
        else{
            String sqlStringB = "SELECT a.confidence,a.time,b.from_person_id, c.* FROM " 
                    +"(select task_id,black_id,time,confidence from "+ GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO+"  WHERE confidence >= "+threshold+")a LEFT JOIN "
                    +GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL+" b  on a.black_id = b.id LEFT JOIN "
                    +GlobalConsts.INTELLIF_BASE + "."+ GlobalConsts.T_NAME_PERSON_DETAIL+" c on b.from_person_id = c.id  LEFT JOIN "
                    +GlobalConsts.INTELLIF_BASE + "."+ GlobalConsts.T_NAME_TASK_INFO+" d on a.task_id = d.id LEFT JOIN "
                    +cameraSql+" on d.source_id = e.id "
                    +"where  c.is_urgent = 0 and c.bank_id = "+ id +" group by b.from_person_id ORDER BY max(a.time) desc limit "+(page-1)*pageSize+","+pageSize;
            try {
                Query query = this.entityManager.createNativeQuery(sqlStringB, PersonDetail.class);
                resultList = (ArrayList<PersonDetail>) query.getResultList();
            } catch (Exception e) {
                LOG.error("findAlarmPersonByBankId  B method error:", e);
            } finally {
                entityManager.close();
            }
        }
        return resultList;
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonByBankIdAndCameras(AlarmQueryDto alarmQueryDto) {
        List<PersonDetail> resp = null;
        String sqlString = "SELECT b.from_person_id, c.* " + "FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " a, "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_PERSON_DETAIL + " c, " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " d  "
                + "where c.is_urgent = 0 and a.confidence >= " + alarmQueryDto.getThreshold() + " and a.black_id = b.id and "
                + "b.from_person_id = c.id and c.bank_id = " + alarmQueryDto.getId() + "  and a.task_id = d.id ";

        if (null != alarmQueryDto.getCameraIds() && !"".equals(alarmQueryDto.getCameraIds())) {
            sqlString += "and d.source_id in (" + alarmQueryDto.getCameraIds() + ") ";
        }
        if (null != alarmQueryDto.getStartTime() && !"".equals(alarmQueryDto.getStartTime())) {
            sqlString += "and a.time >= '" + alarmQueryDto.getStartTime() + "' ";
        }
        if (null != alarmQueryDto.getEndTime() && !"".equals(alarmQueryDto.getEndTime())) {
            sqlString += "and a.time <= '" + alarmQueryDto.getEndTime() + "' ";
        }
        if (null != alarmQueryDto.getText() && !"".equals(alarmQueryDto.getText())) {
            sqlString += "and c.real_name like '%" + alarmQueryDto.getText() + "%' ";
        }

        sqlString += "group by b.from_person_id order by max(a.time) desc LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + ","
                + (alarmQueryDto.getPageSize() * 3) + "";

        sqlString = _userService.processAuthority(sqlString);

        try {
            Query query = this.entityManager.createNativeQuery(sqlString, PersonDetail.class);
            resp = (ArrayList<PersonDetail>) query.getResultList();
        } catch (Exception e) {
            LOG.error("findAlarmPersonByBankIdAndCameras method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonForOffline(AlarmQueryDto alarmQueryDto) {
        List<PersonDetail> resp = null;
        /*SELECT * FROM t_person_detail c WHERE EXISTS(
                SELECT b.id FROM t_black_detail b WHERE EXISTS(
                    SELECT a.id FROM t_alarm_info a, t_task_info d 
                    WHERE confidence >= 0.92 AND a.black_id = b.id  AND a.task_id = d.id
                    ORDER BY a.time DESC
                ) AND b.from_person_id = c.id AND bank_id = 56
            )  AND is_urgent = 0
            LIMIT 0,40*/
        String sqlString = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " c WHERE EXISTS("
                + "SELECT b.id FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " b WHERE EXISTS( " 
                + "SELECT a.id FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " a, "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " d "
                + " WHERE confidence >=" + alarmQueryDto.getThreshold() + " and a.black_id = b.id and a.task_id = d.id ";
        if (null != alarmQueryDto.getCameraIds() && !"".equals(alarmQueryDto.getCameraIds())) {
            sqlString += "and d.source_id in (" + alarmQueryDto.getCameraIds() + ") ";
        }
        if (null != alarmQueryDto.getStartTime() && !"".equals(alarmQueryDto.getStartTime())) {
            sqlString += "and a.time >= '" + alarmQueryDto.getStartTime() + "' ";
        }
        if (null != alarmQueryDto.getEndTime() && !"".equals(alarmQueryDto.getEndTime())) {
            sqlString += "and a.time <= '" + alarmQueryDto.getEndTime() + "' ";
        }
        
        sqlString += " ORDER BY a.time desc )AND b.from_person_id = c.id AND bank_id = " + alarmQueryDto.getId()  + " )AND is_urgent = 0";
        
        if (null != alarmQueryDto.getText() && !"".equals(alarmQueryDto.getText())) {
            sqlString += " and c.real_name like '%" + alarmQueryDto.getText() + "%' ";
        }
        sqlString += " LIMIT " + (alarmQueryDto.getPage() - 1) * alarmQueryDto.getPageSize() + ","
                + alarmQueryDto.getPageSize();
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, PersonDetail.class);
            resp = (ArrayList<PersonDetail>) query.getResultList();
        } catch (Exception e) {
            LOG.error("findAlarmPersonByBankIdAndCameras method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }
    @SuppressWarnings("unchecked")
    @Override
    public int countFindAlarmPersonForOffline(AlarmQueryDto alarmQueryDto) {
        int resp = 0;
        /*SELECT COUNT(*) FROM t_person_detail c WHERE EXISTS(
                SELECT b.id FROM t_black_detail b WHERE EXISTS(
                    SELECT a.id FROM t_alarm_info a, t_task_info d 
                    WHERE confidence >= 0.92 AND a.black_id = b.id  AND a.task_id = d.id
                    ORDER BY a.time DESC
                ) AND b.from_person_id = c.id AND bank_id = 56
            ) AND is_urgent = 0*/
        String sqlString = "SELECT COUNT(*) FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " c WHERE EXISTS("
                + "SELECT b.id FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " b WHERE EXISTS( " 
                + "SELECT a.id FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " a, "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO + " d "
                + " WHERE confidence >=" + alarmQueryDto.getThreshold() + " and a.black_id = b.id and a.task_id = d.id ";
        if (null != alarmQueryDto.getCameraIds() && !"".equals(alarmQueryDto.getCameraIds())) {
            sqlString += "and d.source_id in (" + alarmQueryDto.getCameraIds() + ") ";
        }
        if (null != alarmQueryDto.getStartTime() && !"".equals(alarmQueryDto.getStartTime())) {
            sqlString += "and a.time >= '" + alarmQueryDto.getStartTime() + "' ";
        }
        if (null != alarmQueryDto.getEndTime() && !"".equals(alarmQueryDto.getEndTime())) {
            sqlString += "and a.time <= '" + alarmQueryDto.getEndTime() + "' ";
        }
        
        sqlString += " ORDER BY a.time desc )AND b.from_person_id = c.id AND bank_id = " + alarmQueryDto.getId() + ") AND is_urgent = 0 ";
        
        if (null != alarmQueryDto.getText() && !"".equals(alarmQueryDto.getText())) {
            sqlString += " and c.real_name like '%" + alarmQueryDto.getText() + "%' ";
        }
        try {
            Query query = this.entityManager.createNativeQuery(sqlString);
            resp = Integer.parseInt(query.getResultList().get(0).toString());
        } catch (Exception e) {
            LOG.error("findAlarmPersonByBankIdAndCameras method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonByAttention(long id, int page, int pageSize, int type) {
        List<PersonDetail> resp = null;
        String sqlString = "";
        if (type == 2) {// 取出全部黑白名单
            sqlString = "SELECT a.* FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " a, " + GlobalConsts.INTELLIF_BASE + "."
                    + GlobalConsts.T_NAME_USER_ATTENTION + " b where a.id = b.person_id and a.is_urgent = 0 and b.user_id =" + id
                    + " order by a.created desc LIMIT " + (page - 1) * pageSize + "," + (pageSize) + "";
        } else {
            sqlString = "SELECT a.* FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " a, " + GlobalConsts.INTELLIF_BASE + "."
                    + GlobalConsts.T_NAME_USER_ATTENTION + " b where a.id = b.person_id and a.is_urgent = 0 and a.type = " + type + " and b.user_id =" + id
                    + " order by a.created desc LIMIT " + (page - 1) * pageSize + "," + (pageSize) + "";
        }

        sqlString = _userService.processAuthority(sqlString);
        LOG.info("find Alarm attention sql:" + sqlString);
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, PersonDetail.class);
            resp = (ArrayList<PersonDetail>) query.getResultList();
        } catch (Exception e) {
            LOG.error("findAlarmPersonByAttention method error:", e);
            return null;
        } finally {
            entityManager.close();
        }
        LOG.info("find Alarm count:" + resp.size());
        return resp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventInfo> findAllPersonEvents(long stationId, int pageSize, String personIds, double threshold) {
        String sql = "CALL person_events_pro(:stationId, :count, :personIds, :bankIds, :threshold, :cameraStatement)";
        List<EventInfo> resp = null;

        String cameraStatement = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, "AND f.id in");
        List<PoliceStationAuthority> authorityList = policeStationAuthorityRepository.findByStationId(stationId);
        StringBuilder bankIdsBuilder = new StringBuilder();
        int index = 0;
        if (null == authorityList || authorityList.isEmpty()) {
            LOG.error("单位没有任何授权库信息,stationId:" + stationId);
            return null;
        }
        for (; index < authorityList.size() - 1; index++) {
            PoliceStationAuthority authority = authorityList.get(index);
            bankIdsBuilder.append(authority.getBankId() + ",");
        }
        bankIdsBuilder.append(authorityList.get(index).getBankId());
        final String bankIds = bankIdsBuilder.toString();

        List<String> personIdArrayList = new ArrayList<>();
        String[] personIdArray = personIds.split(",");
        StringBuilder strBuilder = new StringBuilder();
        int offset = 0;
        for (index = 0; index < personIdArray.length; index++) {
            if (personIdArray[index].isEmpty())
                continue;
            if (offset < 39) {
                strBuilder.append(personIdArray[index]);
                strBuilder.append(",");
                offset++;
            } else if (offset == 39) {
                strBuilder.append(personIdArray[index]);
                personIdArrayList.add(strBuilder.toString());
                offset = 0;
                strBuilder = new StringBuilder();
            }
        }
        String lastPersonIds = strBuilder.toString();
        if (lastPersonIds.length() > 0) {
            personIdArrayList.add(lastPersonIds.substring(0, lastPersonIds.length() - 1));
        }

        try {
            List<EventInfo> resultList = Collections.synchronizedList(new ArrayList<>());
            List<Future<?>> tasklist = new ArrayList<>();
            for (String persons : personIdArrayList) {
                tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
                    try {
                        Query query = this.entityManager.createNativeQuery(sql, EventInfo.class);
                        query.setParameter("stationId", stationId).setParameter("count", pageSize).setParameter("personIds", persons)
                                .setParameter("bankIds", bankIds).setParameter("threshold", threshold).setParameter("cameraStatement", cameraStatement);
                        List<EventInfo> result = query.getResultList();
                        CommonUtil.addAreaId(result, stationId);
                        resultList.addAll(result);
                    } catch (Exception e) {
                        LOG.error("findAlarmPersonByCameraId method error, personIds：" + personIds + " error:", e);
                    } finally {
                        entityManager.close();
                    }
                }));
            }
            tasklist.forEach(FunctionUtil::waitTillThreadFinish);
            resp = resultList;

            // Query query = this.entityManager.createNativeQuery(sql,
            // EventInfo.class);
            // query.setParameter("stationId", stationId).setParameter("count",
            // pageSize).setParameter("personIds", personIds)
            // .setParameter("bankIds", bankIds).setParameter("threshold",
            // threshold);
            // resp = query.getResultList();
            updateFaceInfoToEventInfo(resp);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }

        return resp;
    }

    /**
     * 获取alarm对应图片信息
     *
     * @param alarmList
     */
    private void updateFaceInfoToAlarmInfo(List<AlarmInfoDto> alarmList) {
        if (null != alarmList && !alarmList.isEmpty()) {
            Map<Long, List<AlarmInfoDto>> alarmMap = new HashMap<Long, List<AlarmInfoDto>>();
            Map<Long, List<FaceInfo>> faceMap = new HashMap<Long, List<FaceInfo>>();
            Map<Long, List<AlarmInfoDto>> blackAlarmMap = new HashMap<Long, List<AlarmInfoDto>>();
            List<Long> faceIdList = new ArrayList<Long>();
            List<Long> imageIdList = new ArrayList<Long>();
            List<Long> blackIdList = new ArrayList<Long>();
            for (AlarmInfoDto alarm : alarmList) {
                faceIdList.add(alarm.getAlarmFaceId());
                blackIdList.add(alarm.getBlackImageId());

                List<AlarmInfoDto> fAlarmList = alarmMap.get(alarm.getAlarmFaceId());
                if (null == fAlarmList) {
                    fAlarmList = new ArrayList<AlarmInfoDto>();
                    alarmMap.put(alarm.getAlarmFaceId(), fAlarmList);
                }
                fAlarmList.add(alarm);

                List<AlarmInfoDto> bAlarmList = blackAlarmMap.get(alarm.getAlarmFaceId());
                if (null == bAlarmList) {
                    bAlarmList = new ArrayList<AlarmInfoDto>();
                    blackAlarmMap.put(alarm.getAlarmFaceId(), bAlarmList);
                }
                bAlarmList.add(alarm);

            }

            List<FaceInfo> faceList = faceInfoDao.findByIds(faceIdList);
            if (null != faceList && !faceList.isEmpty()) {
                for (FaceInfo face : faceList) {
                    long faceId = face.getId();
                    List<AlarmInfoDto> fAlarmList = alarmMap.get(faceId);
                    for (AlarmInfoDto item : fAlarmList) {
                        item.setFaceImageData(face.getImageData());
                    }

                    List<FaceInfo> faceInfos = faceMap.get(face.getFromImageId());
                    if (faceInfos == null) {
                        faceInfos = new ArrayList<>();
                        faceMap.put(face.getFromImageId(), faceInfos);
                    }
                    faceInfos.add(face);
                    imageIdList.add(face.getFromImageId());
                }
                List<ImageInfo> imageList = imageInfoDao.findByIds(imageIdList);
                if (null != imageList && !imageList.isEmpty()) {
                    for (ImageInfo image : imageList) {
                        long imageId = image.getId();
                        List<FaceInfo> faceInfos = faceMap.get(imageId);
                        for (FaceInfo face : faceInfos) {
                            long faceId = face.getId();
                            List<AlarmInfoDto> fAlarmList = alarmMap.get(faceId);
                            for (AlarmInfoDto item : fAlarmList) {
                                item.setFaceBigImageUri(image.getUri());
                            }
                        }
                    }
                }
            }

            List<ImageInfo> blackImageList = imageInfoDao.findByIds(blackIdList);
            if (null != blackImageList && !blackImageList.isEmpty()) {
                for (ImageInfo image : blackImageList) {
                    long imageId = image.getId();
                    List<AlarmInfoDto> fAlarmList = blackAlarmMap.get(imageId);
                    for (AlarmInfoDto item : fAlarmList) {
                        item.setBlackBigImageUri(image.getUri());
                    }
                }
            }

            // 每个alarmInfo去查一次
            /*
             * for(AlarmInfoDto alarm : alarmList){ long alarmFaceId =
             * alarm.getAlarmFaceId(); long blackImageId =
             * alarm.getBlackImageId(); List<Long> faceIdList = new
             * ArrayList<Long>(); faceIdList.add(alarmFaceId); List<Long>
             * blackImageIdList = new ArrayList<Long>();
             * blackImageIdList.add(blackImageId); List<FaceInfo> faceList =
             * faceInfoDao.findByIds(faceIdList); if(null != faceList &&
             * !faceList.isEmpty()){
             * alarm.setFaceImageData(faceList.get(0).getImageData());
             *
             * long imageId = faceList.get(0).getFromImageId(); List<Long>
             * imageIdList = new ArrayList<Long>(); imageIdList.add(imageId);
             * List<ImageInfo> imageList = imageInfoDao.findByIds(imageIdList);
             * if(null != imageList && !imageList.isEmpty()){
             * alarm.setFaceBigImageUri(imageList.get(0).getUri()); } }
             *
             * List<ImageInfo> blackImageList =
             * imageInfoDao.findByIds(blackImageIdList); if(null !=
             * blackImageList && !blackImageList.isEmpty()){
             * alarm.setBlackBigImageUri((blackImageList.get(0).getUri())); } }
             */
        }
    }

    /**
     * 获取event对应图片信息
     *
     * @param alarmList
     */
    private void updateFaceInfoToEventInfo(List<EventInfo> eventList) {

        Map<Long, List<EventInfo>> eventMap = new HashMap<Long, List<EventInfo>>();
        Map<Long, FaceInfo> faceMap = new HashMap<Long, FaceInfo>();
        List<Long> faceIdList = new ArrayList<Long>();
        List<Long> imageIdList = new ArrayList<Long>();
        for (EventInfo event : eventList) {
            long Id = event.getFaceId();
            if (0 != Id) {
                List<EventInfo> fEventList = eventMap.get(Id);
                if (null == fEventList) {
                    fEventList = new ArrayList<EventInfo>();
                    eventMap.put(Id, fEventList);
                }
                fEventList.add(event);
                faceIdList.add(Id);
            }
        }

        List<FaceInfo> faceList = faceInfoDao.findByIds(faceIdList);
        if (null != faceList && !faceList.isEmpty()) {
            for (FaceInfo face : faceList) {
                long faceId = face.getId();
                List<EventInfo> fEventList = eventMap.get(faceId);
                for (EventInfo item : fEventList) {
                    item.setImageData(face.getImageData());
                }
                faceMap.put(face.getFromImageId(), face);
                imageIdList.add(face.getFromImageId());
            }
            List<ImageInfo> imageList = imageInfoDao.findByIds(imageIdList);
            if (null != imageList && !imageList.isEmpty()) {
                for (ImageInfo image : imageList) {
                    long imageId = image.getId();
                    FaceInfo face = faceMap.get(imageId);
                    long faceId = face.getId();
                    List<EventInfo> fEventList = eventMap.get(faceId);
                    for (EventInfo item : fEventList) {
                        item.setScene(image.getUri());
                    }
                }
            }
        }

        // 每个event查询一次sql
        /*
         * if(null != eventList){ for(EventInfo event : eventList){ long faceId
         * = event.getFaceId(); if(faceId != 0){ List<Long> faceIdList = new
         * ArrayList<Long>(); faceIdList.add(faceId); List<FaceInfo> faceList =
         * faceInfoDao.findByIds(faceIdList); if(null != faceList &&
         * !faceList.isEmpty()){
         * event.setImageData(faceList.get(0).getImageData());
         *
         * long imageId = faceList.get(0).getFromImageId(); List<Long>
         * imageIdList = new ArrayList<Long>(); imageIdList.add(imageId);
         * List<ImageInfo> imageList = imageInfoDao.findByIds(imageIdList);
         * if(null != imageList && !imageList.isEmpty()){
         * event.setScene(imageList.get(0).getUri()); } } } } }
         */
    }

    @Override
    public List<AlarmImageInfo> findImageByAlarmIds(List<Long> alarmIdList,Long userId) {
         String idStr = StringUtils.join(alarmIdList, ",");
         String filterSql = "id in("+idStr+")";
        List<AlarmInfo> alarmInfoList = (List<AlarmInfo>) super.findByFilter(filterSql);
        List<Long> faceIdList = alarmInfoList.stream().map(item -> item.getFaceId()).collect(Collectors.toList());
        List<FaceInfo> faceList = faceInfoDao.findByIds(faceIdList);
        Map<Long, AlarmImageInfo> map = new HashMap<>();
        Map<Long, FaceInfo> faceMap = new HashMap<Long, FaceInfo>();
        alarmInfoList.forEach(item -> {
            AlarmImageInfo alarmImageInfo = new AlarmImageInfo();
            alarmImageInfo.setTime(item.getTime());
            map.put(item.getFaceId(), alarmImageInfo);
        });
        List<Long> imageIdList = new ArrayList<Long>();
        List<CameraDto> cameraAllDtoList = cameraServiceItf.findAllCameraDto(userId);
        Map<Long, List<CameraDto>> cameraMap = cameraAllDtoList.stream().collect(Collectors.groupingBy(CameraDto::getId));
        if (null != faceList && !faceList.isEmpty()) {
            faceList.forEach(faceInfo -> {
                AlarmImageInfo alarmImageInfo = map.get(faceInfo.getId());
                alarmImageInfo.setImageData(faceInfo.getImageData());
                long cameraId = faceInfo.getSourceId();
                List<CameraDto> cameraDtoList = cameraMap.get(cameraId);
                if (cameraDtoList != null && cameraDtoList.size() >= 0) {
                    CameraDto cameraDto = cameraDtoList.get(0);
                    alarmImageInfo.setCameraName(cameraDto.getName());
                    alarmImageInfo.setStationName(cameraDto.getAreaName());
                }
                faceMap.put(faceInfo.getFromImageId(), faceInfo);
                imageIdList.add(faceInfo.getFromImageId());
            });
        }
        List<ImageInfo> imageList = imageInfoDao.findByIds(imageIdList);
        if (null != imageList && !imageList.isEmpty()) {
            imageList.forEach(imageInfo -> {
                long imageId = imageInfo.getId();
                FaceInfo faceInfo = faceMap.get(imageId);
                long faceId = faceInfo.getId();
                map.get(faceId).setUri(imageInfo.getUri());
            });
        }
        return new ArrayList<AlarmImageInfo>(map.values());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonDetail> findAlarmPersonByFkType(long id, String fkType, double threshold, int page, int pageSize) {

        //查询全部的布控库的告警情况
        String fType = "";
        if(Integer.valueOf(fkType).intValue()==0){
           fType = getBkFkBankIds();
        }else{
           fType = fkType;
        }
        List<PersonDetail> resp = null;
        String cameraStatement = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, "AND e.id in");
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "e");
        String sqlString = "SELECT b.from_person_id, c.* FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " a, "
                + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " b, " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_PERSON_DETAIL + " c, " + GlobalConsts.T_NAME_TASK_INFO + " d, " + cameraSql
                +"  where a.task_id = d.id " + cameraStatement + " and c.is_urgent = 0 and d.source_id = e.id"
                + " and a.black_id = b.id and b.from_person_id = c.id and c.bank_id = " + id + " and c.fk_type in ( " + fType + " ) and a.confidence >= "
                + String.valueOf(threshold) + " group by b.from_person_id order by max(a.time) desc LIMIT " + (page - 1) * pageSize + "," + (pageSize) + "";

        try {
            Query query = this.entityManager.createNativeQuery(sqlString, PersonDetail.class);
            resp = (ArrayList<PersonDetail>) query.getResultList();
        } catch (Exception e) {
            LOG.error("findAlarmPersonByBankId method error:", e);
        } finally {
            entityManager.close();
        }
        return resp;
    }

   public String getBkFkBankIds(){

        ArrayList<FkBkBank> fkBank = (ArrayList<FkBkBank>) this._fkBkBankDao.findAll();      
        String fkBkBankIds = "";
        // 给前端返回的时候 只返回布控库 过滤一下吧
        for (int i = 0; i < fkBank.size(); i++) {
            FkBkBank fkbk = fkBank.get(i);
            if (fkbk.getBankno() > 20 && fkbk.getBankno() < 25) {                         
                if (fkBkBankIds.trim() != "") {
                    fkBkBankIds = fkBkBankIds + "," + fkbk.getBankno();
                } else {
                    fkBkBankIds = fkbk.getBankno() + "";
                }

            }
        }

        return fkBkBankIds;

    }

    @Override
    public boolean updateStatusById(long id, int status) {
        try {
            String sql = "UPDATE " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + 
                    " SET STATUS = " + status + " WHERE id = " + id;
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            LOG.error("更新报警记录人工标记失败", e);
            return false;
        }
        return true;
    }

    @Override
    public int sendJinxinOnConfirm(long alarmId) {
        String sql = "select a.time, a.face_id, p.real_name, p.post_object as p_post_object, p.owner, " +
                " ba.post_object b_post_object " +
                " from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO +
                " a left join " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " b " +
                " on a.black_id = b.id left join " + GlobalConsts.INTELLIF_BASE + "." +
                GlobalConsts.T_NAME_PERSON_DETAIL + " p on p.id = b.from_person_id left join " +
                GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_BANK + " ba on b.bank_id = ba.id " +
                " where a.id = " + alarmId;
        Map<String, Object> data = jdbcTemplate.queryForObject(sql, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> row = new HashMap<>();
                row.put("time", rs.getDate("time"));
                row.put("faceId", rs.getLong("face_id"));
                row.put("realName", rs.getString("real_name"));
                row.put("personPostObject", rs.getString("p_post_object"));
                row.put("owner", rs.getString("owner"));
                row.put("bankPostObject", rs.getString("b_post_object"));
                return row;
            }
        });

        String postObject = (String) data.get("personPostObject");
        if (StringUtils.isEmpty(postObject)) {
            postObject = (String) data.get("bankPostObject");
            if (StringUtils.isEmpty(postObject)) {
                postObject = (String) data.get("owner");
            }
        }
        if (StringUtils.isEmpty(postObject)) {
            LOG.warn("send alarm jinxin on confirm: post object is empty!");
            return -1;
        }

        String realName = (String) data.get("realName");
        String dateStr = DateUtil.getDateString((Date) data.get("time"));
        FaceInfo faceInfo = faceService.findOne((Long) data.get("faceId"));
        CameraInfo cameraInfo = (CameraInfo) cameraServiceItf.findById(faceInfo.getSourceId());
        JinxinUtil.sendJinxinAlarmMessage(postObject, faceInfo.getImageData(), dateStr, cameraInfo.getAddr(), realName);

        return 0;
    }


}