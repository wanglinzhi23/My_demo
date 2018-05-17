package intellif.chd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.chd.vo.ZipPath;
import intellif.consts.GlobalConsts;

public interface ZipPathDao extends CrudRepository<ZipPath, Long> {
	
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_ZIP_PATH + " ORDER BY id DESC LIMIT :page,:pagesize ", nativeQuery = true)
	List<ZipPath> findAllByPage(@Param("page")int page,@Param("pagesize")int pagesize);

}
