package intellif.dto;

import java.io.Serializable;
import java.util.List;

import intellif.database.entity.AlarmFaceZipTuple;

/**
 * 
 * @author yktangint
 *
 */
public class AlarmFaceZipDto implements Serializable {

	private static final long serialVersionUID = -6146496368469162186L;
	//导出告警图片的list，每个元素AlarmFaceZipTuple表示一个人，每个人可以有多张告警图片，用list表示（在AlarmFaceZipTuple）里的list
	private List<AlarmFaceZipTuple> alarmFaceZipList;
	public List<AlarmFaceZipTuple> getAlarmFaceZipList() {
		return alarmFaceZipList;
	}
	public void setAlarmFaceZipList(List<AlarmFaceZipTuple> alarmFaceZipList) {
		this.alarmFaceZipList = alarmFaceZipList;
	}
	
}
