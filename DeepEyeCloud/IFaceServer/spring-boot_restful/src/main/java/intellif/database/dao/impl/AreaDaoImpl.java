package intellif.database.dao.impl;

import intellif.database.dao.AreaDao;
import intellif.database.entity.UserInfo;
import intellif.database.entity.Area;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class AreaDaoImpl  extends AbstractCommonDaoImpl<Area> implements AreaDao<Area>{

  
    @Override
    public Class<Area> getEntityClass() {
        // TODO -generated method stub
        return Area.class;
    }

    @Override
    public String getEntityTable() {
        Table table = Area.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
