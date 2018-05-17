package org.lihao.demo.controller;

import org.lihao.demo.api.FaceInfoService;
import org.lihao.demo.entity.FaceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: BusiController.java
 * @Package org.lihao.demo.controller
 * @Description 业务代码的控制器
 * @date 2018 04-09 19:23.
 */
@Controller
public class BusiController {

	@Autowired
	private FaceInfoService faceInfoService;

	@GetMapping("")
	public String index(){
		return "index";
	}

	@PostMapping("/faceInfo/insert")
	public @ResponseBody int insert(FaceInfo faceInfo){
		return faceInfoService.insert(faceInfo);
	}

	@PostMapping("/faceInfo/delete/{id:\\d+}")
	public @ResponseBody int delete(@PathVariable("id") Long id){
		return faceInfoService.delete(id);
	}

	@PostMapping("/faceInfo/update")
	public @ResponseBody int update(FaceInfo faceInfo){
		return faceInfoService.update(faceInfo);
	}

	@GetMapping("/faceInfo/all")
	public @ResponseBody List<FaceInfo> listAll(){
		return faceInfoService.listAll();
	}

	@GetMapping("/faceInfo/{id:\\d+}")
	public @ResponseBody FaceInfo getById(@PathVariable("id") Long id){
		return faceInfoService.getById(id);
	}

}
