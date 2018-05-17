package intellif.database.dao.impl;

import java.util.List;

import intellif.database.dao.PersonDetailDao;
import intellif.database.entity.PersonDetail;
import intellif.dto.PersonFullDto;
import intellif.service.impl.PersonDetailServiceImpl;

import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class PersonDetailDaoImpl  extends AbstractCommonDaoImpl<PersonDetail> implements PersonDetailDao<PersonDetail>{
    private static Logger LOG = LogManager.getLogger(PersonDetailServiceImpl.class);

  
    @Override
    public Class<PersonDetail> getEntityClass() {
        // TODO -generated method stub
        return PersonDetail.class;
    }

    @Override
    public String getEntityTable() {
        Table table = PersonDetail.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }

    @Override
    public List<PersonFullDto> getPersonsBySql(String sql) {
        LOG.info("DB SQL:"+sql);
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<PersonFullDto>(PersonFullDto.class));
    }
}
