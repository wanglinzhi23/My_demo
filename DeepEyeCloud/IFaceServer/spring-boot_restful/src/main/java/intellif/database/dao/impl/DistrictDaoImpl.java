package intellif.database.dao.impl;

import intellif.database.dao.AreaDao;
import intellif.database.dao.DistrictDao;
import intellif.database.entity.DistrictInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class DistrictDaoImpl  extends AbstractCommonDaoImpl<DistrictInfo> implements DistrictDao<DistrictInfo>{

  
    @Override
    public Class<DistrictInfo> getEntityClass() {
        // TODO -generated method stub
        return DistrictInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = DistrictInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
