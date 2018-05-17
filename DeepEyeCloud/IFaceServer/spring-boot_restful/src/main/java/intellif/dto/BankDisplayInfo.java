package intellif.dto;

public class BankDisplayInfo {
	
	private long id;//名单库id
	
	private String name;
	
	private int page;
	
	private int pageSize;
	
	private String bkstartime;
	
	private String bkendime;
	
	private String bType; //库类型区分
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getBkstartime() {
		return bkstartime;
	}

	public void setBkstartime(String bkstartime) {
		this.bkstartime = bkstartime;
	}

	public String getBkendime() {
		return bkendime;
	}

	public void setBkendime(String bkendime) {
		this.bkendime = bkendime;
	}

    public String getbType() {
        return bType;
    }

    public void setbType(String bType) {
        this.bType = bType;
    }

  
	

}
