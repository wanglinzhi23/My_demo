package intellif.service;

import intellif.database.dao.AlarmInfoDao;
import intellif.database.entity.PersonDetail;
import intellif.dto.AlarmInfoDto;
import intellif.dto.AlarmQueryDto;
import intellif.dto.AlarmStatisticByStationDto;
import intellif.dto.AlarmStatisticDto;
import intellif.dto.EventsByStationIdKey;
import intellif.dto.QueryInfoDto;
import intellif.database.entity.AlarmImageInfo;
import intellif.database.entity.EventInfo;

import java.util.List;

public interface AlarmServiceItf<T> extends CommonServiceItf<T>{

    public List<AlarmInfoDto> findByCombinedConditions(AlarmInfoDto alarmInfoDto);

    public List<AlarmStatisticDto> findByCountAndBetweenWeek();

    public List<AlarmInfoDto> findByBlackDetailId(long id);


	public List<EventInfo> findEventsByPersonId(QueryInfoDto dto);	
	public List<EventInfo> findEventsByPersonIdCameraIds(long id, String cameraids, float threshold, int page, int pageSize);

	public List<EventInfo> findEventsByPersonIdList(long[] idlist, String cameraids, float threshold, int page, int pageSize);

	public List<EventInfo> findEventsByPersonIdAndCameras(AlarmQueryDto alarmQueryDto);

	public List<PersonDetail> findAlarmPersonByCameraId(String ids, int type, double threshold, int page, int pageSize);

	public List<AlarmStatisticByStationDto> statisticByPoliceStation();

	public List<PersonDetail> findAlarmPersonByAttention(long id, int page, int pageSize,int type);

	public List<PersonDetail> findAlarmPersonByBankId(long id, float threshold, int page, int defaultPageSize);
	

	public List<PersonDetail> findAlarmPersonByFkType(long id, String fkType, double threshold, int page, int pageSize);


	public List<PersonDetail> findAlarmPersonByBankIdAndCameras(AlarmQueryDto alarmQueryDto);
	
	public List<EventInfo> findAllPersonEvents(long stationId, int pageSize, String personIds, double threshold);


	public List<AlarmImageInfo> findImageByAlarmIds(List<Long> alarmIdList,Long userId);

	
	public List<PersonDetail> findAlarmPersonByCameraIdConfidence(String ids, EventsByStationIdKey key);

    public int countFindAlarmPersonForOffline(AlarmQueryDto alarmQueryDto);

    public List<PersonDetail> findAlarmPersonForOffline(AlarmQueryDto alarmQueryDto);
    
    public boolean updateStatusById(long id, int status);

	/**
	 * 确认报警的时候根据配置发送警信
	 * @param alarmId - 告警id
	 * @return
	 */
	public int sendJinxinOnConfirm(long alarmId);
}
