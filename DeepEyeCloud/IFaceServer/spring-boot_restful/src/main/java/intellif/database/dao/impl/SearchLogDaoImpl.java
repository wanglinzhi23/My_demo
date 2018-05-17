package intellif.database.dao.impl;

import intellif.database.dao.SearchLogDao;
import intellif.database.entity.SearchLogInfo;
import intellif.database.entity.UserInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class SearchLogDaoImpl  extends AbstractCommonDaoImpl<SearchLogInfo> implements SearchLogDao<SearchLogInfo>{

  
    @Override
    public Class<SearchLogInfo> getEntityClass() {
        // TODO -generated method stub
        return SearchLogInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = SearchLogInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
