package intellif.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import intellif.database.entity.IndexFaceRecord;

//@Repository
public interface IndexFaceRecordDao extends CrudRepository<IndexFaceRecord, Long> {

	@Query(value = "select max(index_face_id) from face_index_records", nativeQuery = true)
	public Long getLastFaceId();

	@Query(value = "select max(id) from t_face_info_c", nativeQuery = true)
	public Long getMaxFaceId();

	@Transactional
	@Modifying
	@Query(value = "update t_face_info_c set indexed = 1 where id between :startId and :endId", nativeQuery = true)
	public int updateFaceIndexed(@Param("startId") long startId, @Param("endId") long endId);
	
	@Query(value = "select distinct server_url from t_solr_config_info", nativeQuery = true)
	public List<Object> getSolrServerWithCameras();
	
	@Query(value = "select count(1)  from t_solr_config_info", nativeQuery = true)
	public Long totalSolrConfigCount();
}
