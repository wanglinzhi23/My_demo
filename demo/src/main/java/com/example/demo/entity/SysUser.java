package com.example.demo.entity;

import java.util.List;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: SysUser.java
 * @Package com.example.demo.entity
 * @Description
 * @date 2018 05-10 17:01.
 */
public class SysUser {
	private Integer id;
	private String username;
	private String password;
	private String realName;
	private String phone;
	private String email;
	private List<SysRole> roles;

	public List<SysRole> getRoles() {
		return roles;
	}

	public void setRoles(List<SysRole> roles) {
		this.roles = roles;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
