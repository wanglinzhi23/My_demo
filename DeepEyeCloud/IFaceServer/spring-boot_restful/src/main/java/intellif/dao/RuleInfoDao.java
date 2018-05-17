/**
 * 
 */
package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.consts.GlobalConsts;
import intellif.database.entity.RuleInfo;

/**
 * The Interface RuleInfoDao.
 *
 * @author yangboz
 */
//@Transactional
public interface RuleInfoDao extends CrudRepository<RuleInfo, Long> {
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_RULE_INFO+" t WHERE t.rule_name = :value", nativeQuery = true)
	List<RuleInfo> findByRuleName(@Param("value") String value);
	
	List<RuleInfo> findById(long rule_id);
}
