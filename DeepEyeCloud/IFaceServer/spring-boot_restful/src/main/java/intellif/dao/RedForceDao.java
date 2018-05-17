/**
 * 
 */
package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.RedForceRecord;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * The Interface RedMistakeDao.
 *
 * @author shixiaohua
 */
public interface RedForceDao extends CrudRepository<RedForceRecord, Long> {
	
	List<RedForceRecord> findById(long id);
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_RED_FORCE+" WHERE created >= :sTime and created <= :eTime limit :start,:end", nativeQuery = true)
	List<RedForceRecord> findByTimes(@Param("sTime")String sTime,@Param("eTime")String eTime, @Param("start")int start, @Param("end")int end);
}
