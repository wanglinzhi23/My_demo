package intellif.share.service.impl;

import com.google.code.ssm.api.ReadThroughAssignCache;
import intellif.consts.GlobalConsts;
import intellif.share.service.MobileCollectStationCacheItf;
import intellif.database.entity.MobileCollectStationMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zheng Xiaodong on 2017/6/17.
 */
@Service
public class MobileCollectStationCacheImpl implements MobileCollectStationCacheItf {
    private static final String CACHE_NAMESPACE = "mobile_collect_station_map";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @ReadThroughAssignCache(namespace = CACHE_NAMESPACE, expiration = 1200, assignedKey = "stationMap")
    public Map<Long, Long> loadStationMap() {
        String sql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_MOBILE_COLLECT_STATION_MAP;
        List<MobileCollectStationMap> mapList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MobileCollectStationMap.class));
        Map<Long, Long> stationIdMap = new HashMap<>();

        for (MobileCollectStationMap map : mapList) {
            stationIdMap.put(map.getSyncStationId(), map.getMappedStationId());
        }
        return stationIdMap;
    }
}
