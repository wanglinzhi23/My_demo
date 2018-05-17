package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.JuZhuDetail;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface JuZhuDetailDao extends CrudRepository<JuZhuDetail, Long> {

    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_JUZHU_DETAIL + " WHERE indexed = 0 limit 0,20000", nativeQuery = true)
    List<JuZhuDetail> findUnIndexed();

//    @Modifying
//    @Transactional
//	@Query(value = "UPDATE "+GlobalConsts.T_NAME_CID_DETAIL+" SET indexed = 1 WHERE id >= :fId AND id <= :tId AND indexed = 0", nativeQuery = true)
//	void update(@Param("fId") long fId, @Param("tId") long tId);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_JUZHU_DETAIL+"  order by id desc limit :start, :pageSize", nativeQuery = true)
	List<JuZhuDetail> findLast(@Param("start") int start, @Param("pageSize") int pageSize);
	
	
	////// 8.9   为了获取 所有上传 在居住证信息中人脸图片   
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_JUZHU_DETAIL+" WHERE zplxmc = :zplxmc and indexed = :indexed limit 0, 100", nativeQuery = true)
	List<JuZhuDetail> findByIndexedAndZplxmc(@Param("indexed")int indexed, @Param("zplxmc")String zplxmc);
	
	List<JuZhuDetail> findByFromCidId(long id);
}
