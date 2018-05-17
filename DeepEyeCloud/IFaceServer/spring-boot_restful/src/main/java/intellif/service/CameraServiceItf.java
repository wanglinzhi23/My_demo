package intellif.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import intellif.database.entity.CameraInfo;
import intellif.dto.CameraDto;
import intellif.dto.CameraGeometryInfoDto;
import intellif.dto.CameraQueryDto;
import intellif.utils.PageDto;
import intellif.database.entity.AreaCameraStatistic;

public interface CameraServiceItf<T> extends CommonServiceItf<T>{


    public List<CameraDto> findAllCameraDto(Long userId);
    
    public List<CameraInfo> findOneCameraDto(long cameraId);

    public Iterable<CameraInfo> getCameraByPersonId(long id);

	public void addPersonToCamera(long id, List<CameraInfo> cameraList);

	public void delPersonFromCamera(long id, List<CameraInfo> cameraList);
	

	@SuppressWarnings("rawtypes")
    public void addPersonToCameraAreaIds(long id, List<Long> idsList,Class clazz);

	@SuppressWarnings("rawtypes")
    public void delPersonFromCameraAreaIds(long id, List<Long> idsList,Class clazz);


	public void addBlackToCamera(long id, Iterable<CameraInfo> cameraList);

	public void delBlackFromCamera(long id, Iterable<CameraInfo> cameraList);
	
	public List<CameraGeometryInfoDto> findCameraGeometry();
	
	public List<AreaCameraStatistic> findCameraStatistic(Long[] cameraids);

/*	public Page<CameraDto> findAll(CameraDto cameraDto, Pageable pageable);*/
	
	public List<CameraDto> findByIds(List<Long> idList);

	List<CameraInfo> findByName(String name);

	List<CameraInfo> findByStationId(long areaId);

	List<CameraInfo> findInStation();

	List<CameraInfo> findAll(Iterable<Long> cameraIdList);
	

/*	public List<CameraInfo> authorizeQuery(CameraQueryDto cameraQuery);*/

	List<CameraInfo> findAllWithoutAuthorize();
	
	List<CameraInfo> findOtherCameraAll();

	/**
     * 查询用户授权摄像头列表
     * @param cameraQuery
     * @return
     */
    Page<CameraDto> findAll(CameraQueryDto cameraQuery, Pageable pageable);
    
    public  List<Long> getCameraIdsByPersonId(long id);

    public  List<Long> getAreaIdsByPersonId(long id);
    public List<CameraInfo> queryALLCameraInfoByConditions(List<String> filterList);
    public List<CameraInfo> findByTaskId(long taskId);
    public PageDto<CameraInfo> queryUserCamerasByParams(CameraQueryDto cqd);

}
