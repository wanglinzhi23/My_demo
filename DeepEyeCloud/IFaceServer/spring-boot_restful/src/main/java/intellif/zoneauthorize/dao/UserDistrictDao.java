/**
 *
 */
package intellif.zoneauthorize.dao;


import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.UserDistrict;

/**
 * The Interface AlarmInfoDao.
 *
 * @author pengqirong
 */
//@Transactional
public interface UserDistrictDao extends CrudRepository<UserDistrict, Long> {
}
