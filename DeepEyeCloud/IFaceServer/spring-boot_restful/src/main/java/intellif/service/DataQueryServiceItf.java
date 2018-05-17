package intellif.service;

import java.util.List;
import java.util.Map;

import intellif.dto.CameraGeometryInfoDto;
import intellif.dto.CameraStatisticInfoDto;
import intellif.dto.AreaStatisticCameraDataDto;
import intellif.dto.StoredCrimeDataDto;
import intellif.database.entity.AreaCameraStatistic;
import intellif.database.entity.FaceStatisticCount;
import intellif.database.entity.StatisticDataQuery;
import intellif.database.entity.StoredCrimeQuery;

/**
 * 
 * @author yktang, by V1.1.2
 *
 */

public interface DataQueryServiceItf {
	/**
	 * 按照时间和摄像头查询数据统计，返回每个摄像头的按时间分布的统计结果
	 * @param queryobject
	 * @return
	 */
	public Map<Long, List<FaceStatisticCount>> statisticByCameraByTimeslot(StatisticDataQuery queryobject);
	/**
	 * 按摄像头查询的数据统计
	 * @param queryobject
	 * @return
	 */
	public Map<Long, AreaStatisticCameraDataDto> statisticStationCamera(StatisticDataQuery queryobject,List<AreaCameraStatistic> sawresult);
	/**
	 * 按摄像头统计的报警数据统计
	 * @param queryobject
	 * @return
	 */
	public Map<Long, AreaStatisticCameraDataDto> statisticStationCameraAlarm(StatisticDataQuery queryobject);
	/**
	 * 入库数据查询
	 * @param queryobject
	 * @return
	 */
	public List<StoredCrimeDataDto> storedCrimeData(StoredCrimeQuery queryobject);
	/**
	 * 摄像头统计查询
	 * @return
	 */
	public List<CameraStatisticInfoDto> statisticCameraInfo();
	/**
	 * 摄像头地理位置查询
	 * @return
	 */
	public List<CameraGeometryInfoDto> cameraGeometryInfo();
	
	/**
	 * 
	 * faceCountByStationIdNPeriod:大运页面 统计接口
	 * 
	 * @param id
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public Long faceCountByStationIdNPeriod(long id, String starttime, String endtime);
}
