package intellif.database.dao.impl;

import intellif.database.dao.BlackBankDao;
import intellif.database.entity.BlackBank;
import intellif.database.entity.RoleInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;
@Service
public class BlackBankDaoImpl extends AbstractCommonDaoImpl<BlackBank> implements BlackBankDao<BlackBank>{
  
    @Override
    public Class<BlackBank> getEntityClass() {
        // TODO Auto-generated method stub
        return BlackBank.class;
    }

    @Override
    public String getEntityTable() {
        Table table = BlackBank.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
 
}
