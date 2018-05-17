package org.lihao.demo.core.controller;

import org.lihao.demo.core.api.PrivilegeService;
import org.lihao.demo.core.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/demo")
public class PrivilegeController {

	@Autowired
	private PrivilegeService privilegeService;

	@PostMapping("insert")
	public @ResponseBody Integer insert(User user, Map<String, Object> model) {
		return privilegeService.insert(user);
	}

	@PostMapping("delete")
	public  @ResponseBody Integer delete(Long id) {
		return privilegeService.delete(id);
	}

	@PostMapping("update")
	public  @ResponseBody Integer update(User user) {
		return privilegeService.update(user);
	}

	@GetMapping("listAll")
	public @ResponseBody List<User> listAll() {
		return privilegeService.listAll();
	}

	@GetMapping("findById")
	public @ResponseBody User findById(Long id) {
		return privilegeService.findById(id);
	}
}
