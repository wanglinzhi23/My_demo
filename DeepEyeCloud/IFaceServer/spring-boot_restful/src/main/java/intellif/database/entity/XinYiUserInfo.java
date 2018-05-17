package intellif.database.entity;

public class XinYiUserInfo {
	private String currentMainJob;
	private String organName;
	private String dept;
	private String phoneNumber;
	private String realName;
	//信义那边的用户名，即是云天励飞t_user表里的login字段
	private String userName;
	private Long id;
	public String getCurrentMainJob() {
		return currentMainJob;
	}
	public void setCurrentMainJob(String currentMainJob) {
		this.currentMainJob = currentMainJob;
	}
	public String getOrganName() {
		return organName;
	}
	public void setOrganName(String organName) {
		this.organName = organName;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
}
