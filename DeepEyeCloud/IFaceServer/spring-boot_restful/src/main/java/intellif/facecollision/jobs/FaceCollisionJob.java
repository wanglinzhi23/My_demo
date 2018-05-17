package intellif.facecollision.jobs;

import intellif.consts.GlobalConsts;
import intellif.facecollision.request.FaceCollisionParam;
import intellif.facecollision.request.FaceCollisionParamItem;
import intellif.facecollision.service.FaceCollisionServiceItf;
import intellif.facecollision.service.FaceExtractServiceItf;
import intellif.facecollision.vo.FaceCollisionTask;
import intellif.facecollision.vo.FaceExtractTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zheng Xiaodong
 * 人脸碰撞定时任务
 */
@Component
public class FaceCollisionJob {
    private static Logger LOG = LogManager.getLogger(FaceCollisionJob.class);

    @Autowired
    private FaceCollisionServiceItf faceCollisionService;

    @Autowired
    private FaceExtractServiceItf faceExtractService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 相关文件人脸解析完成后执行任务
     */
    @Scheduled(fixedRate = 5 * 1000)
    public void monitorFaceExtractState() {
    
        String sql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK +
                " where status = 0";
        List<FaceCollisionTask> tasks = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FaceCollisionTask.class));
        if (tasks == null)
            return;

        for (FaceCollisionTask task : tasks) {
            String taskParamStr = task.getTaskParam();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            FaceCollisionParam taskParam = null;
            try {
                taskParam = mapper.readValue(taskParamStr, FaceCollisionParam.class);
            } catch (IOException e) {
                LOG.error(e);
            }

            List<FaceCollisionParamItem> items = new ArrayList<>();
            items.addAll(taskParam.getSources());
            items.addAll(taskParam.getTargets());
            List<Long> ids = new ArrayList<>();

            for (FaceCollisionParamItem item : items) {
                // 视频或压缩包
                if (item.getType() == 0 || item.getType() == 1 || item.getType() == 2)
                    ids.add(item.getId());
            }

            String extractTaskSql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_EXTRACT_TASK +
                    " where file_id in (" + StringUtils.join(ids, ",") + ")";
            List<FaceExtractTask> extractTasks = jdbcTemplate.query(extractTaskSql, new BeanPropertyRowMapper<>(FaceExtractTask.class));
            if (extractTasks.size() != ids.size()) {
                // 任务异常 TODO: zxd
            }

            boolean extractFinished = true;
            for (FaceExtractTask et : extractTasks) {
                if (et.getStatus() != 2) {
                    extractFinished = false;
                    break;
                }
            }
            if (taskParam.getMode().equals(0) || extractFinished) {
                new Thread(() -> faceCollisionService.startTask(task)).start();
            }
        }
    }

//    @Scheduled(fixedRate = 100000)
    public void test() {
        boolean flag = true;
        Long fileId = 170L;
        if (flag) {
            faceExtractService.deleteTask(170L);
        }
    }
}
