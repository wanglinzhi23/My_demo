package intellif.dto;

import java.io.Serializable;

public class PoliceCloudLogInfoDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7994179100896609061L;
	private String url;
	private String policeId;
	private String realName;
	private long faceId;
	private long imageIds;
	private int dataType;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPoliceId() {
		return policeId;
	}
	public void setPoliceId(String policeId) {
		this.policeId = policeId;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public long getFaceId() {
		return faceId;
	}
	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}
	public long getImageIds() {
		return imageIds;
	}
	public void setImageIds(long imageIds) {
		this.imageIds = imageIds;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
}
