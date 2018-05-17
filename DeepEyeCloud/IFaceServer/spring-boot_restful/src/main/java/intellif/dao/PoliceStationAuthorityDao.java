package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PoliceStationAuthority;

public interface PoliceStationAuthorityDao extends CrudRepository<PoliceStationAuthority, Long> {

	List<PoliceStationAuthority> findByStationId(long id);

	List<PoliceStationAuthority> findByBankId(long id);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" WHERE station_id = :stationId AND bank_id=:bankId", nativeQuery = true)
	List<PoliceStationAuthority> findByStationIdAndBankId(@Param("stationId") long stationId, @Param("bankId") long bankId);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" WHERE bank_id = :bankId AND type = :type", nativeQuery = true)
    List<PoliceStationAuthority> findByBankIdAndType(@Param("bankId") long bankId, @Param("type") int type);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" WHERE station_id = :stationId AND type>=:autorityType", nativeQuery = true)
	List<PoliceStationAuthority> findByStationIdAndType(@Param("stationId") long stationId, @Param("autorityType") int autorityType);
	
	//库查询 新增
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" WHERE station_id = :stationId AND type=:autorityType", nativeQuery = true)
	List<PoliceStationAuthority> findByStationType(@Param("stationId") long stationId, @Param("autorityType") int autorityType);

	//只根据type查询 新增
    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" WHERE type=:autorityType ORDER BY station_id", nativeQuery = true)
    List<PoliceStationAuthority> findByAuthorityType(@Param("autorityType") int autorityType);
    
	
	@Query(value = "SELECT p.* FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" p left join "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" b on b.id = p.bank_id WHERE b.list_type=:type", nativeQuery = true)
	List<PoliceStationAuthority> findByBankType(@Param("type") int type);
	
	
	@Query(value = "SELECT p.* FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY+" p left join "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" b on b.id = p.bank_id WHERE b.list_type=:type and p.station_id=:id", nativeQuery = true)
	List<PoliceStationAuthority> findByBankTypeAndStationId(@Param("type") int type,@Param("id") long id);
   
	@Modifying
    @Transactional
	@Query(value = "DELETE FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_POLICE_STATION_AUTHORITY + " where station_id = :stationId AND bank_id = :bankId", nativeQuery = true)
    void deleteByStationIdAndBankId(@Param("stationId") long stationId, @Param("bankId") long bankId);
		 
}
