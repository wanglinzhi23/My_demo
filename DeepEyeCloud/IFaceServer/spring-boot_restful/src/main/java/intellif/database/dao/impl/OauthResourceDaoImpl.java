package intellif.database.dao.impl;

import intellif.database.dao.AreaDao;
import intellif.database.dao.OauthResourceDao;
import intellif.database.entity.DistrictInfo;
import intellif.database.entity.OauthResource;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class OauthResourceDaoImpl  extends AbstractCommonDaoImpl<OauthResource> implements OauthResourceDao<OauthResource>{

  
    @Override
    public Class<OauthResource> getEntityClass() {
        // TODO -generated method stub
        return OauthResource.class;
    }

    @Override
    public String getEntityTable() {
        Table table = OauthResource.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
