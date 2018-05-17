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

package intellif.controllers;

import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.dto.JsonObject;
import intellif.settings.CasSSOSetting;
import intellif.database.dao.OauthResourceDao;
import intellif.database.entity.UserInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mortbay.util.ajax.JSON;
//import org.jasig.cas.client.authentication.AttributePrincipal;
//import org.jasig.cas.client.util.AssertionHolder;
//import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 */
@RestController
//@RequestMapping("/user")
// @RequestMapping(GlobalConsts.R_ID_USER)
public class UserLonggangController {

    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(UserLonggangController.class);
    // Autowire an object of type userInfoDao
    @Autowired
    private UserDao userRepository;

    @Autowired
    private RoleDao roleRepository;

    @Autowired
    private OauthResourceDao oauthResourceRepository;
    
    @RequestMapping(value = "/user/sso/auth/{ticket}/{userName}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "龙岗单点登录鉴权")
    public JsonObject casSSOauth(@PathVariable("ticket") String ticket, @PathVariable("userName") String userName) {
        // verify ticket id is authorized and get user name
        JsonObject error = new JsonObject("{\"error\":\"900\",\"message\":\"validate error, please login firstly\"}");
        if(StringUtils.isEmpty(ticket)) {
            return error;
        }
        
        String validateUrl = CasSSOSetting.getTicketValidateUrl()+ticket;
        HttpGet validateGet = new HttpGet(validateUrl);

        String login = null;
        try {
            HttpResponse response = HttpClients.createDefault().execute(validateGet);
            @SuppressWarnings("unchecked")
            Map<String, String> json = (Map<String, String>) JSON.parse(new InputStreamReader(response.getEntity().getContent()));
            String result = json.get("result");        
            if (!"OK".equalsIgnoreCase(result)) {
                return error;
            }
            login = json.get("username");
        } catch (IOException e) {
            
        }
        
        UserInfo user = null;
        try {
            user = this.userRepository.findByLogin(login);
        } catch (Exception ex) {

        }

        if (user == null) {
            return error;
        }
        
        return new JsonObject("{\"user\":\""+login+"\",\"validate\":\""+user.getPassword()+"\",\"code\":200}");
    }

}
