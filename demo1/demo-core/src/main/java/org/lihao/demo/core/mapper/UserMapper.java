package org.lihao.demo.core.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lihao.demo.core.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: UserMapper.java
 * @Package org.lihao.demo.mapper
 * @Description 用户表Mapper对象
 * @date 2018 04-08 16:16.
 */
@Repository
@Mapper
public interface UserMapper {

	int insert(User user);

	int delete(@Param("id") Long id);

	int update(User user);

	List<User> listAll();

	User findById(@Param("id") Long id);
}
