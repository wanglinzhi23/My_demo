package intellif.dto;

import java.io.Serializable;

public class AuditLogInfoDto implements Serializable {
  
	private static final long serialVersionUID = 6477346406740844464L;
    private String starttime;
    private String endtime;
    private String keywords;
    private String stationid;
    private String policeId;
    public String getStationid() {
		return stationid;
	}

	public void setStationid(String stationid) {
		this.stationid = stationid;
	}

	private String owner;
    private long object_status;
    private String message;
    private String title;

    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getObject_status() {
		return object_status;
	}

	public void setObject_status(long object_status) {
		this.object_status = object_status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AuditLogInfoDto(String stime,String etime,String kwords) {
        this.starttime = stime;
        this.endtime = etime;
        this.keywords = kwords;
    }

    public AuditLogInfoDto() {
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

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPoliceId() {
		return policeId;
	}

	public void setPoliceId(String policeId) {
		this.policeId = policeId;
	}

	

   
  
}
