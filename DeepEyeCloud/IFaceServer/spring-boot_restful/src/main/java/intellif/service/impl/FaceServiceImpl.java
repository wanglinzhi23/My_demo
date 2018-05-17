
package intellif.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.dao.AlarmInfoDao;
import intellif.dao.AuditLogDao;
import intellif.dao.BlackBankDao;
import intellif.dao.BlackDetailDao;
import intellif.dao.FaceCameraCountDao;
import intellif.dao.PersonInzonesDao;
import intellif.dao.PoliceStationDao;
import intellif.dao.TableRecordDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.dto.BlackFaceResultDto;
import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceStatisticDto;
import intellif.dto.QueryFaceDto;
import intellif.dto.XinghuoQuery;
import intellif.fk.dto.FindFkPlaceFaceDto;
import intellif.lire.DeleteSolrFaceDataThread;
import intellif.service.FaceServiceItf;
import intellif.service.SolrDataServiceItf;
import intellif.service.UserServiceItf;
import intellif.utils.CommonUtil;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.DateUtil;
import intellif.utils.SqlUtil;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.TableRecord;
import intellif.database.entity.Area;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@Service
public class FaceServiceImpl implements FaceServiceItf {

    private static Logger LOG = LogManager.getLogger(FaceServiceImpl.class);

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    @Autowired
    FaceInfoDaoImpl faceInfoDaoImpl;
    @Autowired
    ImageInfoDaoImpl imageInfoDaoImpl;
    @Autowired
    PersonInzonesDao personInzonesDao;
    @Autowired
    FaceCameraCountDao faceCameraCountRepository;
    @Autowired
    TableRecordDao tableRecordDao;
    @Autowired
    UserServiceItf _userService;
    @Autowired
    BlackDetailDao blackDetailDao;
    @Autowired
    BlackBankDao bankDao;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;
    @Autowired
    private  PoliceStationDao policestationDao;
    @Autowired
    private  AuditLogDao auditLogRepository;
    @Autowired
    private AlarmInfoDao alarmDao;
    @Autowired
    private AuditLogDao logDao;

    private static final String INSERT_FACE_DELETE = "insert into " + GlobalConsts.INTELLIF_FACE + "."
            + GlobalConsts.T_NAME_FACE_DELETE_INFO + " (id,accessories,race,age,face_feature,from_image_id,"
            +"from_person_id,from_video_id,gender,image_data,indexed,source_id,"
            +"source_type,time,version,json,sequence,quality) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    public List<BlackFaceResultDto> parseBlackFaceResultList(int page, int pagesize, List<FaceResultByCameraDto> rsList) {
        if (CollectionUtils.isEmpty(rsList)) {
            return new ArrayList<>();
        }
        List<BlackFaceResultDto> blackList = new ArrayList<BlackFaceResultDto>();
        String authority = "," + _userService .getAuthorityIds(GlobalConsts.READ_AUTORITY_TYPE) + ",";
        FaceResultByCameraDto rs = rsList.get(0);
        List<FaceResultDto> newRsList = rs.getFaceResult();
        if (null != newRsList && !newRsList.isEmpty()) {
            // newRsList = getPageList(newRsList, page, pagesize);
            int index = 0;
            if (page == 0) {
                page = 1;
            }
            for (FaceResultDto item : newRsList) {
                try {
                    String bIdStr = item.getId();
                    Long bId;
                    if (bIdStr.indexOf("_") > 0) {
                        bId = Long.parseLong(bIdStr.split("_")[1]);
                    } else {
                        bId = Long.parseLong(bIdStr);
                    }
                    BlackDetail bd = blackDetailDao.findOne(bId);
                    if (null == bd) {
                        LOG.error("get black data error solr exist db not exist,bId:"
                                + item.getId());
                        continue;
                    }
                    long bankId = bd.getBankId();
                    if (authority.indexOf("," + bankId + ",") < 0) {
                        LOG.info("get black data bank not authorization for this user,bankId:"
                                + bankId);
                        continue;
                    }
                    BlackBank bb = bankDao.findOne(bankId);
                    if (bb == null || bb.getListType() == 1) {
                        LOG.info("get black data bank is not exist or is white data,bankId:"
                                + bankId);
                        continue;
                    }
                    BlackFaceResultDto bDto = new BlackFaceResultDto(item,
                            bd.getBankId());
                    if ((index >= (page - 1) * pagesize)
                            && (index < page * pagesize)) {
                        blackList.add(bDto);
                    } else if (index >= page * pagesize) {
                        break;
                    }
                    index++;
                } catch (Exception e) {
                    LOG.error("get black data error bId:" + item.getId()
                            + " error:", e);
                }
            }
        }
        return blackList;
    }

    @Override
    public FaceInfo findOne(long id) {
        List<Long> idList = new ArrayList<Long>();
        idList.add(id);
        List<FaceInfo> faceList = faceInfoDaoImpl.findByIds(idList);
        if (null != faceList && !faceList.isEmpty()) {
            return faceList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public FaceInfo save(FaceInfo face) {
        return faceInfoDaoImpl.save(face);
    }

    @Override
    public Integer update(List<FaceInfo> faceList) {
        return faceInfoDaoImpl.update(faceList);
    }

    @Override
    public boolean updateIndexedBySequence(long code, long start, long end) {
        return faceInfoDaoImpl.updateIndexedBySequence(code, start, end);
    }

    @Override
    public boolean updateIndexedByTime(long code, String indextTime, String nextTime) {
        return faceInfoDaoImpl.updateIndexedByTime(code, indextTime, nextTime);
    }

    @Override
    public Long count() {
        return faceCameraCountRepository.statisticAll();
    }

    @Override
    public Long statisticByArea(long id) {
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("station_id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "e");
        //return faceCameraCountRepository.statisticByAreId(id);
        BigDecimal resp = new BigDecimal("0");
        BigDecimal resp1  = null;
        //权限的判断
        String auth = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, "");
        String sqlString = "";
        if(!auth.trim().equals("")){
        sqlString = "SELECT SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id in (SELECT id from "+ cameraSql+" WHERE station_id = "+id+" and id in";//)";
        sqlString =sqlString + auth+ ")";
        }else{
        sqlString = "SELECT SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id in (SELECT id from "+ cameraSql+" WHERE station_id = "+id+")";    
        }
        System.out.println("统计指定所下有权限的摄像头的人脸抓拍数量 :"+sqlString);
        try {
            Query query = this.entityManager.createNativeQuery(sqlString);
            resp1 = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        if(null != resp1){
            resp = resp1;
        }
        return resp.longValue();    
    }
 
    @Override
    public Long statisticByCameraIds(List<Long> cameraIdList) {
        BigDecimal resp = new BigDecimal("0");
        BigDecimal resp1  = null;
        String idSql = null;
        if(null != cameraIdList && !cameraIdList.isEmpty()){
            StringBuffer sb = new StringBuffer();
            for(Long id: cameraIdList){
                sb.append(",");
                sb.append(String.valueOf(id));
            }
             idSql = sb.toString().substring(1);
             String sqlString = "SELECT SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id in ("+idSql+")";    
             try {
                 Query query = this.entityManager.createNativeQuery(sqlString);
                 resp1 = (BigDecimal) query.getSingleResult();
             } catch (Exception e) {
                 LOG.error("statisticByCameraIds", e);
             } finally {
                 entityManager.close();
             }
             if(null != resp1){
                 resp = resp1;
             }
        }
        return resp.longValue();    
    }

    
    @Override
    public List<FaceStatisticDto> statisticByDay() {
        
        List<FaceStatisticDto> resp = null;
        String auth = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, "");
        String sqlString = "";
        if(!auth.trim().equals("")){
        sqlString = "SELECT time, SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE time >= :date and source_id in";
        sqlString = sqlString + auth;
        sqlString = sqlString + "  group by time";
        }else{
        sqlString = "SELECT time, SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE time >= :date";   
        }   
        System.out.println("按天统计有权限的摄像头最近七天人脸抓拍人次sql :"+sqlString);
        try {
            Query query = this.entityManager.createNativeQuery(sqlString, FaceStatisticDto.class);
            query.setParameter("date", DateUtil.getformatDate(new Date().getTime() - 8 * 24 * 3600* 1000L));
            resp = (ArrayList<FaceStatisticDto>) query.getResultList();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            entityManager.close();
        }
        return resp;        
        /*return faceCameraCountRepository.staticByDate(DateUtil.getformatDate(new Date().getTime() - 8 * 24 * 3600
                * 1000L));*/
    }

    @Override
    public List<FaceInfo> findBySourceId(long sourceId, int page, int pageSize) {
        String queryString = "source_id = " + sourceId + " order by time desc";
        String timeField = "time";
        String endTime = DateUtil.getformatDate(new Date().getTime());
        String startTime = DateUtil.getMonthReduce(endTime, -1);
        return faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize,null);
    }

    @Override
    public List<FaceInfo> findBySourceId(long sourceId, String startTime, String endTime, int page, int pageSize) {
        String queryString = "source_id = " + sourceId + " order by time desc";
        String timeField = "time";
        return faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize,null);
    }

    @Override
    public Long countBySourceId(long sourceId, String startTime, String endTime) {
        String queryString = " source_id = " + sourceId;
        return faceInfoDaoImpl.countByTime(queryString, startTime, endTime, "time");
    }

    @Override
    public List<FaceInfo> findBySourceIds(Long[] sourceIds, String startTime, String endTime, int page, int pageSize) {
        StringBuilder sourceIdSb = new StringBuilder();
        for (int i = 0; i < sourceIds.length; i++) {
            sourceIdSb.append(sourceIds[i]);
            if (i < sourceIds.length - 1)
                sourceIdSb.append(",");
        }
        String queryString = "source_id in (" + sourceIdSb.toString() + ") order by time desc";
        String timeField = "time";
        return faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize,null);
    }

    @Override
    public List<FaceInfo> findByFromImageId(long sourceId) {
        List<Long> idList = new ArrayList<Long>();
        idList.add(sourceId);
        return faceInfoDaoImpl.findByIdsFromOther(idList, "source_id");
    }

    @Override
    public List<FaceInfo> findUnIndexed(String indexTime, String nextTime) {
        String queryString = "source_id < 1000000000000 and indexed=0 order by time desc";
        String timeField = "time";
        return faceInfoDaoImpl.findByTime(indexTime, nextTime, timeField, queryString, 0, 5000,null);
    }

    @Override
    public Date indexNextTime(String indexTime) throws ParseException {
        String queryString = "source_id < 1000000000000 and indexed=0";
        String timeField = "time";
        return faceInfoDaoImpl.indexNextTime(indexTime, timeField, queryString, 5000);
    }

    // public List<FaceInfo> findByStationId(long id, int start, int pageSize) {
    // "SELECT * FROM " + GlobalConsts.T_NAME_FACE_INFO +
    // " WHERE source_id in (SELECT t.id FROM " +
    // GlobalConsts.T_NAME_CAMERA_INFO +
    // " t WHERE t.station_id = :id) order by time desc limit :start, :pageSize",
    // }

    @Override
    public List<FaceInfo> findLast(int page, int pageSize, String dateStr) {
        
        /*String queryString = "source_id < 1000000000000 and time < '" + dateStr + "' order by time desc limit "
        + (page - 1) * pageSize + ", " + pageSize;
*/      
        String auth = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, "");
        String queryString = "";
        if(!auth.trim().equals("")){
        queryString = "source_id < 1000000000000 and source_id in ";
        queryString = queryString + auth;   
        }else{
        queryString = "source_id < 1000000000000";  
        }       
        queryString = queryString + " and time < '" + dateStr + "' order by time desc limit "
                + (page - 1) * pageSize + ", " + pageSize;  
        System.out.println("分页获取所有有权限的摄像头最近抓拍图片sql :"+queryString);
        return faceInfoDaoImpl.findLast(queryString);
    }

    @Override
    public List<FaceInfo> findPersonInzonesByStationId(long stationId, int page, int pageSize, String time) {
        int start = (page - 1) * pageSize;
        List<Long> faceIdList = personInzonesDao.findPersonInzonesFaceIdByStationId(stationId, start, pageSize, time);
        return faceInfoDaoImpl.findByIds(faceIdList);
    }

    // @Override
    // public List<FaceQueryDto> findByCombinedConditions(FaceQueryDto
    // faceQueryDto) throws Exception {
    // List<FaceQueryDto> resp = null;
    // // String sqlString =
    // "SELECT a.id, a.time, a.source_type, a.source_id, a.image_data, a.gender, a.age, a.accessories, b.city, b.county, b.name as camera_name "
    // +
    // // "FROM " + GlobalConsts.T_NAME_FACE_INFO + " a left join " +
    // GlobalConsts.T_NAME_CAMERA_INFO +
    // " b on a.source_id = b.id and a.source_type = 0 " +
    // // "WHERE a.time BETWEEN str_to_date('" + faceQueryDto.getStartTime() +
    // "','%Y-%m-%d %T') AND str_to_date('" + faceQueryDto.getEndTime() +
    // "','%Y-%m-%d %T') ";
    // String sqlString =
    // "SELECT a.id, a.time, a.source_type, a.source_id, a.image_data, a.gender, a.age, a.accessories, b.city, b.county, b.name as camera_name "
    // +
    // "FROM " + GlobalConsts.T_NAME_FACE_INFO + " a, " +
    // GlobalConsts.T_NAME_CAMERA_INFO + " b " +
    // "WHERE  a.source_id = b.id and a.source_type = 0 AND a.time BETWEEN str_to_date('"
    // + faceQueryDto.getStartTime() + "','%Y-%m-%d %T') AND str_to_date('" +
    // faceQueryDto.getEndTime() + "','%Y-%m-%d %T') ";
    //
    // if (null != faceQueryDto.getCounty() &&
    // !"".equals(faceQueryDto.getCounty())) {
    // sqlString += "AND b.county = '" + faceQueryDto.getCounty() + "' ";
    // }
    // if (null != faceQueryDto.getCameraName() &&
    // !"".equals(faceQueryDto.getCameraName())) {
    // sqlString += "AND b.name ='" + faceQueryDto.getCameraName() + "' ";
    // }
    // if (faceQueryDto.getGender() > 0) {
    // sqlString += "AND a.gender =" + faceQueryDto.getGender() + " ";
    // }
    // if (faceQueryDto.getAccessories() > 0) {
    // sqlString += "AND a.accessories =" + faceQueryDto.getAccessories() + " ";
    // }
    // // sqlString += "AND a.age in (" + faceQueryDto.getAgeRange() +
    // ") ORDER BY a.time DESC";
    // sqlString += " ORDER BY a.time DESC";
    //
    // try {
    // Query query = this.entityManager.createNativeQuery(sqlString,
    // FaceQueryDto.class);
    // resp = (ArrayList<FaceQueryDto>) query.getResultList();
    // } catch (Exception e) {
    // LOG.error("", e);
    // } finally {
    // entityManager.close();
    // }
    // return resp;
    // }

    @Override
    public List<FaceInfo> findByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception {
        
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        int age = faceQueryDto.getAge();
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

        if (0 != faceQueryDto.getSourceId()) {
            queryString += "source_id=" + faceQueryDto.getSourceId();
        } else {

        //  queryString += "source_id < 1000000000000";
            String queryS = queryString +"source_id < 1000000000000 and source_id in ";
            String authString = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, queryS);
            if(authString!=null&&!authString.trim().equals("")){
                queryString = authString;
            }else{
              queryString += "source_id < 1000000000000"; 
            }

        }
        
        if(0 == faceQueryDto.getQuality()){
            queryString += " and quality ="+faceQueryDto.getQuality();
        }else if(0 > faceQueryDto.getQuality()){
            queryString += " and quality <="+faceQueryDto.getQuality();
        }
    
        if ((null != faceQueryDto.getDayStartTime()) && (!"".equals(faceQueryDto.getDayStartTime()))
                && (null != faceQueryDto.getDayEndTime()) && (!"".equals(faceQueryDto.getDayEndTime()))) {
            queryString += " AND date_format(time,'%H:%i:%s') between date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayStartTime() + "', '%H:%i:%s')" + " AND date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayEndTime() + "', '%H:%i:%s')";
        }

        if (faceQueryDto.getGender() > 0) {
            queryString += " AND gender =" + faceQueryDto.getGender();
        }

        if (faceQueryDto.getAccessories() > 0) {
            queryString += " AND accessories =" + faceQueryDto.getAccessories();
        }
        if (!"".equals(faceQueryDto.getWeekDay()) && !"0".equals(faceQueryDto.getWeekDay())) {
            queryString += " AND FIND_IN_SET(DAYOFWEEK(time),'" + faceQueryDto.getWeekDay() + "')";
        }
        if (faceQueryDto.getAge() != 0) {
            if (age == 1) {
                queryString += " AND age >= 1 and age <= 3 ";
            }
            if (age == 4) {
                queryString += " AND age >=4 and age <= 5 ";
            }
            if (age == 7) {
                queryString += " AND age >= 6 and age <=7 ";
            }
            if (age == 9) {
                queryString += " AND age >= 8 and age <= 9 ";
            }
        }

        if (null != aa && 1 == aa.length) {
            if (Integer.parseInt(faceQueryDto.getRace()) > 0) {
                queryString += " AND race > 0";
                queryString += " ORDER BY race DESC";// TODO
            } else {
                queryString += " AND race <= 0";
                queryString += " ORDER BY time DESC";
            }
        } else {
            queryString += " ORDER BY time DESC";
        }

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }
        resp = faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize,faceQueryDto.getLastId());
        return resp;
    }
    
    @Override
    public List<FaceInfo> findByXinghuoCombinedParams(XinghuoQuery faceQueryDto, int page, int pageSize) throws Exception {
        
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        int age = faceQueryDto.getAge();
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

        if (0 != faceQueryDto.getSourceId()) {
            queryString += "source_id=" + faceQueryDto.getSourceId();
        } else {

        //  queryString += "source_id < 1000000000000";
            String queryS = queryString +"source_id < 1000000000000 and source_id in ";
            String authString = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class,queryS);
            if(authString!=null&&!authString.trim().equals("")){
                queryString = authString;
            }else{
              queryString += "source_id < 1000000000000"; 
            }

        }
        
        if(0 == faceQueryDto.getQuality()){
            queryString += " and quality ="+faceQueryDto.getQuality();
        }else if(0 > faceQueryDto.getQuality()){
            queryString += " and quality <="+faceQueryDto.getQuality();
        }
    
        if ((null != faceQueryDto.getDayStartTime()) && (!"".equals(faceQueryDto.getDayStartTime()))
                && (null != faceQueryDto.getDayEndTime()) && (!"".equals(faceQueryDto.getDayEndTime()))) {
            queryString += " AND date_format(time,'%H:%i:%s') between date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayStartTime() + "', '%H:%i:%s')" + " AND date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayEndTime() + "', '%H:%i:%s')";
        }

        if (faceQueryDto.getGender() > 0) {
            queryString += " AND gender =" + faceQueryDto.getGender();
        }

        if (faceQueryDto.getAccessories() > 0) {
            queryString += " AND accessories =" + faceQueryDto.getAccessories();
        }
        if (!"".equals(faceQueryDto.getWeekDay()) && !"0".equals(faceQueryDto.getWeekDay())) {
            queryString += " AND FIND_IN_SET(DAYOFWEEK(time),'" + faceQueryDto.getWeekDay() + "')";
        }
        if (faceQueryDto.getAge() != 0) {
            if (age == 1) {
                queryString += " AND age >= 1 and age <= 3 ";
            }
            if (age == 4) {
                queryString += " AND age >=4 and age <= 5 ";
            }
            if (age == 7) {
                queryString += " AND age >= 6 and age <=7 ";
            }
            if (age == 9) {
                queryString += " AND age >= 8 and age <= 9 ";
            }
        }

        if (null != aa && 1 == aa.length) {
            if (Integer.parseInt(faceQueryDto.getRace()) > 0) {
                queryString += " AND race > 0";
                queryString += " ORDER BY race DESC";// TODO
            } else {
                queryString += " AND race <= 0";
                queryString += " ORDER BY time DESC";
            }
        } else {
            queryString += " ORDER BY time DESC";
        }

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }
        resp = faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize,faceQueryDto.getLastId());
        return resp;
    }

    @Override
    public List<ImageInfo> findImageByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize)
            throws Exception {
        List<ImageInfo> resp = null;
        String[] aa = null;
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }
        String selectString = "from_image_id";
        String queryString = "";

        if (0 != faceQueryDto.getSourceId()) {
            queryString += "source_id=" + faceQueryDto.getSourceId();
        } else {
            queryString += "source_id < 1000000000000";
        }
        if(0 == faceQueryDto.getQuality()){
            queryString += " and quality ="+faceQueryDto.getQuality();
        }else if( 0 > faceQueryDto.getQuality()){
            queryString += " and quality <="+faceQueryDto.getQuality();
        }
        if ((null != faceQueryDto.getDayStartTime()) && (!"".equals(faceQueryDto.getDayStartTime()))
                && (null != faceQueryDto.getDayEndTime()) && (!"".equals(faceQueryDto.getDayEndTime()))) {
            queryString += " AND date_format(time,'%H:%i:%s') between date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayStartTime() + "', '%H:%i:%s') AND date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayEndTime() + "', '%H:%i:%s')";
        }
        if (faceQueryDto.getGender() > 0) {
            queryString += " AND gender =" + faceQueryDto.getGender();
        }
        if (faceQueryDto.getAccessories() > 0) {
            queryString += " AND accessories =" + faceQueryDto.getAccessories();
        }
        if (null != aa && 1 == aa.length) {
            queryString += " AND race =" + Integer.parseInt(faceQueryDto.getRace());
        }
        if (!"".equals(faceQueryDto.getWeekDay()) && !"0".equals(faceQueryDto.getWeekDay())) {
            queryString += " AND FIND_IN_SET(DAYOFWEEK(time),'" + faceQueryDto.getWeekDay() + "')";
        }
        queryString += " ORDER BY time DESC";

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }

        List<BigInteger> imageIdList = faceInfoDaoImpl.findByTime(selectString, startTime, endTime, timeField,
                queryString, BigInteger.class, page, pageSize, false,faceQueryDto.getLastId());
        resp = imageInfoDaoImpl.findByIds(imageIdList);

        return resp;
    }

    @Override
    public Long countToday() {
        String selectString = "count(1)";
        String queryString = "source_id < 1000000000000";
        Date now = new Date();
        String startTime = DateUtil.getDateString(now);
        String endTime = DateUtil.getDateString(new Date(now.getTime() + 24 * 3600 * 1000));
        String timeField = "time";
        int page = 1;
        int pageSize = Integer.MAX_VALUE;
        List<BigInteger> count = faceInfoDaoImpl.findByTime(selectString, startTime, endTime, timeField, queryString,
                BigInteger.class, page, pageSize, false,null);
        if(count!=null&&count.size()!=0){
            return count.get(0).longValue();
        }else{
            return 0L;
        }
        
    }
    
    
    @Override
    public Long countMinute() {
        
        long rCount = 0;
        TableRecord table = tableRecordDao.getCurTable(DateUtil.getDateString(new Date()), GlobalConsts.T_FACE_PRE);
        String sql = "select max(sequence) from "+GlobalConsts.INTELLIF_FACE+"."+table.getTableName();
        long maxSequence = imageInfoDaoImpl.countResult(sql);
        if(maxSequence - GlobalConsts.faceSequence > 0 && GlobalConsts.faceSequence != 0){
            rCount = maxSequence - GlobalConsts.faceSequence;
        }
        GlobalConsts.faceSequence = maxSequence;
        return rCount;
    }

    @Override
    public List<Object[]> statisticYesterdayByCamera() {
        String selectString = "source_id, date(time), count(1)";
        String queryString = "source_id < 1000000000000 GROUP BY source_id, date(time)";
        Date now = new Date();
        String startTime = DateUtil.getDateString(new Date(now.getTime() - 24 * 3600 * 1000));
        String endTime = DateUtil.getDateString(now);
        String timeField = "time";
        int page = 1;
        int pageSize = Integer.MAX_VALUE;
        List<Object[]> count = faceInfoDaoImpl.findByTime(selectString, startTime, endTime, timeField, queryString,
                Object[].class, page, pageSize, false,null);
        return count;
    }
    
    @Override
    public List<Object[]> statisticQualityYesterdayByCamera(Date date) {
        String selectString = "quality,source_id, date(time), count(1)";
        String queryString = "source_id < 1000000000000 GROUP BY quality, source_id, date(time)";
        String startTime = DateUtil.getDateString(new Date(date.getTime() - 24 * 3600 * 1000));
        String endTime = DateUtil.getDateString(date);
        String timeField = "time";
        int page = 1;
        int pageSize = Integer.MAX_VALUE;
        List<Object[]> count = faceInfoDaoImpl.findByTime(selectString, startTime, endTime, timeField, queryString,
                Object[].class, page, pageSize, false,null);
        return count;
    }

    @Override
    public Long statisticByDistict(long id) {

        BigDecimal resp = null;
        String sqlString = "select sum(count) from (SELECT SUM(count) as count FROM "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_FACE_CAMERA_COUNT+" WHERE source_id in" 
+"(SELECT c.id from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_DISTRICT+" d LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_AREA+" a on d.id = a.district_id"
+" LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_CAMERA_INFO+" c on c.station_id = a.id WHERE d.id = "+id+") union "+  
     "SELECT SUM(count) as count FROM "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_FACE_CAMERA_COUNT+" WHERE source_id in" 
                +"(SELECT c.id from "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_DISTRICT+" d LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_OTHER_AREA+" a on d.id = a.district_id"
                +" LEFT JOIN "+GlobalConsts.INTELLIF_BASE+"."+GlobalConsts.T_NAME_OTHER_CAMERA_INFO+" c on c.station_id = a.id WHERE d.id = "+id+"))t"; 
                        
        try {
            Query query = this.entityManager.createNativeQuery(sqlString);
            resp = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
        LOG.error("", e);
        } finally {
            entityManager.close();
        }
        if(null == resp){
            resp = new BigDecimal("0");
        }   
        return resp.longValue();    
    
    }   


    /**
     * 超级管理员删除图片
     */
    
    @Override
    @Transactional
    public void deleteFaceById(long faceId) throws Exception {
        FaceInfo fi = findOne(faceId);
        if (null != fi) {
            
            if(fi.getIndexed() == 1){
                DeleteSolrFaceDataThread.faceQueue.offer(faceId);
            }
            jdbcTemplate.update(INSERT_FACE_DELETE, fi.getId(), fi.getAccessories(), fi.getRace(), fi.getAge(), fi.getFaceFeature(), fi.getFromImageId(),
                    fi.getFromPersonId(), fi.getFromVideoId(), fi.getGender(), fi.getImageData(), fi.getIndexed(), fi.getSourceId(), fi.getSourceType(),
                    fi.getTime(), fi.getVersion(), fi.getJson(), fi.getSequence(), fi.getQuality());


            String delSql = "delete  from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_FACE_PRE + "_" + CommonUtil.getCode(faceId) + " where id = "
                    + faceId;
            jdbcTemplate.execute(delSql);

            List<AuditLogInfo> logList = logDao.findByObjectId(faceId);
            if(null != logList && !logList.isEmpty()){
                List<Long> idList = logList.stream().map(item -> item.getId()).collect(Collectors.toList());
                DeleteSolrFaceDataThread.searchFaceQueue.addAll(idList);
            }
            logDao.deleteByObjectId(faceId);
          
            addAuditLogOfDeleteFaceEvent(fi);
            
            alarmDao.deleteByFaceId(faceId);
            

        }

    }   
            
    private void addAuditLogOfDeleteFaceEvent(FaceInfo fi) {
        UserInfo userinfo = CurUserInfoUtil.getUserInfo();
        RoleInfo roleinfo = CurUserInfoUtil.getRoleInfo();
        Long policeStationId = userinfo.getPoliceStationId();
        String stationname = policestationDao.findOne(policeStationId).getStationName(); // 单位名称
        String userrealname = userinfo.getName();
        String user = userinfo.getLogin();
        String accounttype = roleinfo.getCnName();
        AuditLogInfo log = new AuditLogInfo();
        log.setOwner(user);
        log.setOperation("delete");
        log.setObject(GlobalConsts.T_NAME_FACE_DELETE_INFO);
        log.setObjectId(fi.getId());
        log.setObject_status(1);

        log.setTitle(log.getOwner() + "删除了一张图片," + userrealname + "," + stationname);
        log.setMessage(accounttype + user + "删除了一张图片");
        log.setObject_status(17);
        auditLogRepository.save(log);
        LOG.info("delete face record face id:" + fi.getId());
    }
 @Override
    public List<FaceInfo> findFkByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize)
            throws Exception {
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

       // List<TreeNode> cameraInfoList = zoneAuthorizeService.offspring(CameraInfo.class, faceQueryDto.getStationId(),
        List<TreeNode> cameraInfoList = zoneAuthorizeService.offspring(Area.class, faceQueryDto.getStationId(),
                CameraInfo.class);
        Set<Long> cameraIdSet = TreeUtil.idMap(cameraInfoList).get(CameraInfo.class);
        if (faceQueryDto.getStationId() == 0) {
            queryString += " source_id < 1000000000000"; 
        } else {
            if (CollectionUtils.isNotEmpty(cameraIdSet)) {
                String ids = StringUtils.join(cameraIdSet, ",");
                queryString += " source_id in (" + ids + " )";
            }else {
                return new ArrayList<>();
            }
            
        }

        if (0 == faceQueryDto.getQuality()) {
            queryString += " and quality =" + faceQueryDto.getQuality();
        } else if (0 > faceQueryDto.getQuality()) {
            queryString += " and quality <=" + faceQueryDto.getQuality();
        }

        if ((null != faceQueryDto.getDayStartTime()) && (!"".equals(faceQueryDto.getDayStartTime()))
                && (null != faceQueryDto.getDayEndTime()) && (!"".equals(faceQueryDto.getDayEndTime()))) {
            queryString += " AND date_format(time,'%H:%i:%s') between date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayStartTime() + "', '%H:%i:%s')" + " AND date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayEndTime() + "', '%H:%i:%s')";
        }

        if (faceQueryDto.getGender() > 0) {
            queryString += " AND gender =" + faceQueryDto.getGender();
        }

        if (faceQueryDto.getAccessories() > 0) {
            queryString += " AND accessories =" + faceQueryDto.getAccessories();
        }
        if (!"".equals(faceQueryDto.getWeekDay()) && !"0".equals(faceQueryDto.getWeekDay())) {
            queryString += " AND FIND_IN_SET(DAYOFWEEK(time),'" + faceQueryDto.getWeekDay() + "')";
        }

        if (null != aa && 1 == aa.length) {
            if (Integer.parseInt(faceQueryDto.getRace()) > 0) {
                queryString += " AND race > 0";
                queryString += " ORDER BY race DESC";// TODO
            } else {
                queryString += " AND race <= 0";
                queryString += " ORDER BY time DESC";
            }
        } else {
            queryString += " ORDER BY time DESC";
        }

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }
       // resp = faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize);
        resp = faceInfoDaoImpl.findByTimeLatest(startTime, endTime, timeField, queryString, null, page, pageSize);
        return resp;
    }

    @Override
    public List<FaceInfo> findByFkPlace(FindFkPlaceFaceDto findFkPlaceFaceDto, int page, int pageSize) throws Exception {

        List<FaceInfo> resp = null;

        String cameraIds = findFkPlaceFaceDto.getCameraIds();

        String queryString = "source_id < 1000000000000 and source_id in (" + cameraIds + ") ORDER BY time DESC";

        String timeField = "time";

        String startTime = "";
        String endTime = "";

        if (!findFkPlaceFaceDto.getStartTime().isEmpty()) {
            startTime = findFkPlaceFaceDto.getStartTime();
        }
        if (!findFkPlaceFaceDto.getEndTime().isEmpty()) {
            endTime = findFkPlaceFaceDto.getEndTime();
        }

        resp = faceInfoDaoImpl.findByTime(startTime, endTime, timeField, queryString, page, pageSize, findFkPlaceFaceDto.getLastId());
        return resp;
    }
}