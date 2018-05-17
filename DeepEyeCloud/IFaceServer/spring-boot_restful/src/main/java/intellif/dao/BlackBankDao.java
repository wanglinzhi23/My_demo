
/**
 * 
 */
package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.consts.GlobalConsts;
import intellif.database.entity.BlackBank;

/**
 * The Interface BlackBankDao.
 *
 * @author yangboz
 */
//@Transactional
public interface BlackBankDao extends CrudRepository<BlackBank, Long> {

	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" WHERE id in (:ids) order by created desc", nativeQuery = true)
	List<BlackBank> findAll(@Param("ids")String[] ids);
	
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" WHERE id in (:ids) and list_type = :type", nativeQuery = true)
	List<BlackBank> findAllByType(@Param("ids")String[] ids,@Param("type")int type);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" t WHERE t.bank_name = :value", nativeQuery = true)
	List<BlackBank> findByBankName(@Param("value") String value);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" WHERE bank_name = :value and id in (:ids)", nativeQuery = true)
	List<BlackBank> findByBankNameAndIds(@Param("value") String value, @Param("ids")String[] ids);
	
	List<BlackBank> findById(long rule_id);
	
	/*//根据姓名检索库
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" WHERE id in (:ids) and bank_name like %:bankname%", nativeQuery = true)
	List<BlackBank> findByCombinedConditions(@Param("ids")String[] ids,@Param("bankname")String bankName); */
	
	//根据姓名检索库 --本单位库 分白黑名单库 的情况
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" WHERE id in (:ids) and bank_name like %:bankname% and list_type=:blackOrWhite and created >= :startTime and created <:endTime order by created desc", nativeQuery = true)
	List<BlackBank> findByCombinedConditions(@Param("ids")String[] ids,@Param("bankname")String bankName,@Param("blackOrWhite")int blackOrWhite,@Param("startTime")String startTime,@Param("endTime")String endTime); 
	
	@Query(value = "SELECT * FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" WHERE id in (:ids) order by created desc limit :start,:pagesize", nativeQuery = true)
	List<BlackBank> findByPage(@Param("ids")String[] ids,@Param("start")int start,@Param("pagesize")int pagesie);
	
	//分页查找存在重点人员的库，时间倒序
	//select b.* from t_black_bank b WHERE  EXISTS(select 1 from t_black_detail d WHERE d.bank_id = b.id) and b.id in (221,1,217,234) ORDER BY b.created desc limit 0,10

	@Query(value = "SELECT b.* FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" b WHERE EXISTS(select 1 from t_black_detail d WHERE d.bank_id = b.id) and b.id in (:ids) and b.list_type = 0 order by b.created desc limit :start,:pagesize", nativeQuery = true)
	List<BlackBank> findExistBDetailByPage(@Param("ids")String[] ids,@Param("start")int start,@Param("pagesize")int pagesie);
	
	//反恐库查询 按名称查询 按分局查询 和查询所有
	@Query(value = "SELECT b.* FROM  "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_BLACK_BANK+" b WHERE bank_name like %:name% ", nativeQuery = true)
	List<BlackBank> findFkBankList(@Param("name") String name);
	
}