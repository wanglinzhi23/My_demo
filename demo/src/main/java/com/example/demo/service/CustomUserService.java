package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: CustomUserService.java
 * @Package com.example.demo.service
 * @Description
 * @date 2018 05-10 17:13.
 */
@Service
public class CustomUserService implements UserDetailsService {
	@Autowired
	private UserDao userDao;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user = userDao.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名不存在");
		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		// 用于添加用户的权限。只要把用户权限添加到authorities 就万事大吉。
		for (SysRole role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}
}
