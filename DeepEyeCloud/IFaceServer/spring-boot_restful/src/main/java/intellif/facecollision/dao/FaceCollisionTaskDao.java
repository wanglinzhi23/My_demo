package intellif.facecollision.dao;

import intellif.consts.GlobalConsts;
import intellif.facecollision.vo.FaceCollisionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Zheng Xiaodong
 */
public interface FaceCollisionTaskDao extends JpaRepository<FaceCollisionTask, Long> {

    @Query(value="select count(1) from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK
            + " where user_id = :userId and deleted = false ", nativeQuery = true)
    Long countByUserId(@Param("userId") Long userId);

    @Query(value="select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK
            + " where user_id = :userId and deleted = false order by id desc limit :offset, :pageSize", nativeQuery = true)
    List<FaceCollisionTask> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);
}
