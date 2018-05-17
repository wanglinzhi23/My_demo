package intellif.chd.service.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intellif.chd.consts.Constant;
import intellif.chd.service.CacheService;
import intellif.chd.service.ClusterServiceItf;
import intellif.chd.util.FaceUtil;
import intellif.chd.vo.Cluster;
import intellif.chd.vo.Face;
import intellif.chd.vo.FilterFace;
import intellif.configs.PropertiesBean;
import intellif.service.FaceServiceItf;
import intellif.service.ImageServiceItf;
import intellif.utils.FileUtil;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.ImageInfo;

@Service
public class ClusterServiceImpl implements ClusterServiceItf {
    private static final Logger LOG = LogManager.getLogger(ClusterServiceImpl.class);

    @Autowired
    private CacheService cacheService;
    @Autowired
    private ImageServiceItf _imageServiceItf;
    @Autowired
    private FaceServiceItf faceService;
    @Autowired
    private PropertiesBean propertiesBean;

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Cluster> faceInfoClusterAndSort(List<FaceInfo> faceInfoList, float threshold) {

        // 去除不符合要求的照片
        // faceInfoList = filterFaceList(faceInfoList);

        // 排序并去重
        long currentTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(faceInfoList)) {
            return new ArrayList<Cluster>();
        }
        faceInfoList = FaceUtil.sortAndDistinctFaceInfoList(faceInfoList);

        LOG.info("xxxxxxxx FaceUtil.sortAndDistinct need {}ms, faceInfoList.size is {}", System.currentTimeMillis() - currentTime, faceInfoList.size());
        currentTime = System.currentTimeMillis();

        int size = faceInfoList.size();
        LOG.info("xxxxxxxx faceCluster begin, faceInfoList.size is {}", size);
        List<Cluster> resultPersonList = faceCluster(faceInfoList, threshold);
        LOG.info("xxxxxxxx faceCluster need {}ms, faceInfoList.size is {}", System.currentTimeMillis() - currentTime, size);
        // 将结果进行一次排序
        currentTime = System.currentTimeMillis();
        resultPersonList.sort((list1, list2) -> list2.getFaceList().size() - list1.getFaceList().size());
        for (Cluster cluster : resultPersonList) {
            FaceUtil.sortAndDistinct(cluster.getFaceList());
        }
        LOG.info("xxxxxxxx result.sort need {}ms, resultPersonList.size is {}, faceInfoList.size is {}", System.currentTimeMillis() - currentTime,
                resultPersonList.size(), faceInfoList.size());
        return resultPersonList;

    }

    /**
     * 运用多线程过滤掉不符合要求的图片
     * 
     * @param faceInfoList
     * @return
     */
    protected List<FaceInfo> filterFaceList(List<FaceInfo> faceInfoList) {
        if (CollectionUtils.isEmpty(faceInfoList)) {
            return faceInfoList;
        }
        List<FilterFace> filterFaceList = cacheService.filterFaceList();
        if (CollectionUtils.isEmpty(filterFaceList)) {
            return faceInfoList;
        }
        long currentTime = System.currentTimeMillis();
        // 任务列表
        List<ForkJoinTask<Object[]>> taskList = new ArrayList<>();
        // 每个任务每次分析的人脸个数
        final int step = faceInfoList.size() / Constant.MINING_TASK_POOL.getParallelism() + 1;
        for (int i = 0; i < faceInfoList.size(); i += step) {
            // 当前任务分析的起始人脸位置（包含）
            final int offset = i;
            // 当前任务分析的结束人脸位置（不包含）
            final int end = Math.min(i + step, faceInfoList.size());
            // 提交任务，并将任务加入任务列表
            taskList.add(Constant.MINING_TASK_POOL.submit(() -> {
                List<FaceInfo> tempList = new ArrayList<>();
                Map<Long, List<FaceInfo>> filtedMap = new HashMap<>();
                for (int j = offset; j < end; j++) {
                    FaceInfo faceInfo = faceInfoList.get(j);
                    boolean needFilter = false;
                    for (FilterFace filter : filterFaceList) {
                        float score = FaceUtil.javaVerify(faceInfo.getFeature(), filter.takeFeatureFloat());
                        if (Float.compare(score, filter.getThreshold()) >= 0) {
                            List<FaceInfo> filtedList = filtedMap.get(filter.getId());
                            if (null == filtedList) {
                                filtedList = new ArrayList<>();
                                filtedMap.put(filter.getId(), filtedList);
                            }
                            faceInfo.setScore(score);
                            if (!faceInfo.getImageData().startsWith("http")) {
                                faceInfo.setImageData("/" + faceInfo.getImageData());
                            }
                            filtedList.add(faceInfo);
                            needFilter = true;
                            break;
                        }
                    }
                    if (!needFilter) {
                        tempList.add(faceInfo);
                    }
                }
                return new Object[] { tempList, filtedMap };
            }));
        }
        List<FaceInfo> retFaceList = new ArrayList<>();
        Map<Long, List<FaceInfo>> filtedMap = new HashMap<>();
        // 等待任务结束
        for (ForkJoinTask<Object[]> task : taskList) {
            try {
                Object[] objs = task.get();
                Map<Long, List<FaceInfo>> tempMap = (Map<Long, List<FaceInfo>>) objs[1];
                retFaceList.addAll((List<FaceInfo>) objs[0]);
                tempMap.forEach((key, value) -> {
                    List<FaceInfo> filtedList = filtedMap.get(key);
                    if (null == filtedList) {
                        filtedList = new ArrayList<>();
                        filtedMap.put(key, filtedList);
                    }
                    filtedList.addAll(value);
                });
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("catch exception: ", e);
            }
        }
        int filteredSize = 0;
        for (Map.Entry<Long, List<FaceInfo>> entry : filtedMap.entrySet()) {
            entry.getValue().sort((x, y) -> Float.compare(x.getScore(), y.getScore()));
            filteredSize += entry.getValue().size();
        }
        // TemplateUtil.generateHtml(taskId, faceInfoList.size(), filteredSize,
        // filterFaceList, filtedMap);

        LOG.info("xxxxxx filter face list need {}ms, before filter face size is {}, after face size is {}, filter {} face",
                System.currentTimeMillis() - currentTime, faceInfoList.size(), retFaceList.size(), faceInfoList.size() - retFaceList.size());
        return retFaceList;
    }

    /**
     * 多线程聚类分析
     * 
     * @param faceInfoList
     * @param threshold
     * @return
     */
    @Override
    public List<Cluster> faceCluster(List<FaceInfo> faceInfoList, float threshold) {
        List<Cluster> clusterFaceInfoList = new ArrayList<>();

        // 如果人脸列表为空，则返回空结果
        if (CollectionUtils.isEmpty(faceInfoList)) {
            return clusterFaceInfoList;
        }

        faceInfoList.sort((m,n)->(n.getTime().compareTo(m.getTime())));
        // 任务列表
        List<ForkJoinTask<List<Face>>> taskList = new ArrayList<>();
        // 如果人脸列表不为空，则一直分析下去
        while (!faceInfoList.isEmpty()) {
            List<Face> tempFaceList = new ArrayList<>();
            // 取走第一张人脸
            FaceInfo firstInfo = faceInfoList.get(0);
            tempFaceList.add(FaceUtil.convertFaceInfo(firstInfo));
            faceInfoList.set(0, null);

            // 清空任务列表
            taskList.clear();

            // 每个任务每次分析的人脸个数
            final int step = Math.max((faceInfoList.size() - 1) / Constant.MINING_TASK_POOL.getParallelism() + 1, Constant.MIN_STEP);

            for (int i = 1; i < faceInfoList.size(); i += step) {
                // 当前任务分析的起始人脸位置（包含）
                final int offset = i;
                // 当前任务分析的结束人脸位置（不包含）
                final int end = Math.min(i + step, faceInfoList.size());
                // 提交任务，并将任务加入任务列表
                taskList.add(Constant.MINING_TASK_POOL.submit(() -> {
                    List<Face> tempList = new ArrayList<>();
                    for (int j = offset; j < end; j++) {
                        FaceInfo faceInfo = faceInfoList.get(j);
                        // 如果当前人脸符合要求，则将其取走，同时将列表尾部的人脸放到该位置上
                        if (FaceUtil.isLike(firstInfo.getFeature(), faceInfo.getFeature(), threshold)) {
                            tempList.add(FaceUtil.convertFaceInfo(faceInfo));
                            faceInfoList.set(j, null);
                        }
                    }
                    return tempList;
                }));
            }
            // 等待任务结束
            for (ForkJoinTask<List<Face>> task : taskList) {
                try {
                    tempFaceList.addAll(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("catch exception: ", e);
                }
            }

            // 删除所有为null的元素
            faceInfoList.removeIf(face -> null == face);
            long faceId = firstInfo.getId();
            FaceInfo face = this.faceService.findOne(faceId);
            ImageInfo imageInfo = null;
            Cluster cluster = new Cluster();
            if (null != face) {
                imageInfo = _imageServiceItf.findById(face.getFromImageId());
                cluster.setImageUrl(imageInfo.getUri());
            }
            tempFaceList.sort((m, n) -> (n.getTime().compareTo(m.getTime())));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tempFaceList.size() - 1; i++) {
                Date date = tempFaceList.get(i).getTime();
                builder.append(format.format(date) + ",");
            }
            Date date = tempFaceList.get(tempFaceList.size() - 1).getTime();
            builder.append(format.format(date));
            cluster.setTime(builder.toString());
            String imageData = firstInfo.getImageData();
            clusterFaceInfoList.add(cluster.setFaceList(tempFaceList).setFaceUrl(imageData).setFaceId(faceId));
        }
//        saveToDB(clusterFaceInfoList);
        // 清空人脸列表，防止内存泄漏
        faceInfoList.clear();
        return clusterFaceInfoList;
    }

    public void saveToDB(List<Cluster> timesPersons) {
        String tempPath = "export/image/cluster/";
        try {
            for (Cluster cluster : timesPersons) {
                long faceId = cluster.getFaceId();
                String clusterPath = tempPath + faceId + "/";
                String url = cluster.getFaceUrl();
                File file = new File(FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + clusterPath);
                FileUtil.checkFileExist(file);
                String fullName = FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + clusterPath + faceId + ".jpg";
                FileUtil.copyUrl(url, fullName);
                for (Face faceInfo : cluster.getFaceList()) {
                    String fullName1 = FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + clusterPath + faceInfo.getId() + ".jpg";
                    FileUtil.copyUrl(faceInfo.getImageData(), fullName1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
