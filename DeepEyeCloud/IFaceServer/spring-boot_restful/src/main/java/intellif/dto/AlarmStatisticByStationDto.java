package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AlarmStatisticByStationDto implements Serializable {

	private static final long serialVersionUID = 1L;

	// 派出所Id
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 派出所名称
	private String areaName;

	// 报警次数
	private int num;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
