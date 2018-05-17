package intellif.chd.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.chd.consts.GenerateId;

/**
 * 聚类分析出人的信息
 * 
 * @author mmm
 *
 */
public class TimesPerson implements Serializable, Result<TimesPerson, Times> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2139529465041254884L;
	/**
	 * ID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id = GenerateId.getId();

	/**
	 * 次数列表
	 */
	private List<Times> timesList = new ArrayList<Times>();

	/**
	 * 总次数
	 */
	private Integer timesSize = null;

	/**
	 * 总人脸数
	 */
	private Integer faceSize = null;

	/**
	 * 人脸URL
	 */
	private String faceUrl;
	
	/**
	 * 人脸大图
	 */
	private String imageUrl;
	
	private String time;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private Long faceId;

	public List<Times> getTimesList() {
		return timesList;
	}

	public void setTimesList(List<Times> timesList) {
		if (null != timesList) {
			this.timesList = timesList;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTimesSize() {
		return timesSize;
	}

	public void setTimesSize(Integer timesSize) {
		this.timesSize = timesSize;
	}

	public Integer getFaceSize() {
		return faceSize;
	}

	public void setFaceSize(Integer faceSize) {
		this.faceSize = faceSize;
	}
	

	@Override
	public TimesPerson clean() {
		int faceSizeSum = 0;
		for (Times times : this.timesList) {
			faceSizeSum += times.getFaceList().size();
		}
		TimesPerson other = new TimesPerson();
		other.setFaceSize(faceSizeSum);
		other.setFaceUrl(this.getFaceUrl());
		other.setId(this.getId());
		other.setFaceId(this.getFaceId());
		other.setImageUrl(imageUrl);
		other.setTimesSize(this.getTimesList().size());
		return other;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClusterPerson [id=");
		builder.append(id);
		builder.append(", timesList=");
		builder.append(timesList);
		builder.append(", timesSize=");
		builder.append(timesSize);
		builder.append(", faceSize=");
		builder.append(faceSize);
		builder.append(",imageUrl=");
		builder.append(imageUrl);
		builder.append("]");
		return builder.toString();
	}


	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public Long getFaceId() {
		return faceId;
	}

	public void setFaceId(Long faceId) {
		this.faceId = faceId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
