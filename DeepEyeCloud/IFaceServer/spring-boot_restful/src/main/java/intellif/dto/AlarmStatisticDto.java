package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AlarmStatisticDto implements Serializable {
	//
	private static final long serialVersionUID = 1L;

	// @Transient
	private int alarms;// for AlarmServiceItf.findByCountAndBetweenWeek();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 告警等级
	private int level;

	// 告警时间
	private Date time;

	public int getAlarms() {
		return alarms;
	}

	public void setAlarms(int alarms) {
		this.alarms = alarms;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
