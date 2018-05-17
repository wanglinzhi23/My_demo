package intellif.share.controller;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.share.service.ShareDistrictAreaCameraServiceItf;
import intellif.database.entity.Area;
import intellif.database.entity.DistrictInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(GlobalConsts.R_ID_DISTRICT)
public class DistrictController {

    private static Logger LOG = LogManager.getLogger(DistrictController.class);

    @Autowired
    private ShareDistrictAreaCameraServiceItf dacService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取所有行政区域")
    public JsonObject list() {
        try {
            return new JsonObject(dacService.findAll(DistrictInfo.class));
        } catch (Exception e) {
            LOG.error("get all areas error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }
}
