package intellif.service;

import java.util.List;

import intellif.database.entity.ResidentInfo;
import intellif.database.entity.ResidentPerson;

public interface ResidentServiceItf  {
	/**
	 * 获取常住人口信息
	 * 
	 * @return
	 */
	public ResidentInfo getResidentInfo(long areaId, String startTime,
			String endTime);

	/**
	 * 根据areaId获取该区域常住人口列表信息
	 * 
	 * @param areaId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<ResidentPerson> getResidentPersonByAreaId(long areaId,
			String startTime, String endTime, int start, int pageSize);

	/**
	 * 查询某区域常住人口特征汇总（男女，年龄段）
	 * @param areaId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<ResidentPerson> getResidentIndexsByAreaId(long areaId,
			String startTime, String endTime);
}
