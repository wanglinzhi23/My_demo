package intellif.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author  Zheng Xiaodong
 */
public class MobileCollectPersonDto {
    private Long id;
    private Date created;
    private Date updated;
    private String name;
    private Integer gender;
    private String national;
    private String birthday;
    private String cid;
    private String cidPhoto;
    private String address;
    private String collectBigPhoto;
    private String collectSmallPhoto;
    private String creater;
    private Long createrId;
    private Long branchId;
    private Long companyId;
    private String updater;
    private Integer operateStatus;
    private Integer infoMatch;
    private Integer imageMatch;
    private String gawName;
    private String gawFace;
    private Integer gawGender;
    private String gawNational;
    private String similar;
    private Integer send;
    private Integer operateCount;
    private String stationName;
    private Long stationId;
    private String companyName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNational() {
        return national;
    }

    public void setNational(String national) {
        this.national = national;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCidPhoto() {
        return cidPhoto;
    }

    public void setCidPhoto(String cidPhoto) {
        this.cidPhoto = cidPhoto;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCollectBigPhoto() {
        return collectBigPhoto;
    }

    public void setCollectBigPhoto(String collectBigPhoto) {
        this.collectBigPhoto = collectBigPhoto;
    }

    public String getCollectSmallPhoto() {
        return collectSmallPhoto;
    }

    public void setCollectSmallPhoto(String collectSmallPhoto) {
        this.collectSmallPhoto = collectSmallPhoto;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Integer getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(Integer operateStatus) {
        this.operateStatus = operateStatus;
    }

    public Integer getInfoMatch() {
        return infoMatch;
    }

    public void setInfoMatch(Integer infoMatch) {
        this.infoMatch = infoMatch;
    }

    public Integer getImageMatch() {
        return imageMatch;
    }

    public void setImageMatch(Integer imageMatch) {
        this.imageMatch = imageMatch;
    }

    public String getGawName() {
        return gawName;
    }

    public void setGawName(String gawName) {
        this.gawName = gawName;
    }

    public String getGawFace() {
        return gawFace;
    }

    public void setGawFace(String gawFace) {
        this.gawFace = gawFace;
    }

    public Integer getGawGender() {
        return gawGender;
    }

    public void setGawGender(Integer gawGender) {
        this.gawGender = gawGender;
    }

    public String getGawNational() {
        return gawNational;
    }

    public void setGawNational(String gawNational) {
        this.gawNational = gawNational;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public Integer getSend() {
        return send;
    }

    public void setSend(Integer send) {
        this.send = send;
    }

    public Integer getOperateCount() {
        return operateCount;
    }

    public void setOperateCount(Integer operateCount) {
        this.operateCount = operateCount;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
