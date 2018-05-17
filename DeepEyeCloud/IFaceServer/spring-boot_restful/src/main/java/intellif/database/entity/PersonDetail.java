package intellif.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;
import intellif.dto.PersonDto;
import intellif.fk.dto.FkPersonDto;
import intellif.database.entity.PersonDetailAlarmDate;

import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class PersonDetail.
 */
@Entity
@Table(name = GlobalConsts.T_NAME_PERSON_DETAIL,schema=GlobalConsts.INTELLIF_BASE)
//Override the default Hibernation delete and set the deleted flag rather than deleting the record from the db.
//@SQLDelete(sql = "UPDATE " + GlobalConsts.T_NAME_PERSON_DETAIL + " SET deleted = '1' WHERE id = ?")
//Filter added to retrieve only records that have not been soft deleted.
//@Where(clause = "deleted <> '1'")
public class PersonDetail extends InfoBase implements Serializable,Cloneable {
	//private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static String format = "yyyy-MM-dd";
    private static final long serialVersionUID = 4466836933222077896L;
    // 人物编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
    private long id;
    // 真实姓名
    private String realName;
    // 生日
    private Date birthday;
    // 民族
    private String nation;
    // 真实性别
    private int realGender=-1;//0:男,1:女
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
    private long ruleId = 1;
    // 唯一标识
    private long identity=-1;
    // 所属库
    private long bankId=-1;
    // 布控开始时间
	@Transient
	@JsonProperty("starttime")
    private String stime;
    // 布控结束时间
	@Transient
	@JsonProperty("endtime")
    private String etime;
    // 布控开始时间
	@JsonIgnore
    private Date starttime;
    // 布控结束时间
	@JsonIgnore
    private Date endtime;
    // 布控状态 （0：未布控；1：已布控；2：已删除）
    private int status= 1;
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
    //Soft-delete
    //@see: http://featurenotbug.com/2009/07/soft-deletes-using-hibernate-annotations/
    @JsonIgnore
    private char deleted = '0';
    //区分黑白名单 
    private int type = -1;//0为black, 1为write
    
    //区分是否紧急布控人员
    private int isUrgent = 0; //0为非紧急 1为紧急    
    //反恐人员类别 （11：查询-616走访-有证人员 12：查询-616走访-无证儿童 13：查询-616轨迹采集-有证人员  14：查询-616轨迹采集-无证儿童 ）
    //(21:布控-在逃  22：布控-重点外国人  23：布控-来深不知去向  24：布控-17个国家外国人)
    private int fkType;
    //推送警员对象
    private String pushObject;

   

    public PersonDetail(PersonDto personDto) {
        super();
        this.realName = personDto.getRealName();
        this.birthday = personDto.getBirthday();
        this.nation = personDto.getNation();
        this.realGender = personDto.getRealGender();
        this.cid = personDto.getCid();
        this.address = personDto.getAddress();
        this.photoData = personDto.getPhotoData();
        this.crimeType = personDto.getCrimeType();
        this.crimeAddress = personDto.getCrimeAddress();
        this.description = personDto.getDescription();
        this.ruleId = personDto.getRuleId();
        this.identity = personDto.getIdentity();
        this.bankId = personDto.getBankId();
        this.starttime = personDto.getStarttime();
        this.endtime = personDto.getEndtime();
        this.status = personDto.getStatus();
        this.owner = personDto.getOwner();
        this.ownerStation = personDto.getOwnerStation();
        this.important = personDto.getImportant();
        this.deleted = personDto.getDeleted();
        this.arrest = personDto.getArrest();
        this.similarSuspect = personDto.getSimilarSuspect();
        this.inStation = personDto.getInStation();
        this.history = personDto.getHistory();
        this.type = personDto.getType();
        this.isUrgent = personDto.getIsUrgent();
        this.pushObject = personDto.getPushObject();
        
    }
    
    public PersonDetail(PersonDetailAlarmDate personDetailAlarmDate) {
    	super();
    	this.id = personDetailAlarmDate.getId();
    	this.address = personDetailAlarmDate.getAddress();
    	this.arrest = personDetailAlarmDate.getArrest();
    	this.bankId = personDetailAlarmDate.getBankId();
    	this.birthday = personDetailAlarmDate.getBirthday();
    	this.cid = personDetailAlarmDate.getCid();
    	this.crimeAddress = personDetailAlarmDate.getCrimeAddress();
    	this.crimeType = personDetailAlarmDate.getCrimeType();
    	this.deleted = personDetailAlarmDate.getDeleted();
    	this.description = personDetailAlarmDate.getDescription();
    	this.endtime = personDetailAlarmDate.getEndtime();
    	this.etime = personDetailAlarmDate.getEtime();
    	this.history = personDetailAlarmDate.getHistory();
    	this.identity = personDetailAlarmDate.getIdentity();
    	this.important = personDetailAlarmDate.getImportant();
    	this.inStation = personDetailAlarmDate.getInStation();
    	this.owner = personDetailAlarmDate.getOwner();
    	this.ownerStation = personDetailAlarmDate.getOwnerStation();
    	this.photoData = personDetailAlarmDate.getPhotoData();
    	this.nation = personDetailAlarmDate.getNation();
    	this.realGender = personDetailAlarmDate.getRealGender();
    	this.realName = personDetailAlarmDate.getRealName();
        this.ruleId = personDetailAlarmDate.getRuleId();
        this.similarSuspect = personDetailAlarmDate.getSimilarSuspect();
        this.starttime = personDetailAlarmDate.getStarttime();
        this.status = personDetailAlarmDate.getStatus();
        this.type = personDetailAlarmDate.getType();
        this.stime = personDetailAlarmDate.getStime();
    }
     
    public PersonDetail(FkPersonDto fkPersonDto) {
        super();
        this.realName = fkPersonDto.getRealName();
        this.birthday = fkPersonDto.getBirthday();
        this.nation = fkPersonDto.getNation();
        this.realGender = fkPersonDto.getRealGender();
        this.cid = fkPersonDto.getCid();
        this.address = fkPersonDto.getAddress();
        this.bankId = fkPersonDto.getBankId();
        this.starttime = fkPersonDto.getStarttime();
        this.endtime = fkPersonDto.getEndtime();      
        this.owner = fkPersonDto.getOwner();
        this.ownerStation = fkPersonDto.getOwnerStation();      
        this.type = fkPersonDto.getType();   
        this.fkType = fkPersonDto.getFkType();
    }

    //后台new用
    public PersonDetail(boolean state) {
        // TODO Auto-generated constructor stub
    	this.important = 0;
    	this.arrest = 0;
    	this.ruleId = 1;
    	this.isUrgent = 0;
    }
    //后台new用
    public PersonDetail() {
      
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

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

	public void setStime(String stime) {
		this.stime = stime;
		try {
		//	this.starttime = format.parse(stime);
			this.starttime = intellif.utils.DateUtil.getFormatDate(stime, format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setEtime(String etime) {
		this.etime = etime;
		try {
		//	this.endtime =  format.parse(etime);
			this.endtime =intellif.utils.DateUtil.getFormatDate(etime, format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String getPushObject() {
        return pushObject;
    }

    public void setPushObject(String pushObject) {
        this.pushObject = pushObject;
    }
    
	public String getStime() {
		if (getStarttime() == null)
			return "";
		//return format.format(getStarttime());
		return DateFormatUtils.format(getStarttime(), format); 
	}

	public String getEtime() {
		if(getEndtime() == null)
			return "";
		//return format.format(getEndtime());
		return DateFormatUtils.format(getEndtime(), format); 
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
    public void setSex(String sex){
    	if("男".equals(sex)){
    		this.realGender = 0;
    	}else if("女".equals(sex)){
    		this.realGender = 1;
    	}
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(int isUrgent) {
		this.isUrgent = isUrgent;
	}
	
	public int getFkType() {
        return fkType;
    }

    public void setFkType(int fkType) {
        this.fkType = fkType;
    }

    @Override
	public String toString() {
		return "PersonDetail [id=" + id + ", realName=" + realName
				+ ", birthday=" + birthday + ", nation=" + nation
				+ ", realGender=" + realGender + ", cid=" + cid + ", address="
				+ address + ", photoData=" + photoData + ", crimeType="
				+ crimeType + ", crimeAddress=" + crimeAddress
				+ ", description=" + description + ", ruleId=" + ruleId
				+ ", identity=" + identity + ", bankId=" + bankId + ", stime=" + stime + ", etime="
				+ etime + ", starttime=" + starttime + ", endtime=" + endtime
				+ ", status=" + status + ", owner=" + owner + ", ownerStation=" + ownerStation + ", important="
				+ important + ", arrest=" + arrest + ", similarSuspect="
				+ similarSuspect + ", inStation=" + inStation + ", history="
				+ history + ", deleted=" + deleted + ", pushObject=" + pushObject + "]";
	}

	@Override
	public PersonDetail clone() {   
        try {   
            return (PersonDetail) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    }   
	
	public void update(PersonDetail personDetail) {
		if(null!=personDetail.getRealName())
			this.realName = personDetail.getRealName();
		if(null!=personDetail.getBirthday())
			this.birthday = personDetail.getBirthday();
		if(null!=personDetail.getNation())
			this.nation = personDetail.getNation();
		if(1<=personDetail.getRealGender())
			this.realGender = personDetail.getRealGender();
		if(null!=personDetail.getCid())
			this.cid = personDetail.getCid();
		if(null!=personDetail.getAddress())
			this.address = personDetail.getAddress();
		if(null!=personDetail.getPhotoData())
			this.photoData = personDetail.getPhotoData();
		if(0<=personDetail.getCrimeType())
			this.crimeType = personDetail.getCrimeType();
		if(null!=personDetail.getCrimeAddress())
			this.crimeAddress = personDetail.getCrimeAddress();
		if(null!=personDetail.getDescription())
			this.description = personDetail.getDescription();
		if(0<=personDetail.getRuleId())
			this.ruleId = personDetail.getRuleId();
		if(0<=personDetail.getIdentity())
			this.identity = personDetail.getIdentity();
		if(0<=personDetail.getBankId())
			this.bankId = personDetail.getBankId();
		if(null!=personDetail.getStarttime())
			this.starttime = personDetail.getStarttime();
		if(null!=personDetail.getEndtime())
			this.endtime = personDetail.getEndtime();
		if(0<=personDetail.getStatus())
			this.status = personDetail.getStatus();
		if(null!=personDetail.getOwner())
			this.owner = personDetail.getOwner();
		if(0<=personDetail.getImportant())
			this.important = personDetail.getImportant();
		if(0<=personDetail.getArrest())
			this.arrest = personDetail.getArrest();
		if(0<=personDetail.getType())
			this.arrest = personDetail.getType();
		if(0<=personDetail.getIsUrgent())
			this.isUrgent = personDetail.getIsUrgent();
		if(null!=personDetail.getPushObject())
		    this.pushObject = personDetail.getPushObject();
	}

}