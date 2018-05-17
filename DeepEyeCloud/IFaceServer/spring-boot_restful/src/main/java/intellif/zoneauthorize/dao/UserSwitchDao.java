/**
 *
 */
package intellif.zoneauthorize.dao;


import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.UserSwitch;

/**
 * The Interface AlarmInfoDao.
 *
 * @author pengqirong
 */
//@Transactional
public interface UserSwitchDao extends CrudRepository<UserSwitch, Long> {

	UserSwitch findOneByUserId(long userId);
}
