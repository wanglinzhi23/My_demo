package intellif.chd.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.chd.bean.Camera;
import intellif.chd.consts.GenerateId;

/**
 * 次数列表
 * 
 * @author mmm
 *
 */
public class Times implements Serializable, Result<Times, Face> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 491743690435491450L;

	/**
	 * 人脸列表
	 */
	private List<Face> faceList = new ArrayList<Face>();

	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 摄像头信息
	 */
	private Camera camera;

	/**
	 * 人脸数
	 */
	private Integer faceSize;

	/**
	 * ID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id = GenerateId.getId();

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Face> getFaceList() {
		return faceList;
	}

	public void setFaceList(List<Face> faceList) {
		this.faceList = faceList;
	}

	public Integer getFaceSize() {
		return faceSize;
	}

	public void setFaceSize(Integer faceSize) {
		this.faceSize = faceSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Times [faceList=");
		builder.append(faceList);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", camera=");
		builder.append(camera);
		builder.append(", faceSize=");
		builder.append(faceSize);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public Times clean() {
		Times times = new Times();
		times.setCamera(this.getCamera());
		times.setStartTime(this.getStartTime());
		times.setEndTime(this.getEndTime());
		times.setId(this.getId());
		times.setFaceSize(this.getFaceList().size());
		return times;
	}

}
