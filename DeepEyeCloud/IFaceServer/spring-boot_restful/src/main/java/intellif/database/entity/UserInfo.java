/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellif.database.entity;

import intellif.consts.GlobalConsts;
import intellif.utils.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = GlobalConsts.T_NAME_USER, schema = GlobalConsts.INTELLIF_BASE)
public class UserInfo extends InfoBase implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 用户姓名
	@NotEmpty
	private String name;

	// 账号
	@NotEmpty
	@Column(unique = true, nullable = false)
	private String login;

	// 密码
	@NotEmpty
	private String password;
	
	private String email;//邮箱地址
	
	private String remark;//备注

	// 性别（1：男；2：女）
	@Column(name = "gender", nullable = false, columnDefinition = "int(1) default '0'")
	private int gender = 0;

	// 手机号
	@Column(name = "mobile", nullable = false, columnDefinition = "varchar(12) default null ")
	private String mobile;

	// 年龄
	@Column(name = "age", nullable = false, columnDefinition = "int(3) default '0'")
	private int age;

	// 职务
	@Column(name = "post", nullable = false, columnDefinition = "varchar(255) default '?'")
	private String post = "";

	// 所属派出所ID
	@Column(name = "police_station_id", nullable = false, columnDefinition = "int(25) default '-1'")
	private Long policeStationId = -1l;

	@Transient
	// 所在单位及祖先单位的名字，从根开始
	private List<String> policeStationNames;

	// 所在单位名字
	@Transient
	private String policeStationName;


	// 摄像头权限（cameraId,cameraId,cameraId）
	@Column(name = "camera_rights", nullable = false, columnDefinition = "varchar(255) default ''")
	private String cameraRights ="";
	// 关联人脸
	@Column(name = "face_id", nullable = false, columnDefinition = "bigint(20) default -1")
	@JsonSerialize(using = ToStringSerializer.class)
	private long faceId = -1;

	// 重复登录判断 v1.1.2
	@JsonIgnore
	@Transient
	private boolean haslogin;

	// v1.2.0用户访问时限功能
	// 访问限制的开始时间
	@Column(name = "starttime", nullable = false, columnDefinition = "datetime default '1970-01-01 00:00:00'")
	private Date startTime;
	// 访问限制的节航速时间
	@Column(name = "endtime", nullable = false, columnDefinition = "datetime default '2050-01-01 00:00:00'")
	private Date endTime;

	@Transient
	private String areaIds;
	@Transient
	private String cameraIds;
	@Transient
	private Boolean opened;
	// 功能权限id列表
	@Transient
	private String resIds = "1,100,201";//默认中级管理员
	//角色类型名
	@Transient
	private String roleTypeName = "MIDDLE_ADMIN";

    //全区域搜索特殊人群标志 0 非特殊人员 1 普通全区域搜索人员 2特殊全区域搜索人员（联络员）
	private int specialSign;
	
	private String cTypeIds = "1";//  摄像头类型过滤 为空时不过滤
	@Transient
    private int areaCount = 0;
    @Transient
    private int cameraCount = 0;
	@Transient
	private Map<String, String> zone;

	
	public  UserInfo(){
        this.getStartTime();
        this.getEndTime();
	}
	/**
	 * 获取摄像头列表
	 * @return
	 */
	public List<Long> takeCameraIdList() {
		List<Long> retList = new ArrayList<>();
		if (StringUtils.isBlank(cameraIds)) {
			return retList;
		}
		String[] temps = cameraIds.split(",");
		try {
			for (String temp : temps) {
				if (StringUtils.isNotBlank(temp)) {
					retList.add(Long.valueOf(temp.trim()));
				}
			}
			return retList;
		} catch (Exception e) {
			throw new IllegalArgumentException("摄像头列表不合法！", e);
		}
	}
	
	/**
	 * 获取区域列表
	 * @return
	 */
	public List<Long> takeAreaIdList() {
		List<Long> retList = new ArrayList<>();
		if (StringUtils.isBlank(areaIds)) {
			return retList;
		}
		String[] temps = areaIds.split(",");
		try {
			for (String temp : temps) {
				if (StringUtils.isNotBlank(temp)) {
					retList.add(Long.valueOf(temp.trim()));
				}
			}
			return retList;
		} catch (Exception e) {
			throw new IllegalArgumentException("区域列表不合法！", e);
		}
	}

	public Boolean getOpened() {
		return opened;
	}

	public void setOpened(Boolean opened) {
	    if (null == opened) {
	        this.opened = false;
	    } else {
	        this.opened = opened;
	    }
	}

	public String getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(String areaIds) {
		this.areaIds = areaIds;
	}

	public String getCameraIds() {
		return cameraIds;
	}

	public void setCameraIds(String cameraIds) {
		this.cameraIds = cameraIds;
	}

	public Date getStartTime() {
	    try{
            if(null == startTime){
                startTime = DateUtil.getFormatDate("1970-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
            }
            return startTime;
        }catch(Exception e){
            return null;
        }
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
	    try{
	        if(null == endTime){
	            endTime = DateUtil.getFormatDate("2050-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
	        }
	        return endTime;
	    }catch(Exception e){
            return null;
        }
	  
	}



	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isHaslogin() {
		return haslogin;
	}

	public void setHaslogin(boolean haslogin) {
		this.haslogin = haslogin;
	}

	// 功能权限
	// @JsonIgnore
	// @ManyToMany(fetch = FetchType.EAGER)
	// @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name =
	// "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
	// private Set<RoleInfo> roles = new HashSet<RoleInfo>();
	@Column(name = "role_ids", nullable = false, columnDefinition = "text(65534)")
	private String roleIds = "";

	

	public int getSpecialSign() {
        return specialSign;
    }

    public void setSpecialSign(int specialSign) {
        this.specialSign = specialSign;
    }
	public UserInfo(UserInfo userInfo) {
		super();
		this.id = userInfo.getId();
		this.name = userInfo.getName();
		this.login = userInfo.getLogin();
		this.password = userInfo.getPassword();
		this.gender = userInfo.getGender();
		this.mobile = userInfo.getMobile();
		this.age = userInfo.getAge();
		this.post = userInfo.getPost();
		this.policeStationId = userInfo.getPoliceStationId();
		this.cameraRights = userInfo.getCameraRights();

		this.roleIds = userInfo.getRoleIds();
		this.faceId = userInfo.getFaceId();
		this.startTime = userInfo.getStartTime();
		this.endTime = userInfo.getEndTime();
		this.specialSign = userInfo.getSpecialSign();
		this.cTypeIds = userInfo.getcTypeIds();
	}

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

	// public Set<RoleInfo> getRoles() {
	// return roles;
	// }
	//
	// public void setRoles(Set<RoleInfo> roles) {
	// this.roles = roles;
	// }

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

	public Long getPoliceStationId() {
		return policeStationId;
	}

	public void setPoliceStationId(Long policeStationId) {
		this.policeStationId = policeStationId;
	}

	public String getCameraRights() {
		return cameraRights;
	}

	public void setCameraRights(String cameraRights) {
		this.cameraRights = cameraRights;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getcTypeIds() {
        return cTypeIds;
    }

    public void setcTypeIds(String cTypeIds) {
        this.cTypeIds = cTypeIds;
    }

    @Override
	public String toString() {
		return "UserInfo{" + "id=" + id + ", name='" + name + '\'' + ", login='" + login + '\'' + ", password='"
				+ password + '\'' + ", gender=" + gender + ", mobile='" + mobile + '\'' + ", age=" + age + ", post='"
				+ post + '\'' + ", policeStationId=" + policeStationId + ", cameraRights='" + cameraRights + '\''
				+ ", faceId=" + faceId + ", roleIds='" + roleIds + '\'' + ", starttime='" + startTime + '\''
				+ ", endtime='" + endTime + '\'' + '}';
	}

	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

	@JsonIgnore
	public long getMinRoleId() {
		String roleIds = this.getRoleIds();
		long min = Long.MAX_VALUE;
		if (StringUtils.isBlank(roleIds)) {
			return min;
		}

		if (!roleIds.contains(",")) {
			min = Long.parseLong(roleIds.trim());
		} else {
			String[] roleStringArray = roleIds.split(",");
			for (String s : roleStringArray) {
				if (StringUtils.isNotBlank(s)) {
					long temp = Long.parseLong(s.trim());
					if (temp < min) {
						min = temp;
					}
				}
			}
		}

		return min;
	}

	@JsonIgnore
	public Long getRoleId() {
		String roleIds = this.getRoleIds();
		Long roleId = 0L;
		if (roleIds == null || roleIds.equals("")) {
			return roleId;
		}

		if (!roleIds.contains(",")) {
			roleId = Long.parseLong(roleIds);
		}

		return roleId;
	}

	public String getResIds() {
		return resIds;
	}

	public void setResIds(String resIds) {
		this.resIds = resIds;
	}

	public String getRoleTypeName() {
		return roleTypeName;
	}

	public void setRoleTypeName(String roleTypeName) {
		this.roleTypeName = roleTypeName;
	}

	public Map<String, String> getZone() {
		return zone;
	}

	public void setZone(Map<String, String> zone) {
		this.zone = zone;
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
	public UserInfo clone() {
		try {
			return (UserInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public List<String> getPoliceStationNames() {
		return policeStationNames;
	}

	public void setPoliceStationNames(List<String> policeStationNames) {
		this.policeStationNames = policeStationNames;
	}

	public String getPoliceStationName() {
		return policeStationName;
	}

	public void setPoliceStationName(String policeStationName) {
		this.policeStationName = policeStationName;
	}
    public int getAreaCount() {
        return areaCount;
    }
    public void setAreaCount(int areaCount) {
        this.areaCount = areaCount;
    }
    public int getCameraCount() {
        return cameraCount;
    }
    public void setCameraCount(int cameraCount) {
        this.cameraCount = cameraCount;
    }
	
}
