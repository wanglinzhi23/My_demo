package intellif.dto;

import intellif.database.entity.RoleInfo;
import intellif.database.entity.OauthResource;
import intellif.database.entity.UserInfo;

import java.io.Serializable;

/**
 * Created by yangboz on 11/19/15.
 */
public class UserInfoDto implements Serializable {

    private static final long serialVersionUID = 5370696204798786377L;
    private UserInfo userInfo;
    private RoleInfo roleInfo;
    private OauthResource oauthResource;

    public UserInfoDto() {
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    public void setRoleInfo(RoleInfo roleInfo) {
        this.roleInfo = roleInfo;
    }

    public OauthResource getOauthResource() {
        return oauthResource;
    }

    public void setOauthResource(OauthResource oauthResource) {
        this.oauthResource = oauthResource;
    }

    @Override
    public String toString() {
        return "UserInfoDto{" +
                "userInfo=" + userInfo +
                ", roleInfo=" + roleInfo +
                ", oauthResource=" + oauthResource +
                '}';
    }
}
