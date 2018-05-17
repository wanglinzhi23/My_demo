package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.dto.AlarmProcessDto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=GlobalConsts.T_NAME_ALARM_PROCESS,schema=GlobalConsts.INTELLIF_BASE)
public class AlarmProcess extends InfoBase implements Serializable{

	private static final long serialVersionUID = -8473185797276849607L;


	public AlarmProcess(){
		
	}
	public AlarmProcess(AlarmProcessDto apd){
		String alarmStr = apd.getAlarmId();
		Long alarmId = Long.parseLong(alarmStr);
		this.alarmId = alarmId;
		this.type = apd.getType();
		this.userId = apd.getUserId();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 报警ID
	private long alarmId;

	// 防损人员ID或用户ID
	private long userId;
	
	
	//处理类型
	private int type;
	


	public Long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(Long alarmId) {
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