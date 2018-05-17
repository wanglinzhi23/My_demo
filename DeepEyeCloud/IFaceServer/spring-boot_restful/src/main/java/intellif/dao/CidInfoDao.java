package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.CidInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CidInfoDao extends JpaRepository<CidInfo, Long> {

    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_CID_INFO + " WHERE id in (:ids)", nativeQuery = true)
	List<CidInfo> findByIds(@Param("ids") Long[] ids);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_CID_INFO+"  order by id desc limit :start, :pageSize", nativeQuery = true)
	List<CidInfo> findLast(@Param("start") int start, @Param("pageSize") int pageSize);
	
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_CID_INFO+" WHERE GMSFHM=:cid limit 1", nativeQuery = true)
	List<CidInfo> findByCid(@Param("cid")String cid);

}
