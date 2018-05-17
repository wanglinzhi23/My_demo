package intellif.dao;

import intellif.database.entity.AlarmInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yangboz on 15/9/18.
 */
public interface JpaAlarmDao extends JpaRepository<AlarmInfo, Long> {
    List<AlarmInfo> findById(long rule_id);
}

