/**
 *
 */
package intellif.dao;

import intellif.database.entity.TaskInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface TaskInfoDao.
 *
 * @author yangboz
 */
// @Transactional
public interface TaskInfoDao extends CrudRepository<TaskInfo, Long>, Serializable {
    //
    @Query(value = "SELECT * FROM t_task_info t WHERE t.task_name LIKE :value", nativeQuery = true)
    List<TaskInfo> findByTaskName(@Param("value") String value);

    List<TaskInfo> findBySourceId(long value);

    List<TaskInfo> findBySourceType(int value);

    List<TaskInfo> findByStatus(int value);

    // List<TaskInfo> findByStatusAndSourceIdAndSourceTypeAndTaskName(int
    // value);
//	@Query(value = "SELECT t_task_info.* FROM t_server_info sInfo LEFT JOIN t_task_info tInfo ON sInfo.id= :serverId WHERE sInfo.serverName LIKE :serverName", nativeQuery = true)
//	List<TaskInfo> findByServerIdAndServerName(@Param("serverId") int serverId, @Param("serverName") String serverName);
    @Query(value = "SELECT a.* FROM t_task_info a LEFT JOIN t_server_info b ON a.server_id=b.id WHERE b.server_name LIKE :serverName", nativeQuery = true)
    List<TaskInfo> findByServerName(@Param("serverName") String serverName);

    @Query(value = "SELECT * FROM t_task_info WHERE t_task_info.created BETWEEN :start AND :end", nativeQuery = true)
    List<TaskInfo> findByTimePeriod(@Param("start") String start, @Param("end") String end);

    @Query(value = "SELECT a.* FROM t_task_info a LEFT JOIN t_black_bank b ON a.bank_id=b.id WHERE b.bank_name LIKE :bankName", nativeQuery = true)
    List<TaskInfo> findByBankName(@Param("bankName") String bankName);

    @Query(value = "SELECT a.* FROM t_task_info a LEFT JOIN :sourceTable b ON a.source_id = b.id WHERE b.uri LIKE :uri", nativeQuery = true)
    List<TaskInfo> findBySourceTypeAndUri(@Param("sourceTable") String sourceTable, @Param("uri") String uri);
//	@Query(value = "SELECT a.* FROM t_task_info a LEFT JOIN t_camera_info b ON a.source_id = b.id WHERE b.uri LIKE :uri", nativeQuery = true)
//	List<TaskInfo> findBySourceTypeAndUri(@Param("uri") String uri);

    List<TaskInfo> findByBankId(long value);

    List<TaskInfo> findByServerId(long value);

    List<TaskInfo> findByServerIdAndStatus(long value, int status);

}
