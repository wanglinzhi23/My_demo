/**
 * 
 */
package intellif.dao;


import intellif.database.entity.OtherCameraInfo;

import org.springframework.data.repository.CrudRepository;

/**
 * The Interface CameraInfoDao.
 *
 * @author yangboz
 */
//@Transactional
public interface OtherCameraDao extends CrudRepository<OtherCameraInfo,Long> {

}
