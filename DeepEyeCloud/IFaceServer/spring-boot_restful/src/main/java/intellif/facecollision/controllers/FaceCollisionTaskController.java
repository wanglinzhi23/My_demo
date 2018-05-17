package intellif.facecollision.controllers;

import com.wordnik.swagger.annotations.ApiOperation;

import intellif.consts.GlobalConsts;
import intellif.dto.JsonObject;
import intellif.exception.MsgException;
import intellif.facecollision.dto.FaceCollisionQueryParam;
import intellif.facecollision.dto.FaceCollisionTaskDto;
import intellif.facecollision.request.FaceCollisionParam;
import intellif.facecollision.request.FaceCollisionResultRequest;
import intellif.facecollision.request.FaceCollisionTaskRequest;
import intellif.facecollision.request.TargetFaceRequest;
import intellif.facecollision.service.FaceCollisionServiceItf;
import intellif.facecollision.vo.FaceCollisionResult;
import intellif.facecollision.vo.FaceCollisionTask;
import intellif.utils.PageDto;
import intellif.database.entity.FaceInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

import java.util.List;

/**
 * 人脸碰撞任务管理
 * @author Zheng Xiaodong
 */
@RestController
@RequestMapping(GlobalConsts.R_ID_FACE_COLLISION_TASK)
public class FaceCollisionTaskController {
    @Autowired
    private FaceCollisionServiceItf faceCollisionService;

    @RequestMapping(value = "/tasks", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "create face collision task")
    public JsonObject createTask(@RequestBody FaceCollisionParam request) {
        FaceCollisionTask task = null;
        try {
            task = faceCollisionService.createTask(request);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(task);
    }

    @RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "query face collision task detail")
    public JsonObject getTaskDetail(@PathVariable("taskId") Long taskId) {
        FaceCollisionTaskDto taskDto = null;
        try {
            taskDto=faceCollisionService.getTaskDetail(taskId);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(taskDto);
    }

    @RequestMapping(value = "/tasks/result/query", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "query face collision task result")
    public JsonObject getTaskResult(@RequestBody FaceCollisionQueryParam param) {
        PageDto<FaceCollisionResult> result;
        try {
            result = faceCollisionService.getTaskResult(param);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(result.getData(), 0, result.getMaxPages(), (int) result.getCount());
    }


    @RequestMapping(value = "/tasks/result/targetFaces", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "query face collision task result target faces")
    public JsonObject getTargetFaces(@RequestBody TargetFaceRequest targetFaceRequest) {
        PageDto<FaceInfo> faces;
        try {
            faces = faceCollisionService.queryTargetFaces(targetFaceRequest.getTaskId(), targetFaceRequest.getPersonId(),
                    targetFaceRequest.getListType(), targetFaceRequest.getPage(), targetFaceRequest.getPageSize());
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(faces.getData(), 0, faces.getMaxPages(), (int) faces.getCount());
    }

    @RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "delete face collision task")
    public JsonObject deleteTask(@PathVariable("taskId") Long taskId) {
        FaceCollisionTask task = null;
        try {
            faceCollisionService.deleteTask(taskId);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(task);
    }

    @RequestMapping(value = "/tasks/page/{page}/pagesize/{pagesize}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "query my face collision tasks")
    public JsonObject queryUserTasks(@PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {
        PageDto<FaceCollisionTask> tasks = null;
        try {
            tasks = faceCollisionService.queryUserTasks(page, pagesize);
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(tasks.getData(), 0, tasks.getMaxPages(), (int) tasks.getCount());
    }

    @RequestMapping(value = "/tasks/ids", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "query my face collision task by ids")
    public JsonObject queryUserTasksByIds(@RequestBody FaceCollisionTaskRequest request) {
        List<FaceCollisionTask> tasks = null;
        try {
            tasks = faceCollisionService.queryUserTasksByIds(request.getUserId(), request.getIds());
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject(tasks);
    }

    @RequestMapping(value = "/tasks/result/deleteFace", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "delete task collusion result pics")
    public JsonObject deleteResultFaces(@RequestBody FaceCollisionResultRequest request) {
        try {
            faceCollisionService.deleteResultFaces(request.getTaskId(), request.getPersonFaceId(), request.getFaceIds(), request.getListType());
        } catch (MsgException e) {
            return new JsonObject(e.getMessage(), 1001);
        }
        return new JsonObject("ok");
    }
}
