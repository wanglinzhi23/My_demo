package intellif.share.job;

import intellif.chd.settings.MobileCollectSyncSetting;
import intellif.configs.PropertiesBean;
import intellif.consts.GlobalConsts;
import intellif.dao.MobileCollectSyncLogDao;
import intellif.dao.OtherInfoDao;
import intellif.dto.MobileCollectPersonDto;
import intellif.ifaas.EEnginIoctrlType;
import intellif.ifaas.EParamIoctrlType;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.service.IFaceSdkServiceItf;
import intellif.share.service.MobileCollectStationCacheItf;
import intellif.thrift.IFaceSdkTarget;
import intellif.utils.CommonUtil;
import intellif.utils.ImageInfoHelper;
import intellif.database.entity.MobileCollectSyncLog;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 同步移动人员采集库数据
 * @author Zheng Xiaodong
 */
@Component
public class StaticBankSyncJob {
    private static Logger LOG = LogManager.getLogger(StaticBankSyncJob.class);

    private static final String DOWN_PATH = "banksync/";

    private static final String LIST_FILE_NAME = "list.txt";

    private static final String IMG_STORE_PATH = "/var/www/html/mobilecollect/";

    @Autowired
    private OtherInfoDao otherInfoDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PropertiesBean propertiesBean;

    @Autowired
    private MobileCollectSyncLogDao mobileCollectSyncLogDao;

    @Autowired
    private MobileCollectStationCacheItf mobileCollectStationCache;

    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;

//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "${mobilecollect.cron}")
    public void sync() {
        processNew();
        processFailed();
    }

    // 处理新的同步数据
    private void processNew() {
        Map<String, String> lastSyncMap = getLastSyncPoint();
        String lastFileName = lastSyncMap.get("fileName");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar lastDate;

        try {
            lastDate = Calendar.getInstance();
            lastDate.setTime(df.parse(lastSyncMap.get("date")));
        } catch (ParseException e) {
            LOG.error(e);
            return;
        }
        while (isBeforeTomorrow(lastDate.getTime())) {
            processOneDay(lastDate.getTime(), lastFileName);
            lastDate.add(Calendar.DAY_OF_MONTH, 1);
            lastFileName = null;
        }
    }

    private void processOneDay(Date date, String beginAfter) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(date);
        List<String> entries;
        int i;

        downloadFile(dateStr + "/" + LIST_FILE_NAME);
        entries = parseListFile(DOWN_PATH + dateStr + "/" + LIST_FILE_NAME);
        i = entries.indexOf(beginAfter) + 1;
        while (i < entries.size()) {
            processOneZip(date, entries.get(i));
            i++;
        }
    }

    private void processOneZip(Date date, String zipFileName) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(date);

        downloadFile(dateStr + "/" + zipFileName);
        try {
            unzipToDir(DOWN_PATH + dateStr + "/" + zipFileName, "/images", IMG_STORE_PATH
                    + dateStr + "/" + zipFileName.substring(0, zipFileName.length() - 4) + "/images");
        } catch (IOException e) {
            LOG.error(e);
        }
        List<MobileCollectPersonDto> persons = parsePersons(dateStr + "/" + zipFileName);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(persons)) {
            processPersons(dateStr, zipFileName, persons);
            insertLog(dateStr, zipFileName, 1);
        }
        removeDownloadedFile(dateStr + "/" + zipFileName);
    }

    private void insertLog(String dateStr, String zipFileName, Integer syncStatus) {
        MobileCollectSyncLog log = new MobileCollectSyncLog();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            log.setFileDate(df.parse(dateStr));
        } catch (ParseException e) {
            LOG.error(e);
        }
        Date now = new Date();
        log.setFileName(zipFileName);
        log.setSyncStatus(syncStatus);
        log.setCreated(now);
        log.setUpdated(now);

        mobileCollectSyncLogDao.save(log);
    }

    private void processPersons(String dateStr, String zipFileName, List<MobileCollectPersonDto> persons) {
        String insertDetailSql = "insert into " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_DETAIL +
                " (id, created, updated, face_feature, from_cid_id, from_image_id, image_data, indexed, version, zplxmc) " +
                " values (?, ?, ?, null, ?, 0, ?, -1, 0, " +  GlobalConsts.MOBILE_INFO_TYPE + ") ";
        String insertInfoSql = "insert into " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO +
                " (id, created, updated, extend_field, extend_field1, extend_field2, extend_field3, extend_field4, extend_field5, xs, xb, MZMC, GMSFHM, XJZDZ) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String getMaxIdSql = "select max(id) from " + GlobalConsts.INTELLIF_STATIC + "." + GlobalConsts.T_NAME_OTHER_INFO +
                " where type = " + GlobalConsts.MOBILE_INFO_TYPE;

        Long maxId = jdbcTemplate.queryForObject(getMaxIdSql, Long.class);
        Long baseId = maxId;

        if (maxId == null) {
            baseId = getValidRandomId();
        }

        Date now = new Date();
        String imageBaseUrl = ImageInfoHelper.getMobileCollectSyncPath()
                + "/" + dateStr + "/" + zipFileName.substring(0, zipFileName.length() - 4)  + "/";
        for (int i = 0; i < persons.size(); i++) {
            MobileCollectPersonDto person = persons.get(i);
            jdbcTemplate.update(insertInfoSql, baseId + 1 + i, now, now, person.getId(), person.getCreated().getTime(), person.getUpdated().getTime(),
                    imageBaseUrl + person.getCollectBigPhoto(), mapStationId(person.getStationId()), person.getCompanyName(), person.getName(), person.getGender(), person.getNational(),
                    person.getCid(), person.getAddress());
            jdbcTemplate.update(insertDetailSql, baseId + 1 + i, now, now, baseId + 1 + i, imageBaseUrl + person.getCollectSmallPhoto());
        }

        // notice c++;
        try {
            long stIndex = baseId + 1;
            long enIndex = baseId + persons.size();
            List<IFaaServiceThriftClient> targetList = iFaceSdkServiceItf
                    .getAllTarget();
            Random ran = new Random();
            int aa = ran.nextInt(targetList.size());
            IFaceSdkTarget target = targetList.get(aa);
            LOG.info("select engine index:" + aa
                    + " start Index:" + stIndex
                    + " end Index:" + enIndex);
            target.iface_engine_ioctrl(
                    EEnginIoctrlType.ENGIN_IOCTRL_IOCTRL
                            .getValue(),
                    EParamIoctrlType.PARAM_IOCTRL_OTHER_IMPORT.getValue(), 10000000, stIndex, enIndex);
        } catch (Exception e) {
            LOG.error("notice c++ pk base error", e);
        }

    }

    private Long mapStationId(Long syncStationId) {
        Map<Long, Long> stationIdMap = mobileCollectStationCache.loadStationMap();
        return stationIdMap.get(syncStationId);
    }

    private Long getValidRandomId() {
        String randomStr = CommonUtil.getFixLenthString(8);
        long baseId = Long.parseLong(randomStr
                + "000000000");

        while(true){
            //静态库表主键重复检查
            boolean isFind = false;
            isFind = isIdExists(baseId + 1);
            if(isFind){
                randomStr = CommonUtil.getFixLenthString(8);
                baseId = Long.parseLong(randomStr
                        + "000000000");
            } else {
                break;
            }
        }
        return baseId;
    }

    private boolean isIdExists(long id){
        return otherInfoDao.findOne(id) != null;
    }

    private List<MobileCollectPersonDto> parsePersons(String zipFileName) {
        List<MobileCollectPersonDto> persons = new ArrayList<>();
        try {
            FileSystem fs = FileSystems.newFileSystem(Paths.get(DOWN_PATH + zipFileName), null);
            byte[] bytes = Files.readAllBytes(fs.getPath("person.txt"));
            String content = new String(bytes, Charset.forName("UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            persons.addAll(Arrays.asList(mapper.readValue(content, MobileCollectPersonDto[].class)));
        } catch (IOException e) {
            LOG.error(e);
        }
        return persons;
    }

    public static void unzipToDir(String zipFile, String pathInZip, String targetDir) throws IOException {
        Path targetPath = Paths.get(targetDir);
        if (Files.notExists(targetPath))
            Files.createDirectories(targetPath);
        FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFile), null);
        Files.walkFileTree(fs.getPath(pathInZip), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetPath = Paths.get(targetDir + File.separator + fs.getPath(pathInZip).relativize(file));
                if (Files.notExists(targetPath.getParent()))
                    Files.createDirectories(targetPath.getParent());
                Files.copy(file, targetPath);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean isBeforeTomorrow(Date date) {
        Calendar tomorrow = Calendar.getInstance();

        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        return date.before(tomorrow.getTime());
    }

    private List<String> parseListFile(String filePath) {
        List<String> nameList = new ArrayList<>();
        Path path = Paths.get(filePath);

        try {
            nameList = Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOG.error(e);
        }
        return nameList;
    }

    // 处理以前同步失败的数据
    private void processFailed() {
    }

    private boolean downloadFile(String fileName) {
        URL url = null;
        try {
            url = new URL(MobileCollectSyncSetting.getServerUrl() + fileName);
            FileUtils.copyURLToFile(url, new File(DOWN_PATH + fileName));
        } catch (IOException e) {
            LOG.error(e);
            return false;
        }
        return true;
    }

    private void removeDownloadedFile(String fileName) {
        Path filePath = Paths.get(DOWN_PATH + fileName);
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    private Map<String, String> getLastSyncPoint() {
        String sql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_MOBILE_COLLECT_SYNC_LOG +
                " order by file_date desc, id desc limit 1";
        Map<String, String> lastSync = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        List<MobileCollectSyncLog> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MobileCollectSyncLog.class));

        if (CollectionUtils.isEmpty(result)) {
            lastSync.put("date", df.format(new Date()));
            lastSync.put("fileName", null);
        } else {
            lastSync.put("date", df.format(result.get(0).getFileDate()));
            lastSync.put("fileName", result.get(0).getFileName());
        }
        return lastSync;
    }
}
