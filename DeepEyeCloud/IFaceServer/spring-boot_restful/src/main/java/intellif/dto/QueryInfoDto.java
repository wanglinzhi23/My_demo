package intellif.dto;

public class QueryInfoDto {
	
	private String ids;
	
	private float threshold;
	

	private int listType = 2;
	
	private String fkType;
    // �ڵ�����
    private String nodeType = "area"; 
    // ��ҳҳ��
    private int page;
    // ��ҳ��С
    private int pageSize;

    private long userId = 0l;
    
    private long personId;
    
    private String status = "0,1,2"; //报警状态 默认包含所有 0 未处理  1 已处理 2误报
    
    private int onlyFirst = 0; //是否只返回最新的一条未处理报警数据，如果已处理，则不返回，0 否 1 是

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

	
	//反恐平台区域id
	private long subInstitutionId;
	//反恐平台子区域id
	private long localInstitutionId;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

    public String getFkType() {
        return fkType;
    }

    public void setFkType(String fkType) {
        this.fkType = fkType;
    }

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
	
    public long getSubInstitutionId() {
        return subInstitutionId;
    }

    public void setSubInstitutionId(long subInstitutionId) {
        this.subInstitutionId = subInstitutionId;
    }

    public long getLocalInstitutionId() {
        return localInstitutionId;
    }

    public void setLocalInstitutionId(long localInstitutionId) {
        this.localInstitutionId = localInstitutionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOnlyFirst() {
        return onlyFirst;
    }

    public void setOnlyFirst(int onlyFirst) {
        this.onlyFirst = onlyFirst;
    }

}
