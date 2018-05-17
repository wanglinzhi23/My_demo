package intellif.dto;

import intellif.database.entity.InfoBase;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class UserDto extends InfoBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 账号
    private String login;

    // 密码
    private String password;

    // 用户姓名
    private String name;

    // 性别（1：男；2：女）
    private int gender;

    // 手机号
    private String mobile;
    
    // 年龄
    private int age;
    
    // 职务
    private String post;
    
    // 所属派出所名称
    private String policeStationName;
    
    // 摄像头权限（cameraId,cameraId,cameraId）
    private String cameraRights;
    
    // 功能权限 用户角色名称
    private String roles;
    // 用户角色id
    private Long roleId;
    
    //全区域搜索特殊人员标志
    private int specialSign;
    
    private String cTypeIds;

	@Transient
	private String startTime;

	@Transient
	private String endTime;

	@Transient
	private int startAge;

	@Transient
	private int endAge;
	@Transient
	private String[] userIds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getPoliceStationName() {
		return policeStationName;
	}

	public void setPoliceStationName(String policeStationName) {
		this.policeStationName = policeStationName;
	}

	public String getCameraRights() {
		return cameraRights;
	}

	public void setCameraRights(String cameraRights) {
		this.cameraRights = cameraRights;
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

	public int getStartAge() {
		return startAge;
	}

	public void setStartAge(int startAge) {
		this.startAge = startAge;
	}

	public int getEndAge() {
		return endAge;
	}

	public void setEndAge(int endAge) {
		this.endAge = endAge;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

    public int getSpecialSign() {
        return specialSign;
    }

    public void setSpecialSign(int specialSign) {
        this.specialSign = specialSign;
    }

    public String getcTypeIds() {
        return cTypeIds;
    }

    public void setcTypeIds(String cTypeIds) {
        this.cTypeIds = cTypeIds;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
    }

	
}
