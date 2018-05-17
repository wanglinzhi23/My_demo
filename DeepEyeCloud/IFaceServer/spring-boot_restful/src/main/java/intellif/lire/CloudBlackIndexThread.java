package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.BlackDetailDao;
import intellif.database.entity.BlackDetail;
import intellif.settings.SolrCloudSetting;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloudBlackIndexThread extends Thread {

    private static Logger LOG = LogManager.getLogger(CloudBlackIndexThread.class);

    private static final CloudSolrClient SOLR_CLOUD = new CloudSolrClient(SolrCloudSetting.getZkServers()); 
    static{								 
    	SOLR_CLOUD.setDefaultCollection("intellifusion");
	}
    
    @Autowired
    private BlackDetailDao blackDetailDao;

//    @Scheduled(fixedRate = 300000)
    public void run() {
    	if(!GlobalConsts.run){
    		return;
    	}
        Date now = new Date();
        try {
        	boolean haveValue;
        	do{
        		haveValue = false;
        		Iterable<BlackDetail> blackList = this.blackDetailDao.findUnIndexed();
        		LOG.info("Start black indexing ...");
        		int i = 0;
        		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        		for (BlackDetail black : blackList) {
        			if(null == black.getFaceFeature() || black.getFaceFeature().length<700) continue;
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

        				docs.add(solrDoc);
        				black.setIndexed(1);
        				i++;

        			} catch (Exception e) {
        				LOG.error("Error reading image or black indexing it.");
        				e.printStackTrace();
        			}
        		}
        		if (!docs.isEmpty()) {
            		haveValue = true;
        			SOLR_CLOUD.add(docs);
        			SOLR_CLOUD.commit(true, true, true);
        			blackDetailDao.save(blackList);
        		}
        	} while(haveValue);
        } catch (Throwable e) {
        	LOG.error("Error reading image or black indexing it.");
        	LOG.error("create solr index error for back", e);
        }
        LOG.info("Finished black indexing ...");
        LOG.info("Black Index cost time：" + (new Date().getTime() - now.getTime()));
    }

}
