package intellif.dto;

import intellif.dto.QueryInfoDto;

import java.io.Serializable;

public class EventsByStationIdKey implements Serializable{
	
	private static final long serialVersionUID = -6298527133277506215L;
	private long id;
	private String ids;
	private float threshold;
	private int page;
	private int pagesize;

	private int type = 2;
	private String nodeType;
	private long userId = 0l;
	private String status = "0,1,2"; //报警状态 默认包含所有 0 未处理  1 已处理 2误报
    private int onlyFirst = 0; //是否只返回最新的一条未处理报警数据，如果已处理，则不返回，0 否 1 是
	
	public EventsByStationIdKey() {
		super();
	}

	
	public EventsByStationIdKey(QueryInfoDto dto) {
		super();
		this.ids = dto.getIds();
		this.threshold = dto.getThreshold();
		this.type = dto.getListType();
		this.page = dto.getPage();
		this.pagesize = dto.getPageSize();
		this.nodeType = dto.getNodeType();
		this.userId = dto.getUserId();
		this.status = dto.getStatus();
		this.onlyFirst = dto.getOnlyFirst();
	}

	public EventsByStationIdKey(long id, float threshold, int page, int pagesize) {
		super();
		this.id = id;
		this.threshold = threshold;
		this.page = page;
		this.pagesize = pagesize;
	}
	
	public EventsByStationIdKey(String ids, float threshold, int type, int page, int pagesize) {
		super();
		this.ids = ids;
		this.threshold = threshold;
		this.type = type;
		this.page = page;
		this.pagesize = pagesize;

	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}


    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }


    public long getUserId() {
        return userId;
    }


    public void setUserId(long userId) {
        this.userId = userId;
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
