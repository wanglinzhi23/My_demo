package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PoliceMan;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PoliceManDao extends CrudRepository<PoliceMan, Long> {

	// 根据警号查询警员
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO
			+ "  WHERE police_no = :policeno", nativeQuery = true)
	List<PoliceMan> findByPoliceNo(@Param("policeno") String policeno);

	// 根据 单位station 查询警员
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO
			+ "  WHERE station_id = :stationid", nativeQuery = true)
	List<PoliceMan> findByStationId(@Param("stationid") long stationid);
	
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICEMAN_INFO + " WHERE police_no in (:policenos)", nativeQuery = true)
	List<PoliceMan> findByPoliceNoArray(@Param("policenos") String[] policenos);
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_POLICEMAN_INFO + " SET name = :name, phone = :phone, station_id = :stationId WHERE police_no = :policeNo", nativeQuery = true)
    void updatePoliceman(@Param("policeNo") String policeNo, @Param("name") String name, @Param("phone") String phone, @Param("stationId") Long stationId);
	
	@Query(value = "SELECT distinct(police_no) FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO , nativeQuery = true)
	List<String> findAllPoliceno();
	
}
