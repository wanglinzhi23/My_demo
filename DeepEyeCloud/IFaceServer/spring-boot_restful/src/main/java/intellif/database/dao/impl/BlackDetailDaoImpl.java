package intellif.database.dao.impl;

import intellif.database.dao.BlackDetailDao;
import intellif.database.entity.BlackDetail;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class BlackDetailDaoImpl  extends AbstractCommonDaoImpl<BlackDetail> implements BlackDetailDao<BlackDetail>{

  
    @Override
    public Class<BlackDetail> getEntityClass() {
        // TODO -generated method stub
        return BlackDetail.class;
    }

    @Override
    public String getEntityTable() {
        Table table = BlackDetail.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
