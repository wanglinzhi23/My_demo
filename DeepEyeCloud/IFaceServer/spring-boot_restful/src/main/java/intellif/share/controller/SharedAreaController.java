package intellif.share.controller;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.share.service.ShareDistrictAreaCameraServiceItf;
import intellif.database.entity.Area;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_SHARED_AREA)
public class SharedAreaController {

    private static Logger LOG = LogManager.getLogger(SharedAreaController.class);

    @Autowired
    private ShareDistrictAreaCameraServiceItf dacService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取所有区域")
    public JsonObject list() {
        try {
            return new JsonObject(this.dacService.findAll(Area.class));
        } catch (Exception e) {
            LOG.error("get all areas error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

}
