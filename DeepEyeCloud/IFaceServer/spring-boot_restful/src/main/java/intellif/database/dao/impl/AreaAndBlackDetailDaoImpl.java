package intellif.database.dao.impl;

import intellif.consts.GlobalConsts;
import intellif.database.dao.AreaAndBlackDetailDao;
import intellif.database.entity.AreaAndBlackDetail;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
@Service
public class AreaAndBlackDetailDaoImpl extends AbstractCommonDaoImpl<AreaAndBlackDetail> implements AreaAndBlackDetailDao<AreaAndBlackDetail>{
  
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Override
    public Class<AreaAndBlackDetail> getEntityClass() {
        // TODO Auto-generated method stub
        return AreaAndBlackDetail.class;
    }

    @Override
    public String getEntityTable() {
        Table table = AreaAndBlackDetail.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }

    @Override
    public List<BigInteger> findAreaIdsByPersonId(long pId) {
        String sql = "select c.area_id from "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AREA_BLACKDETAIL
                +" c LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL
                +" d on d.id = c.blackdetail_id LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL
                +" p on d.from_person_id = p.id WHERE p.id = "+pId+" GROUP BY c.area_id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(BigInteger.class));
    }
 
}
