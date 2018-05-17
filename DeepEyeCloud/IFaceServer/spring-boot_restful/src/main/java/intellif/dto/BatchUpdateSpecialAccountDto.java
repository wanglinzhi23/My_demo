package intellif.dto;

public class BatchUpdateSpecialAccountDto {
	
	private String userIds;
	private Integer specialSign;

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public Integer getSpecialSign() {
		return specialSign;
	}

	public void setSpecialSign(Integer specialSign) {
		this.specialSign = specialSign;
	}
}
