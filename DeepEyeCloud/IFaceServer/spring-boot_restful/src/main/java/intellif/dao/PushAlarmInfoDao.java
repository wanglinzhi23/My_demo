package intellif.dao;

import intellif.consts.GlobalConsts;
import intellif.database.entity.PushAlarmInfo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface PushAlarmInfoDao extends CrudRepository<PushAlarmInfo, Long> {
	
	   
	    @Modifying
	    @Transactional
		@Query(value = "UPDATE "+ GlobalConsts.INTELLIF_BASE + "." +GlobalConsts.T_NAME_PUSH_ALARM_INFO+" SET checked = 1 where checked=0 and receiver_no= :receiver_no limit :pagesize ", nativeQuery = true)
		void updatechecked(@Param("receiver_no") String receiver_no,@Param("pagesize") int pagesize);
	
}
