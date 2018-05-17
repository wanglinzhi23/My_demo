package intellif.dao;

import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.PoliceCloudAuditLogInfo;

public interface PoliceCloudAuditLogDao extends CrudRepository<PoliceCloudAuditLogInfo, Long> {

}
