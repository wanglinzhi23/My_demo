/**
 * 
 */
package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intellif.consts.GlobalConsts;
import intellif.database.entity.BlackBank;
import intellif.database.entity.ImageInfo;

/**
 * The Interface ImageInfoDao.
 *
 * @author yangboz
 */
public interface ImageInfoDao extends CrudRepository<ImageInfo, Long> {
	
}
