/**
 * 
 */
package intellif.dao;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.RedDetail;

/**
 * The Interface PersonDetailDao.
 *
 * @author yangboz
 */
public interface RedDetailDao extends CrudRepository<RedDetail, Long> {
	 List<RedDetail> findByFromPersonId(long id);
	 List<RedDetail> findByFromImageId(long id);
	 
	 @Query(value = "SELECT r.from_image_id FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_RED_DETAIL+" r WHERE from_person_id = :pId", nativeQuery = true)
	List<BigInteger> findRedDetailImageIdsByPersonId(@Param("pId")long pId);
	 
	 @Modifying
	 @Transactional
	 void deleteByFromImageId(long id);

	@Modifying
	@Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_RED_DETAIL+" SET json = :json WHERE id = :id", nativeQuery = true)
	void updateJson(@Param("id")long id, @Param("json")String json);
}
