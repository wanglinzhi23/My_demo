package intellif.dao;

import intellif.database.entity.CrimeAlarmInfo;

import org.springframework.data.repository.CrudRepository;

//@Repository
public interface CrimeAlarmDao extends CrudRepository<CrimeAlarmInfo, Long> {

}
