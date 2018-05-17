package intellif.dto;

public class StaticIInfoDto  {
	
	// 身份证号
	private String gmsfzhm;
	
	// 性别
	private Integer gender = 0; 
	
	// 录入地点
	private String addr;
	
	// 图片id
	private String imageIds;
	
	// 静态数据类型(5:在逃 6:警综 7,8,9:三类人员)
	private Integer type;

	public String getGmsfzhm() {
		return gmsfzhm;
	}

	public void setGmsfzhm(String gmsfzhm) {
		this.gmsfzhm = gmsfzhm;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getImageIds() {
		return imageIds;
	}

	public void setImageIds(String imageIds) {
		this.imageIds = imageIds;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
}
