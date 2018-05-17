package intellif.database.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author yktangint
 *
 */
public class AlarmFaceZipTuple implements Serializable {

	private static final long serialVersionUID = -112953297556109536L;
	private long personId;
	//要导出的告警ID
	private List<Long> alarmIdList;
	public long getPersonId() {
		return personId;
	}
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	public List<Long> getAlarmIdList() {
		return alarmIdList;
	}
	public void setAlarmIdList(List<Long> alarmIdList) {
		this.alarmIdList = alarmIdList;
	}
}
