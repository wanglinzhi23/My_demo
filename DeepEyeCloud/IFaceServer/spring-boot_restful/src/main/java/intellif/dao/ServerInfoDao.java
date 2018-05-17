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
import intellif.database.entity.ServerInfo;

/**
 * The Interface ServerInfoDao.
 *
 * @author yangboz
 */
//@Transactional
public interface ServerInfoDao extends CrudRepository<ServerInfo, Long> {
	//
	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_SERVER_INFO+" t WHERE t.server_name = :value", nativeQuery = true)
	List<ServerInfo> findByServerName(@Param("value") String value);
	
	
	List<ServerInfo> findById(long server_id);

    List<ServerInfo> findByIdNotNullOrderByIdDesc();

    List<ServerInfo> findByIp(String ip);

    List<ServerInfo> findByIpAndPort(String ip, int port);

	@Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_SERVER_INFO+" t WHERE t.type = :type", nativeQuery = true)
	List<ServerInfo> findByType(@Param("type") int type);
}
