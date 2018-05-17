package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.LossPrePerson;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.RedDetail;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface LossPreDao extends CrudRepository<LossPrePerson, Long>{
	 @Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_LOSS_PRE+" WHERE station_id=:stationId AND weixin_id=:weixinId", nativeQuery = true)
	 List<LossPrePerson> findByWeixinIdAndStationId(@Param("stationId")long stationId, @Param("weixinId")String weixinId);
	 List<LossPrePerson> findByWeixinId(String weixinId);
	 List<LossPrePerson> findByStationId(long stationId);
}
