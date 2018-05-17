package intellif.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AlarmProcessDto implements Serializable {

	private static final long serialVersionUID = -4164466471301810546L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	// 报警ID
	private String alarmId;

	// 用户ID
	private Long userId;
	
	//处理类型
	private Integer type;  //1 已处理, 2误报
	

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	
	
}