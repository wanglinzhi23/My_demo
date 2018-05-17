package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_CRIME_ALARM_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class CrimeAlarmInfo implements Serializable{

	private static final long serialVersionUID = -2180116892478832158L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 告警的罪犯人脸ID
	private long crimeFaceId;

	// 告警的罪犯信息ID
	private long crimePersonId;
	
	// 摄像头ID
	private long cameraId;

	// 告警的人脸ID
    @JsonSerialize(using=ToStringSerializer.class)
	private long faceId;
	
	// 告警的人脸图片地址
	private String faceUrl;

	// 告警置信度
	private double confidence;
	
	// 告警时间
	private Date time;
	
	// 告警状态
	private int status;//0:未查看,1:标记正确,2:标记错误

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCrimeFaceId() {
		return crimeFaceId;
	}

	public void setCrimeFaceId(long crimeFaceId) {
		this.crimeFaceId = crimeFaceId;
	}

	public long getCrimePersonId() {
		return crimePersonId;
	}

	public void setCrimePersonId(long crimePersonId) {
		this.crimePersonId = crimePersonId;
	}

	public long getCameraId() {
		return cameraId;
	}

	public void setCameraId(long cameraId) {
		this.cameraId = cameraId;
	}

	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}