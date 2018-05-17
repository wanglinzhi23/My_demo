package intellif.dto;

import javax.persistence.*;

import java.io.Serializable;
import java.sql.Date;

@Entity
public class PushAlarmInfoDto implements Serializable {

private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	//发送者 userid
	private String sendUserId;

	//接收者警号 多个用，间隔
	private String receiverNos;
	
	//与接收警号对应的警员姓名 多个用，间隔
	private String receiverName;
	
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

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getReceiverNos() {
		return receiverNos;
	}

	public void setReceiverNos(String receiverNos) {
		this.receiverNos = receiverNos;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
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
