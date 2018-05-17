package intellif.dto;

import java.io.Serializable;

import intellif.database.entity.BlackBank;
import intellif.database.entity.InfoBase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class BankInfoDto extends InfoBase implements Serializable{


	private static final long serialVersionUID = -1907618819651015110L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 黑名单库编号
	private String bankNo;
	
	private String createUser;
	
	private String url;
	
	private long totalCount;
	

	private String stationName;

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

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}


    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }


	

}
