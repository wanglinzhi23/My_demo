package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dto.FaceStatisticDto;
import intellif.database.entity.FaceCameraCount;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FaceCameraCountDao extends CrudRepository<FaceCameraCount, Long> {

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id = :id and time >= :date order by time desc", nativeQuery = true)
	List<FaceCameraCount> findBySourceIdByTime(@Param("id") long id, @Param("date") String date);

	@Query(value = "SELECT SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id = :id", nativeQuery = true)
	long statisticBySourceId(@Param("id") long id);

	@Query(value = "SELECT SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT, nativeQuery = true)
	long statisticAll();

	@Query(value = "SELECT time, SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE time >= :date group by time", nativeQuery = true)
	List<FaceStatisticDto> staticByDate(@Param("date") String date);
	
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT + " WHERE source_id in (:ids) and time >= :startdate and time <= :enddate order by time asc", nativeQuery = true)
	List<FaceCameraCount> findBySourceIdByPeriod(@Param("ids") Long[] ids, @Param("startdate") String startdate, @Param("enddate") String enddate);
	
	@Query(value = "SELECT id, source_id, time, sum(count) as count FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT + " WHERE time >= :startdate and time <= :enddate group by DATE_FORMAT(time, '%y-%m-%d') order by time asc", nativeQuery = true)
	List<FaceCameraCount> findByPeriod(@Param("startdate") String startdate, @Param("enddate") String enddate);

	@Query(value = "SELECT SUM(count) FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id in (SELECT id from "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" WHERE station_id = :id)", nativeQuery = true)
	Long statisticByAreId(@Param("id") long id);
	
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FACE_CAMERA_COUNT+"  WHERE source_id = :id and time = :date order by time desc", nativeQuery = true)
    List<FaceCameraCount> findBySourceIdAndTime(@Param("id") long id, @Param("date") String date);

}
