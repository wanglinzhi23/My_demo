package intellif.service;

import java.util.List;
import java.util.Map;

import intellif.dto.AlarmQueryDto;
import intellif.database.entity.CountInfo;
import intellif.database.entity.EventInfo;
import intellif.database.entity.OtherInfo;
import intellif.database.entity.StatisticDataQuery;

public interface CrimeAlarmServiceItf {

	public List<EventInfo> findEventsByPersonId(AlarmQueryDto alarmQueryDto);

	public List<OtherInfo> findAlarmPerson(AlarmQueryDto alarmQueryDto);

	public Map<Long, List<CountInfo>> findAlarmCount(StatisticDataQuery requestBody);
	
	public List<OtherInfo> findAlarmPersonForOffline(AlarmQueryDto alarmQueryDto);

	public int countAlarmPersonForOffline(AlarmQueryDto alarmQueryDto);
}
