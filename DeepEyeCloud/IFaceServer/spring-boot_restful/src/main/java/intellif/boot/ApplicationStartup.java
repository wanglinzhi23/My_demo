package intellif.boot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import intellif.consts.GlobalConsts;
import intellif.dao.CameraInfoDao;
import intellif.dao.OtherCameraDao;
import intellif.dao.TableRecordDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.OtherCameraInfo;
import intellif.lire.CameraInfoThread;
import intellif.lire.CloudBlackIndexThread;
import intellif.service.ImageServiceItf;
import intellif.service.PoliceStationServiceItf;
import intellif.service.TableDivideServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.TableDivideSetting;
import intellif.database.entity.PoliceStation;
import intellif.database.entity.PoliceStationAuthority;
import intellif.database.entity.TableRecord;
import intellif.database.entity.SystemSwitch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger LOG = LogManager.getLogger(CloudBlackIndexThread.class);
    private final static String dateFormatHMS = GlobalConsts.YMDHMS;
    private final static String dateFormatYMD = GlobalConsts.YMD;
    private TableRecordDao tableRecordDao;
    private TableDivideServiceItf iTableDivideServiceItf;
    private CameraInfoDao _cameraInfoDao;
    private OtherCameraDao _otherCameraDao;
    @Autowired
    private UserServiceItf _userService;
    @Autowired
    private PoliceStationServiceItf policeStationService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String FIX_AUTHORITY = "FIX_AUTHORITY";
    private static final String CAMERA_TRANSFER = "CAMERA_TRANSFER";
    private static final String SYSTEM_SWITCH_QUERY_SQL = "select * from " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." + GlobalConsts.T_NAME_SYSTEM_SWITCH
            + " where opened = ? and switch_type = ?";

    private static final String SYSTEM_SWITCH_UPDATE_SQL = "update " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." + GlobalConsts.T_NAME_SYSTEM_SWITCH
            + " set opened = ? where id = ?";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        tableRecordDao = event.getApplicationContext().getBean(TableRecordDao.class);
        iTableDivideServiceItf = event.getApplicationContext().getBean(TableDivideServiceItf.class);
        _cameraInfoDao = event.getApplicationContext().getBean(CameraInfoDao.class);
        _otherCameraDao = event.getApplicationContext().getBean(OtherCameraDao.class);
        _userService = event.getApplicationContext().getBean(UserServiceItf.class);
        policeStationService = event.getApplicationContext().getBean(PoliceStationServiceItf.class);
        jdbcTemplate = event.getApplicationContext().getBean(JdbcTemplate.class);

        checkFaceTablesExist();
        initCamera();
        
        new Thread() {
            @Override
            public void run() {
                fixStationAuthority();
                camerasTransferToArea();
            }
        }.start();
    }

    public void initCamera() {
        LOG.info("startup init camera start");
        ConcurrentHashMap<Long, CameraInfo> cMap = new ConcurrentHashMap<Long, CameraInfo>();
        List<CameraInfo> cameraList = (List<CameraInfo>) _cameraInfoDao.findAll();
        List<OtherCameraInfo> otherList = (List<OtherCameraInfo>) _otherCameraDao.findAll();
        if (null != cameraList && !cameraList.isEmpty()) {
            for (CameraInfo ci : cameraList) {
                cMap.put(ci.getId(), ci);
            }

        }
        if (null != otherList && !otherList.isEmpty()) {
            for (OtherCameraInfo oci : otherList) {
                CameraInfo cii = new CameraInfo(oci);
                cMap.put(cii.getId(), cii);
            }

        }
        CameraInfoThread.cameraMap = cMap;
        LOG.info("startup init camera end");

    }

    public void checkFaceTablesExist() {
        try {
            long code = 1;
            Date startDate = null;
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            calendar.setTime(currentDate);
            calendar.add(Calendar.MONTH, 2);
            calendar.set(Calendar.DATE, 0);
            Date nextMonthDate = calendar.getTime();// 获取下一个月的最后一天
            // String nextMonthStr = dateFormatYMD.format(nextMonthDate);
            String nextMonthStr = DateUtil.formatDate(nextMonthDate, dateFormatYMD);

            TableRecord firsttable = tableRecordDao.findFirstOrderByTime();
            if (null == firsttable) {
                String startTime = TableDivideSetting.getTable_divide_starttime();// 运营环境开始时间
                // startDate = dateFormatHMS.parse(startTime);
                startDate = intellif.utils.DateUtil.getFormatDate(startTime, dateFormatHMS);
            } else {
                startDate = firsttable.getEndTime();
                code = firsttable.getTableCode() + 1;
            }
            // String startStr = dateFormatYMD.format(startDate);
            String startStr = DateUtil.formatDate(startDate, dateFormatYMD);
            if (!startStr.equals(nextMonthStr) && startDate.getTime() < nextMonthDate.getTime()) {
                // 需要分表到下个月末
                long startTimeStamp = startDate.getTime();
                // Date nmDate = dateFormatHMS.parse(nextMonthStr+" 23:59:59");
                Date nmDate = intellif.utils.DateUtil.getFormatDate(nextMonthStr + " 23:59:59", dateFormatHMS);
                long nextMonthStamp = nmDate.getTime();
                int step = TableDivideSetting.getTable_divide_size();
                do {
                    Calendar tCalendar = Calendar.getInstance();
                    tCalendar.setTimeInMillis(startTimeStamp);
                    Date tableStartTime = tCalendar.getTime();
                    tCalendar.setTimeInMillis(startTimeStamp + step * 24 * 60 * 60 * 1000l);
                    Date tableEndTime = tCalendar.getTime();
                    iTableDivideServiceItf.createTables(tableStartTime, tableEndTime, code);
                    startTimeStamp = startTimeStamp + step * 24 * 60 * 60 * 1000l;
                    code++;

                } while (startTimeStamp <= nextMonthStamp);

            }
        } catch (Exception e) {
            LOG.error("divide table error", e);
        }
    }

    /**
     * t_system_switch表中，switch_type为FIX_AUTHORITY的项，
     * 若数据库中开关为开，则执行，用于补全t_police_station_authority表，解决上级单位没有下级单位创建的库的编辑权限的问题
     */
    public void fixStationAuthority() {
        long stationId = 0L;
        long bankId;
        List<PoliceStation> forefathers = null;
        try {
            List<SystemSwitch> systemSwitchList = jdbcTemplate.query(SYSTEM_SWITCH_QUERY_SQL, new BeanPropertyRowMapper<SystemSwitch>(SystemSwitch.class),
                    true, FIX_AUTHORITY);
            if (!CollectionUtils.isEmpty(systemSwitchList)) {
                LOG.info("Started to fix t_police_station_authority table...");
                List<PoliceStationAuthority> authority = _userService.getAuthorityByOnlyType(GlobalConsts.CONTROL_AUTORITY_TYPE);
                for (PoliceStationAuthority policeStationAuthority : authority) {
                    if (stationId != policeStationAuthority.getStationId()) {
                        stationId = policeStationAuthority.getStationId();
                        forefathers = policeStationService.getForefathers(stationId);
                    }
                    bankId = policeStationAuthority.getBankId();
                    for (PoliceStation forefather : forefathers) {
                        _userService.createAuthorityOrIgnore(forefather.getId(), bankId);
                    }
                }
                jdbcTemplate.update(SYSTEM_SWITCH_UPDATE_SQL, false, systemSwitchList.get(0).getId());
                LOG.info("Finished to fix t_police_station_authority table...");
            }
        } catch (Exception e) {
            LOG.error("Failed to fix t_police_station_authority table", e);
        }
    }
    /**
     * 本方法用于：
     * 在t_carema_blackdetail表中，同一个blackdetail对应的多个camera如果涵盖了一个area中的所有camera，
     * 那么将这些数据从t_carema_blackdetail中删除，并在t_area_blackdetail表中加入对应数据
     */
    public void camerasTransferToArea() {
        try {
            List<SystemSwitch> systemSwitchList = jdbcTemplate.query(SYSTEM_SWITCH_QUERY_SQL, new BeanPropertyRowMapper<SystemSwitch>(SystemSwitch.class),
                    true, CAMERA_TRANSFER);
            if (!CollectionUtils.isEmpty(systemSwitchList)) {
                LOG.info("Started to transfer cameras to area...");
                String sql1 = "SELECT COUNT(*), station_id FROM " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." +
                        GlobalConsts.T_NAME_CAMERA_INFO + " GROUP BY station_id";
                String sql2 = "SELECT id, station_id FROM " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." +  
                        GlobalConsts.T_NAME_CAMERA_INFO;
                String sql3 = "SELECT DISTINCT blackdetail_id FROM " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." +
                        GlobalConsts.T_NAME_CAMERA_BLACKDETAIL;
                String sql4 = "SELECT DISTINCT camera_id FROM " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." +
                        GlobalConsts.T_NAME_CAMERA_BLACKDETAIL + " WHERE blackdetail_id = ?";
                String sql5 = "DELETE FROM " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." +
                        GlobalConsts.T_NAME_CAMERA_BLACKDETAIL + " WHERE blackdetail_id = ? AND camera_id " +  
                        " IN(SELECT id FROM " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." + 
                        GlobalConsts.T_NAME_CAMERA_INFO + " WHERE station_id = ?)";
                String sql6 = "INSERT INTO " + GlobalConsts.INTELLIF_AREA_AUTHORIZE + "." +  
                        GlobalConsts.T_NAME_AREA_BLACKDETAIL + " (blackdetail_id, area_id) VALUES (?, ?)";
                //key stationId value总数 
                Map<Long, Integer> cameraCount = jdbcTemplate.queryForObject(sql1, new RowMapper<Map<Long, Integer>>() {
                    @Override
                    public Map<Long, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Map<Long, Integer> map = new HashMap<Long, Integer>();
                        do {
                            map.put(rs.getLong(2), rs.getInt(1));
                        } while (rs.next());
                        return map;
                    }
                });
                //key cameraid value stationId
                Map<Long, Long> cameraList = jdbcTemplate.queryForObject(sql2, new RowMapper<Map<Long, Long>>() {
                    @Override
                    public Map<Long, Long> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Map<Long, Long> map = new HashMap<Long, Long>();
                            do {
                                map.put(rs.getLong(1), rs.getLong(2));
                            } while(rs.next());
                        return map;
                    }
                });
                // 1 2 3布控图像的id列表
                List<Long> blackdetailIdList = jdbcTemplate.queryForList(sql3, Long.class); 
                for (Long blackdetailId : blackdetailIdList) {
                    //1 2 3布控这个图像的摄像头列表
                    List<Long> cameraIdList = jdbcTemplate.queryForList(sql4, new Object[] {blackdetailId}, Long.class);
                    Map<Long, Integer> cameraCountCopy = new HashMap<Long, Integer>();
                    cameraCountCopy.putAll(cameraCount);
                    for (Long cameraId : cameraIdList) {
                        try {
                            Object stationIdObj = cameraList.get(cameraId);
                            if (null != stationIdObj) {
                                long stationId = (long)stationIdObj;
                                Object lastObj = cameraCountCopy.get(stationId);
                                if (null != lastObj) {
                                    int last =  (int)lastObj - 1;
                                    cameraCountCopy.put(stationId, last);
                                    if (last == 0) {
                                        jdbcTemplate.update(sql5, new Object[]{blackdetailId, stationId});
                                        jdbcTemplate.update(sql6, new Object[]{blackdetailId, stationId});
                                        cameraCountCopy.remove(stationId);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("Failed to check cameraId:" + cameraId + ",blackdetailId:" + blackdetailId, e);
                        }
                    }
                }
                jdbcTemplate.update(SYSTEM_SWITCH_UPDATE_SQL, false, systemSwitchList.get(0).getId());
                LOG.info("Finished to transfer cameras to area...");
            }
        } catch (EmptyResultDataAccessException e) {
            LOG.info("Can't find any camera here, stop transferring");
        } catch (Exception e) {
            LOG.error("Failed to transfer cameras to area", e);
        }
    }
}