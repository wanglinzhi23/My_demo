package intellif.database.dao.impl;

import intellif.database.dao.AreaDao;
import intellif.database.dao.CameraInfoDao;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.UserInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class CameraDaoImpl  extends AbstractCommonDaoImpl<CameraInfo> implements CameraInfoDao<CameraInfo>{

  
    @Override
    public Class<CameraInfo> getEntityClass() {
        // TODO -generated method stub
        return CameraInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = CameraInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
