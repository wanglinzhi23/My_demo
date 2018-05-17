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

package intellif.service.impl;

import intellif.dao.OauthAccessTokenDao;
import intellif.dao.RoleDao;
import intellif.dao.UserDao;
import intellif.lire.UserOnlineThread;
import intellif.database.entity.OnLineUserInfo;
import intellif.database.entity.RoleInfo;
import intellif.database.entity.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

//import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	
//	public static boolean haslogin = false;
    @Autowired
    private UserDao userRepository;
    
    @Autowired
    private OauthAccessTokenDao oauthAccessTokenDao;

//    @Autowired
//    private RoleRepository roleRepository;

    @Autowired
    public UserDetailsServiceImpl(RoleDao roleRepository) {
        UserRepositoryOAuthUserDetails.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByLogin(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException(String.format("OAuth UserInfo %s does not exist!", username));
        }
        ////////////////////////////////处理强制下线逻辑
       Map<String, OnLineUserInfo> onLineUserMap = UserOnlineThread.onlineusersinfo; 
        if(onLineUserMap.containsKey(username)){
        
        	/*System.err.println("成败在此一举 该账号已经登录啦！！！！！！！！！！！！！！！！！！！！我要踢掉老账号啦 ");       
        	oauthAccessTokenDao.deleteByName(username); */
        	//haslogin = true ;
        	String loginInfo = (String) UserOnlineThread.visitedusers.get(username);
        	if(loginInfo!=null){
        		 loginInfo = loginInfo.split(",")[0]+","+true;
        		 UserOnlineThread.visitedusers.put(username, loginInfo);
        	}
   	
        }
        
        return new UserRepositoryOAuthUserDetails(userInfo);
    }

    private final static class UserRepositoryOAuthUserDetails extends UserInfo implements UserDetails {

        private static final long serialVersionUID = 1L;

        public static RoleDao roleRepository;

        private UserRepositoryOAuthUserDetails(UserInfo userInfo) {
            super(userInfo);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            String[] roleIds = this.getRoleIds().split(",");
            Collection<RoleInfo> authorities = new HashSet<RoleInfo>();
            for (int i = 0; i < roleIds.length; i++) {
                if ("".equals(roleIds[i])) {
                    continue;
                }
                long roleId = Long.valueOf(roleIds[i]);
                RoleInfo roleInfo = roleRepository.findOne(roleId);
                authorities.add(roleInfo);
            }
//            return getRoles();
            return authorities;
        }

        @Override
        public String getUsername() {
            return getLogin();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

}
