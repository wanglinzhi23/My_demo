package intellif.database.entity;

import java.io.Serializable;

import intellif.consts.GlobalConsts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=GlobalConsts.T_NAME_POLICEMAN_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class PoliceMan implements Serializable{
	
	
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

	//警号
	private String policeNo;
	
	//警员姓名
	private String name;
	
	//警员性别
	private int sex;
	
	//警员手机
	private String phone;
	
	//警员所属单位id
	private Long stationId;

	public String getPoliceNo() {
		return policeNo;
	}

	public void setPoliceNo(String policeNo) {
		this.policeNo = policeNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

	
}
