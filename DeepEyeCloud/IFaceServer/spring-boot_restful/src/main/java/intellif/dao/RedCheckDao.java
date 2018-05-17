/**
 * 
 */
package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.RedCheckRecord;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * The Interface RedMistakeDao.
 *
 * @author shixiaohua
 */
public interface RedCheckDao extends CrudRepository<RedCheckRecord, Long> {
	
	List<RedCheckRecord> findById(long id);
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_RED_CHECK+" WHERE created >= :sTime and created <= :eTime limit :start,:end", nativeQuery = true)
	List<RedCheckRecord> findByTimes(@Param("sTime")String sTime, @Param("eTime")String eTime, @Param("start")int start, @Param("end")int end);
}
