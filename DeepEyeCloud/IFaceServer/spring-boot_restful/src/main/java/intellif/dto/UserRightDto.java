package intellif.dto;

import java.util.List;





import intellif.database.entity.OauthResource;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;


public class UserRightDto {
private UserInfo userinfo;
private  List<RoleInfo> roleInfoList;
private List<OauthResource> oauthResourceList;
public UserInfo getUserinfo() {
	return userinfo;
}
public void setUserinfo(UserInfo userinfo) {
	this.userinfo = userinfo;
}
public List<RoleInfo> getRoleInfoList() {
	return roleInfoList;
}
public void setRoleInfoList(List<RoleInfo> roleInfoList) {
	this.roleInfoList = roleInfoList;
}
public List<OauthResource> getOauthResourceList() {
	return oauthResourceList;
}
public void setOauthResourceList(List<OauthResource> oauthResourceList) {
	this.oauthResourceList = oauthResourceList;
}


}