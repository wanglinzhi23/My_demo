package intellif.dto;

public class SearchUserDto {
	private Long stationId;

	// 用户类型名字，对应Roletypes的name字段
	private String userTypeName;
	
	private String specialSign;
	
	private String cTypeIds;//  摄像头类型过滤 为空时不过滤

	// 用户名
	private String name;

	private int page;
	
	private int pageSize;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}

	public String getUserTypeName() {
		return userTypeName;
	}

	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getSpecialSign() {
        return specialSign;
    }

    public void setSpecialSign(String specialSign) {
        this.specialSign = specialSign;
    }

    public String getcTypeIds() {
        return cTypeIds;
    }

    public void setcTypeIds(String cTypeIds) {
        this.cTypeIds = cTypeIds;
    }
	
}
