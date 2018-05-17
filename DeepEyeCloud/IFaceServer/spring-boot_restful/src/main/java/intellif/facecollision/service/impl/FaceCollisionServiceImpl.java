package intellif.facecollision.service.impl;

import com.intellif.core.cluster.SearchClusterService;
import com.intellif.core.cluster.vo.Cluster;

import intellif.consts.GlobalConsts;
import intellif.dao.UploadedFileDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.exception.MsgException;
import intellif.facecollision.dao.FaceCollisionTaskDao;
import intellif.facecollision.dto.FaceCollisionQueryParam;
import intellif.facecollision.dto.FaceCollisionTaskDto;
import intellif.facecollision.request.FaceCollisionParam;
import intellif.facecollision.request.FaceCollisionParamItem;
import intellif.facecollision.service.FaceCollisionServiceItf;
import intellif.facecollision.vo.FaceCollisionResult;
import intellif.facecollision.vo.FaceCollisionTask;
import intellif.facecollision.vo.FaceExtractTask;
import intellif.service.FaceServiceItf;
import intellif.utils.CurUserInfoUtil;
import intellif.utils.FaceCollisionComparable;
import intellif.utils.FaceInfoComparable;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.PageDto;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.UploadedFile;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zheng Xiaodong
 */
@Service
public class FaceCollisionServiceImpl implements FaceCollisionServiceItf {
    private static Logger LOG = LogManager.getLogger(FaceCollisionServiceImpl.class);
    private static final String RESULT_STORE_PATH = "face_collision/";

    private static final ConcurrentHashMap<Long, Double> progressMap = new ConcurrentHashMap<>();

    @Autowired
    private FaceCollisionTaskDao faceCollisionTaskDao;

    @Autowired
    private FaceInfoDaoImpl faceInfoDaoImpl;

    @Autowired
    private UploadedFileDao uploadedFileRepository;

    @Autowired
    private FaceServiceItf faceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public FaceCollisionTask createTask(FaceCollisionParam faceCollisionParam) {
        // 校验 TODO: zxd
        String checkNameSql = "select count(1) from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK
                + " where task_name = ? ";
        Long count = jdbcTemplate.queryForObject(checkNameSql, new String[]{faceCollisionParam.getTaskName()}, Long.class);
        if (count > 0)
            throw new MsgException("任务名称重复");

        FaceCollisionTask task = new FaceCollisionTask();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        String paramJson = "";
        try {
            paramJson = mapper.writeValueAsString(faceCollisionParam);
        } catch (IOException e) {
            LOG.error(e);
        }

        task.setTaskName(faceCollisionParam.getTaskName());
        task.setTaskParam(paramJson);
        task.setUserId(CurUserInfoUtil.getUserInfo().getId());
        task.setStatus(0);
        task.setDeleted(false);
        task = faceCollisionTaskDao.save(task);

        return task;
    }

    @Override
    public int deleteTask(Long taskId) {
        String deleteSql = "delete from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK +
                " where id = ?";
        jdbcTemplate.update(deleteSql, new Long[] {taskId});
        return 0;
    }

    @Override
    public PageDto<FaceCollisionTask> queryUserTasks(int page, int pageSize) {
        Long userId = CurUserInfoUtil.getUserInfo().getId();
        Long count = faceCollisionTaskDao.countByUserId(userId);
        List<FaceCollisionTask> tasks = faceCollisionTaskDao.findByUserId(userId, (page - 1) * pageSize, pageSize);
        return new PageDto<>(tasks, count, page, pageSize);
    }

    @Override
    public void startTask(FaceCollisionTask task) {
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

        List<FaceCollisionResult> resultList = new ArrayList<>();
        if (taskParam.getMode().equals(0)) {
            resultList = manualClusterMode(task, taskParam);
        } else if (taskParam.getMode().equals(1)) {
            resultList = hybridClusterMode(task, taskParam);
        } else {
            resultList = autoClusterMode(task, taskParam);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(baos);
            oout.writeObject(resultList);
            oout.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            File file = new File(RESULT_STORE_PATH + "task_" + task.getId());
            if(!file.getParentFile().exists()){
              file.getParentFile().mkdirs();  
            }
            FileUtils.writeByteArrayToFile(file, baos.toByteArray());
        } catch (FileNotFoundException e) {
            LOG.error("create task_taskId FileNotFound error: "+e);
        } catch (IOException e) {
            LOG.error("create task_taskId IO error: "+e);
        }

        String updateStatusSql = "update " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK
                + " set status = ?, complete_time = CURTIME(), progress = 100 where id = " + task.getId();
        jdbcTemplate.update(updateStatusSql, 2);
    }

    private List<FaceCollisionResult> manualClusterMode(FaceCollisionTask task, FaceCollisionParam taskParam) {
        List<FaceInfo> sourceFaces = faceInfoDaoImpl.findByIds(taskParam.getFaceIds());
        List<FaceInfo> targetFaces = getFacesFromParamItem(taskParam.getTargets());

        SearchClusterService clusterService = SearchClusterService.getInstance();
        clusterService.init();
        List<FaceCollisionResult> resultList = new ArrayList<>();
        int order = 1;

        if (sourceFaces != null) {
            int sourceFaceSize = sourceFaces.size();
            for (int i = 0; i < sourceFaceSize; i++) {
                double progress = (double) (i+1) / (sourceFaceSize) * 100;
                progressMap.put(task.getId(), progress);
                FaceInfo sourceClusterFace = sourceFaces.get(i);
                Cluster<FaceInfo> sourceCluster = clusterFromFace(sourceClusterFace);
                FaceCollisionResult result = getSearchList(clusterService,sourceCluster,targetFaces,taskParam.getThreshold().floatValue(), order);
                if(null != result){
                    order++;
                    result.setMode(GlobalConsts.face_collusion_mannual);
                    resultList.add(result);
                }
            }
        }
        Collections.sort(resultList, new FaceCollisionComparable());
        return resultList;
    }

    private List<FaceCollisionResult> hybridClusterMode(FaceCollisionTask task, FaceCollisionParam taskParam) {
        List<FaceInfo> manualSourceFaces = faceInfoDaoImpl.findByIds(taskParam.getFaceIds());
        List<FaceInfo> targetFaces = getFacesFromParamItem(taskParam.getTargets());
        List<FaceInfo> autoSourceFaces = getFacesFromParamItem(taskParam.getSources());

        SearchClusterService clusterService = SearchClusterService.getInstance();
        clusterService.init();
        List<FaceCollisionResult> resultList = new ArrayList<>();
        int order = 1;

        if (manualSourceFaces != null) {
            for (int i = 0; i < manualSourceFaces.size(); i++) {
                FaceInfo sourceClusterFace = manualSourceFaces.get(i);
                Cluster<FaceInfo> sourceCluster = clusterFromFace(sourceClusterFace);
                FaceCollisionResult result = getSearchList(clusterService,sourceCluster,targetFaces,taskParam.getThreshold().floatValue(), order);
                if(null != result){
                    result.setMode(GlobalConsts.face_collusion_mannual);
                    resultList.add(result);
                    order++;
                }
            }
        }

        Iterator<FaceInfo> it = autoSourceFaces.iterator();
        while(it.hasNext()){
            FaceInfo face = it.next();
            boolean containsFlag = false;
            for (FaceInfo f : manualSourceFaces) {
                if (f.getId().equals(face.getId())) {
                    containsFlag = true;
                    break;
                }
            }
            if (containsFlag) {
               it.remove();
            }
        }
        List<Cluster<FaceInfo>> autoSourceClusterList = clusterService.faceCluster(autoSourceFaces, taskParam.getThreshold().floatValue());
        if (autoSourceClusterList != null) {
            int autoSourceClusterListSize = autoSourceClusterList.size();
            for (int i = 0; i < autoSourceClusterListSize; i++) {
                double progress = (double) (i+1) / (autoSourceClusterListSize) * 100;
                progressMap.put(task.getId(), progress);
                Cluster<FaceInfo> sourceCluster = autoSourceClusterList.get(i);
                FaceCollisionResult result = getSearchList(clusterService,sourceCluster,targetFaces,taskParam.getThreshold().floatValue(), order);
                if(null != result){
                    result.setMode(GlobalConsts.face_collusion_auto);
                    resultList.add(result);
                    order++;
                }
            }
        }
        Collections.sort(resultList, new FaceCollisionComparable());
        return resultList;
    }
    /**
     * 源图片数据搜索目标图片数据
     * @param clusterService
     * @param sourceCluster
     * @param targetList
     * @param score
     * @param order
     * @return
     */
private FaceCollisionResult getSearchList(SearchClusterService clusterService,Cluster<FaceInfo> sourceCluster,List<FaceInfo> targetList,float score,int order){
    try{
    FaceCollisionResult result = new FaceCollisionResult();
    FaceInfo sourceClusterFace = sourceCluster.getFace();
    result.setOrder(order);
    Collections.sort(sourceCluster.getFaceList(), new FaceInfoComparable("score"));
    result.setSourceCluster(sourceCluster);
    List<FaceInfo> tList = clusterService.searchFaceList(sourceClusterFace.getFeature(), String.valueOf(sourceClusterFace.getVersion()), targetList, score);
    if(!CollectionUtils.isEmpty(tList)){
        Collections.sort(tList, new FaceInfoComparable("score"));
        List<FaceInfo> fList = new ArrayList<FaceInfo>();
        fList.addAll(tList);
        result.setTargetFaces(fList);
        targetList.removeAll(tList);//去重
    }
    result.setSourceCount(result.getSourceCluster().getFaceList().size());
    result.setTargetCount(result.getTargetFaces().size());
    return result;
    }catch(Exception e){
        LOG.error("source cluster face data search desc face data error:",e);
        return null;
    }
}
    private List<FaceCollisionResult> autoClusterMode(FaceCollisionTask task, FaceCollisionParam taskParam) {
        List<FaceInfo> sourceFaces = getFacesFromParamItem(taskParam.getSources());
        List<FaceInfo> targetFaces = getFacesFromParamItem(taskParam.getTargets());
        SearchClusterService clusterService = SearchClusterService.getInstance();
        clusterService.init();
        List<Cluster<FaceInfo>> sourceClusterList = clusterService.faceCluster(sourceFaces, taskParam.getThreshold().floatValue());
        List<FaceCollisionResult> resultList = new ArrayList<>();

        if (sourceClusterList != null) {
            int sourceClusterSize = sourceClusterList.size();
            int order = 1;
            for (int i = 0; i < sourceClusterSize; i++) {
                double progress = (double) (i+1) / (sourceClusterSize) * 100;
                progressMap.put(task.getId(), progress);
                Cluster<FaceInfo> sourceCluster = sourceClusterList.get(i);
                FaceCollisionResult result = getSearchList(clusterService,sourceCluster,targetFaces,taskParam.getThreshold().floatValue(), order);
                if(null != result){
                    result.setMode(GlobalConsts.face_collusion_auto);
                    resultList.add(result);
                    order++;
                }
              /*  for (int j = 0; j < targetClusterSize; j++) {
                    double progress = (((double) ((i * targetClusterSize) + j + 1)) / (sourceClusterSize * targetClusterSize)) * 100;
                    progressMap.put(task.getId(), progress);
                    Cluster<FaceInfo> targetCluster = targetClusterList.get(j);*/
                    
                    
              /*      FaceInfo targetClusterFace = targetCluster.getFace();
                    float score = clusterService.compare(sourceClusterFace.getFeature(),
                            targetClusterFace.getFeature(), "" + targetClusterFace.getVersion());
                    if (score >= taskParam.getThreshold()) {
                        FaceCollisionResult result = new FaceCollisionResult();
                        result.setSourceCluster(sourceCluster);
                        result.setTargetCluster(targetCluster);
                        result.setOrder(order++);
                        resultList.add(result);
                        break;
                    }*/
                
            }
        }
        Collections.sort(resultList, new FaceCollisionComparable());
        return resultList;
    }

    private Cluster<FaceInfo> clusterFromFace(FaceInfo face) {
        Cluster<FaceInfo> faceInfoCluster = new Cluster<>();
        List<FaceInfo> faceList = new ArrayList<>();
        face.setScore(1);
        faceList.add(face);
        faceInfoCluster.setFaceList(faceList);
        faceInfoCluster.setFace(face);
        return faceInfoCluster;
    }

    @Override
    public PageDto<FaceInfo> queryTargetFaces(long taskId, long personId, int listType, int page, int pageSize) {
        FaceCollisionTask task = faceCollisionTaskDao.findOne(taskId);
        if (task.getStatus() != 2)
            throw new MsgException("任务未完成");

        PageDto<FaceInfo> faces = null;
        try {
            byte[] reArr = FileUtils.readFileToByteArray(new File(RESULT_STORE_PATH + "task_" + taskId));
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(reArr));
            List<FaceCollisionResult> resultList = (List<FaceCollisionResult>) ois.readObject();
            List<FaceInfo> targetFaces = null;
            List<FaceInfo> targetFacesData = null;
            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    FaceCollisionResult res = resultList.get(i);
                    if (res.getPersonFace().getId().equals(personId)) {
                        if (listType == 0) {
                            targetFaces = res.getSourceFaces();
                        } else {
                            targetFaces = res.getTargetFaces();
                        }
                    }
                }
            }
            if (targetFaces != null) {
                int startIndex = (page - 1) * pageSize;
                int endIndex = page * pageSize;
                targetFacesData = targetFaces.subList(startIndex, endIndex <= targetFaces.size() ? endIndex : targetFaces.size());
            }
            faces = new PageDto<>(targetFacesData, targetFaces.size(), page, pageSize);
        } catch (IOException e) {
            LOG.error(e);
            throw new MsgException("获取目标人脸出错");
        } catch (ClassNotFoundException e) {
            LOG.error(e);
        }
        return faces;
    }

    private List<FaceInfo> getFacesFromParamItem(List<FaceCollisionParamItem> paramItems) {
        List<Long> ids = new ArrayList<>();
        StringBuilder idsStr = new StringBuilder();
        for (int i = 0; i < paramItems.size(); i++) {
            FaceCollisionParamItem item = paramItems.get(i);
            ids.add(item.getId());
            idsStr.append(item.getId());
            if (i < paramItems.size() - 1)
                idsStr.append(",");
        }

        String querySourceIdSql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_EXTRACT_TASK
                + " where file_id in (" + idsStr.toString() + ")";
        List<FaceExtractTask> extractTasks = jdbcTemplate.query(querySourceIdSql, new BeanPropertyRowMapper<>(FaceExtractTask.class));
        List<Long> taskIds = new ArrayList<>();
        for (int j = 0; j < extractTasks.size(); j++) {
            FaceExtractTask task = extractTasks.get(j);
            taskIds.add(task.getId());
        }

        String queryTimeSql = "select min(created) as start_time, max(updated) as end_time from " + GlobalConsts.INTELLIF_BASE + "."
                + GlobalConsts.T_NAME_FACE_EXTRACT_TASK + " where file_id in (" + idsStr.toString() + ")";
        List<TimeWrapper> timeWrapper = jdbcTemplate.query(queryTimeSql, new BeanPropertyRowMapper<>(TimeWrapper.class));

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        startTime.setTime(timeWrapper.get(0).getStartTime());
        endTime.setTime(timeWrapper.get(0).getEndTime());
        // 时间范围扩大5分钟，降低服务器时间不准的影响
        startTime.add(Calendar.MINUTE, -5);
        endTime.add(Calendar.MINUTE, 5);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String startTimeStr = df.format(startTime.getTime());
        String endTimeStr = df.format(endTime.getTime());

        return faceService.findBySourceIds(taskIds.toArray(new Long[]{}), startTimeStr, endTimeStr, 1, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PageDto<FaceCollisionResult> getTaskResult(FaceCollisionQueryParam param) {
        FaceCollisionTask task = faceCollisionTaskDao.findOne(param.getTaskId());
        if (task.getStatus() != 2)
            throw new MsgException("任务未完成");

        PageDto<FaceCollisionResult> result = null;
        try {
            byte[] reArr = FileUtils.readFileToByteArray(new File(RESULT_STORE_PATH + "task_" + param.getTaskId()));
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(reArr));
            List<FaceCollisionResult> resultList = (List<FaceCollisionResult>) ois.readObject();
            int startIndex = (param.getPage() - 1) * param.getPageSize();
            int endIndex = param.getPage() * param.getPageSize();
            Collections.sort(resultList, new FaceCollisionComparable());
            List<FaceCollisionResult> returnList = new ArrayList<FaceCollisionResult>();
            for(FaceCollisionResult cresult : resultList){
                if(cresult.getTargetCount() >= param.getTargetCount()){
                    returnList.add(cresult);
                }
            }
            List<FaceCollisionResult> dataList = returnList.subList(startIndex, endIndex <= returnList.size() ? endIndex : returnList.size());
            getFixNumFaceCollisionResult(dataList,param.getFaceCount());
            result = new PageDto<>(dataList, returnList.size(), param.getPage(), param.getPageSize());
        } catch (Exception e) {
            LOG.error("getTaskResult error:",e);
            throw new MsgException("获取结果出错");
        } 
        return result;
    }
    /**
     * 截取集合List元素长度
     * @param dataList
     * @param endIndex
     */
private void getFixNumFaceCollisionResult(List<FaceCollisionResult> dataList,int endIndex){
    if(!CollectionUtils.isEmpty(dataList)){
        for(FaceCollisionResult result : dataList){
            if(result.getSourceCount() > endIndex){
                result.getSourceCluster().setFaceList(result.getSourceCluster().getFaceList().subList(0, endIndex));
            }
            if(result.getTargetCount() > endIndex){
                result.setTargetFaces(result.getTargetFaces().subList(0, endIndex));
            }
        }
    }
}
    public static class TimeWrapper {
        private Date startTime;
        private Date endTime;

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
    }

    @Override
    public FaceCollisionTaskDto getTaskDetail(Long taskId){
        FaceCollisionTaskDto taskDto=new FaceCollisionTaskDto();
        FaceCollisionTask task=faceCollisionTaskDao.findOne(taskId);
        if(task!=null){
            try {
                BeanUtils.copyProperties(taskDto, task);
            } catch (Exception e) {
                LOG.error(e);
            }
            FaceCollisionParam param=null;
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            if(StringUtils.isNotBlank(task.getTaskParam())){
                try {
                    param=mapper.readValue(task.getTaskParam(), FaceCollisionParam.class);
                } catch (IOException e) {
                   LOG.error(e);
                }
                if(param==null)
                    return taskDto;
                taskDto.setTargets(getUploadedFileList(param.getTargets()));
                taskDto.setSources(getUploadedFileList(param.getSources()));
                List<FaceInfo> faceList = faceInfoDaoImpl.findByIds(param.getFaceIds());
                taskDto.setFaces(faceList);
                taskDto.setThreshold(param.getThreshold());
                taskDto.setMode(param.getMode());
            }
        }
        return taskDto;
    }

    private List<UploadedFile> getUploadedFileList(List<FaceCollisionParamItem> paramItems) {
        List<UploadedFile> uploadedFiles=new ArrayList<>();
        for(FaceCollisionParamItem item:paramItems){
            if(item.getId()!=null){
                UploadedFile file=uploadedFileRepository.findOne(item.getId());
                uploadedFiles.add(file);
            }else{
                uploadedFiles.add(null);
            }
        }
        return uploadedFiles;
    }

    @Override
    public List<FaceCollisionTask> queryUserTasksByIds(Long userId, String ids) {
        if (userId == null)
            userId = CurUserInfoUtil.getUserInfo().getId();
        if (ids == null)
            return new ArrayList<>();

        String sql = "select * from " + GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_FACE_COLLISION_TASK
                + " where user_id = ? and id in (" + ids + ")";
        List<FaceCollisionTask> tasks = jdbcTemplate.query(sql, new Long[]{userId}, new BeanPropertyRowMapper<>(FaceCollisionTask.class));
        for (FaceCollisionTask task : tasks) {
            double progress = getTaskProgress(task.getId());
            if (progress != -1)
                task.setProgress(progress);
            else if (task.getProgress() == null) {
                task.setProgress(0d);
            }
        }
        return tasks;
    }

    @Override
    public double getTaskProgress(Long taskId) {
        Double progress = progressMap.get(taskId);
        return progress == null ? -1 : progress;
    }

    @Override
    public void deleteResultFaces(Long taskId, Long personFaceId, String faceIds, int listType) {

        Set<Long> faceIdsSet = new HashSet<>();
        String[] faceIdArr = faceIds.split(",");
        for (int i = 0; i < faceIdArr.length; i++) {
            faceIdsSet.add(Long.valueOf(faceIdArr[i]));
        }

        FaceCollisionTask task = faceCollisionTaskDao.findOne(taskId);
        if (task.getStatus() != 2)
            throw new MsgException("任务未完成");

        PageDto<FaceInfo> faces = null;
        try {
            byte[] reArr = FileUtils.readFileToByteArray(new File(RESULT_STORE_PATH + "task_" + taskId));
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(reArr));
            List<FaceCollisionResult> resultList = (List<FaceCollisionResult>) ois.readObject();
            List<FaceInfo> targetFaces = null;
            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    FaceCollisionResult res = resultList.get(i);
                    if (res.getPersonFace().getId().equals(personFaceId)) {
                        if (listType == 0) {
                            targetFaces = res.getSourceFaces();
                        } else {
                            targetFaces = res.getTargetFaces();
                        }
                        break;
                    }
                }
            }
            if (targetFaces != null) {
                Iterator<FaceInfo> it = targetFaces.iterator();
                while (it.hasNext()) {
                    FaceInfo face = it.next();
                    if (faceIdsSet.contains(face.getId())) {
                        it.remove();
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oout = null;
            try {
                oout = new ObjectOutputStream(baos);
                oout.writeObject(resultList);
                oout.close();
            } catch (IOException e) {
                LOG.error(e);
            }

            try {
                File file = new File(RESULT_STORE_PATH + "task_" + task.getId());
                FileUtils.writeByteArrayToFile(file, baos.toByteArray());
            } catch (FileNotFoundException e) {
                LOG.error(e);
            } catch (IOException e) {
                LOG.error(e);
            }

        } catch (IOException e) {
            LOG.error(e);
            throw new MsgException("获取目标人脸出错");
        } catch (ClassNotFoundException e) {
            LOG.error(e);
        }
    }
}
