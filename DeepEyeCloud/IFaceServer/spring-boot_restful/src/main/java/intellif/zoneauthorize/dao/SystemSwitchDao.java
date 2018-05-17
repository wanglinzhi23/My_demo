/**
 *
 */
package intellif.zoneauthorize.dao;


import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.SystemSwitch;

/**
 * The Interface AlarmInfoDao.
 *
 * @author pengqirong
 */
//@Transactional
public interface SystemSwitchDao extends CrudRepository<SystemSwitch, Long> {

	SystemSwitch findOneBySwitchType(String switchType);
}
