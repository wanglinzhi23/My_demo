package intellif.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import intellif.dto.BlackFaceResultDto;
import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceStatisticDto;
import intellif.dto.QueryFaceDto;
import intellif.dto.XinghuoQuery;
import intellif.fk.dto.FindFkPlaceFaceDto;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;

public interface FaceServiceItf {

	/**
	 * 获取指定id的人脸
	 * @param id
	 * @return
	 */
	FaceInfo findOne(long id);

	/**
	 * 创建或更新对象（根据id判断是否为新对象）
	 * @param face
	 * @return
	 */
	FaceInfo save(FaceInfo face);

	/**
	 * 批量更新对象
	 * @param faceList
	 * @return
	 */
	Integer update(List<FaceInfo> faceList);

	/**
	 * 统计
	 * @return
	 */
	Long count();
	
	/**
	 * 分页获取指定摄像头抓拍人脸
	 * @param sourceId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<FaceInfo> findBySourceId(long sourceId, int page, int pageSize);

	/**
	 * 分页获取指定摄像头抓拍人脸
	 * @param sourceId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<FaceInfo> findBySourceId(long sourceId, String startTime, String endTime, int page, int pageSize);

	/**
	 * 获取指定摄像头抓拍人脸数量
	 * @param sourceId
	 * @return
	 */
	Long countBySourceId(long sourceId, String startTime, String endTime);

	/**
	 * 分页获取指定摄像头抓拍人脸
	 * @param sourceIds
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<FaceInfo> findBySourceIds(Long[] sourceIds, String startTime, String endTime, int page, int pageSize);

	/**
	 * 根据图片id 获取图片对应的人脸
	 * @param sourceId
	 * @return
	 */
	List<FaceInfo> findByFromImageId(long sourceId);

	/**
	 * 根据开始时间与结束时间 获取未建立索引的人脸
	 * @param indexTime
	 * @param nextTime
	 * @return
	 */
	List<FaceInfo> findUnIndexed(String indexTime, String nextTime);

	/**
	 * 根据开始时间 获取索引结束时间
	 * @param indexTime
	 * @return
	 * @throws ParseException 
	 */
	Date indexNextTime(String indexTime) throws ParseException;

	/**
	 * 根据截止时间 分页获取最近抓拍人脸
	 * @param page
	 * @param pageSize
	 * @param dateStr
	 * @return
	 */
	List<FaceInfo> findLast(int page, int pageSize, String dateStr);

	/**
	 * 根据截止时间 分页获取指定区域最近出现人脸
	 * @param stationId
	 * @param page
	 * @param pageSize
	 * @param time
	 * @return
	 */
	List<FaceInfo> findPersonInzonesByStationId(long stationId, int page, int pageSize, String time);

	/**
	 * 根据查询条件分页获取人脸
	 * @param faceQueryDto
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	List<FaceInfo> findByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception;

	/**
	 * 根绝查询条件分页获取人脸的原图
	 * @param faceQueryDto
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	List<ImageInfo> findImageByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception;

	/**
	 * 统计当天采集人脸数
	 * @return
	 */
	Long countToday();

	/**
	 * 统计最近一分钟采集人脸数
	 * @return
	 */
	Long countMinute();
	
	/**
	 * 统计前一天各摄像头采集人脸数
	 * @return
	 */
	List<Object[]> statisticYesterdayByCamera();

	
	/**
	 * 统计某天各摄像头采集高低质量人脸数
	 * @return
	 */
	List<Object[]> statisticQualityYesterdayByCamera(Date date);
	
	/**
	 * 统计指定区域
	 * @param id
	 * @return
	 */
	Long statisticByArea(long id);
	
	/**
	 * 统计指定行政区域
	 * @param id
	 * @return
	 */
	Long statisticByDistict(long id);

	/**
	 * 统计最近七天每天采集人脸数
	 * @return
	 */
	List<FaceStatisticDto> statisticByDay();

	/**
	 * 根据Sequence开始与结束标号更新Indexed为1
	 * @param code
	 * @param start
	 * @param end
	 * @return
	 */
	boolean updateIndexedBySequence(long code, long start, long end);

	/**
	 * 根据开始时间与结束时间更新Indexed为1
	 * @param code
	 * @param indextTime
	 * @param nextTime
	 * @return
	 */
	boolean updateIndexedByTime(long code, String indextTime, String nextTime);

	List<FaceInfo> findByXinghuoCombinedParams(XinghuoQuery faceQueryDto, int page, int pageSize) throws Exception;

    List<BlackFaceResultDto> parseBlackFaceResultList(int page, int pagesize, List<FaceResultByCameraDto> rsList);
    
    List<FaceInfo> findFkByCombinedParams(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception; //运运的场所接口

     /**
      * 超级管理员删除图片，v1.2.8功能
      * @param faceId
      */
    void deleteFaceById(long faceId) throws Exception;

	
	 /**
     * 统计指定摄像头列表采集总数
     * @param cameraIdList
     * @return
     */
    public Long statisticByCameraIds(List<Long> cameraIdList);
    
        /**
     * 根据查询条件分页获取指定摄像头下人脸  -- 反恐场所抓拍查询
     * @param findFkPersonDto
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    List<FaceInfo> findByFkPlace(FindFkPlaceFaceDto findFkPlaceFaceDto, int page, int pageSize) throws Exception;

}