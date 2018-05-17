package intellif.share.job;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.database.entity.TableRecord;
import intellif.zoneauthorize.job.RefreshCacheJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 定时记录每个摄像头的最后采集数据时间，并记录下来，用于优化查询
 * @author Zheng Xiaodong
 */
@Component
public class FaceSyncDetectionJob {

    private static Logger LOG = LogManager.getLogger(RefreshCacheJob.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TableRecordDao tableRecordDao;

    private final String tableShortName = "t_face";

    /**
     * 定时统计每个摄像头的最后采集数据时间
     */
    @Scheduled(fixedRateString = "7200000")
    @Transactional
    public void calcCameraLatestTime() {
        Date syncTime = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String syncTimeStr = format.format(syncTime);

        Date lastSyncTime = getLastSyncTime();
        Calendar start = Calendar.getInstance();
        start.setTime(lastSyncTime);
        start.set(Calendar.HOUR_OF_DAY, -1);  // 摄像头采集时间有时会不准确，将时间提前一个小时，降低漏数据的概率

        Map timeMap = calcLatestTime(format.format(start.getTime()), syncTimeStr);
        updateCameraLatestTime(timeMap);
        updateLastSyncTime(syncTime);
    }

    private Date getLastSyncTime() {
        String sql = "select last_time from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME + " where camera_id = -1";
        Date lastSyncTime = jdbcTemplate.queryForObject(sql, Date.class);
        return lastSyncTime ;
    }

    private void updateLastSyncTime(Date lastSyncTime) {
        String sql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME + " set last_time = ? where camera_id = -1";
        jdbcTemplate.update(sql, new Date[] {lastSyncTime});
    }

    /*
     * 在指定时间段内，计算每个摄像头的最后采集数据的时间
     * @param startTime - 开始时间
     * @param endTime - 结束时间
     */
    private Map<Long, Date> calcLatestTime(String startTime, String endTime) {
        List<TableRecord> resp = tableRecordDao.findTableByTime(startTime, endTime, tableShortName);
        Map<Long, Date> timeMap = new HashMap<>();
        String sql;

        if (CollectionUtils.isEmpty(resp))
            return Collections.emptyMap();

        for (TableRecord record : resp) {
            sql = "select source_id as camera_id, max(time) as latest_time from " + GlobalConsts.INTELLIF_FACE + "." + record.getTableName()
                    + " where time < ? and time >= ? group by source_id";
            List<CameraLatestTime> result = jdbcTemplate.query(sql, new String[] {endTime, startTime}, new BeanPropertyRowMapper<>(CameraLatestTime.class));

            if (result == null) continue;

            for (CameraLatestTime c : result) {
                Long cameraId = c.getCameraId();
                Date preTime = timeMap.get(cameraId);
                if (preTime == null || preTime.before(c.getLatestTime()))
                    timeMap.put(cameraId, c.getLatestTime());
            }
        }
        return timeMap;
    }

    /* 更新每个摄像头的最后采集时间 */
    private void updateCameraLatestTime(Map<Long, Date> timeMap) {
        for (Map.Entry<Long, Date> entry : timeMap.entrySet()) {
            updateSingleCamera(entry.getKey(), entry.getValue());
        }
    }

    /* 更新单个摄像头的最后采集时间 */
    private void updateSingleCamera(Long cameraId, Date latestTime) {
        String sql = "select count(*) from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME + " where camera_id = ?";
        String updateSql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME + " set last_time = ? where camera_id = ?";
        String insertSql = "insert into " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_LAST_CAPTURE_TIME + "(camera_id, last_time) values (?, ?)";
        Long count = jdbcTemplate.queryForObject(sql, new Long[] {cameraId}, Long.class);
        if (count > 0) {
            jdbcTemplate.update(updateSql, new Object[] {latestTime, cameraId});
        } else {
            jdbcTemplate.update(insertSql, new Object[] {cameraId, latestTime});
        }
    }

    public static class CameraLatestTime {
        private Long cameraId;
        private Date latestTime;

        public Long getCameraId() {

            return cameraId;
        }

        public void setCameraId(Long cameraId) {
            this.cameraId = cameraId;
        }

        public Date getLatestTime() {
            return latestTime;
        }

        public void setLatestTime(Date latestTime) {
            this.latestTime = latestTime;
        }
    }
}
