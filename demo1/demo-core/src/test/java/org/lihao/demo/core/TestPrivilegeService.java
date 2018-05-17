package org.lihao.demo.core;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lihao.demo.core.api.PrivilegeService;
import org.lihao.demo.core.entity.User;
import org.lihao.demo.core.util.NameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Lihao
 * @version V1.0
 * @Title: TestUserService.java
 * @Package org.lihao
 * @Description User Service Test
 * @date 2018 04-08 18:19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoCoreApplication.class)
public class TestPrivilegeService {

	@Autowired
	private PrivilegeService privilegeService;

	@Test
	public void insertTest(){
		User user = new User();
		user.setUsername("xiaoming"+new Random().nextInt(1000));
		user.setNickname(NameUtil.getRandomName());
		user.setPassword("********");
		user.setModifierId(0L);
		user.setModifierName("admin");
		user.setModifyTime(new Date());
		int count = privilegeService.insert(user);
		Assertions.assertThat(count>0);
	}


	@Test
	public void deleteTest(){
		int count = privilegeService.delete(1L);
		Assertions.assertThat(count>0);
	}

	@Test
	public void findByIdTest(){
		User user = privilegeService.findById(2L);
		Assertions.assertThat(user!=null);
	}

	@Test
	public void listAllTest(){
		List<User> users = privilegeService.listAll();
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
		int count = privilegeService.update(user);
		Assertions.assertThat(count>0);
	}
}
