package intellif.dto;


import java.util.List;

public class BankDto {
	
	//0,1,2分别对应可查看库 可编辑库和本单位库
	private int authorityType;
	//0,1分别对应黑名单和白名单库 
	private int balckOrWhite;
	
	private String bankName;
	
	private String startTime;
	
	private String endTime;
	
	private List<Long> pList;//库授权单位列表条件查询


	public int getAuthorityType() {
		return authorityType;
	}

	public void setAuthorityType(int authorityType) {
		this.authorityType = authorityType;
	}

	public int getBalckOrWhite() {
		return balckOrWhite;
	}

	public void setBalckOrWhite(int balckOrWhite) {
		this.balckOrWhite = balckOrWhite;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

    public List<Long> getpList() {
        return pList;
    }

    public void setpList(List<Long> pList) {
        this.pList = pList;
    }

	
}
