package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.core.tree.Tree;
import intellif.core.tree.itf.TreeNode;
import intellif.dao.UserDao;
import intellif.service.FaceServiceItf;
import intellif.settings.JinxinSetting;
import intellif.utils.DateUtil;
import intellif.utils.JinxinUtil;
import intellif.database.entity.Area;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.UserInfo;
import intellif.zoneauthorize.common.LocalCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


@Component
public class JinxinAlarmThread extends Thread {

    private static Logger LOG = LogManager.getLogger(JinxinAlarmThread.class);
    private static String pIds = null;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    private UserDao userDao;
    
    @SuppressWarnings({ "rawtypes" })
   // @Scheduled(fixedDelay = 60000)
    public void run() {
        try {
            if (!JinxinSetting.run || StringUtil.isEmpty(JinxinSetting.getpIds())) {
                return;
            }
            Tree tree = LocalCache.tree;
            if (tree == null) {
                return;
            }
            if (null == pIds) {
                List<TreeNode> tList = new ArrayList<TreeNode>();
                for (String item : JinxinSetting.getpIds().split(",")) {
                    List<TreeNode> treeList = tree.offspringList(Area.class, Long.valueOf(item), Area.class, true);
                    if (!CollectionUtils.isEmpty(treeList)) {
                        tList.addAll(treeList);
                    }
                }
                if (!CollectionUtils.isEmpty(tList)) {
                    List<Long> curList = tList.stream().map(s -> s.getId()).collect(Collectors.toList());
                    pIds = StringUtils.join(curList, ",");
                }
            }

            if (null == pIds) {
                LOG.error("parse jinxin.alarm.areaIds error, areaIds:" + JinxinSetting.getpIds());
            }

            Date now = new Date();

            String dateStr = DateUtil.getDateString(now);
            String sql = "SELECT a.id,a.face_id,a.time,t.source_id,u.login,p.real_name from " + GlobalConsts.INTELLIF_BASE + "."
                    + GlobalConsts.T_NAME_ALARM_INFO + " a LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_BLACK_DETAIL
                    + " b on a.black_id = b.id LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_PERSON_DETAIL
                    + " p on b.from_person_id = p.id " + " LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_TASK_INFO
                    + " t on a.task_id = t.id " + " LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_USER + " u  on u.name = p.owner "
                    + " LEFT JOIN " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO + " c  on c.id = t.source_id " + " LEFT JOIN "
                    + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_AREA + " area  on c.station_id = area.id " + " where  a.time >'" + dateStr
                    + "' and a.confidence >" + JinxinSetting.getConfidence() + " and a.send = 0 " + " and area.id in(" + pIds
                    + ") group by a.id order by a.time desc limit 0,100";

            List<Map<String, Object>> lists = jdbcTemplate.queryForList(sql);

            if (!CollectionUtils.isEmpty(lists)) {
                List<Long> idList = new ArrayList<Long>();
                for (Map itemMap : lists) {
                    Long cId = (Long) itemMap.get("source_id");
                    Long fId = (Long) itemMap.get("face_id");
                    String login = (String) itemMap.get("login");
                    Date time = (Date) itemMap.get("time");
                    String realName = (String) itemMap.get("real_name");
                    Long id = (Long) itemMap.get("id");
                    try {
                        CameraInfo ci = tree.treeNodeWithOutTreeInfo(CameraInfo.class, cId);
                        FaceInfo fi = this.faceService.findOne(fId);
                        String iUrl = fi.getImageData();
                        String cName = ci.getName();
                    JinxinUtil.sendJinxinAlarmMessage(login, iUrl, DateUtil.getformatDate(time), cName, realName);
                    idList.add(id);
                    }catch(Exception e){
                        LOG.error("send jinxin alarm item error,phone:" + login + ",alarmId:" + id + ",e:", e);
                    }
                }
                if (!CollectionUtils.isEmpty(idList)) {
                    String idStr = StringUtils.join(idList, ",");
                    String updateSql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_ALARM_INFO + " set send = 1 where id in(" + idStr
                            + ")";
                    jdbcTemplate.update(updateSql);
                }
            }

        } catch (Exception e) {
            LOG.info("send jinxin alarm error:", e);
        }
    }

}