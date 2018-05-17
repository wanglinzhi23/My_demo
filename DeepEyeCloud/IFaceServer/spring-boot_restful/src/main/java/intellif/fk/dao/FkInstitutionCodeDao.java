package intellif.fk.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.fk.vo.FkInstitutionCode;
import intellif.fk.vo.FkPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface FkInstitutionCodeDao extends CrudRepository<FkInstitutionCode, Long> {

    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FK_INSTITUTION_CODE+" t WHERE t.JGDM=:institutionCode", nativeQuery = true)
    List<FkInstitutionCode> findInstitionByCode(@Param("institutionCode") String institutionCode);
    
    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FK_INSTITUTION_CODE+" t WHERE t.JGMC=:JGMC", nativeQuery = true)
    List<FkInstitutionCode> findInstitionByName(@Param("JGMC") String JGMC);

}
