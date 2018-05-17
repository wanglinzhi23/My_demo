/**
 * 
 */
package intellif.dao;

import org.springframework.data.repository.CrudRepository;

import intellif.database.entity.PersonInfo;

/**
 * The Interface PersonInfoDao.
 *
 * @author yangboz
 */
public interface PersonInfoDao extends CrudRepository<PersonInfo, Long> {

}
