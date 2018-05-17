package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.dto.RedDto;
import intellif.validate.SexType;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Entity
@Table(name = GlobalConsts.T_NAME_IFACE_CONFIG,schema=GlobalConsts.INTELLIF_BASE)
public class IFaceConfig extends InfoBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 通信配置类，engine和api同时用到
	 */
	
	private static Logger LOG = LogManager.getLogger(IFaceConfig.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String conKey;
	
	private int conValue;
	
	private String brief;

	public IFaceConfig(String confKey,int confValue,String brief){
		this.conKey = confKey;
		this.conValue = confValue;
		this.brief = brief;
	}
	public IFaceConfig(){
		
	}
	
	public String getConKey() {
		return conKey;
	}
	public void setConKey(String conKey) {
		this.conKey = conKey;
	}
	public int getConValue() {
		return conValue;
	}
	public void setConValue(int conValue) {
		this.conValue = conValue;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
}
