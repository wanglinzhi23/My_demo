package intellif.database.dao.impl;

import java.util.List;

import intellif.database.dao.AlarmInfoDao;
import intellif.database.entity.AlarmInfo;
import intellif.database.entity.EventInfo;
import intellif.database.entity.UserInfo;
import intellif.service.impl.AlarmServiceImpl;

import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
@Service
public class AlarmInfoDaoImpl extends AbstractCommonDaoImpl<AlarmInfo> implements AlarmInfoDao<AlarmInfo>{
    private static Logger LOG = LogManager.getLogger(AlarmInfoDaoImpl.class);
    @Override
    public Class<AlarmInfo> getEntityClass() {
        // TODO Auto-generated method stub
        return AlarmInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = AlarmInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }

    @Override
    public List<Long> getListLongBySql(String sql) {
        return jdbcTemplate.queryForList(sql, Long.class);
    }


    @Override
    public List<EventInfo> getPersonEventListBySql(String sql) {
        LOG.info("DB SQL:"+sql);
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<EventInfo>(EventInfo.class));
    }
 
}
