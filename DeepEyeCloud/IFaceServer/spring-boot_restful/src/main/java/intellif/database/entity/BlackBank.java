package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_NAME_BLACK_BANK,schema=GlobalConsts.INTELLIF_BASE)
public class BlackBank extends InfoBase implements Serializable,Cloneable{

	private static final long serialVersionUID = -1907618819651015110L;
	
	public BlackBank()
	{
	    
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 黑名单库编号
	private String bankNo;
	
	private String createUser;
	
	private String url = "0";
	
	
	
	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	// 黑名单库名称
	private String bankName;
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	// 黑名单库描述
	private String bankDescription;

	public String getBankDescription() {
		return bankDescription;
	}

	public void setBankDescription(String bankDescription) {
		this.bankDescription = bankDescription;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private long stationId;

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
	
	private int listType;
	
	

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	@Override
	public BlackBank clone() {   
        try {   
            return (BlackBank) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    }

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String pushObject;

    public String getPushObject() {
        return pushObject;
    }

    public void setPushObject(String pushObject) {
        this.pushObject = pushObject;
    }
	
	
}
