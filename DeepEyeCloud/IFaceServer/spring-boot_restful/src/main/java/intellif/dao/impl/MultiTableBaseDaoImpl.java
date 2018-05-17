package intellif.dao.impl;

import intellif.annotation.MultiTablePrefix;
import intellif.consts.GlobalConsts;
import intellif.dao.MultiTableBaseDao;
import intellif.dao.TableRecordDao;
import intellif.share.service.SharedFaceServiceItf;
import intellif.utils.CommonUtil;
import intellif.utils.DateUtil;
import intellif.utils.SqlComputeTask;
import intellif.utils.SqlUtil;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.TableRecord;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MultiTableBaseDaoImpl<T> implements MultiTableBaseDao<T> {
    
    private static Logger LOG = LogManager.getLogger(MultiTableBaseDaoImpl.class);

    // 用于优化人脸查询, 一个根据结束时间偏移的小时数
    public static final int QUERY_FACE_END_TIME_OFFSET = -2;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    TableRecordDao tableRecordDao;
    @Autowired
    SharedFaceServiceItf faceService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Class<T> entityClass;
    private String tableName;
    protected String tableShortName;
    
    public MultiTableBaseDaoImpl() {  
        Type genType = getClass().getGenericSuperclass();  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
        entityClass = (Class) params[0];  
        MultiTablePrefix multiTable = entityClass.getAnnotation(MultiTablePrefix.class);
        tableShortName = multiTable.shortName();
        tableName =  multiTable.schema()+"."+multiTable.shortName();
    }  

    @Override
    public T save(T entity) {
        return null;
    }

    @Override
    public Integer update(List<T> faceList) {
        return null;
    }

    @Override
    public Long count() {
        return tableRecordDao.countTatalByTable(tableShortName);
    }
    
    @Override
    public List<T> findByIds(List<?> idList) {
        List<T> respList = new ArrayList<T>();
        if(null == idList||idList.isEmpty()) return respList;
        Map<Long, String> codeMap = new HashMap<Long, String>();
        for(Object id : idList) {
            long codeId = 0;
            if(id instanceof Long) {
                codeId = (long) id;
            } else if(id instanceof BigInteger){
                codeId = ((BigInteger) id).longValue();
            }
            long code =CommonUtil.getCode(codeId);
            if(codeMap.containsKey(code)) {
                codeMap.put(code, codeMap.get(code)+","+codeId);
            } else {
                codeMap.put(code, ""+codeId);
            }
        }
        
        Iterator<Entry<Long, String>> iter = codeMap.entrySet().iterator();
        
        List<String> sqlList = new ArrayList<String>();
        while (iter.hasNext()) {
            Entry<Long, String> entry =  iter.next();
            String Sql = "SELECT * FROM "+tableName+"_"+entry.getKey()+" WHERE id in ("+entry.getValue()+")"; 
            sqlList.add(Sql);
        }
        Future<ArrayList<T>> result = GlobalConsts.MUL_TABLE_FJPOOL.submit(new SqlComputeTask<T>(sqlList, entityClass, entityManager));
        try {
            respList = result.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("ERROR:", e);;
        } finally {
            entityManager.close();
        }
        
        return respList;
    }

    @Override
    public List<T> findByIdsFromOther(List<Long> idList, String joinField) {
        List<T> respList = new ArrayList<T>();
        if(null == idList||idList.isEmpty()) return respList;
        Map<Long, String> codeMap = new HashMap<Long, String>();
        for(Long id : idList) {
            long code =CommonUtil.getCode(id);
            if(codeMap.containsKey(code)) {
                codeMap.put(code, codeMap.get(code)+","+id);
            } else {
                codeMap.put(code, ""+id);
            }
        }
        
        Iterator<Entry<Long, String>> iter = codeMap.entrySet().iterator();

        List<String> sqlList = new ArrayList<String>();
        while (iter.hasNext()) {
            Entry<Long, String> entry =  iter.next();
            String sql = "SELECT * FROM "+tableName+"_"+entry.getKey()+" WHERE "+joinField+" in ("+entry.getValue()+")";
            sqlList.add(sql);
        }
        Future<ArrayList<T>> result = GlobalConsts.MUL_TABLE_FJPOOL.submit(new SqlComputeTask<T>(sqlList, entityClass, entityManager));
        try {
            respList = result.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("ERROR:", e);;
        } finally {
            entityManager.close();
        }
        
        return respList;
    }

    /*
     * 查找最早的开始时间
     */
    private Date findMaxStartTime(TableRecord tableUnit, String timeField, String latestTimeQueryString) {
        String latestTimeSql = "SELECT DATE_FORMAT(MAX(" + timeField + "), '%Y-%m-%e %H:%i:%s') FROM "
                + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName()
                + " WHERE " + latestTimeQueryString;
        Query query = this.entityManager.createNativeQuery(latestTimeSql);
        String dateStr = (String) query.getSingleResult();
        if (dateStr == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            LOG.error("出错Sql：" + latestTimeSql);
            LOG.error("ERROR:", e);;
        }
        return date;
    }

    @Override
    public List<T> findByTimeUsingStatistic(String startTime, String endTime, String timeField, String queryString,
                                            int page, int pageSize, String lastId, String sourceIds) {
        List<T> respList = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        int start = (page - 1) * pageSize;
        Calendar startDate = Calendar.getInstance();
        Calendar endDate= Calendar.getInstance();
        try {
            startDate.setTime(df.parse(startTime));
            endDate.setTime(df.parse(endTime));
        } catch (ParseException e) {
            LOG.error("时间参数格式不正确[startTime: " + startTime + ", endTime: " + endTime);
            LOG.error("ERROR:", e);
        }

        // 先查询最近一段时间的数据
        Date now = new Date();
        Calendar startDateOffset = Calendar.getInstance();
        if (now.before(endDate.getTime()))
            startDateOffset.setTime(new Date());
        else
            startDateOffset.setTime(endDate.getTime());
        startDateOffset.add(Calendar.HOUR_OF_DAY, QUERY_FACE_END_TIME_OFFSET);
        if (startDateOffset.before(startDate))
            startDateOffset.setTime(startDate.getTime());
        Map<String, Object> resultMap;
        resultMap = findByTimeOffset(df.format(startDateOffset.getTime()), df.format(endDate.getTime()),
                timeField, queryString, start, pageSize, lastId);
        respList = (List<T>) resultMap.get("data");
        long startPoint = (Long) resultMap.get("startPoint");  // 已查询的数据偏移指针

        int remainCount = 0;   // 还需要查询的条数
        if (respList.size() < pageSize)
            remainCount = pageSize - respList.size();

        if (remainCount <= 0)
            return respList;

        Date lastCalcTime = queryLastCalcCameraTime();  // 最后统计时间
        Date camerasLastTime;
        if (lastCalcTime == null || lastCalcTime.before(startDateOffset.getTime()))
            camerasLastTime = startDateOffset.getTime(); // 定时统计的时间没覆盖到要查询的时间
        else
            camerasLastTime = queryCameraLatestTime(sourceIds);

        if (camerasLastTime == null)
            return new ArrayList<T>();
        else if (camerasLastTime.after(startDateOffset.getTime()))
            camerasLastTime = startDateOffset.getTime();

        List<T> remainData = (List<T>) findByTimeOffset(startTime, df.format(camerasLastTime),
                timeField, queryString, (start - startPoint) > 0 ? (start - startPoint) : 0, remainCount, lastId).get("data");
        respList.addAll(remainData);
        return respList;
    }

    /*
     * 查询摄像头最后采集数据的时间
     */
    private Date queryCameraLatestTime(String cameraIds) {
        if (cameraIds == null || "".equals(cameraIds.trim()))
            return null;
        String sql = "select max(last_time) from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME
                + " where camera_id in (" + cameraIds + ")";
        return jdbcTemplate.queryForObject(sql, Date.class);
    }

    /* 查询最后一次统计摄像头抓拍的时间 */
    private Date queryLastCalcCameraTime() {
        String sql = "select last_time from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME
                + " where camera_id = -1";
        return jdbcTemplate.queryForObject(sql, Date.class);
    }

    
    private Map<String, Object> findByTimeOffset(String startTime, String endTime, String timeField, String queryString,
                                    long start, int pagesize, String lastId) {
        List<T> respList = new ArrayList<T>();
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);

        if(null == resp || resp.isEmpty()) return null;

        // 数据范围指针
        long startPoint = 0;
        for(TableRecord tableUnit : resp) {
            // 拼装Sql
            String sqlUnit = null;
            
            String  forceTimeSqlUnit = "SELECT * FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" force index(t_face_time_source_id) WHERE "+timeField+" >= :startTime and "+timeField+" < :endTime";
            String  forceSourceForcesqlUtil = "SELECT * FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" force index(t_face_source_id_time) WHERE "+timeField+" >= :startTime and "+timeField+" < :endTime";
            String  nForcesqlUtil = "SELECT * FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= :startTime and "+timeField+" < :endTime";
             if(GlobalConsts.T_FACE_PRE.equals(tableShortName)){
                 try{
                     if(!StringUtils.isBlank(queryString)){
                         String str = StringUtils.deleteWhitespace(queryString);
                         String cids = str.trim().split("source_idin")[1];
                         String temp = cids.substring(0,cids.indexOf(")"));
                         if(temp.split(",").length > 10){
                             sqlUnit = forceTimeSqlUnit;
                         }else{
                             sqlUnit = forceSourceForcesqlUtil;
                         }
                     }else{
                         sqlUnit = nForcesqlUtil;
                     }
                 }catch(Exception e){
                     sqlUnit = nForcesqlUtil; 
                 }
             }else{
                 sqlUnit = nForcesqlUtil;
             }
             
            String countSql = "SELECT count(1) FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= '"+startTime+"' and "+timeField+" < '"+endTime+"'";

            if(lastId != null && lastId.trim().length() > 0){
                FaceInfo fi = faceService.findOne(Long.parseLong(lastId));
                long code = CommonUtil.getCode(Long.parseLong(lastId));
                String tableName = GlobalConsts.T_FACE_PRE+"_"+code;
                if(tableName.equals(tableUnit.getTableName())){
                    String seqSql = " and sequence < "+fi.getSequence();
                    sqlUnit += seqSql;
                    countSql += seqSql;
                }
            }
            if(null!=queryString&&!queryString.isEmpty()) {
                sqlUnit += (" and " + queryString);
                countSql += (" and " + queryString);
            }

            // 分页数量获取
//          if(endPoint >= start) {
            sqlUnit += " limit :start, :pageSize";
            //LOG.info(sqlUnit);
            try {
                Query query = this.entityManager.createNativeQuery(sqlUnit, entityClass);
                query.setParameter("startTime", startTime).setParameter("endTime", endTime).setParameter("start", (start-startPoint>0?start-startPoint:0)).setParameter("pageSize", (pagesize-respList.size()));
                List<T> faceResp = (ArrayList<T>) query.getResultList();
                respList.addAll(faceResp);
            } catch (Exception e) {
                LOG.error("出错Sql："+sqlUnit);
                LOG.error("ERROR:", e);;
            } finally {
                entityManager.close();
            }
            if(respList.size()>=pagesize) break;
            // 当前据量指针前移
            if(respList.size()==0) {
                startPoint += countResult(countSql);
            } else {
                startPoint += (start+respList.size());
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("startPoint", startPoint);
        resultMap.put("data", respList);
        return resultMap;
    }


    @Override
    public List<T> findByTime(String startTime, String endTime, String timeField, String queryString, int page, int pagesize,String lastId) {
        return (List<T>) findByTimeOffset(startTime, endTime, timeField, queryString, (page - 1) * pagesize, pagesize,lastId).get("data");
    }

    @Override
    public List<T> findByDistrictAndTime(String startTime, String endTime, String timeField,
                                         long districtId, String queryString, int page, int pagesize,String lastId) {
        List<T> respList = new ArrayList<T>();
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);


        if(null == resp || resp.isEmpty()) return null;

        long start = (page - 1) * pagesize;

        // 数据范围指针
        long startPoint = 0;
        
        List<String> cameraFields = new ArrayList<String>();
        cameraFields.add("id");
        cameraFields.add("station_id");
        String cameraSql = SqlUtil.buildAllCameraTable(cameraFields, "c");
        
        List<String> areaFields = new ArrayList<String>();
        areaFields.add("id");
        areaFields.add("district_id");
        String areaSql = SqlUtil.buildAllAreaTable(areaFields, "a");
        
        for(TableRecord tableUnit : resp) {
            // 拼装Sql
            String sqlUnit = "SELECT f.* FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() +
                    " f LEFT JOIN " +cameraSql+ " on f.source_id = c.id LEFT JOIN "+areaSql
                    +" ON a.id = c.station_id WHERE a.district_id = :districtId and " 
                    + timeField + " >= :startTime and "
                    + timeField + " < :endTime";
            String countSql = "SELECT COUNT(1) FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() +
                    " f LEFT JOIN "  +cameraSql+ " on f.source_id = c.id LEFT JOIN "+areaSql
                    +" ON a.id = c.station_id WHERE a.district_id = :districtId and " 
                    + timeField + " >= :startTime and "
                    + timeField + " < :endTime";
            if(lastId != null && lastId.trim().length() > 0){
                FaceInfo fi = faceService.findOne(Long.parseLong(lastId));
                 long code = CommonUtil.getCode(Long.parseLong(lastId));
                 String tableName = GlobalConsts.T_FACE_PRE+"_"+code;
                 if(tableName.equals(tableUnit.getTableName())){
                     String seqSql = " and sequence < "+fi.getSequence();
                     sqlUnit += seqSql;
                     countSql += seqSql;
                 }
            }
            
            if(null!=queryString&&!queryString.isEmpty()) {
                sqlUnit += (" and " + queryString);
                countSql += (" and " + queryString);
            }

            sqlUnit += " limit :start, :pageSize";
            // LOG.info(sqlUnit);
            try {
                Query query = this.entityManager.createNativeQuery(sqlUnit, entityClass);
                query.setParameter("startTime", startTime).setParameter("endTime", endTime)
                        .setParameter("districtId", districtId)
                        .setParameter("start", (start-startPoint>0?start-startPoint:0))
                        .setParameter("pageSize", (pagesize-respList.size()));
                List<T> faceResp = (ArrayList<T>) query.getResultList();
                respList.addAll(faceResp);
            } catch (Exception e) {
                LOG.error("出错Sql："+sqlUnit);
                LOG.error("ERROR:", e);;
            } finally {
                entityManager.close();
            }
            if(respList.size()>=pagesize) break;
            // 当前据量指针前移
            if(respList.size()==0) {
                startPoint += countResult(countSql);
            } else {
                startPoint += (start+respList.size());
            }
        }

        return respList;
    }

    @Override
    public List<T> findAll(String queryString) {
        List<T> respList = new ArrayList<T>();
        List<TableRecord> resp = tableRecordDao.findAllByTableName(tableShortName);

////        String Sql = "SELECT * FROM "+GlobalConsts.T_NAME_TABLES+" where short_name = '"+GlobalConsts.T_NAME_FACE_INFO+"'";
//      String Sql = "SELECT * FROM " + GlobalConsts.T_NAME_TABLES + " where short_name = :tableName";
//      LOG.info(Sql);
//      try {
//          Query query = this.entityManager.createNativeQuery(Sql, TableRecord.class);
//          query.setParameter("tableName", tableShortName);
//          resp = (ArrayList<TableRecord>)query.getResultList();
//      } catch (Exception e) {
//          LOG.error("出错Sql："+Sql);
//          LOG.error("ERROR:", e);;
//      } finally {
//          entityManager.close();
//      }
//      
        if(null == resp || resp.isEmpty()) return null;
        
        List<String> sqlList = new ArrayList<String>();
        for(TableRecord tableUnit : resp) {
            String sqlUnit = "SELECT * FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName();
            if(StringUtil.isNotBlank(queryString)) {
                sqlUnit += (" WHERE " + queryString);
            }else{
               return null; 
            }
            sqlList.add(sqlUnit);
        }

        Future<ArrayList<T>> result = GlobalConsts.MUL_TABLE_FJPOOL.submit(new SqlComputeTask<T>(sqlList, entityClass, entityManager));
        try {
            respList = result.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("ERROR:", e);;
        } finally {
            entityManager.close();
        }
        
        return respList;
    }

    @Override
    public List<T> findLast(String queryString) {
        List<T> respList = new ArrayList<T>();
        TableRecord resp = tableRecordDao.getCurTable(DateUtil.getDateString(new Date()), tableShortName);
        if(null == resp) return null;

        String sqlUnit = "SELECT * FROM "+GlobalConsts.INTELLIF_FACE+"."+resp.getTableName();
        if(StringUtil.isNotBlank(queryString)) {
            sqlUnit += (" WHERE " + queryString);
        }else{
            return null;
        }
        // LOG.info(sqlUnit);
        try {
            Query query = this.entityManager.createNativeQuery(sqlUnit, entityClass);
            List<T> faceResp = (ArrayList<T>) query.getResultList();
            respList.addAll(faceResp);
        } catch (Exception e) {
            LOG.error("出错Sql："+sqlUnit);
            LOG.error("ERROR:", e);;
        } finally {
            entityManager.close();
        }
        
        return respList;
    }

    @Override
    public long countResult(String Sql) {
        // LOG.info(Sql);
        BigInteger resp = new BigInteger("0");
        try {
            Query query = this.entityManager.createNativeQuery(Sql);
            resp = (BigInteger) query.getSingleResult();
        } catch (NoResultException e) {
        } catch (Exception e) {
            LOG.error("出错Sql："+Sql);
            LOG.error("ERROR:", e);;
        }finally {
            entityManager.close();
        }
        return resp == null ? 0 : resp.longValue();
    }

    
    
    @Override
    public <E> List<E> findByTime(String selectString, String startTime, String endTime, String timeField, String queryString, Class<E> entityClass, int page, int pagesize, boolean isPo,String lastId) {
        List<E> respList = new ArrayList<E>();
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);
////        String Sql = "SELECT * FROM "+GlobalConsts.T_NAME_TABLES+" WHERE " + "((start_time <= '"+startTime+"' and end_time > '"+startTime+"') or (start_time >= '"+startTime+"' and start_time < '"+endTime+"')) and short_name = '"+GlobalConsts.T_NAME_FACE_INFO+"'";
//      String Sql = "SELECT * FROM " + GlobalConsts.T_NAME_TABLES  + " WHERE ((start_time <= :startTime and end_time > :startTime) or (start_time >= :startTime and start_time < :endTime)) and short_name = :tableName";
//      
//      LOG.info(Sql);
//      try {
//          Query query = this.entityManager.createNativeQuery(Sql, TableRecord.class);
//          query.setParameter("startTime", startTime).setParameter("endTime", endTime).setParameter("tableName", tableShortName);
//          resp = (ArrayList<TableRecord>)query.getResultList();
//      } catch (Exception e) {
//          LOG.error("出错Sql："+Sql);
//          LOG.error("ERROR:", e);
//      } finally {
//          entityManager.close();
//      }
        
        if(null == resp || resp.isEmpty()) return null;
        
        long start = (page - 1) * pagesize;
        
        // 数据范围指针
        long startPoint = 0;
//      long endPoint = 0;
        boolean isFirst = true;
        for(TableRecord tableUnit : resp) {
            // 拼装Sql
            String sqlUnit = "SELECT "+selectString+" FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= :startTime and "+timeField+" < :endTime";
            String countSql = "SELECT count(1) FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= '"+startTime+"' and "+timeField+" < '"+endTime+"'";
            
            if(lastId != null && lastId.trim().length() > 0){
                FaceInfo fi = faceService.findOne(Long.parseLong(lastId));
                 long code = CommonUtil.getCode(Long.parseLong(lastId));
                 String tableName = GlobalConsts.T_FACE_PRE+"_"+code;
                 if(tableName.equals(tableUnit.getTableName())){
                     String seqSql = " and sequence < "+fi.getSequence();
                     sqlUnit += seqSql;
                     countSql += seqSql;
                 }
            }
            
            if(null!=queryString&&!queryString.isEmpty()) {
                sqlUnit += (" and " + queryString);
                countSql += (" and " + queryString);
            }
            
//          endPoint += countResult(countSql);
            // 分页数量获取
//          if(endPoint >= start) {
                sqlUnit += " limit :start, :pageSize";
                //LOG.info(sqlUnit);
                try {
                    Query query;
                    if(isPo) {
                        query = this.entityManager.createNativeQuery(sqlUnit, entityClass);
                    } else {
                        query = this.entityManager.createNativeQuery(sqlUnit);
                    }
                    query.setParameter("startTime", startTime).setParameter("endTime", endTime).setParameter("start", (start-startPoint>0?start-startPoint:0)).setParameter("pageSize", (pagesize-respList.size()));
                    List<E> faceResp = (ArrayList<E>) query.getResultList();
                    respList.addAll(faceResp);
                } catch (Exception e) {
                    LOG.error("出错Sql："+sqlUnit);
                    LOG.error("ERROR:", e);;
                } finally {
                    entityManager.close();
                }
                if(respList.size()>=pagesize) break;
//          }
            // 当前据量指针前移
                if(respList.size()==0) {
                    startPoint += countResult(countSql);
                } else {
                    startPoint += (start+respList.size());
                }
        }
        
        return respList;
    }

    @Override
    public List<Object[]> findByTime(String selectString, String startTime, String endTime, String timeField, String queryString, int page, int pagesize,String lastId) {

        List<Object[]> respList = new ArrayList<Object[]>();
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);
////        String Sql = "SELECT * FROM "+GlobalConsts.T_NAME_TABLES+" WHERE " + "((start_time <= '"+startTime+"' and end_time > '"+startTime+"') or (start_time >= '"+startTime+"' and start_time < '"+endTime+"')) and short_name = '"+GlobalConsts.T_NAME_FACE_INFO+"'";
//      String Sql = "SELECT * FROM " + GlobalConsts.T_NAME_TABLES  + " WHERE ((start_time <= :startTime and end_time > :startTime) or (start_time >= :startTime and start_time < :endTime)) and short_name = :tableName";
//      
//      LOG.info(Sql);
//      try {
//          Query query = this.entityManager.createNativeQuery(Sql, TableRecord.class);
//          query.setParameter("startTime", startTime).setParameter("endTime", endTime).setParameter("tableName", tableShortName);
//          resp = (ArrayList<TableRecord>)query.getResultList();
//      } catch (Exception e) {
//          LOG.error("出错Sql："+Sql);
//          LOG.error("ERROR:", e);
//      } finally {
//          entityManager.close();
//      }
        
        if(null == resp || resp.isEmpty()) return null;

        long start = (page - 1) * pagesize;
        
        // 数据范围指针
        long startPoint = 0;
//      long endPoint = 0;
        for(TableRecord tableUnit : resp) {
            // 拼装Sql
            String sqlUnit = "SELECT "+selectString+" FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= :startTime and "+timeField+" < :endTime";
            String countSql = "SELECT count(1) FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= '"+startTime+"' and "+timeField+" < '"+endTime+"'";
             if(lastId != null && lastId.trim().length() > 0){
                    FaceInfo fi = faceService.findOne(Long.parseLong(lastId));
                     long code = CommonUtil.getCode(Long.parseLong(lastId));
                     String tableName = GlobalConsts.T_FACE_PRE+"_"+code;
                     if(tableName.equals(tableUnit.getTableName())){
                         String seqSql = " and sequence < "+fi.getSequence();
                         sqlUnit += seqSql;
                         countSql += seqSql;
                     }
                }
            
            if(null!=queryString&&!queryString.isEmpty()) {
                sqlUnit += (" and " + queryString);
                countSql += (" and " + queryString);
            }
            
//          endPoint += countResult(countSql);
            // 分页数量获取
//          if(endPoint >= start) {
                sqlUnit += " limit :start, :pageSize";
                // LOG.info(sqlUnit);
                try {
                    Query query = this.entityManager.createNativeQuery(sqlUnit);
                    query.setParameter("startTime", startTime).setParameter("endTime", endTime).setParameter("start", (start-startPoint>0?start-startPoint:0)).setParameter("pageSize", (pagesize-respList.size()));
                    List<Object[]> faceResp = query.getResultList();
                    respList.addAll(faceResp);
                } catch (Exception e) {
                    LOG.error("出错Sql："+sqlUnit);
                    LOG.error("ERROR:", e);;
                } finally {
                    entityManager.close();
                }
                if(respList.size()>=pagesize) break;
//          }
            // 当前据量指针前移
                if(respList.size()==0) {
                    startPoint += countResult(countSql);
                } else {
                    startPoint += (start+respList.size());
                }
        }
        
        return respList;
    }

  /*  @Override
    public List<T> findByTime(String startTime, String endTime, String timeField, String queryString, int page, int pagesize) {
        return findByTimeLatest(startTime, endTime, timeField, queryString, null, page, pagesize);
    }*/
    
    
    @Override
    public List<T> findByTimeLatest(String startTime, String endTime, String timeField, String queryString,
                                    String latestTimeQueryString, int page, int pagesize) {
        List<T> respList = new ArrayList<T>();      
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);      
        if(null == resp || resp.isEmpty()) return null;

        if (latestTimeQueryString != null && "".equals(latestTimeQueryString.trim())) {
            Date latestTime = null;
            for(TableRecord tableUnit : resp) {
                Date tableLatestTime = this.findMaxStartTime(tableUnit, timeField, latestTimeQueryString);
                if (latestTime.before(tableLatestTime)) {
                    latestTime = tableLatestTime;
                }
            }
            if (latestTime != null) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String latestDateStr = df.format(latestTime);
                startTime = latestDateStr;
            }
        }

        long start = (page - 1) * pagesize;
        // 数据范围指针
        long startPoint = 0;
        for(TableRecord tableUnit : resp) {
            // 拼装Sql
            String sqlUnit = "SELECT * FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= :startTime and "+timeField+" < :endTime";
            String countSql = "SELECT count(1) FROM "+GlobalConsts.INTELLIF_FACE+"."+tableUnit.getTableName()+" WHERE "+timeField+" >= '"+startTime+"' and "+timeField+" < '"+endTime+"'";

            if(null!=queryString&&!queryString.isEmpty()) {
                sqlUnit += (" and " + queryString);
                countSql += (" and " + queryString);
            }

            // 分页数量获取
//          if(endPoint >= start) {
            sqlUnit += " limit :start, :pageSize";
            LOG.info(sqlUnit);
            try {
                Query query = this.entityManager.createNativeQuery(sqlUnit, entityClass);
                query.setParameter("startTime", startTime).setParameter("endTime", endTime).setParameter("start", (start-startPoint>0?start-startPoint:0)).setParameter("pageSize", (pagesize-respList.size()));
                List<T> faceResp = (ArrayList<T>) query.getResultList();
                respList.addAll(faceResp);
            } catch (Exception e) {
                LOG.error("出错Sql："+sqlUnit);
                LOG.error("ERROR:", e);;
            } finally {
                entityManager.close();
            }
            if(respList.size()>=pagesize) break;
            // 当前据量指针前移
            if(respList.size()==0) {
                startPoint += countResult(countSql);
            } else {
                startPoint += (start+respList.size());
            }
        }

        return respList;
    }

    public Long countByTime(String queryString, String startTime, String endTime, String timeField) {
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);

        if (null == resp || resp.isEmpty()) return null;

        long count = 0;
        for (TableRecord tableUnit : resp) {
            String countSql = "SELECT count(1) FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() +
                    " WHERE " + timeField + " >= '" + startTime + "' and " + timeField + " < '" + endTime + "'";

            if (null != queryString && !queryString.isEmpty()) {
                countSql += (" and " + queryString);
            }

            count += countResult(countSql);
        }

        return count;
    }
    
    public List<T> findByTimeOffsetForDayun(String startTime, String endTime, String timeField, String queryString, long start, int pagesize,
            Long sequence) {
         List<T> respList = new ArrayList<T>();
         List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);

         if (null == resp || resp.isEmpty())
             return null;

         for (TableRecord tableUnit : resp) {
             // 拼装Sql
             String sqlUnit = "SELECT * FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " WHERE sequence in(";
             sqlUnit = sqlUnit + "SELECT sequence FROM " + GlobalConsts.INTELLIF_FACE + "." + tableUnit.getTableName() + " WHERE " + timeField + " >= :startTime and "
                     + timeField + " < :endTime";
             if(org.apache.commons.lang3.StringUtils.isNotBlank(queryString)) {
                 sqlUnit += (" and " + queryString);
             }
             sqlUnit += " ) and sequence >= "+ sequence;
             sqlUnit += " order by sequence desc limit :start, :pageSize";
             LOG.info("sqlUnit={},{},{},{},{}",sqlUnit,startTime,endTime, start, pagesize);
             try {
                 Query query = this.entityManager.createNativeQuery(sqlUnit, entityClass);
                 query.setParameter("startTime", startTime).setParameter("endTime", endTime)
                         .setParameter("start", start).setParameter("pageSize", pagesize);
                 List<T> faceResp = (ArrayList<T>) query.getResultList();
                 respList.addAll(faceResp);
             } catch (Exception e) {
                 LOG.error("出错Sql：" + sqlUnit);
                 LOG.error("ERROR:", e);
                 ;
             } finally {
                 entityManager.close();
             }
         }
         
         return respList;
     }

}