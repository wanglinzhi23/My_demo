package intellif.facecollision.service;

import intellif.facecollision.vo.FaceExtractTask;
import intellif.utils.PageDto;
import intellif.database.entity.FaceInfo;

/**
 * @author Zheng Xiaodong
 */
public interface FaceExtractServiceItf {
    /**
     * 创建人脸解析任务
     * @param fileId - 文件id
     */
    FaceExtractTask createTask(Long fileId);

    /**
     * 删除人脸解析任务
     * @param taskId - 任务id
     */
    int deleteTask(Long taskId);

    /**
     * 查询文件解析出来的人脸
     */
    PageDto<FaceInfo> queryExtractedFaces(Long fileId, Integer page, Integer pageSize);
}
