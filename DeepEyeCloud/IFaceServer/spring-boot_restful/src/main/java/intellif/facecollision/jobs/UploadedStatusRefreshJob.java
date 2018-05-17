package intellif.facecollision.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.service.ResumableJsServiceItf;

/**
 * 文件上传状态修改定时任务
 * @author shijt
 *
 */
@Component
public class UploadedStatusRefreshJob {

    @Autowired
    private ResumableJsServiceItf uploadedStatusService;

    @Scheduled(fixedDelay = 3 * 1000)
    public void updateFileUploadStatus() {
        uploadedStatusService.updateTableBatch();
    }
}