package intellif.database.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AlarmPersonDetail implements Serializable{
	
	private static final long serialVersionUID = -7466564312747980542L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private String realName;
    private int realGender=0;//1:男,2:女
    private String address;
    private String crimeAddress;
    private String description;
    private String crimeName;
    private String subcrimeName;
    private String bankName;
    private String starttime;
    private String endtime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public int getRealGender() {
		return realGender;
	}
	public void setRealGender(int realGender) {
		this.realGender = realGender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCrimeName() {
		return crimeName;
	}
	public void setCrimeName(String crimeName) {
		this.crimeName = crimeName;
	}
	public String getSubcrimeName() {
		return subcrimeName;
	}
	public void setSubcrimeName(String subcrimeName) {
		this.subcrimeName = subcrimeName;
	}
	public String getCrimeAddress() {
		return crimeAddress;
	}
	public void setCrimeAddress(String crimeAddress) {
		this.crimeAddress = crimeAddress;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
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
    
}
