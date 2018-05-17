package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.OtherInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OtherInfoDao extends JpaRepository<OtherInfo, Long> {

    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO + " WHERE id in (:ids)", nativeQuery = true)
	List<OtherInfo> findByIds(@Param("ids") Long[] ids);

	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_STATIC + "." +GlobalConsts.T_NAME_OTHER_INFO+"  order by id desc limit :start, :pageSize", nativeQuery = true)
	List<OtherInfo> findLast(@Param("start") int start, @Param("pageSize") int pageSize);

}
