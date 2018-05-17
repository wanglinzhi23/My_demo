package intellif.fk.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.fk.vo.FkPersonAttr;

public interface FkPersonAttrDao extends CrudRepository<FkPersonAttr, Long> {

    List<FkPersonAttr> findByFromPersonId(long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FK_PERSON_ATTR + " SET ic_card = :icCard WHERE from_person_id=:personId", nativeQuery = true)
    void updateIcCard(@Param("personId") long personId, @Param("icCard") String icCard);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FK_PERSON_ATTR
            + " SET mac_Address = :macAddress WHERE from_person_id=:personId", nativeQuery = true)
    void updateMacAddress(@Param("personId") long personId, @Param("macAddress") String macAddress);
    
    @Query(value = "SELECT ic_card FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FK_PERSON_ATTR+" t WHERE t.from_person_id = :personId", nativeQuery = true)
    String getIcCard(@Param("personId") long personId);
    
    @Query(value = "SELECT mac_address FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FK_PERSON_ATTR+" t WHERE t.from_person_id = :personId", nativeQuery = true)
    String getMacAddress(@Param("personId") long personId);

}
