package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import intellif.database.entity.InfoBase;

@Entity
public class PersonFullDto extends InfoBase implements Serializable {
	
	private static final long serialVersionUID = -8548966889080962721L;

	// 犯罪类型名称（大类名称-小类名称）
	private String crimeName;
	
	// 布控区域（龙岗所,布吉所,...）
	private String areaName;
	
	// 人物编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // 真实姓名
    private String realName;
    // 生日
    private Date birthday;
    // 民族
    private String nation;
    // 真实性别
    private int realGender=0;//1:男,2:女
    // 证件号
    private String cid;
    // 家庭住址
    private String address;
    // 证件头像照片Id
    private String photoData;
    // 犯罪类型
    private long crimeType=-1;;
    // 犯罪地址
    private String crimeAddress;
    // 说明
    private String description;
    // 规则ID
    private long ruleId = -1;
    // 唯一标识
    private long identity=-1;
    // 所属库
    private long bankId=0;
    // 布控开始时间
    private Date starttime;
    // 布控结束时间
    private Date endtime;
    // 布控状态 （0：未布控；1：已布控；2：已删除）
    private int status=-1;
    // 入库人
    private String owner;
    // 入库人单位
    private String ownerStation;
    // 是否重点（0：非重点；1：重点）
    private int important = -1;
    // 是否抓捕（0：不抓捕；1：抓捕；2：已抓捕）
    private int arrest = -1;
    // 相似嫌疑人
    private int similarSuspect = 0;
    // 警局出入记录数
    private int inStation = 0;
    // 历史抓拍次数
    private int history = 0;
    @JsonIgnore
    private char deleted = '0';
    //黑白名单
    private int type = 0;//0 black ,1 white
    //紧急布控人员标志
    private int isUrgent = 0; //0 非紧急 1紧急
	public String getCrimeName() {
		return crimeName;
	}

	public void setCrimeName(String crimeName) {
		this.crimeName = crimeName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

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

	public int getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(int isUrgent) {
		this.isUrgent = isUrgent;
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

	public String getPhotoData() {
		return photoData;
	}

	public void setPhotoData(String photoData) {
		this.photoData = photoData;
	}

	public long getCrimeType() {
		return crimeType;
	}

	public void setCrimeType(long crimeType) {
		this.crimeType = crimeType;
	}

	public String getCrimeAddress() {
		return crimeAddress;
	}

	public void setCrimeAddress(String crimeAddress) {
		this.crimeAddress = crimeAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public long getIdentity() {
		return identity;
	}

	public void setIdentity(long identity) {
		this.identity = identity;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerStation() {
		return ownerStation;
	}

	public void setOwnerStation(String ownerStation) {
		this.ownerStation = ownerStation;
	}

	public int getImportant() {
		return important;
	}

	public void setImportant(int important) {
		this.important = important;
	}

	public int getArrest() {
		return arrest;
	}

	public void setArrest(int arrest) {
		this.arrest = arrest;
	}

	public int getSimilarSuspect() {
		return similarSuspect;
	}

	public void setSimilarSuspect(int similarSuspect) {
		this.similarSuspect = similarSuspect;
	}

	public int getInStation() {
		return inStation;
	}

	public void setInStation(int inStation) {
		this.inStation = inStation;
	}

	public int getHistory() {
		return history;
	}

	public void setHistory(int history) {
		this.history = history;
	}

	public char getDeleted() {
		return deleted;
	}

	public void setDeleted(char deleted) {
		this.deleted = deleted;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
