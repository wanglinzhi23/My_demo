package intellif.share.service.impl;

import intellif.consts.GlobalConsts;
import intellif.dao.BlackDetailDao;
import intellif.dao.SolrConfigDao;
import intellif.database.entity.BlackDetail;
import intellif.database.entity.CameraInfo;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceResultPKDto;
import intellif.dto.ProcessInfo;
import intellif.dto.SearchFaceDto;
import intellif.service.IFaceSdkServiceItf;
import intellif.service.UserServiceItf;
import intellif.settings.ServerSetting;
import intellif.share.service.ShareSolrDataServiceItf;
import intellif.utils.FaceResultDtoComparable;
import intellif.utils.FileUtil;
import intellif.utils.FunctionUtil;
import intellif.utils.MemcachedSpace;
import intellif.database.entity.BankMatchResultTuple;
import intellif.database.entity.BlackDetailRealName;
import intellif.database.entity.SolrConfigInfo;
import intellif.zoneauthorize.service.ZoneAuthorizeServiceItf;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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
public class ShareSolrDataServiceImpl implements ShareSolrDataServiceItf {

    private static Logger LOG = LogManager.getLogger(ShareSolrDataServiceImpl.class);
    // private static HttpSolrClient server=null;
    private static Map<Long, HttpSolrClient> serverMap = new HashMap<Long, HttpSolrClient>();
    private static Map<String, HttpSolrClient> urlServerMap = new HashMap<String, HttpSolrClient>();
    private static List<HttpSolrClient> allServer = new ArrayList<HttpSolrClient>();
    private static String url = ServerSetting.getSolrServer();
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 200, 5, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE), new ThreadPoolExecutor.CallerRunsPolicy());
    private static final String RESULT_DOCS = "docs";
    private static final String RESULT_TOTAL = "result_total";
    private static int maxSize = ServerSetting.getSolrResultMaxSize();

    @Autowired
    SolrConfigDao solrConfigRepository;
    @Autowired
    private BlackDetailDao _blackDetailDao;
    @Autowired
    private IFaceSdkServiceItf iFaceSdkServiceItf;
    @Autowired
    private UserServiceItf userService;
    @Autowired
    private MemcachedSpace memcachedSpace;

    @Autowired
    ZoneAuthorizeServiceItf zoneAuthorizeService;

    @Override
    public HttpSolrClient getServer(String core) throws MalformedURLException {
        if (core == null)
            core = "otherinfo";
        String solrUrl = url + core;
        if (!urlServerMap.containsKey(solrUrl)) {
            synchronized (this) {
                if (!urlServerMap.containsKey(solrUrl)) {
                    HttpSolrClient server = new HttpSolrClient(solrUrl);
                    server.setSoTimeout(ServerSetting.getSolrSearchTimeOutTime()); // socket
                                                                                   // read
                                                                                   // timeout
                    // server.setConnectionTimeout(30000);
                    server.setConnectionTimeout(ServerSetting.getSolrServerConnectOutTime());
                    server.setDefaultMaxConnectionsPerHost(100);
                    server.setMaxTotalConnections(100);
                    server.setFollowRedirects(false); // defaults to false
                    // allowCompression defaults to false.
                    // Server side must support gzip or deflate for this to have
                    // any effect.
                    server.setAllowCompression(true);
                    server.setMaxRetries(1); // defaults to 0. > 1 not
                                             // recommended.
                    urlServerMap.put(solrUrl, server);
                }
            }
        }
        return urlServerMap.get(solrUrl);
    }

    @Override
    public HttpSolrClient getServer(long source_id) throws MalformedURLException {
        if (!serverMap.containsKey(source_id)) {
            List<SolrConfigInfo> solrConfigList = solrConfigRepository.findBySourceId(source_id);
            if (solrConfigList.size() > 0) {
                if (!urlServerMap.containsKey(solrConfigList.get(0).getServerUrl())) {
                    HttpSolrClient server = new HttpSolrClient(solrConfigList.get(0).getServerUrl());
                    server.setSoTimeout(ServerSetting.getSolrSearchTimeOutTime()); // socket
                                                                                   // read
                                                                                   // timeout
                    // server.setConnectionTimeout(30000);
                    server.setConnectionTimeout(ServerSetting.getSolrServerConnectOutTime());
                    server.setDefaultMaxConnectionsPerHost(100);
                    server.setMaxTotalConnections(100);
                    server.setFollowRedirects(false); // defaults to false
                    // allowCompression defaults to false.
                    // Server side must support gzip or deflate for this to have
                    // any effect.
                    server.setAllowCompression(true);
                    server.setMaxRetries(1); // defaults to 0. > 1 not
                                             // recommended.
                    urlServerMap.put(solrConfigList.get(0).getServerUrl(), server);
                }
                serverMap.put(source_id, urlServerMap.get(solrConfigList.get(0).getServerUrl()));
            }
        }
        return serverMap.get(source_id);
    }

    public synchronized List<HttpSolrClient> getAllServer() throws MalformedURLException {
        if (allServer.size() == 0) {
            Iterable<SolrConfigInfo> solrConfigList = solrConfigRepository.findAll();
            Set<String> urlSet = new HashSet<String>();
            for (SolrConfigInfo solrConfigInfo : solrConfigList) {
                urlSet.add(solrConfigInfo.getServerUrl());
            }
            for (String url : urlSet) {
                if (!urlServerMap.containsKey(url)) {
                    HttpSolrClient server = new HttpSolrClient(url);
                    server.setSoTimeout(ServerSetting.getSolrSearchTimeOutTime()); // socket
                                                                                   // read
                                                                                   // timeout
                    // server.setConnectionTimeout(30000);
                    server.setConnectionTimeout(ServerSetting.getSolrServerConnectOutTime());
                    server.setDefaultMaxConnectionsPerHost(100);
                    server.setMaxTotalConnections(100);
                    server.setFollowRedirects(false); // defaults to false
                    // allowCompression defaults to false.
                    // Server side must support gzip or deflate for this to have
                    // any effect.
                    server.setAllowCompression(true);
                    server.setMaxRetries(1); // defaults to 0. > 1 not
                                             // recommended.
                    urlServerMap.put(url, server);
                }
                allServer.add(urlServerMap.get(url));
            }
        }
        return allServer;
    }

    // @Override
    // @ReadThroughSingleCache(namespace = "face_search_bycamera", expiration =
    // 600)
    // public TreeMap<Long, List<FaceResultDto>>
    // searchFaceByFaceIdInAll(@ParameterValueKeyProvider String key, String
    // faceFeature, float scoreThreshold, int type) throws Exception {
    // TreeMap<Long, List<FaceResultDto>> faceRsMap = new TreeMap<Long,
    // List<FaceResultDto>>();
    // SolrQuery query = new SolrQuery();
    // query.set("iff","true");
    // query.set("threshold", scoreThreshold+"");
    // query.set("fq","type:"+type);
    // query.set("feature",faceFeature);
    // query.set("rows",2000000000);
    // QueryResponse rsp =getInstance().getServer().query(query);
    // NamedList namedlist = rsp.getResponse();
    // ObjectMapper objectMapper = new ObjectMapper();
    // List<FaceResultDto> pojos =
    // objectMapper.convertValue(namedlist.get(RESULT_DOCS), new
    // TypeReference<List<FaceResultDto>>() { });
    // Collections.sort(pojos, new FaceResultDtoComparable("time"));
    // List<FaceResultDto> pojos = searchFaceByFaceIdInBank(key, faceFeature,
    // scoreThreshold, type);
    // if(type == 0) {
    // faceRsMap.put(-1L, pojos);
    // } else {
    // for(FaceResultDto face : pojos) {
    // if(faceRsMap.containsKey(face.getCamera())){
    // faceRsMap.get(face.getCamera()).add(face);
    // } else {
    // List<FaceResultDto> faceMap = new ArrayList<FaceResultDto>();
    // faceMap.add(face);
    // faceRsMap.put(face.getCamera(), faceMap);
    // }
    // }
    // }
    // return faceRsMap;
    // }

    // @Override
    // @ReadThroughSingleCache(namespace = "face_search_by_type", expiration =
    // 600)
    // public List<FaceResultDto> searchFaceByType(@ParameterValueKeyProvider
    // String key, String faceFeature, float scoreThreshold, int type) throws
    // Exception {
    // SolrQuery query = new SolrQuery();
    // query.set("iff","true");
    // query.set("threshold", scoreThreshold+"");
    // query.set("fq","type:"+type);
    // query.set("feature",faceFeature);
    // query.set("rows",2000000000);
    // QueryResponse rsp =getInstance().getServer().query(query);
    // NamedList<Object> namedlist = rsp.getResponse();
    // ObjectMapper objectMapper = new ObjectMapper();
    // List<FaceResultDto> pojos =
    // objectMapper.convertValue(namedlist.get(RESULT_DOCS), new
    // TypeReference<List<FaceResultDto>>() { });
    // Collections.sort(pojos, new FaceResultDtoComparable("time"));
    // return pojos;
    // }
    //
    // @Override
    // @ReadThroughSingleCache(namespace = "face_search_by_camera", expiration =
    // 600)
    // public List<FaceResultDto> searchFaceByCamera(@ParameterValueKeyProvider
    // String key, long cameraId, String faceFeature, float scoreThreshold, int
    // type) throws Exception {
    // SolrQuery query = new SolrQuery();
    // query.set("iff","true");
    // query.set("threshold", scoreThreshold+"");
    // query.set("fq","camera:"+cameraId);
    // query.set("feature",faceFeature);
    // query.set("rows",2000000000);
    //// query.addFilterQuery("camera:"+cameraId+" AND time:[NOW-30MINUTE TO
    // *]");
    // QueryResponse rsp =getInstance().getServer().query(query);
    // NamedList<Object> namedlist = rsp.getResponse();
    // ObjectMapper objectMapper = new ObjectMapper();
    // List<FaceResultDto> pojos =
    // objectMapper.convertValue(namedlist.get(RESULT_DOCS), new
    // TypeReference<List<FaceResultDto>>() { });
    // Collections.sort(pojos, new FaceResultDtoComparable("score"));
    // return pojos;
    // }

    @Override
    @ReadThroughSingleCache(namespace = "face_search_by_type", expiration = 600)
    public List<FaceResultDto> searchFaceByType(@ParameterValueKeyProvider final String key, final String faceFeature, float scoreThreshold, int type)
            throws Exception {
        FileUtil.log("key:" + key + " type:" + type + " 未使用缓存!");

        final SolrQuery query = new SolrQuery();
        query.setRequestHandler("/topsearch");
        query.set("iff", "true");
        query.set("fq", "type:" + type);
        query.set("threshold", scoreThreshold + "");
        query.set("feature", faceFeature);
        query.set("featureAmount",faceFeature.split(";").length);
        query.set("rows", Math.min(1000, maxSize));
        // query.addFilterQuery("type:"+type+" AND time:[* TO
        // 2016-01-27T16:00:00Z]");

        final List<FaceResultDto> resultList = Collections.synchronizedList(new ArrayList<FaceResultDto>());
        final List<Integer> finishList = Collections.synchronizedList(new ArrayList<Integer>());
        final StringBuffer logBuffer = new StringBuffer();
        logBuffer.append("检索记录：" + query.get("fq") + "\r\n");

        if (type < 1 || type > 2) {
            HttpSolrClient solrServer = getServer(GlobalConsts.coreMap.get(type));
			query.set("rows", ServerSetting.getSolrStaticResultMaxSize());
			query.set("useRabbit", ServerSetting.isUseRabbit());
            long now = System.currentTimeMillis();
            QueryResponse rsp = solrServer.query(query);
            NamedList<Object> namedlist = rsp.getResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
            });
            resultList.addAll(pojos);
            logBuffer.append(
                    "	" + faceFeature.substring(0, 100) + " | " + solrServer.getBaseURL() + " ---> " + (System.currentTimeMillis() - now) + " ms" + "\r\n");
        } else {
            int num = 0;
            List<HttpSolrClient> allServer = getAllServer();
            for (final HttpSolrClient server : allServer) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        try {
                            long now = System.currentTimeMillis();
                            QueryResponse rsp = server.query(query);
                            NamedList<Object> namedlist = rsp.getResponse();
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
                            });
                            resultList.addAll(pojos);
                            logBuffer.append("	" + faceFeature.substring(0, 100) + " | " + server.getBaseURL() + " ---> " + (System.currentTimeMillis() - now)
                                    + " ms" + "\r\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            finishList.add(1);
                        }
                    }
                });
                num++;
            }

            // 等待任务线程全部执行完毕
            while (finishList.size() < num) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        LOG.debug(logBuffer.toString());
        List<FaceResultDto> tempList = null;
        if(type == GlobalConsts.FACE_INFO_TYPE || type == GlobalConsts.INSTATION_INFO_TYPE){
        // Collections.sort(resultList, new FaceResultDtoComparable("time"));
        tempList = zoneAuthorizeService.filterById(CameraInfo.class, resultList, null);
        }else{
            tempList = resultList;
        }
        LOG.info("search result num:" + tempList.size());
        return tempList;
    }


    @Override
    @ReadThroughSingleCache(namespace = "face_search_by_type", expiration = 600)

	public List<FaceResultDto> searchFaceByType(@ParameterValueKeyProvider final String key, final String faceFeature, SearchFaceDto sfd) throws Exception {
		int type = sfd.getType();
		int age = sfd.getAge();

	    int accessories = sfd.getAccessories();
	    int gender = sfd.getGender();
	    String race = sfd.getRace();

		FileUtil.log("key:"+key+" type:"+type+" 未使用缓存!");
        long begin = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String searchStartTime = sdf.format(new Date(sdf.parse(sfd.getStarttime()).getTime()-3600*1000*8));
		searchStartTime = searchStartTime.replace(" ", "T");
		String searchEndTime = sdf.format(new Date(sdf.parse(sfd.getEndtime()).getTime()-3600*1000*8));
		searchEndTime = searchEndTime.replace(" ", "T");
		
		final SolrQuery query = new SolrQuery();
		query.setRequestHandler("/topsearch");
		query.set("iff","true");
		query.set("threshold", sfd.getScoreThreshold()+"");
		query.set("feature",faceFeature);
		query.set("featureAmount",faceFeature.split(";").length);
//		query.addFilterQuery("type:"+type+" AND time:[* TO 2016-01-27T16:00:00Z]");
		
		final List<FaceResultDto> resultList = Collections.synchronizedList(new ArrayList<FaceResultDto>());
		final List<Integer> finishList = Collections.synchronizedList(new ArrayList<Integer>());
		final StringBuffer logBuffer = new StringBuffer();

		if(type < 1 || type > 2) {
			query.set("fq","type:"+type);
			query.set("rows", ServerSetting.getSolrStaticResultMaxSize());
			query.set("useRabbit", ServerSetting.isUseRabbit());
			logBuffer.append("检索记录："+query.get("fq")+"\r\n");
            HttpSolrClient solrServer = getServer(GlobalConsts.coreMap.get(type));
			long now = System.currentTimeMillis();
            QueryResponse rsp = solrServer.query(query);
			NamedList<Object> namedlist = rsp.getResponse();
			ObjectMapper objectMapper = new ObjectMapper();
			List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() { });
			resultList.addAll(pojos);
			logBuffer.append("	"+faceFeature.substring(0, 100)+" | "+solrServer.getBaseURL() + " ---> "+(System.currentTimeMillis()-now)+" ms"+"\r\n");
		} else {
			query.set("sort", sfd.getSort());
			String filterStr = "type:"+type;
			if (age == 1) {
                filterStr += " AND age:[1 TO 3]";
            }
            if (age == 4) {
                filterStr += " AND age:[4 TO 5]";
            }
            if (age == 7) {
                filterStr += " AND age:[6 TO 7]";
            }
            if (age == 9) {
                filterStr += " AND age:[8 TO 9]";
            }
            
            if (gender == 1 || gender == 2) {
                filterStr += " AND gender:" + gender;
            }
            
            if (accessories != 0) {
                filterStr += " AND accessories:" + accessories;
            }
            

            if (race != null && !race.isEmpty()) {

                if (!race.split(",")[0].equals("0")) {
                    filterStr += " AND race:" + race;
                }
            }

			filterStr += " AND time:[" + searchStartTime + "Z TO " + searchEndTime + "Z]";
			if (0 == sfd.getQuality()) {
				filterStr += " AND quality:" + sfd.getQuality();
			} else if (0 > sfd.getQuality()) {
				filterStr += " AND quality:{* TO 0}";
			}

			query.addFilterQuery(filterStr);

            logBuffer.append("检索记录：" + query.get("fq") + "\r\n");
            int num = 0;
            List<HttpSolrClient> allServer = getAllServer();
            query.set("rows", Math.min(1000, maxSize));
            System.out.println(sdf.format(new Date()) + " -> " + faceFeature.substring(0, 100) + " | 开始发起Solr任务!!!");
            for (final HttpSolrClient server : allServer) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        try {
                            long now = System.currentTimeMillis();
                            QueryResponse rsp = server.query(query);
                            NamedList<Object> namedlist = rsp.getResponse();
                            ObjectMapper objectMapper = new ObjectMapper();

							List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() { });
							resultList.addAll(pojos);
							logBuffer.append(sdf.format(new Date())+"	"+faceFeature.substring(0, 100)+" | "+server.getBaseURL() + " ---> "+(System.currentTimeMillis()-now)+" ms"+"\r\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            finishList.add(1);
                        }
                    }
                });
                num++;
            }

            System.out.println(sdf.format(new Date()) + " -> " + faceFeature.substring(0, 100) + " | 开始等待Solr任务!!!");
            // 等待任务线程全部执行完毕
            while (finishList.size() < num) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        LOG.debug(logBuffer.toString());
        // Collections.sort(resultList, new FaceResultDtoComparable("time"));
        LOG.info("search real result num:" + resultList.size() +" search cost time:" + (System.currentTimeMillis()-begin));

        begin = System.currentTimeMillis();
        List<FaceResultDto> tempList = null;
        Collections.sort(resultList, new FaceResultDtoComparable(sfd.getSort()));
        if (resultList.size() > maxSize) {
			tempList = new ArrayList<FaceResultDto>(resultList.subList(0, maxSize));
        } else  {
            tempList = resultList;
        }
        
        if(type == GlobalConsts.FACE_INFO_TYPE || type == GlobalConsts.INSTATION_INFO_TYPE){
         tempList = zoneAuthorizeService.filterById(CameraInfo.class, tempList, null);
        }

        LOG.info("search filter result num:" + tempList.size() +" filter cost time:" + (System.currentTimeMillis()-begin));
        return tempList;
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
		final SolrQuery query = new SolrQuery();
		query.setRequestHandler("/topsearch");
		query.set("iff","true");
		query.set("threshold", scoreThreshold+"");
//		query.set("fq","type:"+type);
		query.set("feature",faceFeature);
		query.set("featureAmount",faceFeature.split(";").length);
		query.set("rows",Math.min(1000, maxSize));
//		query.addFilterQuery("type:"+type+" AND time:["+searchStartTime+"Z TO "+searchEndTime+"Z]");

        final List<FaceResultDto> resultList = Collections.synchronizedList(new ArrayList<FaceResultDto>());
        final List<Integer> finishList = Collections.synchronizedList(new ArrayList<Integer>());
		
		if(type < 1 || type > 2) {
			query.set("fq","type:"+type);
			query.set("useRabbit", ServerSetting.isUseRabbit());
			query.set("rows", ServerSetting.getSolrStaticResultMaxSize());
            HttpSolrClient solrServer = getServer(GlobalConsts.coreMap.get(type));
            QueryResponse rsp = solrServer.query(query);
			NamedList<Object> namedlist = rsp.getResponse();
			ObjectMapper objectMapper = new ObjectMapper();
			List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() { });
			resultList.addAll(pojos);
		} else {
			query.addFilterQuery("type:"+type+" AND time:["+searchStartTime+"Z TO "+searchEndTime+"Z]");
			int num = 0;
			List<HttpSolrClient> allServer = getAllServer();
			for(final HttpSolrClient server : allServer) {
				threadPool.submit(new Runnable() {
					public void run() {
						try {
							QueryResponse rsp = server.query(query);
							NamedList<Object> namedlist = rsp.getResponse();
							ObjectMapper objectMapper = new ObjectMapper();
							List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() { });
                            resultList.addAll(pojos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            finishList.add(1);
                        }
                    }
                });
                num++;
            }

            // 等待任务线程全部执行完毕
            while (finishList.size() < num) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        List<FaceResultDto> tempList = null;
        // Collections.sort(resultList, new FaceResultDtoComparable("time"));
        if(type == GlobalConsts.FACE_INFO_TYPE || type == GlobalConsts.INSTATION_INFO_TYPE){
            tempList = zoneAuthorizeService.filterById(CameraInfo.class, resultList, null);
        }else{
            tempList = resultList;
        }
        LOG.info("search result num:" + tempList.size() +" cost time:" + (System.currentTimeMillis()-now));
        return tempList;
    }

    @Override
    @ReadThroughSingleCache(namespace = "face_search_by_camera", expiration = 600)
    public List<FaceResultDto> searchFaceByCamera(@ParameterValueKeyProvider String key, long cameraId, String faceFeature, float scoreThreshold, int type, int hours)
            throws Exception {
        FileUtil.log("key:" + key + " type:" + type + " 未使用缓存!");

        final SolrQuery query = new SolrQuery();
        query.setRequestHandler("/topsearch");
        query.set("iff", "true");
        query.set("threshold", scoreThreshold + "");
        // query.set("fq","camera:"+cameraId);
        query.set("feature", faceFeature);
        query.set("featureAmount",faceFeature.split(";").length);
        query.set("rows",Math.min(1000, maxSize));
        int min = 60*hours;
        query.addFilterQuery("camera:" + cameraId + " AND time:[NOW-"+min+"MINUTE TO *]");

        final List<FaceResultDto> resultList = Collections.synchronizedList(new ArrayList<FaceResultDto>());
        final List<Integer> finishList = Collections.synchronizedList(new ArrayList<Integer>());
        int num = 0;

        List<HttpSolrClient> allServer = getAllServer();
        for (final HttpSolrClient server : allServer) {
            threadPool.submit(new Runnable() {
                public void run() {
                    try {
                        QueryResponse rsp = server.query(query);
                        NamedList<Object> namedlist = rsp.getResponse();
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<FaceResultDto> pojos = objectMapper.convertValue(namedlist.get(RESULT_DOCS), new TypeReference<List<FaceResultDto>>() {
                        });
                        resultList.addAll(pojos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        finishList.add(1);
                    }
                }
            });
            num++;
        }

        // 等待任务线程全部执行完毕
        int time = 0;
        while (finishList.size() < num) {
            try {
                Thread.sleep(100);
                time++;
            } catch (InterruptedException e) {
                e.printStackTrace();
			}
    		if(time >= 150) break;
    	}

        Collections.sort(resultList, new FaceResultDtoComparable("score"));
        List<FaceResultDto> tempList = null;
        if(type == GlobalConsts.FACE_INFO_TYPE || type == GlobalConsts.INSTATION_INFO_TYPE){
         tempList = zoneAuthorizeService.filterById(CameraInfo.class, resultList, null);
        }else{
            tempList = resultList;
        }
        LOG.info("search result num:" + tempList.size());
        return tempList;
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
//			final String faceFeature = blackDetail.getBase64FaceFeature();
			final String faceFeature = memcachedSpace.getFacefeatureFromId(GlobalConsts.BLACK_INFO_TYPE+":"+blackDetail.getId(), blackDetail.getId(), GlobalConsts.BLACK_INFO_TYPE);
			if(faceFeature == null || faceFeature.equals("")) {
				process.incrementFailedNumWithLock();
				continue;
			}
			
			final HttpSolrClient solrClient = getServer(GlobalConsts.coreMap.get(staticbankid));
			solrClient.setSoTimeout(ServerSetting.getSolrSearchTimeOutTime());  // socket read timeout  
			solrClient.setConnectionTimeout(ServerSetting.getSolrServerConnectOutTime());  
			solrClient.setDefaultMaxConnectionsPerHost(100);  
			solrClient.setMaxTotalConnections(100);
			solrClient.setFollowRedirects(false);  // defaults to false  
			solrClient.setAllowCompression(true);  
			tasklist.add(threadPool.submit(() -> {
				try {
					SolrQuery query = new SolrQuery();
					query.setRequestHandler("/topsearch");
					query.set("iff","true");
					query.set("rows",matchnum);
					query.set("type", 1);
					query.set("feature",faceFeature);
		            QueryResponse rsp = solrClient.query(query);
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

	@Override
	public void deleteById(String core, List<String> ids) throws Exception {
		getServer(core).deleteById(ids);
		getServer(core).commit();
	}
	
	@Override
	public void deleteById(String core, String id) throws Exception {
		getServer(core).deleteById(id);
		getServer(core).commit();
	}


	@Override
	public void addBlackDetail(String core, BlackDetail black){
		List<BlackDetail> bDetailList = _blackDetailDao.findById(black.getId());
		if(null != bDetailList&& !bDetailList.isEmpty()){
			BlackDetail bd = bDetailList.get(0);
			if(null == bd.getFaceFeature() || bd.getFaceFeature().length!=724) return;
		try {
			SolrInputDocument solrDoc = new SolrInputDocument();
			solrDoc.addField("id", bd.getId());
				solrDoc.addField("time", bd.getCreated());
				solrDoc.addField("file", bd.getImageData());
				solrDoc.addField("type", GlobalConsts.BLACK_BANK_TYPE);
				solrDoc.addField("feature", Base64.encodeBase64String(bd.getFaceFeature()));
				solrDoc.addField("camera", bd.getFromPersonId());
				solrDoc.addField("version", bd.getVersion());
				getServer(core).add(solrDoc);
				getServer(core).commit(true, true, true);
				bd.setIndexed(1);
				_blackDetailDao.save(bd);
				LOG.info("嫌疑人人脸已经成功索引 blackid:"+bd.getId());
			} catch (Exception e) {
				LOG.error("Error reading image or indexing it.");
				e.printStackTrace();
			}
		}
    
	}
	
}
