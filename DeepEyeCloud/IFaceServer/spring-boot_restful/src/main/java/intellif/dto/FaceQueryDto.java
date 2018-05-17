package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 人脸查询DTO
 * @author Peng Cheng
 *
 */

@Entity
public class FaceQueryDto implements Serializable {

	private static final long serialVersionUID = -7086632919902068545L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	// 人脸编号
	private long id;

	// 数据源ID
	private long sourceId;

	// 数据源类型
	/**
	 * 0 摄像头
	 * 1 视频
	 * 2 图片
	 */
	private int sourceType;
	
	// 城市
	private String city;
	
	// 地区
	private String county;
	
	// 摄像头名称
	private String cameraName;

	// 录入时间
	private Date time;

	// 人脸图像数据
	private String imageData;//URI
	
	// 性别
	private int gender=0;
	
	// 年龄
	private int age;
	
	// 配饰
	private int accessories=0;

    @Transient
	private String startTime;

    @Transient
	private String endTime;

    @Transient
	private String ageRange;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getAccessories() {
		return accessories;
	}

	public void setAccessories(int accessories) {
		this.accessories = accessories;
	}

	public String getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

}
