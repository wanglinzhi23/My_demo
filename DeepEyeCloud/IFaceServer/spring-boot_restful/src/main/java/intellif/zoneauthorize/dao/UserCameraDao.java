/**
 *
 */
package intellif.zoneauthorize.dao;

import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.UserCamera;

/**
 * The Interface AlarmInfoDao.
 *
 * @author pengqirong
 */
//@Transactional
public interface UserCameraDao extends CrudRepository<UserCamera, Long> {
}
