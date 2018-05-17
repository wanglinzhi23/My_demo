/**
 *
 */
package intellif.zoneauthorize.dao;


import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.UserArea;

/**
 * The Interface AlarmInfoDao.
 *
 * @author pengqirong
 */
//@Transactional
public interface UserAreaDao extends CrudRepository<UserArea, Long> {
}
