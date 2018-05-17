package intellif.dao.impl;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import intellif.chd.dto.FaceQuery;
import intellif.chd.settings.CameraNodeIdSetting;
import intellif.consts.GlobalConsts;
import intellif.utils.ApplicationResource;
import intellif.utils.CommonUtil;
import intellif.utils.DateUtil;
import intellif.utils.FunctionUtil;
import intellif.utils.SqlUtil;
import intellif.database.entity.AreaCameraStatistic;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.FaceStatisticCount;
import intellif.database.entity.StatisticDataQuery;
import intellif.database.entity.TableRecord;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@Service
public class FaceInfoDaoImpl extends MultiTableBaseDaoImpl<FaceInfo> {

    private static Logger LOG = LogManager.getLogger(FaceInfoDaoImpl.class);

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ZoneAuthorizeServiceItf zoneAuthorizeService;

    public static final int LIMIT = 50000;

    public static final long STEP_TIME = 2 * 60 * 60 * 1000L;

    public static long cameraNodeId = CameraNodeIdSetting.getNodeId();

    @Override
    @Transactional
    public FaceInfo save(FaceInfo entity) {
        int resp = 0;
        String sql;
        boolean isNew = (null == entity.getId() || entity.getId() == 0);
        if (isNew) {
            TableRecord table = tableRecordDao.getCurTable(DateUtil.getDateString(new Date()), GlobalConsts.T_FACE_PRE);
            long id = CommonUtil.createId(table.getTableCode());
            entity.setId(id);
            sql = "INSERT INTO " + GlobalConsts.INTELLIF_FACE + "." + table.getTableName()
                    + "(id,accessories,race,age,face_feature,from_image_id,from_person_id,from_video_id,gender,image_data,indexed,source_id,source_type,time,version,json) "
                    + "VALUES (:id,:accessories,:race,:age,:face_feature,:from_image_id,:from_person_id,:from_video_id,:gender,:image_data,:indexed,:source_id,:source_type,:time,:version,:json)";
        } else {
            long code = CommonUtil.getCode(entity.getId());
            sql = "UPDATE " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_INFO + "_" + code
                    + " SET accessories = :accessories,race = :race,age = :age,from_image_id = :from_image_id,from_person_id = :from_person_id,from_video_id = :from_video_id,gender = :gender,"
                    + "image_data = :image_data,indexed = :indexed,source_id = :source_id,source_type = :source_type,time = :time,version = :version,json = :json WHERE id = :id";
        }
        try {
            Query query = this.entityManager.createNativeQuery(sql);
            query.setParameter("id", entity.getId());
            if (isNew) {
                query.setParameter("face_feature", entity.getFaceFeature());
            }
            query.setParameter("accessories", entity.getAccessories());
            query.setParameter("race", entity.getRace());
            query.setParameter("age", entity.getAge());
            query.setParameter("from_image_id", entity.getFromImageId());
            query.setParameter("from_person_id", entity.getFromPersonId());
            query.setParameter("from_video_id", entity.getFromVideoId());
            query.setParameter("gender", entity.getGender());
            query.setParameter("image_data", entity.getImageData());
            query.setParameter("indexed", entity.getIndexed());
            query.setParameter("source_id", entity.getSourceId());
            query.setParameter("source_type", entity.getSourceType());
            query.setParameter("time", entity.getTime());
            query.setParameter("version", entity.getVersion());
            query.setParameter("json", entity.getJson());
            query.executeUpdate();
        } catch (Exception e) {
            LOG.error("出错Sql：" + sql);
            LOG.error("ERROR:", e);
        } finally {
            entityManager.close();
        }
        return entity;
    }

    @Override
    @Transactional
    public Integer update(final List<FaceInfo> faceList) {
        if (null == faceList || faceList.isEmpty())
            return 0;
        Map<Long, List<FaceInfo>> codeMap = new HashMap<Long, List<FaceInfo>>();
        for (FaceInfo face : faceList) {
            long code = CommonUtil.getCode(face.getId());
            if (codeMap.containsKey(code)) {
                codeMap.get(code).add(face);
            } else {
                List<FaceInfo> faceListByCode = new ArrayList<FaceInfo>();
                faceListByCode.add(face);
                codeMap.put(code, faceListByCode);
            }
        }

        Iterator<Entry<Long, List<FaceInfo>>> iter = codeMap.entrySet().iterator();

        int num = 0;
        while (iter.hasNext()) {
            Entry<Long, List<FaceInfo>> entry = iter.next();
            String sql = "UPDATE " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_INFO + "_" + entry.getKey()
                    + " SET accessories = ?,race = ?,age = ?,from_image_id = ?,from_person_id = ?,"
                    + "from_video_id = ?,gender = ?,image_data = ?,indexed = ?,source_id = ?,source_type = ?,time = ?,version = ?,json = ? WHERE id = ?";
            final List<FaceInfo> faceListByCode = entry.getValue();
            BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    FaceInfo entity = faceListByCode.get(i);
                    ps.setInt(1, entity.getAccessories());
                    ps.setInt(2, entity.getRace());
                    ps.setInt(3, entity.getAge());
                    ps.setLong(4, entity.getFromImageId());
                    if (entity.getFromPersonId() == null) {
                        ps.setNull(5, Types.BIGINT);
                    } else {
                        ps.setLong(5, entity.getFromPersonId());
                    }
                    if (entity.getFromVideoId() == null) {
                        ps.setNull(6, Types.BIGINT);
                    } else {
                        ps.setLong(6, entity.getFromVideoId());
                    }
                    ps.setInt(7, entity.getGender());
                    ps.setString(8, entity.getImageData());
                    ps.setInt(9, entity.getIndexed());
                    ps.setLong(10, entity.getSourceId());
                    ps.setInt(11, entity.getSourceType());
                    ps.setTimestamp(12, new Timestamp(entity.getTime().getTime()));
                    ps.setInt(13, entity.getVersion());
                    if (entity.getJson() == null) {
                        ps.setNull(14, Types.VARCHAR);
                    } else {
                        ps.setString(14, entity.getJson());
                    }
                    ps.setLong(15, entity.getId());
                }

                public int getBatchSize() {
                    return faceListByCode.size();
                }
            };
            num += jdbcTemplate.batchUpdate(sql, setter).length;
        }

        return num;
    }

    // 根据 sequence 获取未索引数据
    public List<FaceInfo> findLastUnindexed(Date date, String queryString, int pagesize) {
        List<FaceInfo> respList = new ArrayList<FaceInfo>();
        List<TableRecord> resp = tableRecordDao.find2LastOrderByTime(DateUtil.getDateString(date), "t_face");
        if (null == resp || resp.isEmpty())
            return null;

        for (TableRecord tableUnit : resp) {
            if (GlobalConsts.INDEX_TABLE_MAP.containsKey(tableUnit.getShortName())
                    && tableUnit.getTableCode() < GlobalConsts.INDEX_TABLE_MAP.get(tableUnit.getShortName()))
                continue;

            long sequence = 0;
            if (GlobalConsts.INDEX_TABLE_MAP.containsKey(tableUnit.getTableName())) {
                sequence = GlobalConsts.INDEX_TABLE_MAP.get(tableUnit.getTableName());
            }

            // 拼装Sql
            String sqlUnit = "SELECT * FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " WHERE sequence > :sequence";
            if (null != queryString && !queryString.isEmpty()) {
                sqlUnit += (" and " + queryString);
            }
            sqlUnit += "  order by sequence asc limit :start, :pageSize";
            LOG.info(sqlUnit);

            try {
                Query query = this.entityManager.createNativeQuery(sqlUnit, FaceInfo.class);
                query.setParameter("sequence", sequence).setParameter("start", 0).setParameter("pageSize", (pagesize - respList.size()));
                List<FaceInfo> faceResp = (ArrayList<FaceInfo>) query.getResultList();
                respList.addAll(faceResp);
            } catch (Exception e) {
                LOG.error("出错Sql：" + sqlUnit);
                LOG.error("ERROR:", e);
                ;
            } finally {
                entityManager.close();
            }
            if (respList.size() >= pagesize) {
                GlobalConsts.INDEX_TABLE_MAP.put(tableUnit.getTableName(), sequence + pagesize);
                break;
            } else {
                GlobalConsts.INDEX_TABLE_MAP.put(tableUnit.getShortName(), tableUnit.getTableCode() + 1);
                break;
            }
        }

        return respList;
    }

    public Date indexNextTime(String startTime, String timeField, String queryString, int pagesize) throws ParseException {
        String endTime = DateUtil.getformatDate(new Date().getTime());
        Date indexTime = DateUtil.getFormatDate(startTime, "yyyy-MM-dd HH:mm:ss");
        Timestamp nextTime = null;
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);
        if (null == resp || resp.isEmpty())
            return indexTime;

        TableRecord tableUnit = resp.get(0);
        // 拼装Sql
        String sqlUnit = "SELECT max(time) FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " WHERE " + timeField + " >= :startTime";
        if (null != queryString && !queryString.isEmpty()) {
            sqlUnit += (" and " + queryString);
        }
        sqlUnit += " limit 0, :pageSize";
        LOG.info(sqlUnit);

        try {
            Query query = this.entityManager.createNativeQuery(sqlUnit);
            query.setParameter("startTime", startTime).setParameter("pageSize", pagesize);
            nextTime = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            LOG.error("出错Sql：" + sqlUnit);
            LOG.error("ERROR:", e);
            return tableUnit.getEndTime();
        } finally {
            entityManager.close();
        }

        if (nextTime == null) {
            if (resp.size() > 1) {
                return tableUnit.getEndTime();
            } else {
                return indexTime;
            }
        }

        if ((indexTime.getTime() - nextTime.getTime() == 0 || tableUnit.getEndTime().getTime() - nextTime.getTime() < 60 * 1000) && resp.size() > 1) {
            return tableUnit.getEndTime();
        }

        return nextTime;
    }

    @Transactional
    public boolean updateIndexedBySequence(long code, long start, long end) {
        String sql = "UPDATE " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_INFO + "_" + code + " SET indexed = 1 WHERE sequence >=" + start
                + " and sequence<=" + end + " and source_id < 100000000";
        try {
            Query query = this.entityManager.createNativeQuery(sql);
            query.executeUpdate();
        } catch (Exception e) {
            LOG.error("出错Sql：" + sql);
            LOG.error("ERROR:", e);
            return false;
        } finally {
            entityManager.close();
        }
        return true;
    }

    @Transactional
    public boolean updateIndexedByTime(long code, String indexTime, String nextTime) {
        String sql = "UPDATE " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_INFO + "_" + code + " SET indexed = 1 WHERE time >='" + indexTime
                + "' and time<'" + nextTime + "' and source_id < 100000000";
        try {
            Query query = this.entityManager.createNativeQuery(sql);
            query.executeUpdate();
        } catch (Exception e) {
            LOG.error("出错Sql：" + sql);
            LOG.error("ERROR:", e);
            return false;
        } finally {
            entityManager.close();
        }
        return true;
    }

    public Map<Long, List<FaceStatisticCount>> findByTimeASC(StatisticDataQuery queryobject) {
        List<FaceStatisticCount> respList = Collections.synchronizedList(new ArrayList<FaceStatisticCount>());
        List<TableRecord> resp;
        String startTime = queryobject.getStarttime();
        String endTime = queryobject.getEndtime();
        Long[] cameraids = queryobject.getCameraids();
        int timeslot = queryobject.getTimeslot();
        int quality = queryobject.getQuality();
        resp = tableRecordDao.findTableByTimeASC(startTime, endTime, tableShortName);

        if (null == resp || resp.isEmpty())
            return null;
        List<Future<?>> tasklist = new ArrayList<>();
        for (TableRecord tableUnit : resp) {
            final StringBuilder sqlStringBuilder = new StringBuilder(
                    "select r.id, r.source_id, r.gtime as time, count(*) as count from (SELECT face.id, face.source_id, ");
            switch (timeslot) {
            case 0:
                sqlStringBuilder.append("DATE_FORMAT(face.time, '%Y-%m-%d %H:%i:%s') as gtime FROM ");
                break;
            case 1:
                sqlStringBuilder.append("DATE_FORMAT(face.time, '%Y-%m-%d %H:%i') as gtime FROM ");
                break;
            case 2:
                sqlStringBuilder.append("DATE_FORMAT(face.time, '%Y-%m-%d %H') as gtime FROM ");
                break;
            case 3:
                sqlStringBuilder.append("DATE_FORMAT(face.time, '%Y-%m-%d') as gtime FROM ");
                break;
            }
            sqlStringBuilder.append(GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " face IGNORE INDEX(t_face_time) ");

            if (cameraids[0] != 0) {
                sqlStringBuilder.append("WHERE time >= '" + startTime + "' and time <= '" + endTime + "'");
                sqlStringBuilder.append(" and face.source_id in (:cameraids)");
            } else {
                sqlStringBuilder.append(", " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO + " camera ");
                sqlStringBuilder.append("WHERE face.time >= '" + startTime + "' and face.time <= '" + endTime + "' and face.source_id = camera.id");
            }
            if (0 == quality) {
                sqlStringBuilder.append(" and face.quality = 0 ");
            } else if (0 > quality) {
                sqlStringBuilder.append(" and face.quality <= " + quality);
            }
            sqlStringBuilder.append(") r GROUP BY gtime, source_id order by gtime ");

            tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
                try {
                    Query query = entityManager.createNativeQuery(sqlStringBuilder.toString(), FaceStatisticCount.class);
                    if (cameraids[0] != 0) {
                        query.setParameter("cameraids", Arrays.asList(cameraids));
                    }
                    List<FaceStatisticCount> faceRespObject = query.getResultList();
                    respList.addAll(faceRespObject);
                } catch (Exception e) {
                    LOG.error("ERROR: " + e);
                    e.printStackTrace();
                } finally {
                    // entityManager.close();
                }
            }));
        }
        // 等待线程结束
        tasklist.forEach(FunctionUtil::waitTillThreadFinish);
        Collections.sort(respList, (m, n) -> m.getTime().compareTo(n.getTime()));
        Map<Long, List<FaceStatisticCount>> respon = respList.stream().collect(Collectors.groupingBy(FaceStatisticCount::getSourceId));
        return respon;
    }

    public List<AreaCameraStatistic> findStatisticCameraData(StatisticDataQuery queryobject) {
        List<AreaCameraStatistic> respList = Collections.synchronizedList(new ArrayList<>());
        List<TableRecord> resp = tableRecordDao.findTableByTimeASC(queryobject.getStarttime(), queryobject.getEndtime(), tableShortName);
        int quality = queryobject.getQuality();
        if (null == resp || resp.isEmpty())
            return null;

        String cameraStatement = "";
        if (queryobject.getCameraids()[0] == 0) {
            cameraStatement = zoneAuthorizeService.ucsqlManipulate(CameraInfo.class, " and camera.id in");
        } else {
            List<Long> cameraidList = zoneAuthorizeService.filterIds(CameraInfo.class, Arrays.asList(queryobject.getCameraids()), null);
            cameraStatement = " and camera.id in (" + cameraidList.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(",")) + ") ";
        }

        final String cameraState = cameraStatement;
        List<Future<?>> tasklist = new ArrayList<>();

        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("name");
        cameraFields.add("station_id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "camera");
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("id");
        areaFields.add("area_name");
        String areaSql = SqlUtil.buildAllAreaTable(areaFields, "area");
        
        for (TableRecord tableUnit : resp) {
            tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
                String sqlUnit = "SELECT camera.id AS id, area.area_name, camera.name, count(*) as count, camera.station_id FROM " + GlobalConsts.INTELLIF_FACE
                        + "." + tableUnit.getTableName() + " face, " + cameraSql+ ", "
                        + areaSql + " WHERE ";

                if (0 == quality) {
                    sqlUnit += " face.quality=0 and ";
                } else if (0 > quality) {
                    sqlUnit += " face.quality <= " + quality + " and ";
                }

                sqlUnit += ("face.time >= :startTime and face.time < :endTime" + cameraState
                        + " AND camera.id = face.source_id AND area.id = camera.station_id GROUP BY camera.id ORDER BY camera.station_id");

                Query query = entityManager.createNativeQuery(sqlUnit, AreaCameraStatistic.class);
                // if (queryobject.getCameraids()[0] != 0) {
                // query.setParameter("cameraids",
                // Arrays.asList(queryobject.getCameraids()));
                // }
                query.setParameter("startTime", queryobject.getStarttime()).setParameter("endTime", queryobject.getEndtime());
                try {
                    List<AreaCameraStatistic> faceResp = (ArrayList<AreaCameraStatistic>) query.getResultList();
                    respList.addAll(faceResp);
                } catch (Exception e) {
                    LOG.error("出错Sql：" + sqlUnit);
                    LOG.error("ERROR:", e);
                } finally {
                    entityManager.close();
                }
            }));
        }
        // 等待线程结束
        tasklist.forEach(FunctionUtil::waitTillThreadFinish);
        return respList;
    }

    /**
     * 查询人脸信息列表
     * 
     * @param startTime
     * @param endTime
     * @param cameraIds
     * @return
     */
    public List<FaceInfo> findFaceInfo(FaceQuery query) {
        List<FaceInfo> respList = new ArrayList<FaceInfo>();
        List<Future<List<FaceInfo>>> tasklist = new ArrayList<>();

        List<TableRecord> resp = tableRecordDao.findTableByTimeASC(query.getStartTimeString(), query.getEndTimeString(), tableShortName);

        for (TableRecord tableUnit : resp) {
            final StringBuilder sqlStringBuilder = new StringBuilder("SELECT face.* FROM ");
            sqlStringBuilder.append(GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " face,");
            sqlStringBuilder.append(GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_OTHER_CAMERA_INFO + " camera ");
            sqlStringBuilder.append(" WHERE face.quality >= 0 AND `time` >= ':startTime:' and `time` < ':endTime:' AND face.source_id = camera.id ");
            sqlStringBuilder.append(" AND camera.node_id = " + cameraNodeId);
            String sql = sqlStringBuilder.toString();
            final long queryStartTime = query.getStartTime().getTime();
            final long queryEndTime = query.getEndTime().getTime();
            if (queryEndTime <= queryStartTime) {
                continue;
            }

            for (long startTime = queryStartTime, endTime = startTime + STEP_TIME; startTime < queryEndTime; startTime = endTime, endTime = startTime
                    + STEP_TIME) {
                final String realSql = sql.replaceAll(":startTime:", DateUtil.getformatDate(new Date(startTime))).replaceAll(":endTime:",
                        DateUtil.getformatDate(new Date(Math.min(endTime, queryEndTime))));
                tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
                    long currentTime = System.currentTimeMillis();
                    List<FaceInfo> faceList = jdbcTemplate.query(realSql, new BeanPropertyRowMapper<FaceInfo>(FaceInfo.class));
                    LOG.info("sql is {}, need {} ms, query result size is {}", realSql, System.currentTimeMillis() - currentTime, faceList.size());
                    return faceList;
                }));
            }
            break;
        }
        // 等待线程结束
        for (Future<List<FaceInfo>> future : tasklist) {
            try {
                respList.addAll(future.get());
            } catch (Throwable e) {
                LOG.error("catch exception: ", e);
            }
        }
        return respList;
    }
	
		public Long findByStationNPeriod(long id, String starttime, String endtime) {
		AtomicLong total = new AtomicLong(0);
		List<TableRecord> resp = tableRecordDao.findTableByTimeASC(starttime, endtime, tableShortName);
		if(null == resp || resp.isEmpty()) return total.get();
		List<Future<?>> tasklist = new ArrayList<>();
		
		List<String> cameraFields = new ArrayList<String>();
	    cameraFields.add("id");
	    cameraFields.add("station_id");
	    String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "camera");
		for(TableRecord tableUnit : resp) {
			tasklist.add(ApplicationResource.THREAD_POOL.submit(() -> {
				String sqlUnit = "SELECT count(*) as count FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " face, " 
					+ cameraSql+ " WHERE camera.station_id = " + id + " and face.time >= '" + starttime
					+ "' and face.time <= '" + endtime + "' and face.source_id = camera.id";
				Query query = entityManager.createNativeQuery(sqlUnit);
				try {
					Object faceResp = query.getResultList();
					List<Object> countTempRes = (ArrayList<Object>)faceResp;
					total.addAndGet(((BigInteger)countTempRes.get(0)).longValue());
				} catch (Exception e) {
					LOG.error("出错Sql："+sqlUnit);
					LOG.error("ERROR:", e);
				} finally {
					entityManager.close();
				}
			}));
		}
		//等待线程结束
		tasklist.forEach(FunctionUtil::waitTillThreadFinish);
		return total.get();
	}
}
