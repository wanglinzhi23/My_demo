package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dto.FaceStreamRequest;
import intellif.dto.JsonObject;
import intellif.exception.MsgException;
import intellif.service.FaceStreamServiceItf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zheng Xiaodong
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_FACE_STREAM)
public class FaceStreamController {
    
    @Autowired
    private FaceStreamServiceItf faceStreamService;
    
    private static Logger LOG = LogManager.getLogger(FaceStreamController.class);

    @RequestMapping(value = "/count", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "实时统计场馆人数")
    public JsonObject getFaceStreamCount(@RequestBody FaceStreamRequest request) {
        long count;
        try {
            count = faceStreamService.getRealTimeCount(request);
        } catch (MsgException e) {
            LOG.error("error",e);
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(count);
    }
    
    @RequestMapping(value = "/countByCameras", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "根据摄像头实时统计场馆人数")
    public JsonObject getFaceStreamCountByCameras(@RequestBody FaceStreamRequest request) {
        long count;
        try {
            count = faceStreamService.getFaceStreamCount(request);
        } catch (MsgException e) {
            LOG.error("error",e);
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(count);
    }

}
