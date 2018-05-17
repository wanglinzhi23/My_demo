package intellif.fk.dto;

import intellif.dao.AreaDao;
import intellif.dao.DistrictDao;
import intellif.database.entity.PersonDetail;
import intellif.fk.vo.FkPersonAttr;
import intellif.validate.IDCardFormat;
import intellif.database.entity.InfoBase;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import com.blogspot.na5cent.exom.annotation.Column;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class FkPersonDto extends InfoBase implements Serializable,Cloneable {

    private static final long serialVersionUID = -1588902803798110245L;
    
    //----------------------------------------反恐平台传递的  相对persondetail 新增 的fkPersonAttr属性------------------    
    //所属分局编号  第一期里面只支持龙岗分局的
    private String districtId;
     //所属派出所编号  
    private String areaId;
    
    private String subInstitutionCode;
     
    private String localInstitutionCode;
    //人员在反恐平台状态
    private String fkStatus;
    //职业
    private String profession;
    //联系方式
    private String phoneNumber;
    //户籍地址
    private String registerAddress;
    //户籍地址区划
    private String registerAddressDivision;
    //现住址区划
    private String addressDivision;
    //户籍派出所
    private String registerPoliceStation;
    //国籍
    private String nationality;
    //曾用名
    private String usedName;
    //更新标识
    private String updateIdentification;
    //照片类型 1.url 2.base64
    private int photoType;
    //---------------------------------------persondetail中非默认 反恐平台传递过来的属性-----------------------------------
    //PersonDetail编号
    private long id;
    //图片编号
    private String imageIds;
    //反恐人员类别 （11：查询-616走访-有证人员 12：查询-616走访-无证儿童 13：查询-616轨迹采集-有证人员  14：查询-616轨迹采集-无证儿童 ）
    //(21:布控-在逃  22：布控-重点外国人  23：布控-来深不知去向  24：布控-17个国家外国人
    private int fkType;
    // 真实姓名
    private String realName;
    // 生日
    private Date birthday;
    // 民族
    private String nation;
    // 性别
    private int realGender=0;//根据bug单2637修改 未知为0  1为男      2为女   
    // 证件号
    @IDCardFormat(fieldName = "身份证:")
    @Column(name = "身份证")
    private String cid;
    // 家庭住址
    private String address;   
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
    
    //-------------------------------------jsonIgnore了部分persondetail中的字段 可以针对反恐人员默认设定的属性---------------------
    @JsonIgnore
    private int status=-1;
    // 入库人
    @JsonIgnore
    private String owner;
    // 入库人单位
    @JsonIgnore
    private String ownerStation;   
    //区分黑白名单 
    @JsonIgnore
    private int type = -1;//0为black, 1为write
    // 证件头像照片Id
    @JsonIgnore
    private String photoData;
    // 规则ID
    @JsonIgnore
    private long ruleId = -1;
    // 所属库\
    @JsonIgnore
    private long bankId=-1;
    // 是否抓捕（0：不抓捕；1：抓捕；2：已抓捕）
    @JsonIgnore
    private int arrest = -1;
    // 是否重点（0：非重点；1：重点）
    @JsonIgnore
    private int important = -1;
    
    public String getImageIds() {
        return imageIds;
    }

    public void setImageIds(String imageIds) {
        this.imageIds = imageIds;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public int getFkType() {
        return fkType;
    }

    public void setFkType(int fkType) {
        this.fkType = fkType;
    }

    public String getFkStatus() {
        return fkStatus;
    }

    public void setFkStatus(String fkStatus) {
        this.fkStatus = fkStatus;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getRegisterPoliceStation() {
        return registerPoliceStation;
    }

    public void setRegisterPoliceStation(String registerPoliceStation) {
        this.registerPoliceStation = registerPoliceStation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getUsedName() {
        return usedName;
    }

    public void setUsedName(String usedName) {
        this.usedName = usedName;
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

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FkPersonDto(){
        
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getArrest() {
        return arrest;
    }

    public void setArrest(int arrest) {
        this.arrest = arrest;
    }
    

    public int getImportant() {
        return important;
    }

    public void setImportant(int important) {
        this.important = important;
    }

    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegisterAddressDivision() {
        return registerAddressDivision;
    }

    public void setRegisterAddressDivision(String registerAddressDivision) {
        this.registerAddressDivision = registerAddressDivision;
    }

    public String getAddressDivision() {
        return addressDivision;
    }

    public void setAddressDivision(String addressDivision) {
        this.addressDivision = addressDivision;
    }

    public String getUpdateIdentification() {
        return updateIdentification;
    }

    public void setUpdateIdentification(String updateIdentification) {
        this.updateIdentification = updateIdentification;
    }

    public int getPhotoType() {
        return photoType;
    }

    public void setPhotoType(int photoType) {
        this.photoType = photoType;
    }

    public String getSubInstitutionCode() {
        return subInstitutionCode;
    }

    public void setSubInstitutionCode(String subInstitutionCode) {
        this.subInstitutionCode = subInstitutionCode;
    }

    public String getLocalInstitutionCode() {
        return localInstitutionCode;
    }

    public void setLocalInstitutionCode(String localInstitutionCode) {
        this.localInstitutionCode = localInstitutionCode;
    }

    //用于查询接口返回 反恐平台所需要的所有字段  
    public FkPersonDto(PersonDetail personDetail,FkPersonAttr fkPersonAttr) {
        //super();
        this.photoData = personDetail.getPhotoData();
        this.id = personDetail.getId();
        this.fkType = personDetail.getFkType();
        this.realName = personDetail.getRealName();
        this.birthday = personDetail.getBirthday();
        this.nation = personDetail.getNation();
        this.realGender = personDetail.getRealGender();
        this.cid = personDetail.getCid();
        this.address = personDetail.getAddress();
        this.stime = personDetail.getStime();
        this.etime = personDetail.getEtime();
              
        this.districtId = fkPersonAttr.getDistricId()+"";
        this.areaId =  fkPersonAttr.getAreaId()+"";
        this.subInstitutionCode = fkPersonAttr.getFkSubInstitutionCode();
        this.localInstitutionCode =  fkPersonAttr.getFkLocalInstitutionCode();       
        this.fkStatus = fkPersonAttr.getFkStatus();
        this.profession = fkPersonAttr.getProfession();
        this.phoneNumber = fkPersonAttr.getPhoneNumber();
        this.registerAddress = fkPersonAttr.getRegisterAddress();
        this.registerPoliceStation = fkPersonAttr.getRegisterPoliceStation();
        this.nationality = fkPersonAttr.getNationality();
        this.usedName = fkPersonAttr.getUsedName(); 
        this.registerAddressDivision = fkPersonAttr.getRegisterAddressDivision();
        this.addressDivision = fkPersonAttr.getAddressDivision();
        this.updateIdentification = fkPersonAttr.getUpdateIdentification();
        this.photoType = fkPersonAttr.getPhotoType();
                }


    //用于更新接口 剥离出 t_person_detail表中需要更新的字段 其他字段 默认属性不变
    public PersonDetail getPersonDetail(){
        PersonDetail personDetail = new PersonDetail();
        personDetail.setId(id);
        personDetail.setPhotoData(photoData);
       // personDetail.setImageIds();
        personDetail.setFkType(fkType); 
        personDetail.setRealName(realName);
        personDetail.setBirthday(birthday);
        personDetail.setNation(nation);
        personDetail.setRealGender(realGender);
        personDetail.setCid(cid);   
        personDetail.setAddress(address);    
        personDetail.setEndtime(endtime);
        personDetail.setStarttime(starttime);
       
        return personDetail;
    }

    //用于更新接口  剥离出需要更新的fk_person_attr表中的属性字段
    public FkPersonAttr getFkPersonAttr(){
        FkPersonAttr fkPersonAttr = new FkPersonAttr();
        try{
        fkPersonAttr.setAreaId(Long.valueOf(areaId).longValue());   
        fkPersonAttr.setDistricId(Long.valueOf(districtId).longValue());
        }catch(Exception e){
            System.out.println("districtId或者areaId转化成数字失败");
        }
        fkPersonAttr.setFkSubInstitutionCode(subInstitutionCode);   
        fkPersonAttr.setFkLocalInstitutionCode(localInstitutionCode);
        fkPersonAttr.setFkStatus(fkStatus);
        fkPersonAttr.setProfession(profession);
        fkPersonAttr.setPhoneNumber(phoneNumber);
        fkPersonAttr.setRegisterAddress(registerAddress);
        fkPersonAttr.setRegisterPoliceStation(registerPoliceStation);
        fkPersonAttr.setNationality(nationality);
        fkPersonAttr.setUsedName(usedName);        
        fkPersonAttr.setFromPersonId(id); 
        fkPersonAttr.setAddressDivision(addressDivision);
        fkPersonAttr.setRegisterAddressDivision(registerAddressDivision);
        fkPersonAttr.setUpdateIdentification(updateIdentification);
        fkPersonAttr.setPhotoType(photoType);
        
        return fkPersonAttr;
    }
    
    
    @Override
    public FkPersonAttr clone() {   
        try {   
            return (FkPersonAttr) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    }  

    
    @Override
    public String toString() {
        return "FkPersonDto [id=" + id + ", photoData=" + photoData
                + ", fkType=" + fkType + ", realName=" + realName
                + ", birthday=" + birthday + ", nation=" + nation + ", realGender="
                + realGender + ", cid=" + cid + ", address="
                + address + ", stime=" + stime
                + ", etime=" + etime + ", districtId=" + districtId
                + ", areaId=" + areaId + ", fkStatus=" + fkStatus + ", profession=" + profession+ ", phoneNumber=" + phoneNumber + ",registerAddress="
                + registerAddress + ", registerPoliceStation=" + registerPoliceStation+ ", photoType=" + photoType + ", nationality=" + nationality
                + ", usedName=" + usedName + "]";
    }
    
    
}