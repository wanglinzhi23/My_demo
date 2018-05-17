package intellif.dao;

import java.math.BigInteger;
import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.DistrictInfo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DistrictDao extends CrudRepository<DistrictInfo, Long> {

    @Query(value = "SELECT id FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_DISTRICT+" t WHERE t.district_no = :districtCode", nativeQuery = true)
    List<BigInteger> findDistrictIdByCode(@Param("districtCode") String districtCode);
    

    //查找所在节点分局 即本地分局 （可直接布控的分局）
    @Query(value = "SELECT id FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_DISTRICT+" t WHERE t.local = :local", nativeQuery = true)
    List<BigInteger> findDistrictIdByLocal(@Param("local") int local);



}