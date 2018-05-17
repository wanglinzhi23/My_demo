package com.example.demo.dao;

import com.example.demo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: UserDaoMapper.java
 * @Package com.example.demo.dao
 * @Description
 * @date 2018 05-10 17:21.
 */
//@Repository
@Mapper
public interface UserDao {
	 SysUser findByUserName(String username);
}
