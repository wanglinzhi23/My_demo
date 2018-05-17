package intellif.dto;


import intellif.database.entity.ImageDetail;
import intellif.database.entity.ImageInfo;

public class IdentityInfoDto {
	private String XP;//照片
	private String ZZXZ;//地址
	private String FWCS;//
	private String XB;//性别
	private String XM;//姓名
	private String SFZH;//身份证号
	private String CSRQ;//出生日期
	private ImageInfo imageInfo;
	private ImageDetail imageDetail;
	public String getXP() {
		return XP;
	}
	public void setXP(String xP) {
		XP = xP;
	}

	public String getFWCS() {
		return FWCS;
	}
	public void setFWCS(String fWCS) {
		FWCS = fWCS;
	}
	public String getXB() {
		return XB;
	}
	public void setXB(String xB) {
		XB = xB;
	}
	public String getXM() {
		return XM;
	}
	public void setXM(String xM) {
		XM = xM;
	}
	public String getSFZH() {
		return SFZH;
	}
	public void setSFZH(String sFZH) {
		SFZH = sFZH;
	}
	public String getZZXZ() {
		return ZZXZ;
	}
	public void setZZXZ(String zZXZ) {
		ZZXZ = zZXZ;
	}
	public ImageInfo getImageInfo() {
		return imageInfo;
	}
	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}
	public String getCSRQ() {
		return CSRQ;
	}
	public void setCSRQ(String date) {
		CSRQ = date;
	}
    public ImageDetail getImageDetail() {
        return imageDetail;
    }
    public void setImageDetail(ImageDetail imageDetail) {
        this.imageDetail = imageDetail;
    }
	
	

}
