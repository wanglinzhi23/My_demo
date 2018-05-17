package intellif.service.impl;

import com.google.code.ssm.api.InvalidateAssignCache;
import com.google.code.ssm.api.ReadThroughAssignCache;

import intellif.consts.GlobalConsts;
import intellif.core.tree.Tree;
import intellif.dao.PoliceStationDao;
import intellif.database.entity.PoliceStation;
import intellif.service.PoliceStationCacheItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng Xiaodong on 2017/4/30.
 * 单位缓存实现类
 */
@Service
@Transactional
public class PoliceStationCacheImpl implements PoliceStationCacheItf {
    private static final String CACHE_NAMESPACE = "police_statiosn";
    private static Logger LOG = LogManager.getLogger(PoliceStationCacheImpl.class);
    
    @Autowired
    private PoliceStationDao policeStationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @ReadThroughAssignCache(namespace = CACHE_NAMESPACE, expiration = 600, assignedKey = "tree")
    public Tree tree() {
        String sql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICE_STATION;
        List<PoliceStation> stations = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PoliceStation.class));
        List<PoliceStation> stationList = new ArrayList<>();
        if (stations != null) {
            for (PoliceStation s : stations) {
                stationList.add(s);
            }
        }
        return Tree.TreeBuilder.newInstance().add(PoliceStation.class, stationList).build();
    }
    
    /**
     * 更新单位及以上单位字段值(userCount用户个数,specialTotalNum全区域特殊用户个数,specialUseNum全区域特殊用户名额)
     * @param fieldName
     * @param sId
     * @param value
     */
    @Override
    @InvalidateAssignCache(namespace = CACHE_NAMESPACE, assignedKey = "tree")  
    public void updatePoliceStationTreeValues(String fieldName,long sId,int value) {
        loopChangePSValues(fieldName,sId,value);
    }


    @Override
    @InvalidateAssignCache(namespace = CACHE_NAMESPACE, assignedKey = "tree")
    public PoliceStation save(PoliceStation station) {
        return policeStationDao.save(station);
    }

    @Override
    @InvalidateAssignCache(namespace = CACHE_NAMESPACE, assignedKey = "tree")
    public void delete(long id) {
        policeStationDao.delete(id);
    }

    private void loopChangePSValues(String fieldName, long sId,int value){
        PoliceStation ps = policeStationDao.findOne(sId);
        if(null != ps){
            changeFieldValue(ps,fieldName,value);
            policeStationDao.save(ps);
            long parentId = ps.getParentId();
            if(parentId != 0){
                loopChangePSValues(fieldName, parentId, value);
            }
        }
    }

    public  Object changeFieldValue(Object o,String fieldName,int value){
        try {
            Field f = o.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(o, f.getInt(o)+value);
            return o;
        } catch (Exception e) {
           LOG.error("change field error:",e);
           return null;
        } 
    } 
    
}
