package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PoliceManAuthority;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PoliceManAuthorityDao extends
		CrudRepository<PoliceManAuthority, Long> {

	@Query(value = "SELECT type FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
			+ "  WHERE police_no = :policeno", nativeQuery = true)
	List<Integer> findAuthTypeByPoliceNo(@Param("policeno") String policeno);

	// 精确查询 警员是否有某种权限
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
			+ "  WHERE police_no = :policeno and type = :type", nativeQuery = true)
	List<PoliceManAuthority> findByPoliceNoAndAuthType(
			@Param("policeno") String policeno, @Param("type") int type);

	// 校验 权限值为type的警号数据库是不是真的都不存在
	@Query(value = "SELECT police_no FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
			+ "  WHERE type = :type", nativeQuery = true)
	List<String> findByType(@Param("type") int type);

	// 获取警员权限列表
	@Query(value = "select police_no,group_concat(distinct type) as types from "
			+ GlobalConsts.INTELLIF_BASE
			+ "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
			+ " group by police_no", nativeQuery = true)
	List<String> findAuthorityList();

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
			+ " WHERE police_no = :policeno and type = :type ", nativeQuery = true)
	void deleteByPoliceNoAndType(@Param("policeno") String policeno,
			@Param("type") int type);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY
			+ " WHERE police_no = :policeno", nativeQuery = true)
	void deleteByPoliceNo(@Param("policeno") String policeno);

}
