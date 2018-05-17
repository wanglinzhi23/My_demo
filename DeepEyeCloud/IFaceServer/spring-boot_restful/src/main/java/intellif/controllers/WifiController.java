package intellif.controllers;

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
import intellif.service.WifiAccessInfoServiceItf;

/*
 * created by 大运项目, yktangint
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_WIFI)
public class WifiController {

	private static Logger LOG = LogManager.getLogger(WifiController.class);
	
	@Autowired
	private WifiAccessInfoServiceItf wifiAccessInfoServiceItf; 

	@RequestMapping(value = "/countDevice/starttime/{starttime}/endtime/{endtime}", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "返回连接Wifi的设备数量")
	public JsonObject countWifiDevice(@PathVariable("starttime") String starttime, @PathVariable("endtime") String endtime) {
		return new JsonObject(wifiAccessInfoServiceItf.countTotalAccessDevice(starttime, endtime));
	}
}
