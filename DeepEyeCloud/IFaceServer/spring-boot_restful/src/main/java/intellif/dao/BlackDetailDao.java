/**
 * 
 */
package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CidDetail;

/**
 * The Interface BlackDetailDao.
 *
 * @author yangboz
 */
//@Transactional
public interface BlackDetailDao extends CrudRepository<BlackDetail, Long> {

	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" WHERE bank_id in (:ids)", nativeQuery = true)
	List<BlackDetail> findAll(@Param("ids")String[] ids);
	
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" WHERE id=:id AND bank_id in (:ids)", nativeQuery = true)
	List<BlackDetail> findOne(@Param("id")long id, @Param("ids")String[] ids);
	
	List<BlackDetail> findByBankId(long id);
	
	List<BlackDetail> findByFromPersonId(long id);

	List<BlackDetail> findById(long black_id);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" WHERE bank_id in (:ids) order by created desc limit :start, :pageSize", nativeQuery = true)
	List<BlackDetail> findLast(@Param("start") int start, @Param("pageSize") int pageSize, @Param("ids")String[] ids);


    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" SET bank_id = :id WHERE from_person_id in (:ids) AND bank_id in (:banks)", nativeQuery = true)
	void modifyBank(@Param("id")long id, @Param("ids")String[] ids, @Param("banks")String[] banks);


    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " WHERE indexed = 0 limit 0,5000", nativeQuery = true)
    List<BlackDetail> findUnIndexed();
    
    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL + " WHERE bank_id = :bankId order by created desc limit :start, :pagesize", nativeQuery = true)
    List<BlackDetail> findBankDetailPage(@Param("start") int start, @Param("pagesize") int pagesize, @Param("bankId") long bankId);

    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_DETAIL+" SET json = :json WHERE id = :id", nativeQuery = true)
	void updateJson(@Param("id")long id, @Param("json")String json);

}
