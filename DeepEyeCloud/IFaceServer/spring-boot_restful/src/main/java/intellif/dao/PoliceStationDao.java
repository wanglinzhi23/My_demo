/**
 * 
 */
package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PoliceStation;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Interface PoliceStationDao.
 *
 * @author Peng Cheng
 */
//@Transactional
public interface PoliceStationDao extends CrudRepository<PoliceStation, Long> {
	//
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION+" t WHERE t.station_name = :value", nativeQuery = true)
	List<PoliceStation> findByBankName(@Param("value") String value);
	
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION+" t WHERE t.station_name like %:value%", nativeQuery = true)
	List<PoliceStation> findByLikeName(@Param("value") String value);
    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION+" SET person_threshold = :threshold", nativeQuery = true)
	void updateThreshold(@Param("threshold") long threshold);
    
    
    @Query(value = "SELECT p.id as station_id,p.station_name,c.name FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION+" p left join "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CAMERA_INFO+" c on p.id=c.station_id left join "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_TASK_INFO+" t on c.id= t.source_id WHERE t.id=:taskId",nativeQuery = true)
	List<Object[]> findPoliceByTaskId(@Param("taskId") long taskId);
	
	List<PoliceStation> findByParentId(long parentId);
	
	@Query(value = "select p.* from "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_MOBILE_COLLECT_STATION_MAP+" m INNER JOIN "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION+" p on p.id = m.mapped_station_id",nativeQuery = true)
	List<PoliceStation> findLongGangPCS();
	    
}
