package intellif.zoneauthorize.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.zoneauthorize.bean.ZoneQuery;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

@RestController
@RequestMapping(GlobalConsts.R_ID_ZONE)
public class ZoneController {
    
    private static Logger LOG = LogManager.getLogger(ZoneController.class);
    
    @Autowired
    private ZoneAuthorizeServiceItf zoneAuthorizeService;
    
    /**
     * 根据系统开关类型查询开关的值
     * @param switchType
     * @return
     */
    @RequestMapping(value = "/child", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "查询下级区域列表")
    public JsonObject child(@RequestBody ZoneQuery zoneQuery) {
        return new JsonObject(zoneAuthorizeService.child(zoneQuery));
    }

    /**
     * 根据系统开关类型查询开关的值
     * @param switchType
     * @return
     */
    @RequestMapping(value = "/forefather", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "查询上级区域列表")
    public JsonObject forefather(@RequestBody ZoneQuery zoneQuery) {
        return new JsonObject(zoneAuthorizeService.forefatherList(zoneQuery));
    }
}
