package intellif.database.dao.impl;

import intellif.database.dao.AreaDao;
import intellif.database.dao.UserAreaDao;
import intellif.database.entity.SearchLogInfo;
import intellif.database.entity.UserArea;
import intellif.database.entity.UserInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class UserAreaDaoImpl  extends AbstractCommonDaoImpl<UserArea> implements UserAreaDao<UserArea>{

  
    @Override
    public Class<UserArea> getEntityClass() {
        // TODO -generated method stub
        return UserArea.class;
    }

    @Override
    public String getEntityTable() {
        Table table = UserArea.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
