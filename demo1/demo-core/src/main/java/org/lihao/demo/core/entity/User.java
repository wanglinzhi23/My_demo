package org.lihao.demo.core.entity;

import java.util.Date;

/**
 * @author Lihao
 * @version V1.0
 * @Title: User.java
 * @Package org.lihao.demo.entity
 * @Description User Entity
 * @date 2018 04-08 16:27.
 */
public class User {
	/**
	 * 主键
	 */
	private Long id;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 修改时间
	 */
	private Date modifyTime;
	/**
	 * 修改人员ID
	 */
	private Long modifierId;
	/**
	 * 修改人员名称
	 */
	private String modifierName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	public String getModifierName() {
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}
}
