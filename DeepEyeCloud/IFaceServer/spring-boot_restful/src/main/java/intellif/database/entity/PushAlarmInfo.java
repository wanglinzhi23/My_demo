package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;


import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=GlobalConsts.T_NAME_PUSH_ALARM_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class PushAlarmInfo implements Serializable{
	
	
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	//发送者 的userid
	private long sendUserId;
	
	//接收者警号  一个接受者对应一条信息 一个对象
	private String receiverNo;
	
	//告警消息id
	private long alarmId;
	
	//推送时间
	private Date time;
	
	//警员是否已查看该消息    默认为0  查看后状态变为1
 	private int checked;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(long sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getReceiverNo() {
		return receiverNo;
	}

	public void setReceiverNo(String receiverNo) {
		this.receiverNo = receiverNo;
	}

	public long getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getChecked() {
		return checked;
	}

	public void setChecked(int checked) {
		this.checked = checked;
	}


	
	
}
