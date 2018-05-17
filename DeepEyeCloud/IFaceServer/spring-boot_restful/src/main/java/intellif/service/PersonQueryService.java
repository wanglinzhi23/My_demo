package intellif.service;


import intellif.database.entity.CameraInfo;
import intellif.dto.PersonStatisticCount;

import java.util.Date;
import java.util.List;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: PersonQueryService.java
 * @Package intellif.service
 * @Description 人流查询
 * @date 2018 05-03 16:42.
 */
public interface PersonQueryService {
	/**
	 * 今日实时人流统计
	 * @param type
	 * @return
	 */
	List<PersonStatisticCount> getTodayPersonCount(int type,List<CameraInfo>areaId);

	/**
	 * 历史实时人流统计
	 * @param timeType 10:日, 20:月,30:年
	 * @param personType 10:总榜,20:性别榜,30:年龄榜
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	List<PersonStatisticCount> getHistoryPersonCount(int timeType, int personType, Date startTime, Date endTime,List<CameraInfo>areaId);
}
