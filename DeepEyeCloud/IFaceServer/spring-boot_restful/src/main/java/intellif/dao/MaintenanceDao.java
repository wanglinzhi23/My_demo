package intellif.dao;

import java.util.List;

import intellif.database.entity.LossPrePerson;
import intellif.database.entity.MaintenancePerson;

import org.springframework.data.repository.CrudRepository;

public interface MaintenanceDao extends CrudRepository<MaintenancePerson, Long> {
	 List<MaintenancePerson> findByWeixinId(String weixinId);
}
