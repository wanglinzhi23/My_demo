package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.AuditLogInfo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yangboz on 12/2/15.
 */
public interface AuditLogDao extends CrudRepository<AuditLogInfo, Long> {

//	void deleteByObjectId(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AUDIT_LOG+" WHERE object = :object and object_id=:objectId", nativeQuery = true)
	void deleteByObjectAndObjectId(@Param("object") String object, @Param("objectId") long objectId);

    @Modifying
    @Transactional
    void deleteByObjectId(long objectId);
    
    List<AuditLogInfo> findByObjectId(long objectId);
}
