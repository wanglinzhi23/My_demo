package intellif.fk.vo;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import intellif.database.entity.InfoBase;

public class FKPersonInfo  extends InfoBase{
    private static final long serialVersionUID = 1254898017115628327L;
   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    //证件号码
    private String certId;
    //类别代码
    private String fkType;
    private String name;
    private String nation;
    //采集地点详细地址
    private String address;
    private String startDate;
    private String endDate;
    //人员所属分局代码
    private String branchCode;
    //人员所属派出所代码
    private String stationCode;
    //采集地址所属分局代码
    private String policeCode;
    //采集地址所属派出所代码
    private String officeCode;
    //采集地点区划代码
    private String areaCode;
    private String largerMap;
    private String smallMap;
    
    private String lomgitude;
    private String dimensionality;
    //推送告警信息时添加的备注信息
    private String notes;
    //人脸位置信息
    private String rect;
    //国籍代码
    private String nationality;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCertId() {
        return certId;
    }
    public void setCertId(String certId) {
        this.certId = certId;
    }
    public String getFkType() {
        return fkType;
    }
    public void setFkType(String fkType) {
        this.fkType = fkType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNation() {
        return nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getBranchCode() {
        return branchCode;
    }
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    public String getStationCode() {
        return stationCode;
    }
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    public String getPoliceCode() {
        return policeCode;
    }
    public void setPoliceCode(String policeCode) {
        this.policeCode = policeCode;
    }
    public String getOfficeCode() {
        return officeCode;
    }
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }
    public String getAreaCode() {
        return areaCode;
    }
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getLargerMap() {
        return largerMap;
    }
    public void setLargerMap(String largerMap) {
        this.largerMap = largerMap;
    }
    public String getSmallMap() {
        return smallMap;
    }
    public void setSmallMap(String smallMap) {
        this.smallMap = smallMap;
    }
    public String getLomgitude() {
        return lomgitude;
    }
    public void setLomgitude(String lomgitude) {
        this.lomgitude = lomgitude;
    }
    public String getDimensionality() {
        return dimensionality;
    }
    public void setDimensionality(String dimensionality) {
        this.dimensionality = dimensionality;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getRect() {
        return rect;
    }
    public void setRect(String rect) {
        this.rect = rect;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    
    
}
