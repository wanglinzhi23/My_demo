package intellif.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class XinghuoQuery implements Serializable {

	private static final long serialVersionUID = 8518971621814736433L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 数据源ID
    private long sourceId;
	// 数据开始时间
	private String starttime;
	
	// 数据开始时间
	private String endtime;

	//周期日开始时间
	private String  dayStartTime;
	
	//周期日结束时间
	private String  dayEndTime;
	//星期几
	private String weekDay;
	//年龄
	private int age;
	//穿戴
	private int accessories;
	//种族
	private String race;
	//性别
	private int gender;
	//图片质量
	private int quality;
	
	//摄像头集合 ,号分开
	private String sourceIds[];
	
	private String code;
	
	private String lastId;
	

	public String[] getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(String[] sourceIds) {
		this.sourceIds = sourceIds;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getDayStartTime() {
		return dayStartTime;
	}

	public void setDayStartTime(String dayStartTime) {
		this.dayStartTime = dayStartTime;
	}

	public String getDayEndTime() {
		return dayEndTime;
	}

	public void setDayEndTime(String dayEndTime) {
		this.dayEndTime = dayEndTime;
	}

	

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
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

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

  

}
