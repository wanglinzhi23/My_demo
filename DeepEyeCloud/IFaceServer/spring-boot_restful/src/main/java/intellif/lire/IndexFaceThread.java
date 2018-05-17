package intellif.lire;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.consts.GlobalConsts;
import intellif.dao.IndexFaceRecordDao;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.settings.ServerSetting;
import intellif.utils.DateUtil;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;

@Component
public class IndexFaceThread extends Thread {

	private static Logger LOG = LogManager.getLogger(IndexFaceThread.class);
	
	private int count=0;//日志打印次数计数器
	private long last_log_print_time=System.currentTimeMillis(); //上次日志打印时间
	private String last_log_content;//上次日志打印内容
	private long diff_time=5*60*1000; //日志打印时差(毫秒)
	
	@Autowired
	private FaceServiceItf faceService;

	@Autowired
	private CameraServiceItf cameraService;

	@Autowired
	private IndexFaceRecordDao recordRepository;

	private Queue<HttpSolrClient> solrHostQueue = new LinkedList<HttpSolrClient>();

	static class SolrClient extends HttpSolrClient {
		private static final long serialVersionUID = 1L;
		private String solrUrl;

		public SolrClient(String baseURL) {
			super(baseURL);
			this.solrUrl = baseURL;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((solrUrl == null) ? 0 : solrUrl.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SolrClient other = (SolrClient) obj;
			if (solrUrl == null) {
				if (other.solrUrl != null)
					return false;
			} else if (!solrUrl.equals(other.solrUrl))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return solrUrl;
		}
	}

	private Long solrConfigCount = 0L;

	@SuppressWarnings("deprecation")
	@PostConstruct
	public void setUp() {
		solrConfigCount = recordRepository.totalSolrConfigCount();
		List<Object> solrServerCameras = recordRepository.getSolrServerWithCameras();
		for (Object object : solrServerCameras) {
			HttpSolrClient server = new SolrClient(object.toString());
			server.setSoTimeout(30000); // socket read timeout
			server.setConnectionTimeout(30000);
			server.setDefaultMaxConnectionsPerHost(100);
			server.setMaxTotalConnections(100);
			server.setFollowRedirects(false); // defaults to false
			// allowCompression defaults to false.
			// Server side must support gzip or deflate for this to have any
			// effect.
			server.setAllowCompression(true);
			server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
			solrHostQueue.add(server);
		}

	}

	//@Scheduled(fixedDelay = 1000)
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		Long rate = ServerSetting.getIndexRate() * 1000;
//		LOG.info("solr.index.rate:" + rate);
		//改为只打一次
		if(count==0){
			LOG.info("solr.index.rate:" + rate);
			count++;
		}
		while (true) {
			Long configCount = recordRepository.totalSolrConfigCount();
			if (!solrConfigCount.equals(configCount)) {
				solrHostQueue.clear();
				setUp();
			}
			int total = 0;
			Map<Long, Integer> inStationCameraIdMap = new HashMap<Long, Integer>();
			try {
				long startTime = System.currentTimeMillis();

				Iterable<CameraInfo> cameraList = this.cameraService.findAllWithoutAuthorize();
				for (CameraInfo camera : cameraList) {
					if (camera.getInStation() == 1)
						inStationCameraIdMap.put(camera.getId(), 1);
				}

				// Long startFaceId = recordRepository.getLastFaceId() + 1;
				// long lastFaceId = startFaceId + ServerSetting.getIndexStep();
				// List<FaceInfo> faceList =
				// null;//this._faceInfoDao.findUnIndexedByLastFaceid(startFaceId,
				// lastFaceId);

				Date date = Calendar.getInstance().getTime();
				long time = date.getTime() / 1000;
				long time1 = time - ServerSetting.getIndexHours() * 60 * 60;
				Date date1 = new Date(time1 * 1000);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String s = sdf.format(date1);
				Date date2 = sdf.parse(s);

				String sTime = DateUtil.getformatDate(date2.getTime());
				String eTime = DateUtil.getformatDate(new Date().getTime());
				List<FaceInfo> faceList = this.faceService.findUnIndexed(sTime, eTime);
				if (faceList == null || faceList.isEmpty()) {
					// Long maxFaceId = recordRepository.getMaxFaceId();
					// if (lastFaceId < maxFaceId) {
					// IndexFaceRecord faceRecord = new
					// IndexFaceRecord(lastFaceId, 0L, "index");
					// recordRepository.save(faceRecord);
					// }

					try {
						Thread.sleep(rate);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				// long beginFaceId = faceList.get(0).getId();
				// Long recordFaceId = faceList.get(faceList.size() - 1).getId();
				// LOG.info("DB search start with {}  end with {}", startFaceId,
				// lastFaceId);
//注释掉该日志打印
//				LOG.info("db load data time is: {}ms with {} faces, Start indexing ..............",
//						(System.currentTimeMillis() - startTime), faceList.size());
				// Map<Long, List<FaceInfo>> updateMap = new HashMap<Long,
				// List<FaceInfo>>();

				List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
				for (FaceInfo face : faceList) {
					if (null == face.getFaceFeature() || face.getFaceFeature().length != 724)
						continue;
					try {
						SolrInputDocument solrDoc = new SolrInputDocument();
						solrDoc.addField("id", face.getId() + "");
						solrDoc.addField("time", face.getTime());
						solrDoc.addField("quality", face.getQuality());
						solrDoc.addField("file", face.getImageData());
						if (inStationCameraIdMap.containsKey(face.getSourceId())) {
							solrDoc.addField("type", GlobalConsts.INSTATION_INFO_TYPE);
						} else {
							solrDoc.addField("type", GlobalConsts.FACE_INFO_TYPE);
						}
						byte[] oriFeature = face.getFaceFeature();
//						if(oriFeature.length < 724) {
//							byte[] newFeature = new byte[724];
//							System.arraycopy(oriFeature,0,newFeature,0,oriFeature.length);
//							solrDoc.addField("feature", Base64.encodeBase64String(newFeature));
//						} else {
							solrDoc.addField("feature", Base64.encodeBase64String(oriFeature));
//						}
						solrDoc.addField("camera", face.getSourceId());
						solrDoc.addField("version", face.getVersion());
						solrDoc.addField("age", face.getAge());
						solrDoc.addField("gender", face.getGender());
						solrDoc.addField("accessories", face.getAccessories());
						solrDoc.addField("race", face.getRace());
						face.setIndexed(1);
						
						documents.add(solrDoc);
						
						if(documents.size() >= 500) {
							while(true) {
								HttpSolrClient solrServer = solrHostQueue.poll();
								try {

									solrServer.add(documents);
									solrServer.commit(true, true, true);

									total += documents.size();
									LOG.info(DateUtil.getformatDate(System.currentTimeMillis()) + "：" + solrServer.getBaseURL()
											+ " 成功索引 " + documents.size() + " 条数据");
									last_log_content="true";//成功时将上次日志打印内容设为true
									
									solrHostQueue.add(solrServer);
									documents.clear();
									break;
								} catch(Exception ee) {
									solrHostQueue.add(solrServer);
//									LOG.error("Error connect Solr Server.", ee);
//									ee.printStackTrace();
									String log_content="Error connect Solr Server.";
									printLog(log_content,ee);
									
									
								}
							}
						}

					} catch (Exception e) {
//						LOG.error("Error reading image or indexing it.", e);
//						e.printStackTrace();
						String log_content="Error reading image or indexing it.";
						printLog(log_content,e);
					}
				}


				if(documents.size() > 0) {
					while(true) {
						HttpSolrClient solrServer = solrHostQueue.poll();
						try {

							solrServer.add(documents);
							solrServer.commit(true, true, true);

							total += documents.size();
							LOG.info(DateUtil.getformatDate(System.currentTimeMillis()) + "：" + solrServer.getBaseURL()
									+ " 成功索引 " + documents.size() + " 条数据");
							last_log_content="true";
							
							solrHostQueue.add(solrServer);
							documents.clear();
							break;
						} catch(Exception ee) {
							solrHostQueue.add(solrServer);
//							LOG.error("Error connect Solr Server.", ee);
//							ee.printStackTrace();
							String log_content="Error connect Solr Server.";
							printLog(log_content,ee);
						}
					}
				}

				long executeTime = System.currentTimeMillis() - startTime;

				// IndexFaceRecord faceRecord = new
				// IndexFaceRecord(recordFaceId, executeTime, "index");
				// recordRepository.save(faceRecord);
				this.faceService.update(faceList);
				// recordRepository.updateFaceIndexed(startFaceId,
				// recordFaceId);
//				LOG.info("Finished indexing ...");
				LOG.info("Index face total : " + total + "   Index cost time：" + executeTime + "ms");
			} catch (Throwable e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断是否需要打印日志
	 * @param log_content 日志内容
	 * @param ee 异常
	 */
	private void printLog(String log_content, Exception ee) {
		if(!log_content.equals(last_log_content)){//异常状态切换时打印日志
			LOG.error(log_content, ee);
			last_log_content=log_content;
			last_log_print_time=System.currentTimeMillis();
		}else if(System.currentTimeMillis()-last_log_print_time>=diff_time){
			LOG.error(log_content, ee);
			last_log_print_time=System.currentTimeMillis();
		}
		
	}

}