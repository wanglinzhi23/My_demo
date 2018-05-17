package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PoliceManAuthority;
import intellif.database.entity.PoliceManAuthorityType;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PoliceManAuthorityTypeDao extends
		CrudRepository<PoliceManAuthorityType, Long> {


	@Query(value = "SELECT distinct(type_id) FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY_TYPE , nativeQuery = true)
	List<Integer> findAllType();
	
	@Query(value = "SELECT * FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY_TYPE , nativeQuery = true)
	List<PoliceManAuthorityType> findTypeName();
	
	@Query(value = "SELECT count(1) FROM " + GlobalConsts.INTELLIF_BASE + "."
			+ GlobalConsts.T_NAME_POLICEMAN_INFO_AUTHORITY_TYPE , nativeQuery = true)
	BigInteger findTypeCounts();
	

}
