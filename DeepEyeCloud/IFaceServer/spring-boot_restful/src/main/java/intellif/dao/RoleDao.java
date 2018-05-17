package intellif.dao;

import intellif.database.entity.RoleInfo;
import intellif.database.entity.ApiResourceInfo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yangboz on 11/16/15.
 */
public interface RoleDao extends CrudRepository<RoleInfo, Long> {
    @Query(value = "select r.* from t_role r, t_user u where u.id = :userId and find_in_set(r.id, u.role_ids)", nativeQuery = true)
    List<RoleInfo> queryRoleInfoByUserId(@Param("userId") long userId);
}
