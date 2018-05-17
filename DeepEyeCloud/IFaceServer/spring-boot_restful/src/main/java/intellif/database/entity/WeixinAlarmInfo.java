package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class WeixinAlarmInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4083258444089266160L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long stationId;
	private String name;// 摄像头名称
	private String stationName;// 所名称

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
