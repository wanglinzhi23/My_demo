package intellif.facecollision.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import intellif.consts.GlobalConsts;
import intellif.dao.UploadedFileDao;
import intellif.facecollision.dao.FaceExtractTaskDao;
import intellif.facecollision.service.FaceExtractServiceItf;
import intellif.facecollision.vo.FaceExtractTask;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.FaceServiceItf;
import intellif.service.IFaceSdkServiceItf;
import intellif.utils.PageDto;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.UploadedFile;

/**
 * @author Zheng Xiaodong
 */
@Service
public class FaceExtractServiceImpl implements FaceExtractServiceItf {
    private static Logger LOG = LogManager.getLogger(FaceExtractServiceImpl.class);

    @Autowired
    private FaceExtractTaskDao faceExtractTaskDao;
    @Autowired
    private UploadedFileDao uploadFileDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

    public FaceExtractTask createTask(Long fileId) {
        FaceExtractTask oldTask = faceExtractTaskDao.findByFileIdAndDeleted(fileId, false);
        if (oldTask != null)
            return oldTask;

        UploadedFile uploadedFile = uploadFileDao.findOne(fileId);
        FaceExtractTask newTask = new FaceExtractTask();
        newTask.setFileId(fileId);
        newTask.setFileType(fileTypeToTaskType(uploadedFile.getFileType()));
        newTask.setStatus(0);
        newTask.setTaskName(uploadedFile.getFileName());
        newTask.setArchiveUrl(uploadedFile.getFileUrl());
        newTask.setTotal(0L);
        newTask.setCurrent(0L);
        newTask.setDeleted(false);
        FaceExtractTask savedTask = faceExtractTaskDao.save(newTask);

        try {
            iFaceSdkServiceItf.getCenterServer().task_snaper_create(fileTypeToTaskType(uploadedFile.getFileType()),
                    newTask.getId());
        } catch (TException e) {
            LOG.error(e);
        }

        return savedTask;
    }

    public int deleteTask(Long taskId) {
        FaceExtractTask task = faceExtractTaskDao.findOne(taskId);
        try {
            iFaceSdkServiceItf.getCenterServer().task_snaper_terminate(fileTypeToTaskType(task.getFileType()), task.getId());
        } catch (TException e) {
            LOG.error(e);
        }

        String sql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_EXTRACT_TASK +
                " set deleted = 1 where id = ? ";
        // 设置任务为已删除
        try {
            jdbcTemplate.update(sql, taskId);  
        } catch (Exception e) {
            LOG.error(e);
        }
        
        return 0;
    }

    @Override
    public PageDto<FaceInfo> queryExtractedFaces(Long fileId, Integer page, Integer pageSize) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        FaceExtractTask task = faceExtractTaskDao.findByFileId(fileId);
        if(task==null)
            return null;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        startTime.setTime(task.getCreated());
        endTime.setTime(task.getUpdated());
        // 时间范围扩大5分钟，降低服务器时间不准的影响
        startTime.add(Calendar.MINUTE, -5);
        endTime.add(Calendar.MINUTE, 5);

        String startTimeStr = df.format(startTime.getTime());
        String endTimeStr = df.format(endTime.getTime());
        List<FaceInfo> faces = faceService.findBySourceId(task.getId(), startTimeStr, endTimeStr, page, pageSize);
        long count = faceService.countBySourceId(task.getId(), startTimeStr, endTimeStr);
        return new PageDto<FaceInfo>(faces, count, page, pageSize);
    }

    private int fileTypeToTaskType(int fileType) {
        int taskType;
        if (fileType == 0) {
            taskType = 1;
        } else {
            taskType = 10;
        }
        return taskType;
    }
}
