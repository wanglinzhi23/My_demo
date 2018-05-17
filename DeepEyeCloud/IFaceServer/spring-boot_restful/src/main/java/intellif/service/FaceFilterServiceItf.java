package intellif.service;

import intellif.dto.BatchUpdateFilteredFaceDto;
import intellif.dto.FilteredFaceQueryDto;
import intellif.exception.MsgException;
import intellif.utils.PageDto;
import intellif.database.entity.FaceFilterType;
import intellif.database.entity.FilteredFaceInfo;

import java.util.List;

public interface FaceFilterServiceItf {
    List<FaceFilterType> getFilterTypes();

    /**
     * 添加非人脸过滤
     * @param faceId - 人脸id
     * @param typeId - 过滤类型id
     * @throws MsgException
     */
    void addFilteredFace(Long faceId, Long typeId) throws MsgException;

    /**
     * 分页查询过滤的人脸
     * @param filteredFaceQueryDto
     */
    PageDto<FilteredFaceInfo> searchFilteredFace(FilteredFaceQueryDto filteredFaceQueryDto);

    /**
     * 批量更新人脸过滤的分类
     * @param updateDto
     */
    void batchUpdate(BatchUpdateFilteredFaceDto updateDto);

    /**
     * 批量删除人脸过滤
     * @param updateDto
     */
    void delete(BatchUpdateFilteredFaceDto updateDto);

    /**
     * 通知引擎单个人脸过滤
     * @param faceId
     */
    public void noticeEnginceOneFace(Long faceId);

    /**
     * 通知人擎所有人脸过滤
     */
    public void noticeEnginceAllFace();
}
