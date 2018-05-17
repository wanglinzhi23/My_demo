package intellif.lire;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import intellif.consts.GlobalConsts;
import intellif.settings.XinYiSettings;
import intellif.utils.JinxinUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class QiangdanSyncThread extends Thread{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static Logger LOG = LogManager.getLogger(QiangdanSyncThread.class);
    //每分钟的第30秒触发一次
   // @Scheduled(cron = "30 * * * * ?")
    public void run() {
        try {
            /*String result = "[{\"cameraLens\": \"111\",\"policeNoList\": \"00123,0456,789\"}," +
                            "{\"cameraLens\": \"222\",\"policeNoList\": \"123\"}," +
                            "{\"cameraLens\": \"333\",\"policeNoList\": \"12345\"}]";*/
            String result = JinxinUtil.caller(XinYiSettings.getQiangdanSyncUrl(), null);
            if (!result.equals("failed")) {
                String sql1 = "SELECT MAX(id) FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_QIANGDAN_CAMERA_POLICE;
                Long maxID = jdbcTemplate.queryForObject(sql1, Long.class);
                JSONArray jsonArray = JSONArray.fromObject(result);
                String sql2 = "INSERT INTO " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_QIANGDAN_CAMERA_POLICE + 
                        "(camera_id, police_no, create_time, endtime) value (?, ?, NOW(), ?)";
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String targetTime = df.format(new Date()) + " 23:59:59";
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject)object;
                    String cameraLens = jsonObject.getString("cameraLens");
                    String policeNoList = jsonObject.getString("policeNoList");
                    String[] poNList = policeNoList.split(",");
                    for (String policeNo : poNList) {
                        jdbcTemplate.update(sql2, Long.parseLong(cameraLens), policeNo, targetTime);
                    }
                }
                if (null != maxID) {
                    String sql3 = "DELETE FROM " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_QIANGDAN_CAMERA_POLICE + " WHERE id <=" + maxID;
                    jdbcTemplate.execute(sql3);
                }
            } else {
                LOG.info("请求失败，未能得到信义返回的消息。");
            }
        } catch (Exception e) {
            LOG.error("同步信义枪弹库数据失败", e);
        }
    }
}