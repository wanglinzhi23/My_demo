package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Entity
@Table(name = GlobalConsts.T_NAME_RED_FORCE,schema=GlobalConsts.INTELLIF_BASE)
public class RedForceRecord extends InfoBase implements Serializable{

	/**
	 * 红名单误报信息记录
	 */
	private static final long serialVersionUID = 7193516788002170428L;
	
	private static Logger LOG = LogManager.getLogger(RedForceRecord.class);


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Transient
	private String type = "红名单检索";
	
	private String message;//操作记录
	
	private String operaPerson;//操作人员
	
	private String cmpPerson;//比对人员
	
	private String station;//单位
	
	private String reason;//检索事由
	
	private long sId;//检索图片ID
	
	private long rId;//比中红名单ID

	public RedForceRecord(String message,String operaPerson,String cmpPerson,String station,String reason,long sId,long rId){
     this.message = message;
     this.operaPerson = operaPerson;
     this.cmpPerson = cmpPerson;
     this.station = station;
     this.reason = reason;
     this.sId = sId;
     this.rId = rId;
	}
	public RedForceRecord(){
		
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	public String getOperaPerson() {
		return operaPerson;
	}

	public void setOperaPerson(String operaPerson) {
		this.operaPerson = operaPerson;
	}

	public String getCmpPerson() {
		return cmpPerson;
	}

	public void setCmpPerson(String cmpPerson) {
		this.cmpPerson = cmpPerson;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getsId() {
		return sId;
	}

	public void setsId(long sId) {
		this.sId = sId;
	}

	public long getrId() {
		return rId;
	}

	public void setrId(long rId) {
		this.rId = rId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
