package intellif.dao;

import intellif.database.entity.SearchErrCode;
import intellif.database.entity.SearchLogInfo;

import org.springframework.data.repository.CrudRepository;

public interface SearchErrCodeDao extends
		CrudRepository<SearchErrCode, Long> {

}
