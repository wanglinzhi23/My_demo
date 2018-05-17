package intellif.database.dao.impl;

import intellif.database.dao.AreaDao;
import intellif.database.dao.RedDetailDao;
import intellif.database.entity.DistrictInfo;
import intellif.database.entity.RedDetail;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class RedDetailDaoImpl  extends AbstractCommonDaoImpl<RedDetail> implements RedDetailDao<RedDetail>{

  
    @Override
    public Class<RedDetail> getEntityClass() {
        // TODO -generated method stub
        return RedDetail.class;
    }

    @Override
    public String getEntityTable() {
        Table table = RedDetail.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
