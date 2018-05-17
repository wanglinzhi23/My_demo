package org.lihao.demo.core;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lihao.demo.core.entity.User;
import org.lihao.demo.core.mapper.UserMapper;
import org.lihao.demo.core.util.NameUtil;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: TestUserMapper.java
 * @Package org.lihao.demo
 * @Description UserMapper Test
 * @date 2018 04-08 18:27.
 */
@MybatisTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoCoreApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebMvc
public class TestUserMapper {

	@Autowired
	private UserMapper userMapper;

	@Test
	public void insertTest(){
		User user = new User();
		user.setUsername("xiaoming");
		user.setNickname(NameUtil.getRandomName());
		user.setPassword("********");
		user.setModifierId(0L);
		user.setModifierName("admin");
		user.setModifyTime(new Date());
		int count = userMapper.insert(user);
		Assertions.assertThat(count>0);

	}

	@Test
	public void deleteTest(){
		int count = userMapper.delete(1L);
		Assertions.assertThat(count>0);
	}

	@Test
	public void findByIdTest(){
		User user = userMapper.findById(2L);
		Assertions.assertThat(user!=null);
	}

	@Test
	public void listAllTest(){
		List<User> users = userMapper.listAll();
		Assertions.assertThat(users!=null && users.size()>0);
	}

	@Test
	public void updateTest(){
		User user = new User();
		user.setId(2L);
		user.setNickname(NameUtil.getRandomName());
		user.setPassword("++++++");
		user.setModifierId(0L);
		user.setModifierName("admin");
		user.setModifyTime(new Date());
		int count = userMapper.update(user);
		Assertions.assertThat(count>0);
	}
}
