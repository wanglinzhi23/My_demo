package intellif.lire;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import intellif.consts.GlobalConsts;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.settings.ServerSetting;
import intellif.settings.SolrCloudSetting;
import intellif.utils.CommonUtil;
import intellif.utils.DateUtil;
import intellif.utils.FileUtil;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;

@Component
public class CloudFaceIndexThread extends Thread {

	private static Logger LOG = LogManager.getLogger(CloudFaceIndexThread.class);

	private static final CloudSolrClient SOLR_CLOUD = new CloudSolrClient(SolrCloudSetting.getZkServers());

	static {
		SOLR_CLOUD.setDefaultCollection("intellifusion");
	}

	@Autowired
	private FaceServiceItf faceService;
	@Autowired
	private CameraServiceItf cameraService;

//	@Scheduled(fixedDelay = 1000)
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		Long rate = ServerSetting.getIndexRate() * 1000;
		LOG.info("solr.index.rate:" + rate);
		while (true) {
			Date now = new Date();
			Map<Long, Integer> inStationCameraIdMap = new HashMap<Long, Integer>();
			try {
				Iterable<CameraInfo> cameraList = this.cameraService.findInStation();
				for (CameraInfo camera : cameraList) {
					if (camera.getInStation() == 1)
						inStationCameraIdMap.put(camera.getId(), 1);
				}
//				Date date = Calendar.getInstance().getTime();
//				long time = date.getTime() / 1000;
//				long time1 = time - 7 * 24 * 60 * 60;
//				Date date1 = new Date(time1 * 1000);
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				String s = sdf.format(date1);
//				Date date2 = sdf.parse(s);

				String indexTime;
				if(!new File("indexTime").exists()) {
					System.out.println("IndexTime is not exists..");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					indexTime = sdf.format(new Date())+" 00:00:00";
					System.out.println("new IndexTime is "+indexTime+"..");
				} else {
					List<String> values = FileUtil.readFile("indexTime");
					if(values.size()<=0) {
						System.out.println("IndexTime is error..");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						indexTime = sdf.format(new Date())+" 00:00:00";
						System.out.println("new IndexTime is "+indexTime+"..");
					} else {
						indexTime = values.get(0);
						System.out.println("IndexTime is "+indexTime+"..");
					}
				}
				
				String nextTime = DateUtil.getformatDate(this.faceService.indexNextTime(indexTime).getTime());
				List<FaceInfo> faceList = this.faceService.findUnIndexed(indexTime, nextTime);

				LOG.info("Start Face indexing ...");
				int i = 0;
				List<FaceInfo> saveList = new ArrayList<FaceInfo>();
				List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

				for (FaceInfo face : faceList) {
					if (null == face.getFaceFeature() || face.getFaceFeature().length < 700)
						continue;
					try {
						SolrInputDocument solrDoc = new SolrInputDocument();
						ByteBuffer buffer = ByteBuffer.allocate(8);
						buffer.putLong(0, face.getId());
						solrDoc.addField("docid", Base64.encodeBase64String(buffer.array()));
						solrDoc.addField("id", face.getId());
	                    solrDoc.addField("createTimeNS", System.nanoTime());
						solrDoc.addField("time", face.getTime());
						solrDoc.addField("file", face.getImageData());
						if (inStationCameraIdMap.containsKey(face.getSourceId())) {
							solrDoc.addField("type", 2);
						} else {
							solrDoc.addField("type", 1);
						}
						solrDoc.addField("feature", Base64.encodeBase64String(face.getFaceFeature()));
						solrDoc.addField("camera", face.getSourceId());
						solrDoc.addField("version", face.getVersion());
						solrDoc.addField("accessories", face.getAccessories());
						solrDoc.addField("age", face.getAge());
						solrDoc.addField("gender", face.getGender());
						solrDoc.addField("race", face.getRace());
						docs.add(solrDoc);

						face.setIndexed(1);
						i++;
						saveList.add(face);
//						if (i % 5000 == 0) {
//							SOLR_CLOUD.add(docs);
//							SOLR_CLOUD.commit(true, true, true);
//							this.faceService.update(saveList);
//							docs.clear();
//							saveList.clear();
//							LOG.info(System.currentTimeMillis() + "：抓拍人已经成功索引 " + i + " 条数据");
//						}
					} catch (Exception e) {
						LOG.error("Error indexing face id："+face.getId(), e);
					}
				}

				if  (!docs.isEmpty()) {
					SOLR_CLOUD.add(docs);
					SOLR_CLOUD.commit(true, true, true);

					long code =CommonUtil.getCode(faceList.get(0).getId());
					if(!this.faceService.updateIndexedByTime(code, indexTime, nextTime)) {
						LOG.error("Error update DataBase table t_face_"+code+" time from "+indexTime+" to "+nextTime);
					}
				}

				new File("indexTime").delete();
				FileUtil.writeStringToFile(nextTime, "indexTime");
				LOG.info("Finished Face indexing ...");
				LOG.info("Face Index cost time：" + (new Date().getTime() - now.getTime()) + " with docs size："+docs.size());
			} catch (Throwable e) {
				LOG.error("Error input SolrCloud or update DataBase.", e);
			}
			try {
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				LOG.error("error on create solr index", e);
			}
		}
	}

}
