package org.lihao.demo.core.service;

import org.lihao.demo.core.api.PrivilegeService;
import org.lihao.demo.core.entity.User;
import org.lihao.demo.core.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: PrivilegeServiceImpl.java
 * @Package org.lihao.demo.service
 * @Description Privilege Service Implements
 * @date 2018 04-08 16:37.
 */
@Service
public class PrivilegeServiceImpl implements PrivilegeService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public int insert(User user) {
		return userMapper.insert(user);
	}

	@Override
	public int delete(Long id) {
		return userMapper.delete(id);
	}

	@Override
	public int update(User user) {
		return userMapper.update(user);
	}

	@Override
	public List<User> listAll() {
		return userMapper.listAll();
	}

	@Override
	public User findById(Long id) {
		return userMapper.findById(id);
	}
}
