package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OauthAccessTokenInfo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;




public interface OauthAccessTokenDao extends CrudRepository<OauthAccessTokenInfo, Long> {
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_OAUTH_ACCESS_TOKEN+" WHERE user_name = :value", nativeQuery = true)
	void deleteByName(@Param("value") String value);
	

	
}
