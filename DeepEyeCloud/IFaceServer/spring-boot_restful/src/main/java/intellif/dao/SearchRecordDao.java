package intellif.dao;

import intellif.database.entity.SearchRecord;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SearchRecordDao extends CrudRepository<SearchRecord, Long> {

	@Query(value = "select * from t_search_record order by id limit 1", nativeQuery = true)
	public SearchRecord getLaskRecord();

}
