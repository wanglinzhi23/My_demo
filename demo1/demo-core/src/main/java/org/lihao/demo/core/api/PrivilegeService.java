package org.lihao.demo.core.api;

import org.apache.ibatis.annotations.Param;
import org.lihao.demo.core.entity.User;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: PrivilegeServcie.java
 * @Package org.lihao.demo.api
 * @Description Privilege Service
 * @date 2018 04-08 16:36.
 */
public interface PrivilegeService {
	int insert(User user);

	int delete(@Param("id") Long id);

	int update(User user);

	List<User> listAll();

	User findById(@Param("id") Long id);
}
