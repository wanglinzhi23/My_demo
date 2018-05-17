package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.database.entity.MarkInfo;
import intellif.database.entity.TaskInfo;

public interface MarkInfoDao extends CrudRepository<MarkInfo, Long>{

	@Query(value = "SELECT * FROM t_user_business_api WHERE user_id =:userid", nativeQuery = true)
    List<MarkInfo> findByUserId(@Param("userid") long userId);
	
}
