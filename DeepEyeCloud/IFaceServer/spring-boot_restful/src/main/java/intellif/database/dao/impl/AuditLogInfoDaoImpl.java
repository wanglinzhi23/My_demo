package intellif.database.dao.impl;

import intellif.database.dao.AuditLogInfoDao;
import intellif.database.dao.BlackBankDao;
import intellif.database.entity.AuditLogInfo;
import intellif.database.entity.BlackBank;
import intellif.database.entity.RoleInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;
@Service
public class AuditLogInfoDaoImpl extends AbstractCommonDaoImpl<AuditLogInfo> implements AuditLogInfoDao<AuditLogInfo>{
  
    @Override
    public Class<AuditLogInfo> getEntityClass() {
        // TODO Auto-generated method stub
        return AuditLogInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = RoleInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
 
}
