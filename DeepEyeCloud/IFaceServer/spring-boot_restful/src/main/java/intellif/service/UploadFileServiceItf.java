package intellif.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import intellif.dto.JsonObject;
import intellif.dto.UploadFileDto;
import intellif.database.entity.UploadedFile;
import intellif.database.entity.UploadedStatus;

public interface UploadFileServiceItf {

    /**
     * 分页查询文件列表
     * @param page
     * @param pagesize
     * @param uploadedFileDto
     * @return
     */
    public ArrayList<UploadedFile> findByPage(int page, int pagesize, UploadFileDto uploadedFileDto);

    /**
     * 删除文件
     * @param file
     * @return
     */
    public JsonObject deleteFile(UploadedFile file);

    /**
     * 获取文件解析状态
     * @param file
     */
    public void getFileExtractStatus(UploadedFile file);

    void getUploadStatus(UploadedFile file, HashMap<String, UploadedStatus> mHashMap);

    /**
     * 批量更新
     * @param fileQueue
     */
    public void updateTableBatch();

    /**
     * 更新等待打包的未删除文件的上传状态
     * @param id
     * @param status
     * @return
     */
    public int updateZipingStatus(long id, int status);
    /**
     * 更新某一字段
     */
    public int updateStatus(long id, int status);
    /**
     * 查找已上传完成但未打包的文件
     * @return
     */
    public List<UploadedFile> findAllUploadedButNotCreatedTaskFile();

    /**
     * 为上传的文件创建解析任务
     * @param uploadFile
     */
    public void createTaskStatus(UploadedFile uploadFile);

    
}