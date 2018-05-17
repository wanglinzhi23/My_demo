package intellif.dao;

import java.util.List;

import intellif.database.entity.PersonDetail;
import intellif.database.entity.SearchReason;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by shixiaohua on 17/01/09.
 */
public interface SearchReasonDao extends JpaRepository<SearchReason, Long> {
	List<SearchReason> findByRName(String name);
}
