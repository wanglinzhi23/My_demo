package intellif.oauth;

import intellif.configs.OauthAuthorizationServerConfiguration;
import intellif.consts.GlobalConsts;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.lire.UserOnlineThread;
import intellif.service.impl.UserDetailsServiceImpl;
import intellif.database.dao.OauthResourceDao;
import intellif.database.entity.OauthResource;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangboz on 11/19/15.
 *
 * @see http://stackoverflow.com/questions/28492116/can-i-include-user-information-while-issuing-an-access-token
 */
@Service
public class OauthTokenEnhancer implements TokenEnhancer {

    private static final Logger LOG = LogManager.getLogger(OauthTokenEnhancer.class);
    
    @Autowired
    private UserDao userRepository;
    @Autowired
    private RoleDao roleRepository;

    @Autowired
    private OauthResourceDao oauthResourceRepository;


    public OauthTokenEnhancer() {

    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<String, Object>();
        String login = userDetails.getUsername();
        LOG.info("UserDetails.login:" + login);
        UserInfo userInfo = this.userRepository.findByLogin(login);
        LOG.info("UserInfo by login:" + userInfo.toString());
        List<RoleInfo> roleInfoList = new ArrayList<RoleInfo>();
        String[] roleIds = userInfo.getRoleIds().split(",");
       // LOG.info("Raw roleIds:" + roleIds.toString());
        LOG.info("Raw roleIds:" + Arrays.toString(roleIds));  
        List<OauthResource> oauthResourceList = new ArrayList<OauthResource>();
        for (int i = 0; i < roleIds.length; i++) {
            long roleId = Long.valueOf(roleIds[i]);
            RoleInfo roleInfo = this.roleRepository.findOne(roleId);
            userInfo.setRoleTypeName(roleInfo.getName());
            roleInfoList.add(roleInfo);
            //
            String[] resIds = roleInfo.getResIds().split(",");
           // LOG.info("Raw resIds:" + resIds.toString());
            LOG.info("Raw resIds:" +  Arrays.toString(resIds));  
            for (int j = 0; j < resIds.length; j++) {
                Long resId = Long.valueOf(resIds[j]).longValue();
                OauthResource oauthResource = (OauthResource) this.oauthResourceRepository.findById(resId);
                oauthResourceList.add(oauthResource);
            }
        }

        LOG.info("OauthTokenEnhancer construct with,userInfo:" + userInfo.toString() + ",roleInfoList:" + roleInfoList.toString() + ",oauthResourceList:" + oauthResourceList.toString());

        //
        additionalInfo.put(GlobalConsts.OAUTH_A_I_K_ROLE_INFO_S, roleInfoList);
        additionalInfo.put(GlobalConsts.OAUTH_A_I_K_USER_INFO, userInfo);
        additionalInfo.put(GlobalConsts.OAUTH_A_I_K_USER_DETAIL, userDetails);
        additionalInfo.put(GlobalConsts.OAUTH_A_I_K_OAUTH_RES_S, oauthResourceList);
        //additionalInfo.put("haslogin", UserDetailsServiceImpl.haslogin);
        String haslogin = "";
        if(UserOnlineThread.visitedusers.get(login)!=null){
        	String[] logininfo = UserOnlineThread.visitedusers.get(login).toString().split(",");
        	if(logininfo.length>=2&&logininfo[1].equals("true")){
        		haslogin = "该账号已经登录啦，你要踢掉它么";
        	}
        }
        additionalInfo.put("haslogin",haslogin);
        
      /*  String outofdate = UserOnlineThread.userOutOfDateState.get(userInfo.getLogin());
        //String outofdate = "";
        Date userEndTime = userInfo.getEndTime();
        Date userStartTime = userInfo.getStartTime();
        Date now = new Date();
        if(userEndTime.before(now)||userStartTime.after(now)){
        outofdate = "true";
        UserOnlineThread.userOutOfDateState.put(userInfo.getLogin(), outofdate);
        }
        additionalInfo.put("outofdate",outofdate);*/
        

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
       // ((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); //当前时间加半小时
      //  ((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000)); //当前时间加2天
        ((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date(System.currentTimeMillis() + OauthAuthorizationServerConfiguration.getToken_expire_in()));
       
        System.err.println("失效时间"+((DefaultOAuth2AccessToken) accessToken).getExpiresIn());
        LOG.info("token的失效时间是 {} ",((DefaultOAuth2AccessToken) accessToken).getExpiresIn());

        return accessToken;
    }

}
