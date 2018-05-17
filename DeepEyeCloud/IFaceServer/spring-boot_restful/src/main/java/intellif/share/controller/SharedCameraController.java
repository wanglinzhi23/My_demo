package intellif.share.controller;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.share.service.ShareDistrictAreaCameraServiceItf;
import intellif.database.entity.CameraInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * <h1>The Class CameraController.</h1>
 * The CameraController which serves request of the form /share/camera
 * and returns a JSON object representing an instance of CameraInfo.
 * @author Zheng Xiaodong
 * @version 1.0
 * @since 2017-03-09
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_SHARED_CAMERA)
public class SharedCameraController {
    private static Logger LOG = LogManager.getLogger(SharedCameraController.class);

    @Autowired
    private ShareDistrictAreaCameraServiceItf dacService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of camera info that " +
            "is successfully get, including camera info from other districts.")
    public JsonObject list() {
        try {
            return new JsonObject(this.dacService.findAll(CameraInfo.class));
        } catch (Exception e) {
            LOG.error("get all areas error", e);
            return new JsonObject(e.getMessage(), 1001);
        }
    }

}
