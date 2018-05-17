package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PersonInzones;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PersonInzonesDao extends CrudRepository<PersonInzones, Long> {

    @Query(value = "SELECT station_id,count(*) FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_INZONES + " group by station_id", nativeQuery = true)
	List<Object[]> countByStationId();
	
	 @Query(value = "SELECT face_id FROM "  + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_INZONES + " WHERE station_id = :stationId and updated <= str_to_date(:time,'%Y%m%d%H%i%s') order by updated desc limit :start, :pageSize", nativeQuery = true)
    List<Long> findPersonInzonesFaceIdByStationId(@Param("stationId") long stationId, @Param("start") int start, @Param("pageSize") int pageSize, @Param("time") String time);

}
