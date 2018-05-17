package intellif.chd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.chd.vo.ContrastFaceInfo;
import intellif.consts.GlobalConsts;

public interface ContrastFaceInfoDao extends CrudRepository<ContrastFaceInfo, Long> {
    
    @Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_CONTRAST_FACE_INFO + " LIMIT :page,:pagesize ", nativeQuery = true)
    List<ContrastFaceInfo> findAllByPage(@Param("page")int page,@Param("pagesize")int pagesize);

}
