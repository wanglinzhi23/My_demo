package intellif.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PoliceManDto implements Serializable {

	private static final long serialVersionUID = -8721847450402807625L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// 警号
	private String policeNo;

	// 警员姓名
	private String name;

	// 警员性别
	private int sex;

	// 警员手机
	private String phone;

	// 警员所属单位id
	private Long stationId;

	// 警员所属单位name
	private String stationName;
	
	// 警员权限开关状态 (与权限t_police_man_authority_type中的权限一一对应    1.表示开     0.表示关)
	@Transient
	private List<Integer> authStatusList = new ArrayList<Integer>();

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

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Integer> getAuthStatusList() {
		return authStatusList;
	}

	public void setAuthStatusList(List<Integer> authStatusList) {
		this.authStatusList = authStatusList;
	}


}