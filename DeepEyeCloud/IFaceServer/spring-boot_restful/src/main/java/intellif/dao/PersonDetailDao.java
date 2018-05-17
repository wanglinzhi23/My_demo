/**
 * 
 */
package intellif.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PersonDetail;

/**
 * The Interface PersonDetailDao.
 *
 * @author yangboz
 */
public interface PersonDetailDao extends CrudRepository<PersonDetail, Long> {

	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" WHERE bank_id in (:ids)", nativeQuery = true)
	List<PersonDetail> findAll(@Param("ids")String[] ids);
	
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" WHERE id=:id AND bank_id in (:ids)", nativeQuery = true)
	List<PersonDetail> findOne(@Param("id")long id, @Param("ids")String[] ids);
	
    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" SET status = 0 WHERE endtime < sysdate() OR starttime > sysdate()", nativeQuery = true)
	void refreshStopStatus();
    
    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" SET status = 1 WHERE endtime >= sysdate() AND starttime <= sysdate()", nativeQuery = true)
	void refreshStartStatus();

	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" WHERE (endtime < sysdate() OR starttime > sysdate()) AND status = 1", nativeQuery = true)
	List<PersonDetail> findNeedStop();
	
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" WHERE  endtime >= sysdate() AND starttime <= sysdate() AND status = 0", nativeQuery = true)
	List<PersonDetail> findNeedStart();

    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" SET bank_id = :id WHERE id in (:ids) AND bank_id in (:banks)", nativeQuery = true)
	void modifyBank(@Param("id")long id, @Param("ids")String[] ids, @Param("banks")String[] banks);

	List<PersonDetail> findByBankId(long id);

    @Query(value = "SELECT count(1) FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL + " WHERE bank_id = :id", nativeQuery = true)
	Long countByBankId(@Param("id")long id);
    
    List<PersonDetail> findByRuleId(long rId);

	List<PersonDetail>  findByCrimeType(long id);
	
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL + " WHERE id in (:ids)", nativeQuery = true)
	List<PersonDetail> findByIds(@Param("ids")String[] ids);
	
	@Modifying
	@Transactional
	@Query(value = "update " + GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL + " p set p.endtime = :date, p.status = :status WHERE p.bank_id = :bankId", nativeQuery = true)
	void updateBankPersonDispatch(@Param("date")Date date,@Param("status")int status,@Param("bankId")int bankId);

	@Query(value = "SELECT count(1) FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PERSON_DETAIL+" p  WHERE  p.bank_id  in (:ids) and p.type = 0", nativeQuery = true)
	Long countByBlackBankIds(@Param("ids")String[] ids);

}
