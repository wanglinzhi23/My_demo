package intellif.zoneauthorize.controller;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.zoneauthorize.dao.SystemSwitchDao;
import intellif.database.entity.SystemSwitch;

@RestController
@RequestMapping(GlobalConsts.R_ID_SYSTEM_SWITCH)
public class SystemSwitchController {
	
	private static Logger LOG = LogManager.getLogger(SystemSwitchController.class);
	
	@Autowired
	private SystemSwitchDao dao;
	
	/**
	 * 根据系统开关类型查询开关的值
	 * @param switchType
	 * @return
	 */
    @RequestMapping(value = "/type/{switchtype}", method = RequestMethod.GET)//
    @ApiOperation(httpMethod = "GET", value = "根据系统开关类型查询开关的值")
    public JsonObject isOpen(@PathVariable("switchtype") String switchType) {
    	Validate.notBlank(switchType, "开关类型不能为空！");
    	SystemSwitch systemSwitch = dao.findOneBySwitchType(switchType.trim());
    	if (null == systemSwitch || null == systemSwitch.getOpened()) {
    		return new JsonObject(false);
    	}
    	return new JsonObject(systemSwitch.getOpened());
    }

}
