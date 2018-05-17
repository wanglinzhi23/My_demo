package intellif.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
public class ShowAlarmInfoDto implements Serializable {

    private static final long serialVersionUID = -8721847450402807625L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    
    // 重点人员姓名
    private String realName;

    // 重点人员人脸照片地址
    private String photoData;

    // 抓拍人脸头像地址
    @Transient
    private String imageData;

    // 抓拍摄像头地址
    private String cameraName;
    
    //////////附加的多返回给前端的信息
    
    // 所属任务ID
 	private long taskId;
 	
 	// 告警消息ID
 	private long alarmId;

 	// 告警的黑名单ID
 	private long blackId;
 	
     // 告警的personID
  	private long fromPersonId;

 	// 告警的人脸ID
    @JsonSerialize(using=ToStringSerializer.class)
 	private BigInteger faceId;
    // 告警置信度
 	private double confidence;
 	
 	// 告警等级
 	private  int level;
 	
 	// 告警时间
 	private Date time;
 	
 	//告警人性别（1男  2女）
 	private int realGender = 0;
 	
 	
 	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPhotoData() {
		return photoData;
	}

	public void setPhotoData(String photoData) {
		this.photoData = photoData;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getBlackId() {
		return blackId;
	}

	public void setBlackId(long blackId) {
		this.blackId = blackId;
	}

	public long getFromPersonId() {
		return fromPersonId;
	}

	public void setFromPersonId(long fromPersonId) {
		this.fromPersonId = fromPersonId;
	}

	public BigInteger getFaceId() {
		return faceId;
	}

	public void setFaceId(BigInteger faceId) {
		this.faceId = faceId;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}

	public int getRealGender() {
		return realGender;
	}

	public void setRealGender(int realGender) {
		this.realGender = realGender;
	}

	
	
 
 	

	
    

   
   

}
