package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.consts.GlobalConsts;
import intellif.database.entity.RedPerson;

public interface PersonRedDao extends CrudRepository<RedPerson, Long>  {
	 @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_RED_PERSON+" t WHERE t.name like %:value%", nativeQuery = true)
	 List<RedPerson> findByLikeName(@Param("value")String value);

}
