package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dto.FaceStreamRequest;
import intellif.service.FaceStreamServiceItf;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zheng Xiaodong
 */
@Service
public class FaceStreamServiceImpl implements FaceStreamServiceItf {
    private static Logger LOG = LogManager.getLogger(FaceStreamServiceImpl.class);
    private static final ConcurrentHashMap<Long, TimedCount> personCount = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_VENUE = 1000;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public long getRealTimeCount(FaceStreamRequest request) {
        TimedCount timedCount;
        boolean flag;
        long count = 0;
        synchronized (this) {
            timedCount = personCount.get(request.getVenueId());
            flag = timedCount != null && StringUtils.equals(timedCount.getStartTime(), request.getStartTime());
            if (timedCount != null)
                count = timedCount.getCount();
        }
        if (flag) {
            return count;
        } else {
            count = queryFaceStreamCount(request.getVenueId(), request.getStartTime());
            synchronized (this) {
                if (personCount.get(request.getVenueId()) == null
                        || !StringUtils.equals(timedCount.getStartTime(), request.getStartTime()))
                    if (personCount.size() > MAX_CACHE_VENUE)
                        personCount.clear();
                    personCount.put(request.getVenueId(), new TimedCount(request.getStartTime(), count));
                return count;
            }
        }
    }

    @Override
    public synchronized void updateRealTimeCount(Long venueId, String startTime, Long count) {
        TimedCount timedCount = personCount.get(venueId);
        timedCount.setStartTime(startTime);
        timedCount.setCount(count);
    }

    @Override
    public void calcFaceStream() {
        for (Map.Entry<Long, TimedCount> entry : personCount.entrySet()) {
            Long venueId;
            String startTime;
            synchronized (this) {
                venueId = entry.getKey();
                startTime = entry.getValue().getStartTime();
            }
            Long count = queryFaceStreamCount(venueId, startTime);
            updateRealTimeCount(venueId, startTime, count);
        }
    }

    public Long queryFaceStreamCount(Long venueId, String startTime) {
        String sql = "select count(1) from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_STREAM +
                " where time >= '" + startTime + "' and venue_id = ? ";
        Long count = jdbcTemplate.queryForObject(sql, new Long[]{venueId}, Long.class);
        return count;
    }

    private static final class TimedCount {
        private String startTime;
        private Long count;

        public TimedCount(String startTime, Long count) {
            this.startTime = startTime;
            this.count = count;
        }

        public synchronized String getStartTime() {
            return startTime;
        }

        public synchronized void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public synchronized Long getCount() {
            return count;
        }

        public synchronized void setCount(Long count) {
            this.count = count;
        }

    }
    
    public long getRealTimeCountByStartTimeAndEndTime(FaceStreamRequest request){
        String sql = "select count(1) from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_STREAM +
                " where time >= '" + request.getStartTime() + "' and time <= '" + request.getEndTime() + "' and venue_id = ? ";
        Long count = jdbcTemplate.queryForObject(sql, new Long[]{request.getVenueId()}, Long.class);
        return count;
    }

    @Override
    public long getFaceStreamCount(FaceStreamRequest request) {
        String sql = "select count(1) from " + GlobalConsts.INTELLIF_FACE + "." + GlobalConsts.T_NAME_FACE_STREAM +
                " where time >= '" + request.getStartTime() + "'";
        if (StringUtils.isNotBlank(request.getEndTime())) {
            sql += " and time <= '" + request.getEndTime() + "'";
        }
        if (request.getVenueId() != null && request.getVenueId() != 0) {
            sql += " and venue_id = " + request.getVenueId();
        }
        if (StringUtils.isNotBlank(request.getCameraIds())) {
            sql += " and src_id in (" + request.getCameraIds() + ")";
        }
        LOG.info("sql={}",sql);
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count;

    }
}
