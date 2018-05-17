package intellif.fk.controller;

import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.dto.PushAlarmInfoDto;
import intellif.fk.dto.FkLoginDto;
import intellif.settings.CasSSOSetting;
import intellif.utils.HttpUtil;
import intellif.database.dao.OauthResourceDao;
import intellif.database.entity.UserInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 
 * 实现反恐平台的单点登录 用作前端的跳转
 *
 */

@RestController
public class FkLogin {

    
    private static Logger LOG = LogManager.getLogger(FkLogin.class);
   
    @Autowired
    private UserDao userRepository;

    @Autowired
    private RoleDao roleRepository;

    @Autowired
    private OauthResourceDao oauthResourceRepository;
    
    @RequestMapping(value = "/fkUser/auth", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "反恐单点登录鉴权")
    public JsonObject casSSOauth(@RequestBody @Valid FkLoginDto fkLoginDto) {
       
        String user = fkLoginDto.getUser();
        String password = fkLoginDto.getPassword();
        String key = fkLoginDto.getKey();
        //String application_id = fkLoginDto.getApplication_id();
        String application_id ="ifaas";
        
        if(StringUtils.isEmpty(user)||StringUtils.isEmpty(password)){
            return new JsonObject("反恐平台单点登录user、password、key、application_id等不能为空", 1001); 
        }
                
        String url = CasSSOSetting.getFkUserMd5ValidateUrl();
        url = url.replace("{application_id}","application_id");
        //请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
        //key值暂定可以为空
        String param ="";
        if(key!=null&&!key.trim().equals("")){
             param = "user="+user+"&password="+password+"&application_id="+application_id+"&key="+key; 
        }else{
             param = "user="+user+"&password="+password+"&application_id="+application_id;
        }
        
        String result =HttpUtil.sendGet(url,param);
        
        System.err.println("result:"+result);
        
        if(result.indexOf("success") != -1){
       // if(true){
            
            UserInfo userInfo = null;
            try {
                userInfo = this.userRepository.findByLogin(user);
            } catch (Exception ex) {
               ex.getStackTrace(); 
               return new JsonObject("something wrong"); 
            }
            
            return new JsonObject(userInfo.getPassword()); 
            
        }else{
            
            return new JsonObject("反恐平台验证不通过",1002); 
            
        }
        
      
     
    }

}
