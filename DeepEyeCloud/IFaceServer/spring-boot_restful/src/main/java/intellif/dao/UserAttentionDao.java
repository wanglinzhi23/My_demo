package intellif.dao;

import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.database.entity.UserAttention;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserAttentionDao extends CrudRepository<UserAttention, Long> {

	List<UserAttention> findByUserId(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_USER_ATTENTION+" WHERE person_id = :personId and user_id=:userId", nativeQuery = true)
	void delete(@Param("personId") long personId, @Param("userId") long userId);

    @Query(value = "SELECT * FROM "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_USER_ATTENTION+" WHERE person_id = :personId and user_id=:userId", nativeQuery = true)
	List<UserAttention> findByUserIdAndPersonId(@Param("userId") long userId, @Param("personId")long personId);
}
