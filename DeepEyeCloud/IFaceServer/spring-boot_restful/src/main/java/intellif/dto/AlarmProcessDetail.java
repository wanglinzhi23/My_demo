package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class AlarmProcessDetail implements Serializable {

	private static final long serialVersionUID = -3590824957295127337L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	// 报警ID
	private String alarmId;

	// 防损人员ID或用户ID
	private long userId;
	
	//重点人员名称
	private String bName;
	//报警图片ID
	private long faceId;
	
	//报警大图
	private String alarmBigurl;
	
	//报警小图
	private String alarmSmallurl;
	
	private long taskId;
	
	//重点人员ID
	private long blackId;
	//重点人员图片ID
	private long blackImageId;
	
	//重点人员小图片
	private String blackSmallurl;
	//重点人员大图片
	private String blackBigurl;
	
	//报警地点
	private String cameraName;
	
	//报警产生时间
	private Date created;
	
	//报警处理时间
	private Date processTime;
	
	
	//相似度
	private float threshold;
	
	//处理类型
	private int type;
	
	private String description;
	
	private String address;
	

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	

	public String getAlarmBigurl() {
		return alarmBigurl;
	}

	public void setAlarmBigurl(String alarmBigurl) {
		this.alarmBigurl = alarmBigurl;
	}

	public String getAlarmSmallurl() {
		return alarmSmallurl;
	}

	public void setAlarmSmallurl(String alarmSmallurl) {
		this.alarmSmallurl = alarmSmallurl;
	}

	

	public String getBlackSmallurl() {
		return blackSmallurl;
	}

	public void setBlackSmallurl(String blackSmallurl) {
		this.blackSmallurl = blackSmallurl;
	}

	public String getBlackBigurl() {
		return blackBigurl;
	}

	public void setBlackBigurl(String blackBigurl) {
		this.blackBigurl = blackBigurl;
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

	public long getBlackId() {
		return blackId;
	}

	public void setBlackId(long blackId) {
		this.blackId = blackId;
	}

	public String getbName() {
		return bName;
	}

	public void setbName(String bName) {
		this.bName = bName;
	}

	public long getBlackImageId() {
		return blackImageId;
	}

	public void setBlackImageId(long blackImageId) {
		this.blackImageId = blackImageId;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	
	
	
}