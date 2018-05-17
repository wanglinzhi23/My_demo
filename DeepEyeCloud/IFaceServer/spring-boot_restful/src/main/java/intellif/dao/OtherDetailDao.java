package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.OtherDetail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OtherDetailDao extends CrudRepository<OtherDetail, Long> {

    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_DETAIL + " WHERE indexed = 0 limit 0,20000", nativeQuery = true)
    List<OtherDetail> findUnIndexed();

//    @Modifying
//    @Transactional
//	@Query(value = "UPDATE "+GlobalConsts.T_NAME_CID_DETAIL+" SET indexed = 1 WHERE id >= :fId AND id <= :tId AND indexed = 0", nativeQuery = true)
//	void update(@Param("fId") long fId, @Param("tId") long tId);

	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_OTHER_DETAIL+"  order by id desc limit :start, :pageSize", nativeQuery = true)
	List<OtherDetail> findLast(@Param("start") int start, @Param("pageSize") int pageSize);
	
	List<OtherDetail> findByZplxmc(String zplxmc);
	
	List<OtherDetail> findByFromCidId(long id);

    @Modifying
    @Transactional
	@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_OTHER_DETAIL+" SET json = :json WHERE id = :id", nativeQuery = true)
	void updateJson(@Param("id")long id, @Param("json")String json);
}
