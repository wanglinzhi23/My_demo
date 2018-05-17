/**
 * 
 */
package intellif.dao;

import java.util.List;

import intellif.database.entity.IFaceConfig;

import org.springframework.data.repository.CrudRepository;

/**
 * The Interface PersonDetailDao.
 *
 * @author yangboz
 */
public interface IFaceConfigDao extends CrudRepository<IFaceConfig, Long> {
	
	public List<IFaceConfig> findByConKey(String key);

}
