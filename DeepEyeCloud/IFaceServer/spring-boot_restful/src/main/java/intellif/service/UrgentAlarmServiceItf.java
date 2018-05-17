package intellif.service;

import intellif.database.entity.BlackBank;
import intellif.database.entity.PersonDetail;
import intellif.database.entity.EventInfo;

import java.util.List;

public interface UrgentAlarmServiceItf {
	public List<PersonDetail> findUrgentAlarmPersonByBankId(long id, int page, int defaultPageSize);
	public List<EventInfo> findEventsByPersonId(long id, float threshold, int page, int pageSize);
	public List<BlackBank> findBanksOfUrgentPersons(String ids);
}
