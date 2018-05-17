package intellif.share.service;


import intellif.dto.AlarmQueryDto;
import intellif.dto.QueryFaceDto;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;

import java.util.List;

public interface SharedFaceServiceItf {
	/**
	 * 根据查询条件分页获取人脸
	 * @param faceQueryDto
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
    List<FaceInfo> findByCombinedParams(QueryFaceDto faceQueryDto, long districtId,
                                        int page, int pageSize) throws Exception;

	List<FaceInfo> findByMultipleCameras(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception;

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
	 * 获取指定id的人脸
	 * @param id
	 * @return
	 */
	FaceInfo findOne(long id);
	/**
	 * 分页获取指定摄像头抓拍人脸
	 * @param sourceId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<FaceInfo> findBySourceId(long sourceId, int page, int pageSize);
	
	/**
    * 根绝查询条件分页获取人脸的原图(大运专用)
    * @param faceQueryDto
    * @param page
    * @param pageSize
    * @return
    * @throws Exception
    */
	public List<FaceInfo> findByMultipleCamerasForDayun(QueryFaceDto faceQueryDto, int page, int pageSize) throws Exception;


}
