package org.lihao.demo.entity;

import java.util.Date;

/**
 * @author Lihao
 * @version V1.0
 * @Title: FaceInfo.java
 * @Package org.lihao.demo.entity
 * @Description Face Information
 * @date 2018 04-09 19:30.
 */
public class FaceInfo {
	private long id;
	private String path;
	private String fileName;
	private Date createTime;
	private int gender;
	private int age;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
