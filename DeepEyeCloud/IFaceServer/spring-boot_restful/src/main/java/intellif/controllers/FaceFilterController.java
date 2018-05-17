package intellif.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import intellif.consts.GlobalConsts;
import intellif.dto.BatchUpdateFilteredFaceDto;
import intellif.dto.FilteredFaceQueryDto;
import intellif.dto.JsonObject;
import intellif.exception.MsgException;
import intellif.service.FaceFilterServiceItf;
import intellif.utils.PageDto;
import intellif.database.entity.FaceFilterType;
import intellif.database.entity.FilteredFaceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 非人脸图片过滤
 * @author Zheng Xiaodong
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_FACE_FILTER)
public class FaceFilterController {
    @Autowired
    private FaceFilterServiceItf faceFilterService;

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "查询所有非人脸过滤类型")
    public JsonObject getFilterTypes() {
        List<FaceFilterType> types = faceFilterService.getFilterTypes();
        return new JsonObject(types);
    }

    @RequestMapping(value = "/type/{typeId}/face/{faceId}", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "添加非人脸过滤")
    public JsonObject addFilteredFace(@PathVariable("faceId") Long faceId, @PathVariable("typeId") Long typeId) {
        try {
            faceFilterService.addFilteredFace(faceId, typeId);
            faceFilterService.noticeEnginceOneFace(faceId);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "分页按条件搜索过滤的人脸")
    public JsonObject searchFilteredFace(@RequestBody FilteredFaceQueryDto filteredFaceQueryDto) {
        PageDto<FilteredFaceInfo> data = null;
        try {
            data = faceFilterService.searchFilteredFace(filteredFaceQueryDto);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        return new JsonObject(data.getData(), 0, data.getMaxPages(), (int) data.getCount());
    }

    @RequestMapping(value = "/type/batchUpdate", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "批量修改过滤的人脸分类")
    public JsonObject batchUpdate(@RequestBody BatchUpdateFilteredFaceDto updateDto) {
        try {
            faceFilterService.batchUpdate(updateDto);
            faceFilterService.noticeEnginceAllFace();
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

    @RequestMapping(value = "batchDelete", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "删除过滤的人脸分类")
    public JsonObject delete(@RequestBody BatchUpdateFilteredFaceDto updateDto) {
        try {
            faceFilterService.delete(updateDto);
            faceFilterService.noticeEnginceAllFace();
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(),1001);
        }
        return new JsonObject(new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK));
    }

}
