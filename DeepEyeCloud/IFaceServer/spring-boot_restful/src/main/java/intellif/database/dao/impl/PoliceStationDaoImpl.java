package intellif.database.dao.impl;

import intellif.database.dao.PoliceStationDao;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.RoleInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;
@Service
public class PoliceStationDaoImpl extends AbstractCommonDaoImpl<PoliceStation> implements PoliceStationDao<PoliceStation>{
  
    @Override
    public Class<PoliceStation> getEntityClass() {
        // TODO Auto-generated method stub
        return PoliceStation.class;
    }

    @Override
    public String getEntityTable() {
        Table table = PoliceStation.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
 
}
