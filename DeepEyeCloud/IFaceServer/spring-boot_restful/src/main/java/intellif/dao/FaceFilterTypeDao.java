/**
 *
 */
package intellif.dao;


import intellif.database.entity.AlarmInfo;
import intellif.database.entity.FaceFilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Zheng Xiaodong
 */
public interface FaceFilterTypeDao extends JpaRepository<FaceFilterType, Long> {
}
