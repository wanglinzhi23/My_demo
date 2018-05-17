package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.JuZhuInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JuZhuInfoDao extends JpaRepository<JuZhuInfo, Long> {

    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_JUZHU_INFO + " WHERE id in (:ids)", nativeQuery = true)
	List<JuZhuInfo> findByIds(@Param("ids") Long[] ids);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_JUZHU_INFO+"  order by id desc limit :start, :pageSize", nativeQuery = true)
	List<JuZhuInfo> findLast(@Param("start") int start, @Param("pageSize") int pageSize);

}
