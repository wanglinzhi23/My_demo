/**
 * 
 */
package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.CameraInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * The Interface CameraInfoDao.
 *
 * @author yangboz
 */
//@Transactional
public interface CameraInfoDao extends CrudRepository<CameraInfo,Long> {
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" t WHERE t.name = :value", nativeQuery = true)
	List<CameraInfo> findByName(@Param("value") String value);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" t WHERE t.station_id = :value", nativeQuery = true)
	List<CameraInfo> findByStationId(@Param("value") long value);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" t WHERE t.station_id in (:value)", nativeQuery = true)
	List<CameraInfo> findByStationIdList(@Param("value") List<Integer> value);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" t WHERE t.in_station = 1", nativeQuery = true)
	List<CameraInfo> findInStation();
	
	List<CameraInfo> findById(long id);
	
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" t WHERE t.code = :value", nativeQuery = true)
	List<CameraInfo> findByCode(@Param("value") String value);
	
	@Query(value = "SELECT camera.* FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO + " camera," + GlobalConsts.INTELLIF_BASE
	        + "." + GlobalConsts.T_NAME_TASK_INFO + " task WHERE camera.id = task.source_id and task.id = :value", nativeQuery = true)
	List<CameraInfo> findByTaskId(@Param("value")long value);
}
