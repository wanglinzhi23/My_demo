package intellif.fk.vo;

import intellif.consts.GlobalConsts;
import intellif.fk.dto.FkPersonDto;
import intellif.database.entity.InfoBase;

import javax.persistence.*;

import java.io.Serializable;


@Entity
@Table(name = GlobalConsts.T_NAME_FK_PERSON_ATTR,schema=GlobalConsts.INTELLIF_BASE)
public class FkPersonAttr extends InfoBase implements Serializable {

    private static final long serialVersionUID = -409284155257335672L;
    // 编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // 关联persondetail表中的id
    private long fromPersonId;
    // 反恐人员在反恐平台的状态
    private String fkStatus;
    //所属分局编号
    private long districId;
    //所属派出所编号 
    private long areaId;
    //反恐平台所属分局机构代码
    private String fkSubInstitutionCode;
    //反恐平台所属派出所机构代码
    private String fkLocalInstitutionCode;
    //曾用名
    private String usedName;
    //国籍
    private String nationality;
    //户籍地址
    private String registerAddress;
    //户籍住址区划
    private String registerAddressDivision;
    //现住址区划
    private String addressDivision;
    //户籍派出所
    private String registerPoliceStation;
    //职业
    private String profession;
    //联系方式
    private String phoneNumber;
    //ic卡
    private String icCard;
    //mac地址
    private String macAddress;
    //更新标识（I/U  插入/更新）
    private String updateIdentification;
    //照片类型 1.url 2.base64
    private int photoType;

    public FkPersonAttr() {
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "id=" + id +
                ", fromPersonId='" + fromPersonId + '\'' +
                ", fkStatus=" + fkStatus +
                ", districId=" + districId +
                ", areaId='" + areaId + '\'' +
                ", fkSubInstitutionCode=" + fkSubInstitutionCode +
                ", fkLocalInstitutionCode='" + fkLocalInstitutionCode + '\'' +
                ", usedName='" + usedName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", registerAddress=" + registerAddress +'\'' +
                 ", registerAddressDivision=" + registerAddressDivision +'\'' +
                  ", addressDivision=" + addressDivision +'\'' +
                ", registerPoliceStation='" + registerPoliceStation + '\'' +
                ", profession=" + profession + '\'' +
                ", phoneNumber=" + phoneNumber + '\'' +
                ", icCard='" + icCard + '\'' +
                ", macAddress=" + macAddress + '\'' +
                ", updateIdentification=" + updateIdentification + '\'' +
                ", photoType=" + photoType + 
                '}';
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

    public String getFkStatus() {
        return fkStatus;
    }

    public void setFkStatus(String fkStatus) {
        this.fkStatus = fkStatus;
    }

    public long getDistricId() {
        return districId;
    }

    public void setDistricId(long districId) {
        this.districId = districId;
    }

    public long getAreaId() {
        return areaId;
    }

    public String getFkSubInstitutionCode() {
        return fkSubInstitutionCode;
    }

    public void setFkSubInstitutionCode(String fkSubInstitutionCode) {
        this.fkSubInstitutionCode = fkSubInstitutionCode;
    }

    public String getFkLocalInstitutionCode() {
        return fkLocalInstitutionCode;
    }

    public void setFkLocalInstitutionCode(String fkLocalInstitutionCode) {
        this.fkLocalInstitutionCode = fkLocalInstitutionCode;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public String getUsedName() {
        return usedName;
    }

    public void setUsedName(String usedName) {
        this.usedName = usedName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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

    public void update(FkPersonAttr fkPersonAttr) {

        if(0<=fkPersonAttr.getDistricId())
            this.districId = fkPersonAttr.getDistricId();
        if(0!=fkPersonAttr.getAreaId())
            this.areaId = fkPersonAttr.getAreaId();
        if(null!=fkPersonAttr.getFkSubInstitutionCode())
            this.fkSubInstitutionCode = fkPersonAttr.getFkSubInstitutionCode();
        if(null!=fkPersonAttr.getFkLocalInstitutionCode())
            this.fkLocalInstitutionCode = fkPersonAttr.getFkLocalInstitutionCode();
        if(null!=fkPersonAttr.getFkStatus())
            this.fkStatus = fkPersonAttr.getFkStatus();
        if(null!=fkPersonAttr.getProfession())
            this.profession = fkPersonAttr.getProfession();
        if(null!=fkPersonAttr.getPhoneNumber())
            this.phoneNumber = fkPersonAttr.getPhoneNumber();
        if(null!=fkPersonAttr.getRegisterAddress())
            this.registerAddress = fkPersonAttr.getRegisterAddress();
        if(null!=fkPersonAttr.getRegisterAddressDivision())
            this.registerAddressDivision = fkPersonAttr.getRegisterAddressDivision();
        if(null!=fkPersonAttr.getAddressDivision())
            this.addressDivision = fkPersonAttr.getAddressDivision();
        if(null!=fkPersonAttr.getUpdateIdentification())
            this.updateIdentification = fkPersonAttr.getUpdateIdentification();
        if(null!=fkPersonAttr.getRegisterPoliceStation())
            this.registerPoliceStation = fkPersonAttr.getRegisterPoliceStation();
        if(null!=fkPersonAttr.getNationality())
            this.nationality = fkPersonAttr.getNationality();
        if(null!=fkPersonAttr.getUsedName())
            this.usedName = fkPersonAttr.getUsedName();
    }

    
    
  
}
