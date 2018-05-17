package intellif.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PersonQueryDto implements Serializable {

	private static final long serialVersionUID = 5158864317118960189L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String cameraIds;
	
	private String cid;
	
	private String address;
	
	private int crimeFriType;
	
	private int crimeSecType;
	
	private int gender;
	
	private String crimeAddr;
	
	private String starttime;
	
	private String endtime;
	
	private String queryText;
	
	/************** begin V1.1.0 版本修改，修复BUG: 277（http://192.168.2.150:81/zentao/bug-view-277.html） by pengqirong @ 2016-10-09  *******/
	// 库ID
	private Long bankId;
	
	private String areaIds;
	
	private int page;
	
	private int pageSize;

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	/************** end V1.1.0 版本修改，修复BUG: 277（http://192.168.2.150:81/zentao/bug-view-277.html） by pengqirong @ 2016-10-09  *******/

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	public String getCameraIds() {
		return cameraIds;
	}

	public void setCameraIds(String cameraIds) {
		this.cameraIds = cameraIds;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCrimeFriType() {
		return crimeFriType;
	}

	public void setCrimeFriType(int crimeFriType) {
		this.crimeFriType = crimeFriType;
	}

	public int getCrimeSecType() {
		return crimeSecType;
	}

	public void setCrimeSecType(int crimeSecType) {
		this.crimeSecType = crimeSecType;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getCrimeAddr() {
		return crimeAddr;
	}

	public void setCrimeAddr(String crimeAddr) {
		this.crimeAddr = crimeAddr;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}



    public String getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(String areaIds) {
        this.areaIds = areaIds;
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
	
}
