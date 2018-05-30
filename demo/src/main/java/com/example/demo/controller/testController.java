package com.example.demo.controller;

import com.example.demo.Util.ExcelExportUtil;
import com.example.demo.entity.StudentVo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Linzhi.Wang
 * @version V1.0
 * @Title: testController.java
 * @Package com.example.demo.controller
 * @Description
 * @date 2018 05-22 17:40.
 */
@Controller
public class testController {
	public void exportExcelTest() {


	List<String> listName = new ArrayList<>();
        listName.add("id");
		        listName.add("名字");
		        listName.add("性别");
	List<String> listId = new ArrayList<>();
        listId.add("id");
		        listId.add("name");
		        listId.add("sex");
	List<StudentVo> list = new ArrayList<>();
        list.add(new

	StudentVo(111,"张三asdf","男"));
			list.add(new

	StudentVo(111,"李四asd","男"));
			list.add(new

	StudentVo(111,"王五","女"));
		ExcelExportUtil<StudentVo> excelEx= new ExcelExportUtil<>();
		excelEx.exportExcel("测试POI导出EXCEL文档",listName,listId,list);

    }
}
