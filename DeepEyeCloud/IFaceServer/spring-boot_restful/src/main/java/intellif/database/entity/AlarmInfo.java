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
@Table(name=GlobalConsts.T_NAME_ALARM_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class AlarmInfo implements Serializable{

	private static final long serialVersionUID = -8473185797276849607L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 所属任务ID
	private long taskId;

	// 告警的黑名单ID
	private long blackId;

	// 告警的人脸ID
    @JsonSerialize(using=ToStringSerializer.class)
	private long faceId;
	
//	// 告警的人脸图片地址
//	private String faceUrl;

	// 告警置信度
	private double confidence;
	
	// 告警等级
	private  int level;
	
	// 告警时间
	private Date time;
	
	// 告警状态
	private int status;//0:未查看,1:标记正确,2:标记错误
	
	private int send;//0未发送到手机微信 1已发送到手机微信
	
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
	
	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

//	public String getFaceUrl()
//	{
//		return faceUrl;
//	}
//
//	public void setFaceUrl(String faceUrl)
//	{
//		this.faceUrl = faceUrl;
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSend() {
		return send;
	}

	public void setSend(int send) {
		this.send = send;
	}
	
}