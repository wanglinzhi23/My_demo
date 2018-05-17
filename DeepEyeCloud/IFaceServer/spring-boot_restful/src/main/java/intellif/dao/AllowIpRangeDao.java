/**
 *
 */
package intellif.dao;


import intellif.consts.GlobalConsts;
import intellif.database.entity.AllowIpRange;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface AllowIpRangeDao extends CrudRepository<AllowIpRange, Long> {
	
	    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALLOW_IPS + " WHERE inet_aton(:checkIp) >= start_ip_number and inet_aton(:checkIp)  <= end_ip_number", nativeQuery = true)
        List<AllowIpRange> findMatchRanges(@Param("checkIp") String checkIp);

 
	    @Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALLOW_IPS + " WHERE inet_aton(:startIp) = start_ip_number and inet_aton(:endIp)  = end_ip_number", nativeQuery = true)
	    List<AllowIpRange> ipexists(@Param("startIp") String startIp,@Param("endIp") String endIp); 
	    
	   /* @Query(value = "REPLACE INTO  " + GlobalConsts.T_NAME_ALLOW_IPS + " (`id`, `start_ip`, `start_ip_number`, `end_ip`, `end_ip_number`, `ip_rang_name`, `user`) VALUES ( null , :startip ,  inet_aton(:startip), :endip ,  inet_aton(:startip), null, null)", nativeQuery = true)
	    List<AllowIpRange> save(@Param("startip") String startip,@Param("endip") String endip);
	    
	    @Query(value = "UPDATE  " + GlobalConsts.T_NAME_ALLOW_IPS + " set id= :id ,start_ip = :startip  ,start_ip_number= inet_aton(:startip),end_ip=:endip ,end_ip_number= inet_aton(:endip) where id=:id ", nativeQuery = true)
	    List<AllowIpRange>  update(@Param("startip") String startip,@Param("endip") String endip,@Param("id") long id);*/
	 
	    
}
