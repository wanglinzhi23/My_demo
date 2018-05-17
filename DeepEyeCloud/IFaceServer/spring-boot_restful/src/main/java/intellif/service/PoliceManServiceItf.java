package intellif.service;

import intellif.dto.PoliceManDto;
import intellif.dto.ShowAlarmInfoDto;

import java.math.BigInteger;
import java.util.List;

public interface PoliceManServiceItf {

	public List<ShowAlarmInfoDto> findByUserId(String id, int pagesize);

	public BigInteger findCountByUserId(String id);

	//public List<PoliceManDto> findPoliceMan();
	
	public List<PoliceManDto> findPoliceMan(PoliceManDto policeManDto,int page,int pageSize);

}
