package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
public class EventInfo  implements Serializable {

	private static final long serialVersionUID = -7840249222359100414L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
    // 所属人Id
    private long personId;
    
    //报警faceId
    @JsonSerialize(using=ToStringSerializer.class)
    private long faceId;
    
    //报警是否已发送出去
    private int send;
    
    // 相似度（-1标示消息数据）
    private double confidence;

    // 人脸图片地址(或消息主体)
    private String imageData;
    
    // 场景图片地址
    private String scene;
    
    // 抓拍摄像头Id
    private String cameraId;
    
    // 区域Id
    private String areaId;
    
    // 抓拍摄像头名称
    private String cameraName;
    
    // 地理位置
    private String geoString;
    
    private String address;
    //报警记录人工标记是否正确
    private String status;
    
//    private long faceId;
    

    // 发生时间
    private Date time;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                
	public String getCameraId() {
		return cameraId;
	}

	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public String getGeoString() {
		return geoString;
	}

	public void setGeoString(String geoString) {
		this.geoString = geoString;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getSend() {
		return send;
	}

	public void setSend(int send) {
		this.send = send;
	}

	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
}
