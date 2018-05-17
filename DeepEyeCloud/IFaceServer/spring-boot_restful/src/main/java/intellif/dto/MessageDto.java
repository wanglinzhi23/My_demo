package intellif.dto;

import java.util.Date;

public class MessageDto {

	// 消息的业务对象ID
	private long id;
	
	// 消息的业务对象父级ID
	private long bankId;
	
	// 消息的业务发生时间
	private Date time;
	
	// 消息的业务类型 （1：发起抓捕）
	private int type;

	public MessageDto(long id, long bankId, Date time, Integer messageType) {
		this.id = id;
		this.bankId = bankId;
		this.time = time;
		this.type = messageType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}
	
}
