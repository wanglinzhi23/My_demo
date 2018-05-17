package intellif.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TException;

import intellif.database.entity.PersonDetail;
import intellif.dto.BankDisplayInfo;
import intellif.dto.MonitorAreaInfo;
import intellif.dto.PersonFullDto;
import intellif.dto.PersonQueryDto;
import intellif.utils.PageDto;
import intellif.database.entity.AlarmPersonDetail;
import intellif.database.entity.BlackDetailRealName;

/**
 * The Interface PersonDetailServiceItf.
 */
public interface PersonDetailServiceItf<T> extends CommonServiceItf<T>{

	public List<PersonDetail> query(PersonQueryDto personQueryDto, int page, int pageSize);

	public void refreshPerson() throws TException;
	
	public boolean refreshPersonStatus(PersonDetail pd) throws TException;

	public PageDto<PersonFullDto> findByBankId(BankDisplayInfo bdi);
	
	public List<BlackDetailRealName> findBlackDetailByBankId(long id);
	
	public List<BlackDetailRealName> findAllGreaterId(long bankid, long blackid);
	
	public List<BlackDetailRealName> findAllLessId(long bankid, long blackid);
	
	public List<AlarmPersonDetail> findAlarmPersonDetail(String[] personids);
	
	public Map<Long, List<String>> findPersonArea(String[] personids);

	public void refreshPersonOfUpdate(long personId);
	
	public Map<String, List<Long>> processParamDataToMap(List<MonitorAreaInfo> areaList);
	
	public void processUserAreaDataToMap(Long userId);
	
    public void noticeEngineUpdateBlackDatas(Long userId);
	
	public PageDto<PersonFullDto> findByQueryParams(PersonQueryDto pqd);
}
