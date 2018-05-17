package intellif.fk.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.fk.vo.FkPlace;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FkPlaceDao extends CrudRepository<FkPlace, Long> {

    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FK_PLACE+" t WHERE t.place_name like %:placeName% or t.place_no like %:placeName%", nativeQuery = true)
    List<FkPlace> getPlaceByNameOrNo(@Param("placeName") String placeName);

    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_FK_PLACE+" t WHERE t.place_name =:placeName", nativeQuery = true)
    List<FkPlace> getPlaceByNameExactly(@Param("placeName") String placeName);
}