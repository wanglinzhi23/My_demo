package intellif.facecollision.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.exception.MsgException;
import intellif.facecollision.service.FaceExtractServiceItf;
import intellif.utils.PageDto;
import intellif.database.entity.FaceInfo;

/**
 * 视频和zip的人脸解析
 * @author Zheng Xiaodong
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_FACE_EXTRACT)
public class FaceExtractTaskController {
    @Autowired
    private FaceExtractServiceItf faceExtractService;

    @RequestMapping(value = "/file/{fileId}/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "query face extract result")
    public JsonObject queryExtractedFaces(@PathVariable Long fileId, @PathVariable Integer page, @PathVariable Integer pagesize) {
        PageDto<FaceInfo> faces = null;
        try {
            faces = faceExtractService.queryExtractedFaces(fileId, page, pagesize);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        if(faces==null)
            return new JsonObject("解析任务不存在！",1001);
        return new JsonObject(faces.getData(), 0, faces.getMaxPages(), (int) faces.getCount());
    }
}
