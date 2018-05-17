package intellif.chd.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import intellif.chd.bean.Camera;
import intellif.chd.bean.PeriodType;
import intellif.chd.consts.Constant;
import intellif.chd.dao.ContrastFaceInfoDao;
import intellif.chd.dao.TimeConfigureDao;
import intellif.chd.dao.ZipPathDao;
import intellif.chd.dto.ChdFaceSearchStatisticDto;
import intellif.chd.dto.FaceQuery;
import intellif.chd.service.ClusterServiceItf;
import intellif.chd.service.TimesClusterPersonItf;
import intellif.chd.util.FaceUtil;
import intellif.chd.vo.Cluster;
import intellif.chd.vo.ContrastFaceInfo;
import intellif.chd.vo.Face;
import intellif.chd.vo.TimeConfigure;
import intellif.chd.vo.Times;
import intellif.chd.vo.TimesPerson;
import intellif.chd.vo.ZipPath;
import intellif.configs.PropertiesBean;
import intellif.dao.SearchRecordDao;
import intellif.dao.impl.FaceInfoDaoImpl;
import intellif.dto.CidInfoDto;
import intellif.exception.MsgException;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.service.SolrServerItf;
import intellif.utils.FileUtil;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;
import intellif.database.entity.SearchRecord;

@Service
public class TimesClusterPersonImpl implements TimesClusterPersonItf {

	@Autowired
	protected FaceInfoDaoImpl faceInfoDao;
	@Autowired
	protected TimeConfigureDao timeConfigureDao;
	@Autowired
	protected ClusterServiceItf clusterService;
	@Autowired
	protected CameraServiceItf cameraService;
	@Autowired
	private SolrServerItf _solrService;
	@Autowired
	private PropertiesBean propertiesBean;
	@Autowired
	private ZipPathDao zipPathDao;
	@Autowired
	private ContrastFaceInfoDao contrastFaceInfoDao;

	private static final Logger LOG = LogManager.getLogger(TimesClusterPersonImpl.class);

	@Override
	@Transactional
	public void start(FaceQuery faceQuery) {
		Constant.MINING_TASK_LIMIT_POOL.submit(new TaskStart(faceQuery));
	}

	@Override
	public List<TimesPerson> parseClusterPerson(FaceQuery faceQuery) {
		// 查询人脸
		setTime(faceQuery);
		List<FaceInfo> faceInfoList = faceInfoDao.findFaceInfo(faceQuery);
		// 聚合人脸
		List<Cluster> clusterList = clusterService.faceInfoClusterAndSort(faceInfoList, faceQuery.getThreshold());
		return parseClusterListByTimes(clusterList, faceQuery);
	}

	@Override
	public void personContrast(List<TimesPerson> timesPersons, FaceQuery faceQuery) {
		List<CidInfoDto> faceSearchStatisticDtoList = new ArrayList<CidInfoDto>();
		String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime() - 1000 * 60 * 60 * 24);
		String tempPath = "export/image/" + time;
		String zipPath = "export/zip/" + time;
		try {
			int k = 1;
			for (TimesPerson timesPerson : timesPersons) {
				List<String> cjUrlList = new LinkedList<>();
				List<String> bzUrlList = new LinkedList<>();
				List<ContrastFaceInfo> faceInfoList = new ArrayList<>();
				faceSearchStatisticDtoList = _solrService.getChdFaceStatistic(timesPerson.getFaceId(), faceQuery.getThreshold());
				faceSearchStatisticDtoList.sort((m,n)->(Float.compare(n.getScore(), m.getScore())));
				cjUrlList.add(timesPerson.getFaceUrl());
				cjUrlList.add(timesPerson.getImageUrl());
				if (faceSearchStatisticDtoList.size() > 10) {
				    faceSearchStatisticDtoList = faceSearchStatisticDtoList.subList(0, 10);
				}
				for (int i = 0; i < faceSearchStatisticDtoList.size(); i++) {
					CidInfoDto cidInfoDto = faceSearchStatisticDtoList.get(i);
					ContrastFaceInfo faceInfo = new ContrastFaceInfo();
					faceInfo.setFaceTime(timesPerson.getTime());
					faceInfo.setCjImage("cj" + k + ".jpg");
					faceInfo.setBzImage("bz" + k + i + ".jpg");
					faceInfo.setIdentity(cidInfoDto.getGmsfhm());
					faceInfo.setName(cidInfoDto.getXm());
					faceInfo.setScore(cidInfoDto.getScore());
					bzUrlList.add(cidInfoDto.getFile());
					faceInfoList.add(faceInfo);
				}
				contrastFaceInfoDao.save(faceInfoList);
				String imagePath = tempPath + "/cj" + k;
				if (!faceInfoList.isEmpty()) {
				    saveImageToDir(cjUrlList,bzUrlList, imagePath, k);
                }
				k++;
			}
			String path = compressZip(zipPath, tempPath);
			saveDB(path, faceQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void saveDB(String path, FaceQuery faceQuery) {
		ZipPath zipPath = new ZipPath();
		zipPath.setStarttime(faceQuery.getStartTime());
		zipPath.setEndtime(faceQuery.getEndTime());
		zipPath.setPath(path);
		zipPathDao.save(zipPath);
	}

	private void saveImageToDir(List<String> cjUrlList, List<String> bzUrlList,String imagePath, int k) throws Exception {
		File file = new File(FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + imagePath);
		FileUtil.checkFileExist(file);
		String path = "";
		
		for (int i = 0; i < cjUrlList.size(); i++) {
			String url = cjUrlList.get(i);
			if (i == 0) {
				path = imagePath + "/cj" + k;
			} else if (i == 1) {
				path = imagePath + "/cj_big" + k;
			}
			String fullName = FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + path + ".jpg";
			boolean status = FileUtil.copyUrl(url, fullName);
			if (!status) {
				FileUtil.deleteFile(file, true);
			}
		}
		for (int i = 0; i < bzUrlList.size(); i++) {
            String url = bzUrlList.get(i);
            path = imagePath + "/bz" + k + i;
            String fullName = FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + path + ".jpg";
            boolean status = FileUtil.copyUrl(url, fullName);
            if (!status) {
                FileUtil.deleteFile(file, true);
            }
        }
		

	}

	private String compressZip(String zipPath, String tempPath) throws Exception {
		File file = new File(FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + zipPath);
		FileUtil.deleteFile(file, true);
		FileUtil.checkFileExist(file);
		FileUtil.zipCompress(FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + tempPath,
				FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + zipPath + ".zip");
		File zipFile = new File(FileUtil.getChdZipUrl(propertiesBean.getIsJar()) + zipPath + ".zip");
		if (!zipFile.exists()) {
			throw new Exception("压缩失败");
		}
		String path = FileUtil.getChdZipHttpUrl(propertiesBean.getIsJar()) + zipPath + ".zip";
		return path;
	}

	private List<TimesPerson> parseClusterListByTimes(List<Cluster> clusterList, FaceQuery faceQuery) {
		Map<Long, Camera> parseCameraMap = cameraMap();

		// 进行次数分析和过滤
		List<TimesPerson> clusterPersonList = new ArrayList<TimesPerson>();
		if (CollectionUtils.isEmpty(clusterList)) {
			LOG.error("xxxxxxx param is invalid, clusterList is {}, parseCameraMap is {}", clusterList);
			return clusterPersonList;
		}
		LOG.info("xxxxxxx clusterList size is {}", clusterList.size());
		for (Cluster cluster : clusterList) {
			List<Face> faceList = cluster.getFaceList();
			// 过滤掉人脸图片少于最小次数的人员
			if (faceList.size() < 1) {
				continue;
			}
			// 对face列表按照时间从小到大排序
			TimesPerson clusterPerson = new TimesPerson();

			// 分析该人员出现的次数
			List<Times> timesList = timesList(parseCameraMap, faceList, faceQuery.getMinTimeInterval());

			// 过滤掉次数未达到搜索要求的人员
			if (timesList.size() < faceQuery.getMinTimes()) {
				continue;
			}

			clusterPerson.setFaceUrl(cluster.getFaceUrl());
			clusterPerson.setFaceId(cluster.getFaceId());
			clusterPerson.setTimesList(timesList);
			clusterPerson.setImageUrl(cluster.getImageUrl());
			clusterPerson.setTime(cluster.getTime());
			// 将符合要求的人员进行封装
			clusterPersonList.add(clusterPerson);
		}
		// 按照次数从大到小排列
		clusterPersonList.sort((o1, o2) -> o2.getTimesList().size() - o1.getTimesList().size());
		LOG.info("xxxxxxx clusterPersonList size is {}", clusterPersonList.size());
		return clusterPersonList;
	}

	protected Map<Long, Camera> cameraMap() {
		List<CameraInfo> cameraList = cameraService.findOtherCameraAll();
		Map<Long, Camera> parseCameraMap = FaceUtil.parseCameraList(cameraList);
		LOG.debug("xxxxxxx parseCameraMap keyset is {}", parseCameraMap.keySet());
		return parseCameraMap;
	}

	protected List<Times> timesList(Map<Long, Camera> parseCameraMap, List<Face> faceList, Long minTimeInterval) {
		List<Times> timesList = new ArrayList<>();
		Times lastTimes = null;

		FaceUtil.sortAndDistinct(faceList);
		for (Face face : faceList) {
			// 如果该人脸对应的摄像头不存在，则过滤掉该人脸
			if (null == parseCameraMap.get(face.getSourceId())) {
				continue;
			}
			// 如果上一次对象为空，则创建新的次对象
			if (lastTimes == null) {
				lastTimes = createTimes(parseCameraMap, face);
				timesList.add(lastTimes);
				continue;
			}
			// 如果cameraId有变化，算作新的一次
			if (!face.getSourceId().equals(lastTimes.getCamera().getId())) {
				lastTimes = createTimes(parseCameraMap, face);
				timesList.add(lastTimes);
				continue;
			}
			// 如果时间超过了次数的最小时间间隔，则算作新的一次
			if (null != minTimeInterval) {
				if (face.getTime().getTime() - lastTimes.getEndTime().getTime() >= minTimeInterval.longValue()) {
					lastTimes = createTimes(parseCameraMap, face);
					timesList.add(lastTimes);
					continue;
				}
			}
			lastTimes.getFaceList().add(face);
			lastTimes.setEndTime(face.getTime());
		}
		sort(timesList);
		return timesList;
	}

	protected static Times createTimes(Map<Long, Camera> parseCameraMap, Face face) {
		Times lastTimes;
		lastTimes = new Times();
		lastTimes.setCamera(parseCameraMap.get(face.getSourceId()));
		lastTimes.getFaceList().add(face);
		lastTimes.setStartTime(face.getTime());
		lastTimes.setEndTime(face.getTime());
		return lastTimes;
	}

	protected void sort(List<Times> timesList) {
		// 按照开始时间倒序排列
		timesList.sort((times1, times2) -> times2.getStartTime().compareTo(times1.getStartTime()));
		for (Times times : timesList) {
			times.getFaceList().sort((face1, face2) -> face2.getTime().compareTo(face1.getTime()));
		}
	}

	private void setTime(FaceQuery faceQuery) {
		TimeConfigure timeConfigure = timeConfigureDao.findAll().get(0);
		faceQuery.setThreshold(timeConfigure.getThreshold());
		String starttime = timeConfigure.getStarttime();
		String endtime = timeConfigure.getEndtime();
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (timeConfigure.getPeriod().equalsIgnoreCase(PeriodType.DAY.name())) {
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date = calendar.getTime();
				String start = dateFormat.format(date) + " " + starttime;
//				String start = "2017-05-31 00:00:00 ";
				Date startDate = dateFull.parse(start);
				String end = dateFormat.format(date) + " " + endtime;
//				String end = "2017-05-31 23:59:59";
				Date endDate = dateFull.parse(end);
				faceQuery.setStartTime(startDate);
				faceQuery.setEndTime(endDate);
			}
			if (timeConfigure.getPeriod().equalsIgnoreCase(PeriodType.WEEK.name())) {
				calendar.add(Calendar.DAY_OF_WEEK, -7);
				date = calendar.getTime();
				String start = dateFormat.format(date) + " 00:00:00";
				Date startDate = dateFull.parse(start);
				calendar.add(Calendar.DAY_OF_WEEK, 6);
				date = calendar.getTime();
				String end = dateFormat.format(date) + " 23:59:59";
				Date endDate = dateFull.parse(end);
				faceQuery.setStartTime(startDate);
				faceQuery.setEndTime(endDate);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public class TaskStart implements Runnable {
		private FaceQuery task;

		public TaskStart(FaceQuery task) {
			super();
			this.task = task;
		}

		@Override
		public void run() {
			try {
				List<TimesPerson> timesPerson = parseClusterPerson(task);
				personContrast(timesPerson, task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
