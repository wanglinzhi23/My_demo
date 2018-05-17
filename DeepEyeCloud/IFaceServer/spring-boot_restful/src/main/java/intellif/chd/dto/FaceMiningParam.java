package intellif.chd.dto;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;
import intellif.database.entity.FaceInfo;

public class FaceMiningParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 开始时间
	 */
	public static final String START_TIME = "startTime";

	/**
	 * 结束时间
	 */
	public static final String END_TIME = "endTime";

	/**
	 * 摄像头id列表
	 */
	public static final String CAMERA_ID_LIST = "cameraIdList";

	/**
	 * 最少出现次数
	 */
	public static final String MIN_TIMES = "minTimes";

	/**
	 * 两次出现最小时间间隔（单位为ms）
	 */
	public static final String MIN_TIME_INTERVAL = "minTimeInterval";

	/**
	 * 犯罪嫌疑人ID
	 */
	public static final String BLACK_ID = "blackId";

	/**
	 * 相似度
	 */
	public static final String THRESHOLD = "threshold";

	/**
	 * 人脸ID
	 */
	public static final String FACE_ID = "faceId";

	/**
	 * 同行分析中嫌疑人出现前多少时间
	 */
	public static final String FRONT_TIME = "frontTime";

	/**
	 * 同行分析中嫌疑人出现后多少时间
	 */
	public static final String BACK_TIME = "backTime";

	/**
	 * 碰撞区域名
	 */
	public static final String CUSTOM_AREA_NAME = "customAreaName";

	/**
	 * 人脸碰撞条件列表
	 */
	public static final String IMPACT_LIST = "impactList";

	/**
	 * 场所id列表
	 */
	public static final String PLACE_ID_LIST = "placeIdList";

	/**
	 * 数据类型
	 */
	public static final String DATA_TYPE = "dataType";

	/**
	 * 开始时间和结束时间
	 */
	public static final String START_TIME_AND_END_TIME = "startTimeAndEndTime";

	/**
	 * python聚类方式
	 */
	public static final String CLUSTER_TYPE_PYTHON = "python";

	/**
	 * search聚类方式
	 */
	public static final String CLUSTER_TYPE_SEARCH = "search";

	// 开始时间
	@JsonSerialize(using = ToStringSerializer.class)
	private Long startTime;

	// 结束时间
	@JsonSerialize(using = ToStringSerializer.class)
	private Long endTime = System.currentTimeMillis();

	// 摄像头id列表
	private List<Long> cameraIdList = null;

	// 最少出现次数
	private int minTimes = 1;

	// 两次出现最小时间间隔（单位为ms）
	private long minTimeInterval = 0;

	/*
	 * // 犯罪嫌疑人ID private Long blackId;
	 */
	// 相似度
	@JsonSerialize(using = ToStringSerializer.class)
	private float threshold = GlobalConsts.DEFAULT_SCORE_THRESHOLD;

	// 人脸ID
	@JsonSerialize(using = ToStringSerializer.class)
	private Long faceId;

	// 同行分析中嫌疑人出现前多少时间
	private long frontTime = 0;

	// 同行分析中嫌疑人出现后多少时间
	private long backTime = 0;

	// 碰撞区域名
	private String customAreaName;

	// 人脸碰撞条件列表
	private List<FaceMiningParam> impactList;

	// 场所id列表
	private List<Long> placeIdList;

	// 数据类型
	private int dataType = GlobalConsts.FACE_INFO_TYPE;

	// 聚类方式
	private String clusterType = CLUSTER_TYPE_SEARCH;

	// 人脸ID列表
	private List<Long> faceIdList = null;

	// 碰撞区域类型
	private String customAreaTyped;
	
	// 人脸信息
	private FaceInfo faceInfo;

	/**
	 * 校验时间
	 */
	public void validStartTimeAndEndTime() {
		long now = System.currentTimeMillis();
		if (null == startTime) {
			throw new IllegalArgumentException("开始时间不能为空！");
		}
		if (null == endTime || endTime < startTime) {
			throw new IllegalArgumentException("结束时间不能为空，不得小于开始时间！");
		}
	}

	/**
	 * 校验摄像头ID列表
	 */
	public void validCameraIdList() {
		if (CollectionUtils.isEmpty(cameraIdList)) {
			throw new IllegalArgumentException("请选择摄像头！");
		}
		for (Long cameraId : cameraIdList) {
			if (null == cameraId) {
				throw new IllegalArgumentException("摄像头信息不得为空！");
			}
		}
	}

	/**
	 * 校验最少出现次数
	 */
	public void validMinTimes() {
		if (minTimes < 1) {
			throw new IllegalArgumentException("最小出现次数不得小于1！");
		}
	}

	/**
	 * 校验两次出现最小时间间隔（单位为ms）
	 */
	public void validMinTimeInterval() {
		if (minTimeInterval < 0) {
			throw new IllegalArgumentException("两次出现最小时间间隔不得小于0！");
		}
	}

	/**
	 * 校验相似度
	 */
	public void validThreshold() {
		if (threshold < 0 && threshold > 1) {
			throw new IllegalArgumentException("相似度不得小于0%，不得大于100%！");
		}
	}

	/**
	 * 校验人脸ID
	 */

	public void validFaceId() {
		if (null == faceId) {
			throw new IllegalArgumentException("请选择人脸！");
		}
	}

	/**
	 * 校验同行分析中嫌疑人出现前多少时间
	 */
	public void validFrontTime() {
		if (frontTime < 0) {
			throw new IllegalArgumentException("嫌疑人出现之前的时间不得小于0！");
		}
	}

	/**
	 * 校验同行分析中嫌疑人出现后多少时间
	 */
	public void validBackTime() {
		if (backTime < 0) {
			throw new IllegalArgumentException("嫌疑人出现之后的时间不得小于0！");
		}
	}

	/**
	 * 校验碰撞区域名
	 */
/*	public void validCustomAreaName() {
		if (StringUtils.isEmpty(customAreaName)) {
			throw new IllegalArgumentException("碰撞区域名不能为空！");
		}
	}*/

	/**
	 * 校验碰撞区域名
	 */
	public void validCustomAreaTyped() {
		if (StringUtils.isEmpty(customAreaTyped)) {
			throw new IllegalArgumentException("碰撞区域类型不能为空！");
		}
	}

	/**
	 * 校验人脸碰撞条件列表
	 */
	public void validImpactList() {
		if (CollectionUtils.isEmpty(impactList) || impactList.size() < 2) {
			throw new IllegalArgumentException("请选择两个或两个以上碰撞区域！");
		}
		Set<String> customAreaTypedSet = new HashSet<>();
		for (FaceMiningParam faceMiningDto : impactList) {
			if (null == faceMiningDto) {
				throw new IllegalArgumentException("碰撞区域信息不得为空！");
			}
			faceMiningDto.validStartTimeAndEndTime();
			faceMiningDto.validCameraIdList();
			// faceMiningDto.validCustomAreaName();
			faceMiningDto.validCustomAreaTyped();
			if (customAreaTypedSet.contains(faceMiningDto.getCustomAreaTyped())) {
				throw new IllegalArgumentException("碰撞区域不得相同！");
			}
			customAreaTypedSet.add(faceMiningDto.getCustomAreaTyped());
		}
	}

	/**
	 * 校验场所id列表
	 */
	public void validPlaceIdList() {
		if (CollectionUtils.isEmpty(placeIdList) || null == placeIdList.get(0)) {
			throw new IllegalArgumentException("请选择场所！");
		}
		for (Long placeId : placeIdList) {
			if (null == placeId) {
				throw new IllegalArgumentException("场所不得为空！");
			}
		}
	}

	/**
	 * 校验数据类型
	 */
	public void validDataType() {
		if (dataType > GlobalConsts.POLICE_INFO_TYPE || dataType < GlobalConsts.BLACK_INFO_TYPE) {
			throw new IllegalArgumentException(
					"数据类型不得大于" + GlobalConsts.POLICE_INFO_TYPE + "，且不得小于" + GlobalConsts.BLACK_INFO_TYPE + "！");
		}
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public List<Long> getCameraIdList() {
		return cameraIdList;
	}

	public void setCameraIdList(List<Long> cameraIdList) {
		this.cameraIdList = cameraIdList;
	}

	public int getMinTimes() {
		return minTimes;
	}

	public void setMinTimes(int minTimes) {
		this.minTimes = minTimes;
	}

	public long getMinTimeInterval() {
		return minTimeInterval;
	}

	public void setMinTimeInterval(long minTimeInterval) {
		this.minTimeInterval = minTimeInterval;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public Long getFaceId() {
		return faceId;
	}

	public void setFaceId(Long faceId) {
		this.faceId = faceId;
	}

	public long getFrontTime() {
		return frontTime;
	}

	public void setFrontTime(long frontTime) {
		this.frontTime = frontTime;
	}

	public long getBackTime() {
		return backTime;
	}

	public void setBackTime(long backTime) {
		this.backTime = backTime;
	}

	public String getCustomAreaName() {
		return customAreaName;
	}

	public void setCustomAreaName(String customAreaName) {
		this.customAreaName = customAreaName;
	}

	public List<FaceMiningParam> getImpactList() {
		return impactList;
	}

	public void setImpactList(List<FaceMiningParam> impactList) {
		this.impactList = impactList;
	}

	public List<Long> getPlaceIdList() {
		return placeIdList;
	}

	public void setPlaceIdList(List<Long> placeIdList) {
		this.placeIdList = placeIdList;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getStartTimeString() {
		if (null == startTime) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(GlobalConsts.YMDHMS);
		return sdf.format(new Date(startTime));
	}

	public void setStartTimeString(String startTimeString) throws ParseException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(GlobalConsts.YMDHMS);
			startTime = sdf.parse(startTimeString).getTime();
		} catch (Throwable e) {
			startTime = null;
		}
	}

	public String getEndTimeString() {
		if (null == endTime) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(GlobalConsts.YMDHMS);
		return sdf.format(new Date(endTime));
	}

	public void setEndTimeString(String endTimeString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(GlobalConsts.YMDHMS);
			endTime = sdf.parse(endTimeString).getTime();
		} catch (Throwable e) {
			endTime = null;
		}
	}

	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		if (CLUSTER_TYPE_PYTHON.equals(clusterType) || CLUSTER_TYPE_SEARCH.equals(clusterType)) {
			this.clusterType = clusterType;
		}
	}

	@Override
	public String toString() {
		return "FaceMiningParam [startTime=" + startTime + ", endTime=" + endTime + ", cameraIdList=" + cameraIdList
				+ ", minTimes=" + minTimes + ", minTimeInterval=" + minTimeInterval + ", threshold=" + threshold
				+ ", faceId=" + faceId + ", frontTime=" + frontTime + ", backTime=" + backTime + ", customAreaName="
				+ customAreaName + ", impactList=" + impactList + ", placeIdList=" + placeIdList + ", dataType="
				+ dataType + ", clusterType=" + clusterType + "]";
		// + ", blackId=" + blackId
	}

	public List<Long> getFaceIdList() {
		return null == faceIdList ? new ArrayList<>() : faceIdList;
	}

	public void setFaceIdList(List<Long> faceIdList) {
		this.faceIdList = faceIdList;
	}

	public String getCustomAreaTyped() {
		return customAreaTyped;
	}

	public void setCustomAreaTyped(String customAreaTyped) {
		this.customAreaTyped = customAreaTyped;
	}

	public FaceInfo getFaceInfo() {
		return faceInfo;
	}

	public void setFaceInfo(FaceInfo faceInfo) {
		this.faceInfo = faceInfo;
	}
}
