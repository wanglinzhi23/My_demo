package intellif.service.impl;

import intellif.database.dao.AuditLogInfoDao;
import intellif.database.dao.CommonDao;
import intellif.database.entity.AuditLogInfo;
import intellif.service.AuditLogInfoServiceItf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogInfoServiceImpl extends AbstractCommonServiceImpl<AuditLogInfo> implements AuditLogInfoServiceItf<AuditLogInfo> {

    private static Logger LOG = LogManager.getLogger(AuditLogInfoServiceImpl.class);
    
    @Autowired
    AuditLogInfoDao auditLogDao;


    @Override
    public CommonDao getDao() {
        // TODO Auto-generated method stub
        return auditLogDao;
    }
    
}
