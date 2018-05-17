package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.AreaAndBlackDetail;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

//@Transactional(readOnly = true)
public interface AreaAndBlackDetailDao extends CrudRepository<AreaAndBlackDetail, Long> {
    List<AreaAndBlackDetail> findByAreaId(long cId);

    List<AreaAndBlackDetail> findByBlackdetailId(long bdId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AREA_BLACKDETAIL+" WHERE area_id = :areaId and blackdetail_id=:blackId", nativeQuery = true)
    void deleteByAreaIdAndBlackdetailId(@Param("areaId") long areaId, @Param("blackId") long blackId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AREA_BLACKDETAIL+" WHERE blackdetail_id=:blackId", nativeQuery = true)
    void deleteByBlackdetailId(@Param("blackId") long blackId);
    
    @Query(value = "select c.area_id from "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_AREA_BLACKDETAIL+" c LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" d on d.id = c.blackdetail_id LEFT JOIN "+GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" p on d.from_person_id = p.id WHERE p.id = :pId GROUP BY c.area_id", nativeQuery = true)
    List<BigInteger> findAreaIdsByPersonId(@Param("pId") long pId);

    
}
