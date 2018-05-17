package intellif.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dao.SolrConfigDao;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceResultPKDto;
import intellif.dto.ProcessInfo;
import intellif.enums.SolrCloudUntils;
import intellif.service.SolrCloudDataServiceItf;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.FileUtil;
import intellif.utils.FunctionUtil;
import intellif.database.entity.BankMatchResultTuple;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.BlackDetailRealName;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

@Service
public class SolrCloudDataServerImpl implements SolrCloudDataServiceItf{  

	private static Logger LOG = LogManager.getLogger(SolrCloudDataServerImpl.class);
	private static final String RESULT_DOCS = "docs";
	private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 200, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE), new ThreadPoolExecutor.CallerRunsPolicy());   

	@Autowired
	SolrConfigDao solrConfigRepository;

	@Override
	@ReadThroughSingleCache(namespace = "face_search_by_type", expiration = 600)
	public List<FaceResultDto> searchFaceByType(@ParameterValueKeyProvider final String key, final String faceFeature, float scoreThreshold, int type) throws Exception {
		FileUtil.log("key:"+key+" type:"+type+" 未使用缓存!");
		long now = System.currentTimeMillis();

		final SolrQuery query = SolrCloudUntils.FACE_CLOUD.getQuery();
		query.set("threshold", scoreThreshold + "");
		query.set("fq", "type:" + type);
		query.set("feature", faceFeature);

		QueryResponse rsp = GlobalConsts.cloudCoreMap.get(type).getCloudClient().query(query);
		NamedList<Object> namedlist = rsp.getResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		List<FaceResultDto> resultList = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
		});

		LOG.debug(key + " ---> " +query.get("fq")+" ---> "+(System.currentTimeMillis()-now)+" ms ---> search result num:"+resultList.size());
		return resultList;
	}

	@Override
	@ReadThroughSingleCache(namespace = "face_search_by_type", expiration = 600)
	public List<FaceResultDto> searchFaceByType(@ParameterValueKeyProvider final String key, final String faceFeature, float scoreThreshold, int type, String startTime, String endTime) throws Exception {
		FileUtil.log("key:"+key+" type:"+type+" 未使用缓存!");
		long now = System.currentTimeMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String searchStartTime = sdf.format(new Date(sdf.parse(startTime).getTime()-3600*1000*8));
		searchStartTime = searchStartTime.replace(" ", "T");
		String searchEndTime = sdf.format(new Date(sdf.parse(endTime).getTime()-3600*1000*8));
		searchEndTime = searchEndTime.replace(" ", "T");

		final SolrQuery query = SolrCloudUntils.CID_CLOUD.getQuery();
		query.set("threshold", scoreThreshold + "");
		query.set("feature", faceFeature);
		if(type < 1 || type > 2) {
			query.set("fq","type:"+type);
		} else {
			query.addFilterQuery("type:"+type+" AND time:["+searchStartTime+"Z TO "+searchEndTime+"Z]");
		}

		QueryResponse rsp = GlobalConsts.cloudCoreMap.get(type).getCloudClient().query(query);
		NamedList<Object> namedlist = rsp.getResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		List<FaceResultDto> resultList = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
		});

		LOG.debug(key + " ---> " +query.get("fq")+" ---> "+(System.currentTimeMillis()-now)+" ms ---> search result num:"+resultList.size());
		return resultList;
	}

	@Override
	@ReadThroughSingleCache(namespace = "face_search_by_time", expiration = 600)
	public List<FaceResultDto> searchFaceByTime(@ParameterValueKeyProvider String key, String faceFeature, float scoreThreshold, int type, String startTime, String endTime) throws Exception {
		FileUtil.log("key:"+key+" type:"+type+" 未使用缓存!");
		long now = System.currentTimeMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String searchStartTime = sdf.format(new Date(sdf.parse(startTime).getTime()-3600*1000*8));
		searchStartTime = searchStartTime.replace(" ", "T");
		String searchEndTime = sdf.format(new Date(sdf.parse(endTime).getTime()-3600*1000*8));
		searchEndTime = searchEndTime.replace(" ", "T");

		final SolrQuery query = SolrCloudUntils.FACE_CLOUD.getQuery();
		query.set("threshold", scoreThreshold + "");
		query.set("feature", faceFeature);
		if(type < 1 || type > 2) {
			query.set("fq","type:"+type);
		} else {
			query.addFilterQuery("type:"+type+" AND time:["+searchStartTime+"Z TO "+searchEndTime+"Z]");
		}

		QueryResponse rsp = GlobalConsts.cloudCoreMap.get(type).getCloudClient().query(query);
		NamedList<Object> namedlist = rsp.getResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		List<FaceResultDto> resultList = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
		});

		LOG.debug(key + " ---> " +query.get("fq")+" ---> "+(System.currentTimeMillis()-now)+" ms ---> search result num:"+resultList.size());
		return resultList;
	}

	@Override
	@ReadThroughSingleCache(namespace = "face_search_by_camera", expiration = 600)
	public List<FaceResultDto> searchFaceByCamera(@ParameterValueKeyProvider String key, long cameraId, String faceFeature, float scoreThreshold, int type) throws Exception {
		FileUtil.log("key:"+key+" type:"+type+" 未使用缓存!");
		long now = System.currentTimeMillis();

		final SolrQuery query = SolrCloudUntils.FACE_CLOUD.getQuery();
		query.set("threshold", scoreThreshold + "");
		query.set("feature", faceFeature);
		query.addFilterQuery("camera:"+cameraId+" AND time:[NOW-240MINUTE TO *]");

		QueryResponse rsp = GlobalConsts.cloudCoreMap.get(type).getCloudClient().query(query);
		NamedList<Object> namedlist = rsp.getResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		List<FaceResultDto> resultList = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
		});

		LOG.debug(key + " ---> " +query.get("fq")+" ---> "+(System.currentTimeMillis()-now)+" ms ---> search result num:"+resultList.size());

		Collections.sort(resultList, new FaceResultDtoComparable("score"));
		return resultList;
	}

	@Override
	public void deleteById(Integer type, List<String> ids) throws Exception {
		SolrCloudUntils cloud = GlobalConsts.cloudCoreMap.get(type);
		cloud.getCloudClient().deleteById(ids);
		cloud.getCloudClient().commit();
	}

	@Override
	public void deleteById(Integer type, String id) throws Exception {
		SolrCloudUntils cloud = GlobalConsts.cloudCoreMap.get(type);
		cloud.getCloudClient().deleteById(id);
		cloud.getCloudClient().commit();
	}

	@Override
	public void addBlackDetail(Integer type, BlackDetail black){
		if(null == black.getFaceFeature() || black.getFaceFeature().length!=724) return;
		try {
			SolrInputDocument solrDoc = new SolrInputDocument();
			solrDoc.addField("id", black.getId());
            solrDoc.addField("createTimeNS", System.nanoTime());
			solrDoc.addField("time", black.getCreated());
			solrDoc.addField("file", black.getImageData());
			solrDoc.addField("type", 0);
			solrDoc.addField("feature", Base64.encodeBase64String(black.getFaceFeature()));
			solrDoc.addField("camera", black.getFromPersonId());
			solrDoc.addField("version", black.getVersion());
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.putLong(0, black.getId());
			solrDoc.addField("docid", Base64.encodeBase64String(buffer.array()));

			SolrCloudUntils cloud = GlobalConsts.cloudCoreMap.get(type);
			cloud.getCloudClient().add(solrDoc);
			cloud.getCloudClient().commit(true, true, true);
			LOG.info("嫌疑人人脸已经成功索引");
		} catch (Exception e) {
			LOG.error("Error reading image or indexing it.");
			e.printStackTrace();
		}

	}
	
	@Override
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> bankMatch(List<BlackDetailRealName> blackDetailList, long targetbankid, int staticbankid, final long key, final int matchnum) throws Exception {
		if(blackDetailList == null) return null;
		final ConcurrentSkipListMap<Long, BankMatchResultTuple> resultmap = new ConcurrentSkipListMap<Long, BankMatchResultTuple>();
		
		final ProcessInfo process = GlobalConsts.bankMatchMap.get(key);
		
		List<Future<?>> tasklist = new ArrayList<>();
		
		for(final BlackDetailRealName blackDetail : blackDetailList) {
			List<FaceResultPKDto> dto = Collections.synchronizedList(new ArrayList<FaceResultPKDto>());
			resultmap.put(Long.valueOf(blackDetail.getId()), new BankMatchResultTuple(blackDetail.getImageData(), blackDetail.getRealName(), dto));
			final String faceFeature = blackDetail.getBase64FaceFeature();
			if(faceFeature == null || faceFeature.equals("")) {
				process.incrementFailedNumWithLock();
				continue;
			}
			
			tasklist.add(threadPool.submit(() -> {
				try {
					SolrQuery query = SolrCloudUntils.FACE_CLOUD.getQuery();
					query.setRequestHandler("/topsearch");
					query.set("rows",matchnum);
					query.set("type", 1);
					query.set("feature",faceFeature);
		    		QueryResponse rsp = GlobalConsts.cloudCoreMap.get(staticbankid).getCloudClient().query(query);
					NamedList<Object> namedlist = rsp.getResponse();
					ObjectMapper objectMapper = new ObjectMapper();
					List<FaceResultPKDto> pojos = objectMapper.convertValue(namedlist.get("docs"), new TypeReference<List<FaceResultPKDto>>() { });
					if(pojos != null) {
						for(FaceResultPKDto pkdto : pojos) {
							pkdto.setFilename(getFileName(pkdto.getFile()));
						}
						resultmap.get(Long.valueOf(blackDetail.getId())).getResultList().addAll(pojos);
					}
					process.incrementSuccessNumWithLock();
				} catch (Exception e) {
					e.printStackTrace();
					process.incrementFailedNumWithLock();
				} finally {
					try {
						//solrClient.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					GlobalConsts.bankMatchMap.put(key, process);
				}
			}));
		}
		// wait for tasks completion
		tasklist.forEach(FunctionUtil::waitTillThreadFinish);
		return resultmap;
	}
	
	public String getFileName(String filePath) {
		int length = filePath.split("/").length;
		String fileName = filePath.split("/")[length - 1];
		return fileName;
	}
}  