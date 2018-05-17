package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.CidDetail;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CidDetailDao extends CrudRepository<CidDetail, Long> {

    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_CID_DETAIL + " WHERE indexed = 0 limit 0,20000", nativeQuery = true)
    List<CidDetail> findUnIndexed();

//    @Modifying
//    @Transactional
//	@Query(value = "UPDATE "+GlobalConsts.T_NAME_CID_DETAIL+" SET indexed = 1 WHERE id >= :fId AND id <= :tId AND indexed = 0", nativeQuery = true)
//	void update(@Param("fId") long fId, @Param("tId") long tId);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_CID_DETAIL+" order by id desc limit :start, :pageSize", nativeQuery = true)
	List<CidDetail> findLast(@Param("start") int start, @Param("pageSize") int pageSize);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_CID_DETAIL+" WHERE zplxmc = :zplxmc and indexed = :indexed limit 0, 100", nativeQuery = true)
	List<CidDetail> findByIndexedAndZplxmc(@Param("indexed")int indexed, @Param("zplxmc")String zplxmc);
	
	List<CidDetail> findByFromCidId(long id);
}
