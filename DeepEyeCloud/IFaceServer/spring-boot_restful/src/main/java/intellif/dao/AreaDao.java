package intellif.dao;

import java.math.BigInteger;
import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.Area;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AreaDao extends CrudRepository<Area, Long> {


    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AREA+" SET person_threshold = :threshold", nativeQuery = true)
	void updateThreshold(@Param("threshold") long threshold);
    
    
    @Query(value = "SELECT id FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AREA+" t WHERE t.area_no = :areaCode", nativeQuery = true)
    List<BigInteger> findAreaIdByCode(@Param("areaCode") String areaCode);
}