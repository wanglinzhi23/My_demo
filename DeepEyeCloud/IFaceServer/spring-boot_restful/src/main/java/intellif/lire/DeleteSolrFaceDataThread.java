package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.IndexFaceRecordDao;
import intellif.service.CameraServiceItf;
import intellif.service.FaceServiceItf;
import intellif.settings.ServerSetting;
import intellif.utils.SolrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteSolrFaceDataThread extends Thread {

    private static Logger LOG = LogManager.getLogger(DeleteSolrFaceDataThread.class);
    private  Long solrConfigCount = 0L;
    
    @Autowired
    private FaceServiceItf faceService;

    @Autowired
    private CameraServiceItf cameraService;

    @Autowired
    private IndexFaceRecordDao recordRepository;

    private List<String> solrUrlList = new ArrayList<String>();
    public static ConcurrentLinkedQueue<Long> faceQueue = new ConcurrentLinkedQueue<Long>();
    public static ConcurrentLinkedQueue<Long> searchFaceQueue = new ConcurrentLinkedQueue<Long>();
    
    
    @SuppressWarnings("deprecation")
    @PostConstruct
    public void setUp() {
        solrConfigCount = recordRepository.totalSolrConfigCount();
        List<Object> solrServerCameras = recordRepository.getSolrServerWithCameras();
        for (Object object : solrServerCameras) {
            solrUrlList.add(object.toString());
        }

    }

    //@Scheduled(fixedDelay = 5000)
    public void run() {
            Long configCount = recordRepository.totalSolrConfigCount();
            if (!solrConfigCount.equals(configCount)) {
                solrUrlList.clear();
                setUp();
            }
            Long id = faceQueue.poll();
            if(null != id){
                for(String url : solrUrlList){
                    try{
                        String filter = "id:"+id;
                        SolrUtil.clearSolrCoreIndex(url, filter);
                    }catch(Exception e){
                        LOG.error("delete solr intellsuion face data  error,client:"+url+",faceId:"+id);
                    }
                }
            }
          
           Long sId = searchFaceQueue.poll();
           String sUrl = null;
           if(null != sId){
               try{
                   sUrl = ServerSetting.getSolrServer()+GlobalConsts.coreMap.get(GlobalConsts.SEARCH_INFO_TYPE);
                   String filter = "id:"+sId;
                   SolrUtil.clearSolrCoreIndex(sUrl, filter);
               }catch(Exception e){
                   LOG.error("delete solr searchinfo face data  error,client:"+sUrl+",faceId:"+sId);
               }
           }
 }
    
}