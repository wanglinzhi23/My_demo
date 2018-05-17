package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.UserApiLimitInfo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserApiLimitDao extends CrudRepository<UserApiLimitInfo, Long> {
	
	@Query(value = "select * from "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_USER_API_LIMIT + " where user_id = :userId and api_id = :apiId", nativeQuery = true)
	public UserApiLimitInfo findApiLimitation(@Param("userId") Long userId, @Param("apiId")Long apiId);
	
	
}
