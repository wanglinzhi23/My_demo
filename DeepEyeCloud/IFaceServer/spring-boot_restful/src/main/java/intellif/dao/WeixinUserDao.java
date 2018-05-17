package intellif.dao;

import intellif.database.entity.WeixinUser;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface WeixinUserDao extends CrudRepository<WeixinUser, Long> {
	 List<WeixinUser> findByUserName(String name);
	 List<WeixinUser> findByOpenId(String id);
}
