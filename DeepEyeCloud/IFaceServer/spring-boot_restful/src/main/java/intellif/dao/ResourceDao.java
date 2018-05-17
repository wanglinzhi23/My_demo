package intellif.dao;

import intellif.database.entity.ApiResourceInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceDao extends CrudRepository<ApiResourceInfo, Long> {

    @Query(value = "select a.* from `t_role` r, t_business_api ba, t_api_resource a "
            + "where r.id = :roleId and  find_in_set(ba.resource_id,r.`res_ids`) "
            + "and ba.api_resource_id = a.id and a.uri = :uri and a.http_method = :httpMethod LIMIT 1", nativeQuery = true)
    public ApiResourceInfo findResources(@Param("roleId") long roleId, @Param("uri") String uri, @Param("httpMethod") String httpMethod);
    
    
	@Query(value = "select DISTINCT a.* from `t_role` r, t_business_api ba, t_api_resource a "
	        + "where r.id = :roleId  and  find_in_set(ba.resource_id,r.`res_ids`) and ba.api_resource_id = a.id", nativeQuery = true)
	public List<ApiResourceInfo> findResources(@Param("roleId") long roleId);

}
