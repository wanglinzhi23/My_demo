package intellif.share.service.impl;


import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.dto.QueryFaceDto;
import intellif.service.AlarmServiceItf;
import intellif.share.service.SharedFaceServiceItf;
import intellif.utils.DateUtil;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;
import intellif.zoneauthorize.util.ZoneAuthorizeUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SharedFaceServiceImpl implements SharedFaceServiceItf {

    private static Logger LOG = LogManager.getLogger(SharedFaceServiceImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    FaceInfoDaoImpl faceInfoDaoImpl;

    @Autowired
    ZoneAuthorizeServiceItf zoneAuthorizeService;

    @Autowired
    ImageInfoDaoImpl imageInfoDaoImpl;
    @Autowired
    private AlarmServiceItf _alarmService;
    @Override
    public FaceInfo findOne(long id) {
        List<Long> idList = new ArrayList<Long>();
        idList.add(id);
        List<FaceInfo> faceList = faceInfoDaoImpl.findByIds(idList);
        if (null != faceList && !faceList.isEmpty()) {
            return faceInfoDaoImpl.findByIds(idList).get(0);
        } else {
            return null;
        }
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
    public List<FaceInfo> findByCombinedParams(QueryFaceDto faceQueryDto, long districtId,
                                               int page, int pageSize) throws Exception {
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

        queryString = buildQuerySQL(faceQueryDto, districtId, "", true);

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }

        String ids = "";
        if (districtId > 0) {
            List<BigInteger> idList = querySourceIdsByDistrict(districtId);
            List<Long> tempList = zoneAuthorizeService.filterIds(CameraInfo.class, ZoneAuthorizeUtil.convertList(idList), null);
            if (CollectionUtils.isNotEmpty(tempList))
                ids = StringUtils.join(tempList, ",");
        } else {
            ids = String.valueOf(faceQueryDto.getSourceId());
        }

        resp = faceInfoDaoImpl.findByTimeUsingStatistic(startTime, endTime, timeField,
                queryString, page, pageSize,faceQueryDto.getLastId(), ids);

        return resp;
    }



    private List<BigInteger> querySourceIdsByDistrict(long districtId) {
        String sqlString = "select c.id from t_camera_info c " +
        "left join t_area a on c.station_id = a.id where a.district_id = " + districtId +
                " union select oc.id from t_other_camera oc left join t_other_area oa " +
                "on oc.station_id = oa.id where oa.district_id = " + districtId;

        List<BigInteger> resp = null;
        try {
            Query query = this.entityManager.createNativeQuery(sqlString);
            resp = query.getResultList();
        } catch (Exception e) {
            LOG.error("querySourceIdsByDistrict districtId:" + districtId + " method error:", e);
        } finally {
            entityManager.close();
        }

        if (resp == null) {
            resp = new ArrayList<BigInteger>();
        }
        return resp;
    }


    private String buildQuerySQL(QueryFaceDto faceQueryDto, long districtId, String faceTableAlias, boolean includeOrderBy) {
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        int age = faceQueryDto.getAge();
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }


        String prefix = "";
        if (faceTableAlias == null || faceTableAlias.trim().equals("")) {
            prefix = "";
        } else {
            prefix = faceTableAlias + ".";
        }
        if (0 != faceQueryDto.getSourceId()) {
            queryString += prefix + "source_id=" + faceQueryDto.getSourceId();
        } else {
            queryString += prefix + "source_id < 1000000000000"; 
        }

        if (districtId > 0) {
            List<BigInteger> idList = querySourceIdsByDistrict(districtId);
            List<Long> tempList = zoneAuthorizeService.filterIds(CameraInfo.class, ZoneAuthorizeUtil.convertList(idList), null);
            if (CollectionUtils.isNotEmpty(tempList)) {
                String ids = StringUtils.join(tempList, ",");
                queryString += " AND " + prefix + "source_id in (" + ids + " )";
            } else {
                queryString += " AND 1 = 2 ";
            }
        } else {
            Set<Long> tempSet = zoneAuthorizeService.idSet(CameraInfo.class, null);
            if (CollectionUtils.isNotEmpty(tempSet)) {
                String ids = StringUtils.join(tempSet, ",");
                queryString += " AND " + prefix + "source_id in (" + ids + " )";
            } else {
                queryString += " AND 1 = 2 ";
            }
        }
        
        
        if (0 == faceQueryDto.getQuality()) {
            queryString += " and quality =" + faceQueryDto.getQuality();
        } else if (0 > faceQueryDto.getQuality()) {
            queryString += " and quality <=" + faceQueryDto.getQuality();
        }

        if (0 == faceQueryDto.getQuality()) {
            queryString += " and quality =" + faceQueryDto.getQuality();
        } else if (0 > faceQueryDto.getQuality()) {
            queryString += " and quality <=" + faceQueryDto.getQuality();
        }

        if ((null != faceQueryDto.getDayStartTime()) && (!"".equals(faceQueryDto.getDayStartTime()))
                && (null != faceQueryDto.getDayEndTime()) && (!"".equals(faceQueryDto.getDayEndTime()))) {
            queryString += " AND date_format(" + prefix + "time,'%H:%i:%s') between date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayStartTime() + "', '%H:%i:%s')" + " AND date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayEndTime() + "', '%H:%i:%s')";
        }

        if (faceQueryDto.getGender() > 0) {
            queryString += " AND " + prefix + "gender =" + faceQueryDto.getGender();
        }

        if (faceQueryDto.getAccessories() > 0) {
            queryString += " AND " + prefix + "accessories =" + faceQueryDto.getAccessories();
        }
        if (!"".equals(faceQueryDto.getWeekDay()) && !"0".equals(faceQueryDto.getWeekDay())) {
            queryString += " AND FIND_IN_SET(DAYOFWEEK(" + prefix + "time),'" + faceQueryDto.getWeekDay() + "')";
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
                queryString += " AND " + prefix + "race > 0";
                if (includeOrderBy) {
                    queryString += " ORDER BY " + prefix + "race DESC ";// TODO
                }
            } else {
                queryString += " AND " + prefix + "race <= 0";

                if (includeOrderBy) {
                    queryString += " ORDER BY " + prefix + "time DESC ";
                }
            }
        } else {
            if (includeOrderBy) {
                queryString += " ORDER BY " + prefix + "time DESC ";
            }
        }
        return queryString;
    }

    
    @Override
    public List<ImageInfo> findImageByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize)
            throws Exception {
        List<ImageInfo> resp = null;
        String[] aa = null;
        int age = faceQueryDto.getAge();
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
        queryString += " ORDER BY time DESC ";

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
    public List<FaceInfo> findByMultipleCameras(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception {
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

        List<Long> sourceIdList = new ArrayList<>();
        String[] sourceIds = faceQueryDto.getSourceIds();
        for (int i = 0; i < sourceIds.length; i++) {
            sourceIdList.add(Long.valueOf(sourceIds[i]));
        }
        Set<Long> cameraIdSet = new HashSet<>(zoneAuthorizeService.filterIds(CameraInfo.class, sourceIdList, null));
        queryString = buildQuerySQL(faceQueryDto, cameraIdSet, "", true);

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }

        String ids = StringUtils.join(cameraIdSet, ",");
        resp = faceInfoDaoImpl.findByTimeUsingStatistic(startTime, endTime, timeField, queryString, page, pageSize,faceQueryDto.getLastId(), ids);

        return resp;
    }

    @Override
    public List<FaceInfo> findByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception {
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

        Class<? extends TreeNode> clazz = ZoneConfig.getNodeTypeMap().get(faceQueryDto.getNodeType());
        List<TreeNode> cameraList = zoneAuthorizeService.offspring(
                ZoneConfig.getNodeTypeMap().get(faceQueryDto.getNodeType()), faceQueryDto.getNodeId(), CameraInfo.class);
        Set<Long> cameraIdSet = TreeUtil.idMap(cameraList).get(CameraInfo.class);
        queryString = buildQuerySQL(faceQueryDto, cameraIdSet, "", true);
        // String latestTimeQueryString = buildQuerySQL(faceQueryDto, cameraIdSet, "", false);

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }

        String ids = StringUtils.join(cameraIdSet, ",");
        resp = faceInfoDaoImpl.findByTimeUsingStatistic(startTime, endTime, timeField, queryString, page, pageSize,faceQueryDto.getLastId(), ids);

        return resp;
    }   
    
    private String buildQuerySQL(QueryFaceDto faceQueryDto, Collection<Long> cameraIdSet, String faceTableAlias, boolean includeOrderBy) {
        List<FaceInfo> resp = null;
        String queryString = "";
        String[] aa = null;
        int age = faceQueryDto.getAge();

        if (null != faceQueryDto.getRace() && !"".equals(faceQueryDto.getRace())) {
            aa = faceQueryDto.getRace().split(",");
        }

        String prefix = "";
        if (faceTableAlias == null || faceTableAlias.trim().equals("")) {
            prefix = "";
        } else {
            prefix = faceTableAlias + ".";
        }
        if (0 != faceQueryDto.getSourceId()) {
            queryString += prefix + "source_id=" + faceQueryDto.getSourceId();
        } else {
            queryString += prefix + "source_id < 1000000000000"; 
        }

        if (CollectionUtils.isNotEmpty(cameraIdSet)) {
                String ids = StringUtils.join(cameraIdSet, ",");
                queryString += " AND " + prefix + "source_id in (" + ids + " )";
        } else {
            queryString += " AND 1 = 2 ";
        }
        
        
        if (0 == faceQueryDto.getQuality()) {
            queryString += " and quality =" + faceQueryDto.getQuality();
        } else if (0 > faceQueryDto.getQuality()) {
            queryString += " and quality <=" + faceQueryDto.getQuality();
        }

        if ((null != faceQueryDto.getDayStartTime()) && (!"".equals(faceQueryDto.getDayStartTime()))
                && (null != faceQueryDto.getDayEndTime()) && (!"".equals(faceQueryDto.getDayEndTime()))) {
            queryString += " AND date_format(" + prefix + "time,'%H:%i:%s') between date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayStartTime() + "', '%H:%i:%s')" + " AND date_format(" + "'00-00-00 "
                    + faceQueryDto.getDayEndTime() + "', '%H:%i:%s')";
        }

        if (faceQueryDto.getGender() > 0) {
            queryString += " AND " + prefix + "gender =" + faceQueryDto.getGender();
        }

        if (faceQueryDto.getAccessories() > 0) {
            queryString += " AND " + prefix + "accessories =" + faceQueryDto.getAccessories();
        }
        if (!"".equals(faceQueryDto.getWeekDay()) && !"0".equals(faceQueryDto.getWeekDay())) {
            queryString += " AND FIND_IN_SET(DAYOFWEEK(" + prefix + "time),'" + faceQueryDto.getWeekDay() + "')";
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
                queryString += " AND " + prefix + "race > 0";
                if (includeOrderBy) {
                    queryString += " ORDER BY " + prefix + "race DESC ";// TODO
                }
            } else {
                queryString += " AND " + prefix + "race <= 0";
                
                if (includeOrderBy) {
                    //适应大运逻辑
                    Long sequence = faceQueryDto.getSequence();
                    if(sequence!=null){
                        queryString += " AND sequence>="+sequence+" ORDER BY " + prefix + "sequence DESC ";
                    }else{
                        queryString += " ORDER BY " + prefix + "time DESC ";
                    }
                }
            }
        } else {
            if (includeOrderBy) {
              //适应大运逻辑
                Long sequence = faceQueryDto.getSequence();
                if(sequence!=null){
                    queryString += " AND sequence>="+sequence+" ORDER BY " + prefix + "sequence DESC ";
                }else{
                    queryString += " ORDER BY " + prefix + "time DESC ";
                }
            }
        }
        return queryString;
    }

    @Override
    public List<FaceInfo> findByMultipleCamerasForDayun(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception {
        List<FaceInfo> resp = null;
        String queryString = "";
        List<Long> sourceIdList = new ArrayList<>();
        String[] sourceIds = faceQueryDto.getSourceIds();
        for (int i = 0; i < sourceIds.length; i++) {
            sourceIdList.add(Long.valueOf(sourceIds[i]));
        }
        Set<Long> cameraIdSet = new HashSet<>(zoneAuthorizeService.filterIds(CameraInfo.class, sourceIdList, null));
        
        queryString = buildQuerySQL(faceQueryDto, cameraIdSet, "", true);

        String startTime = "";
        String endTime = "";
        String timeField = "time";
        if (null != faceQueryDto.getStarttime() && !"".equals(faceQueryDto.getStarttime())) {
            startTime = faceQueryDto.getStarttime();
        }
        if (null != faceQueryDto.getEndtime() && !"".equals(faceQueryDto.getEndtime())) {
            endTime = faceQueryDto.getEndtime();
        }
        
        resp = faceInfoDaoImpl.findByTimeOffsetForDayun(startTime, endTime, timeField, queryString, page, pageSize, faceQueryDto.getSequence());

        return resp;
    }
    

}
