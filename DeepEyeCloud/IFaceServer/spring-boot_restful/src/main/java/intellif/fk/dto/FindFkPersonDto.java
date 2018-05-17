package intellif.fk.dto;


public class FindFkPersonDto {

    private static final long serialVersionUID = -1588902803798110245L;

    //布控库的人员仍然只支持龙岗分局的
    //反恐平台所属分局机构代码id
    private long fkSubInstitutionId;
    //反恐平台所属派出所机构代码id
    private long fkLocalInstitutionId;
    
    private String fkSubInstitutionCode;
    
    private String fkLocalInstitutionCode;
    // 反恐人员类别 （11：查询-616走访-有证人员 12：查询-616走访-无证儿童 13：查询-616轨迹采集-有证人员
    // 14：查询-616轨迹采集-无证儿童 ）
    // (21:布控-在逃 22：布控-重点外国人 23：布控-来深不知去向 24：布控-17个国家外国人    )
    // 多个库时 以 , 间隔    例：11,12
    private String fkType;
    // 真实姓名
    private String realName;
    // 证件号
    private String cid;
    
    //保留districtId 和 areaId 兼容一期老数据  
    private String districtIds;
    
    private String areaIds;


  
    public long getFkSubInstitutionId() {
        return fkSubInstitutionId;
    }

    public void setFkSubInstitutionId(long fkSubInstitutionId) {
        this.fkSubInstitutionId = fkSubInstitutionId;
    }

    public long getFkLocalInstitutionId() {
        return fkLocalInstitutionId;
    }

    public void setFkLocalInstitutionId(long fkLocalInstitutionId) {
        this.fkLocalInstitutionId = fkLocalInstitutionId;
    }

    public String getFkType() {
        return fkType;
    }

    public void setFkType(String fkType) {
        this.fkType = fkType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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

    public String getDistrictIds() {
        return districtIds;
    }

    public void setDistrictIds(String districtIds) {
        this.districtIds = districtIds;
    }

    public String getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(String areaIds) {
        this.areaIds = areaIds;
    }

    
  

}

