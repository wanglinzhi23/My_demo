/**
 *
 */
package intellif.dao;


import intellif.database.entity.AlarmInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The Interface AlarmInfoDao.
 *
 * @author yangboz
 */
//@Transactional

public interface AlarmInfoDao extends CrudRepository<AlarmInfo, Long> {
    List<AlarmInfo> findAllByOrderByTimeAsc();

//	@Query(value = "DELETE FROM "+GlobalConsts.T_NAME_ALARM_INFO+" t WHERE task_id = :taskId", nativeQuery = true)
//	void delAlarmsByTaskId(@Param("taskId") String taskId);
    @Modifying
    @Transactional
    Long deleteByTaskId(long taskId);
    @Modifying
    @Transactional
    Long deleteByBlackId(long blackId);
    
    @Modifying
    @Transactional
    Long deleteByFaceId(long faceId);
}
