package intellif.utils;

import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurUserInfoUtil {

private static Authentication auth= null;
	public static UserInfo getUserInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null){
			authentication = auth;
		}
		return (UserInfo)authentication.getPrincipal();
	}

	public static Authentication getAuth() {
		return auth;
	}

	public static void setAuth(Authentication auth) {
		CurUserInfoUtil.auth = auth;
	}

	public static RoleInfo getRoleInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null){
			authentication = auth;
		}
		Collection<RoleInfo> roleMap = (Collection<RoleInfo>) authentication.getAuthorities();
		RoleInfo role = roleMap.iterator().next();
		return role;
	}
	
	
	public static String getIP(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null){
			authentication = auth;
		}
		String details=authentication.getDetails().toString();
    	String detail[]=details.split(",");
    	String detail2[]=detail[0].split("=");
    	String ip=detail2[1];
    	return ip;
	}
	
	
}
