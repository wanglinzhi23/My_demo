package intellif.database.entity;

import java.io.Serializable;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=GlobalConsts.T_NAME_ALLOW_IPS,schema=GlobalConsts.INTELLIF_BASE)
public class AllowIpRange implements Serializable{
	
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	//起始ip
	private String startIp;
	
	//起始ip的数值
	private long startIpNumber;
	
	//结束ip
	private String endIp;
	
	//结束IP的数值
	private long endIpNumber;
	
	//IP段名称
	private String ipRangName;
	
	//录入用户
	private String user;

	public String getStartIp() {
		return startIp;
	}

	public void setStartIp(String startIp) {
		this.startIp = startIp;
	}

	public long getStartIpNumber() {
		return startIpNumber;
	}

	public void setStartIpNumber(long startIpNumber) {
		this.startIpNumber = startIpNumber;
	}

	public String getEndIp() {
		return endIp;
	}

	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	public long getEndIpNumber() {
		return endIpNumber;
	}

	public void setEndIpNumber(long endIpNumber) {
		this.endIpNumber = endIpNumber;
	}

	public String getIpRangName() {
		return ipRangName;
	}

	public void setIpRangName(String ipRangName) {
		this.ipRangName = ipRangName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
