package intellif.facecollision.service;

import intellif.facecollision.dto.FaceCollisionQueryParam;
import intellif.facecollision.dto.FaceCollisionTaskDto;
import intellif.facecollision.request.FaceCollisionParam;
import intellif.facecollision.request.FaceCollisionTaskRequest;
import intellif.facecollision.vo.FaceCollisionResult;
import intellif.facecollision.vo.FaceCollisionTask;
import intellif.utils.PageDto;
import intellif.database.entity.FaceInfo;

import java.util.List;

/**
 * @author Zheng Xiaodong
 */
public interface FaceCollisionServiceItf {
    /**
     * 创建人脸碰撞任务
     * @param faceCollisionParam - 任务请求参数
     */
    FaceCollisionTask createTask(FaceCollisionParam faceCollisionParam);

    /**
     * 删除任务，同时删除对应的人脸
     * @param taskId - 任务id
     */
    int deleteTask(Long taskId);

    /**
     * 分页查询当前用户的人脸碰撞任务
     * @return 任务列表
     */
    PageDto<FaceCollisionTask> queryUserTasks(int page, int pageSize);

    /**
     * 开始人脸碰撞任务
     */
    void startTask(FaceCollisionTask task);

    /**
     * 查询碰撞目标人物的人脸列表
     */
    PageDto<FaceInfo> queryTargetFaces(long taskId, long personId, int listType, int page, int pageSize);

    /**
     * 查询人脸碰撞任务结果
     * @param taskId
     * @param faceCount
     * @param page
     * @param pageSize
     * @return
     */
    PageDto<FaceCollisionResult> getTaskResult(FaceCollisionQueryParam param);


    /**
     * 查询人脸碰撞任务详情
     * @param taskId
     * @return
     */
    FaceCollisionTaskDto getTaskDetail(Long taskId);

    /**
     * 根据id查询人脸碰撞任务进度
     * @param userId
     * @param ids
     * @return
     */
    List<FaceCollisionTask> queryUserTasksByIds(Long userId, String ids);

    /**
     * 查询正在运行的人脸碰撞任务进度
     * @param taskId
     * @return
     */
    double getTaskProgress(Long taskId);

    /**
     * 删除碰撞结果中的人脸
     * @param taskId
     * @param faceIds
     */
    void deleteResultFaces(Long taskId, Long personFaceId, String faceIds, int listType);
}
