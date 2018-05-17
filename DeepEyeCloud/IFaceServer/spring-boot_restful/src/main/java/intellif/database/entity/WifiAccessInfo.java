package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_WIFI_ACCESS_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class WifiAccessInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String unumber;
	private String mac;
	private Long miltime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUnumber() {
		return unumber;
	}
	public void setUnumber(String unumber) {
		this.unumber = unumber;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public Long getMiltime() {
		return miltime;
	}
	public void setMiltime(Long miltime) {
		this.miltime = miltime;
	}
	
}
