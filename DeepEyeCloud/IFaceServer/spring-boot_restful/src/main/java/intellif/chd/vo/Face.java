package intellif.chd.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;

public class Face implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4198233051627772906L;
	// 人脸编号
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	// 录入时间
	private Date time;
	// 人脸图像数据
	private String imageData;// URI
	
	// 摄像头ID
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Face [id=");
		builder.append(id);
		builder.append(", time=");
		builder.append(time);
		builder.append(", imageData=");
		builder.append(imageData);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append("]");
		return builder.toString();
	}

}
