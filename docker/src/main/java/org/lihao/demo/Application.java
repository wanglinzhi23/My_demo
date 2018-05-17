package org.lihao.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

	@RequestMapping(value = "/export/excel", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView exportExcel() {
		try {
			String[] showName = new String[]{"用户名", "密码", "昵称", "修改时间", "修改人员名称"};
			String[] fieldName = new String[]{"username", "password", "nickname", "modifyTime", "modifierName"};

			List<User> list = new ArrayList<User>();
			User user = new User();
			user.setUsername("xiaoming");
			user.setNickname("小明");
			user.setPassword("000000");
			user.setModifierId(0L);
			user.setModifierName("admin");
			user.setModifyTime(new Date());
			list.add(user);
			list.add(user);
			return new ModelAndView(new ExcelView<User>("用户数据", list, showName, fieldName), new HashMap<String, Object>());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}