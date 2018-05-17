package intellif.dto;

import intellif.database.entity.InfoBase;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class BlackDetailDto extends InfoBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long fromPersonId;
	
	private long fromImageId;
	
	// 黑名描述信息
	private String blackDescription;
	
	// 人脸照片地址
	private String imageData;

	// 黑名单库名称
	private String bankName;
	
	// 真实姓名
	private String realName;

	// 生日
	private Date birthday;
	
	// 民族
	private String nation;

	// 真实性别
	private int realGender;

	// 证件号
	private String cid;

	// 家庭住址
	private String address;
	
	@Transient
	private String startBirthday;
	
	@Transient
	private String endBirthday;
	
	@Transient
	private String startTime;
	
	@Transient
	private String endTime;
	
	private int type;
	
	private int listType;
	
	

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFromPersonId() {
		return fromPersonId;
	}

	public void setFromPersonId(long fromPersonId) {
		this.fromPersonId = fromPersonId;
	}

	public long getFromImageId() {
		return fromImageId;
	}

	public void setFromImageId(long fromImageId) {
		this.fromImageId = fromImageId;
	}

	public String getBlackDescription() {
		return blackDescription;
	}

	public void setBlackDescription(String blackDescription) {
		this.blackDescription = blackDescription;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public int getRealGender() {
		return realGender;
	}

	public void setRealGender(int realGender) {
		this.realGender = realGender;
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

	public String getStartBirthday() {
		return startBirthday;
	}

	public void setStartBirthday(String startBirthday) {
		this.startBirthday = startBirthday;
	}

	public String getEndBirthday() {
		return endBirthday;
	}

	public void setEndBirthday(String endBirthday) {
		this.endBirthday = endBirthday;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
