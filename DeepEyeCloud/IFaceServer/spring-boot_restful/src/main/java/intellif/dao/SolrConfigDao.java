package intellif.dao;

import intellif.database.entity.SolrConfigInfo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SolrConfigDao extends CrudRepository<SolrConfigInfo, Long> {

	List<SolrConfigInfo> findBySourceId(long source_id);

	// List<SolrConfigInfo> findByIdNotNullOrderByIdDesc();
	//
	// List<SolrConfigInfo> findByIp(String ip);
	//
	// List<SolrConfigInfo> findByIpAndPort(String ip, int port);

}
