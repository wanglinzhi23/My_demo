package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class AlarmInfoDto implements Serializable {

    private static final long serialVersionUID = -8721847450402807625L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private long alarmFaceId;
    
    private long blackImageId;

    // 告警等级
    private int level;

    // 告警置信度
    private double confidence;

    // 区县
    private String county;

    // 摄像头名称
    private String cameraName;

    // 重点人员姓名
    private String blackName;

    // 重点人员库名称
    private String bankName;

    // 告警时间
    private Date alarmTime;

    // 告警状态
    private int alarmStatus;

    // 人脸照片地址
    private String blackImageData;

    // 人脸图像数据
    private String faceImageData;

    // 抓拍原始照片
    private String faceBigImageUri;

    // 重点人员原始照片
    private String blackBigImageUri;

    // 重点人员描述信息
    private String blackDescription;

    // 任务名称
    private String taskName;

    @Transient
    private String startTime;

    @Transient
    private String endTime;
    
    private int type;
    
    private int listType;

    private int status;

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
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

    public String getBlackName() {
        return blackName;
    }

    public void setBlackName(String blackName) {
        this.blackName = blackName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getBlackImageData() {
        return blackImageData;
    }

    public void setBlackImageData(String blackImageData) {
        this.blackImageData = blackImageData;
    }

    public String getFaceImageData() {
        return faceImageData;
    }

    public void setFaceImageData(String faceImageData) {
        this.faceImageData = faceImageData;
    }

    public String getFaceBigImageUri() {
        return faceBigImageUri;
    }

    public void setFaceBigImageUri(String faceBigImageUri) {
        this.faceBigImageUri = faceBigImageUri;
    }

    public String getBlackBigImageUri() {
		return blackBigImageUri;
	}

	public void setBlackBigImageUri(String blackBigImageUri) {
		this.blackBigImageUri = blackBigImageUri;
	}

	public String getBlackDescription() {
        return blackDescription;
    }

    public void setBlackDescription(String blackDescription) {
        this.blackDescription = blackDescription;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

	public long getAlarmFaceId() {
		return alarmFaceId;
	}

	public void setAlarmFaceId(long alarmFaceId) {
		this.alarmFaceId = alarmFaceId;
	}

	public long getBlackImageId() {
		return blackImageId;
	}

	public void setBlackImageId(long blackImageId) {
		this.blackImageId = blackImageId;
	}

	

}