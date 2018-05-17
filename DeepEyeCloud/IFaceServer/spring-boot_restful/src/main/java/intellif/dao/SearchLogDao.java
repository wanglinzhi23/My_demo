package intellif.dao;

import intellif.database.entity.SearchLogInfo;
import org.springframework.data.repository.CrudRepository;

public interface SearchLogDao extends
		CrudRepository<SearchLogInfo, Long> {

}
