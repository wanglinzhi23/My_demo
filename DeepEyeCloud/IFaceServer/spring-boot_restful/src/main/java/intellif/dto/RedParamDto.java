package intellif.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RedParamDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5787813175488168396L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String startTime;
	private String endTime;
	private String sId;
	private String rId;
	private String userName;
	private String result;
	private String policePhone;
	private String keywords;
	private int searchType = 0;
	private int page;
	private int pageSize;
	private int type = 0;

	
	

	public String getPolicePhone() {
		return policePhone;
	}

	public void setPolicePhone(String policePhone) {
		this.policePhone = policePhone;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	
}
