package intellif.fk.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FkPersonResultDto {

    private static final long serialVersionUID = -1588902803798110245L;

    //persondetail_id
    @Id   
    private long id;
    //人脸编号
    private String blackDetailId;
    //身份证
    private String cid;
    //头像图片地址
    private String photoData;
    //真实姓名
    private String realName;
    //民族
    private String nation;
    //注册户籍地址
    private String registerAddress;
    //反恐库id
    private long bankId;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getBlackDetailId() {
        return blackDetailId;
    }
    public void setBlackDetailId(String blackDetailId) {
        this.blackDetailId = blackDetailId;
    }
    public String getCid() {
        return cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getPhotoData() {
        return photoData;
    }
    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public String getNation() {
        return nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getRegisterAddress() {
        return registerAddress;
    }
    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }
    public long getBankId() {
        return bankId;
    }
    public void setBankId(long bankId) {
        this.bankId = bankId;
    }
    

    
  

}
