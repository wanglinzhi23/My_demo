/**
 *
 */
package intellif.dao;

import intellif.database.entity.VideoInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import intellif.database.entity.CameraInfo;

/**
 * The Interface VideoInfoDao.
 *
 * @author yangboz
 */
//@Transactional
public interface VideoInfoDao extends CrudRepository<CameraInfo, Long> {

}
