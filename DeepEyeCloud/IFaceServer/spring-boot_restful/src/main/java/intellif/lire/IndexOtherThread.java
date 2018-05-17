package intellif.lire;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.consts.GlobalConsts;
import intellif.dao.OtherDetailDao;
import intellif.service.SolrDataServiceItf;
import intellif.service.StaticBankServiceItf;
import intellif.database.entity.OtherDetail;

@Component
public class IndexOtherThread extends Thread {

    private static Logger LOG = LogManager.getLogger(IndexOtherThread.class);
   
    @Autowired
    private OtherDetailDao otherDetailRepository;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;
    @Autowired
	private StaticBankServiceItf staticBankService;
    //@Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(fixedRate = 60000)
    public void run() {
    	if(!GlobalConsts.run){
    		return;
    	}
    	Date now = new Date();
    	try {
    		do {
                List<OtherDetail> saveList = new ArrayList<OtherDetail>();
                List<SolrInputDocument> docsList = new ArrayList<SolrInputDocument>();
                Iterable<OtherDetail> otherList = this.otherDetailRepository.findUnIndexed();
                HttpSolrClient solrServer = _solrDataServiceItf.getServer(GlobalConsts.coreMap.get(GlobalConsts.CRIME_INFO_TYPE));
    			LOG.info("Start indexing ...");
    			int i = 0;
    			for (OtherDetail other : otherList) {
    				if(null == other.getFaceFeature() || other.getFaceFeature().length!=724) continue;
    				try {
    					SolrInputDocument solrDoc = new SolrInputDocument();
    					solrDoc.addField("id", other.getId());
    					solrDoc.addField("time", other.getCreated());
    					solrDoc.addField("file", other.getImageData());
    					solrDoc.addField("type", other.getZplxmc());
    					solrDoc.addField("feature", Base64.encodeBase64String(other.getFaceFeature()));
    					solrDoc.addField("camera", other.getFromCidId());
    
    					solrDoc.addField("version", other.getVersion());
    					docsList.add(solrDoc);
        				other.setIndexed(1);
        				saveList.add(other);
        				i++;
    					if (i % 3000 == 0) {
    						solrServer.add(docsList);
        					solrServer.commit(true, true, true);
    						List<Long> idList = saveList.stream().map(item -> item.getId()).collect(Collectors.toList());
    						staticBankService.updateIndexOfIds(GlobalConsts.T_NAME_OTHER_DETAIL, idList);
    						saveList.clear();
    						docsList.clear();
    						LOG.info("其他库数据已经成功索引 " + i + " 条数据");
    					}
    				} catch (Exception e) {
    					LOG.error("Error reading image or indexing it.",e);
    					e.printStackTrace();
    				}
    			}
    			if(!saveList.isEmpty()){
    				solrServer.add(docsList);
					solrServer.commit(true, true, true);
    				List<Long> idList = saveList.stream().map(item -> item.getId()).collect(Collectors.toList());
					staticBankService.updateIndexOfIds(GlobalConsts.T_NAME_OTHER_DETAIL, idList);
    			}
    			if(i==0) break;
    		} while(true);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	LOG.info("Finished indexing ...");
    	LOG.info("Index cost time：" + (new Date().getTime() - now.getTime()));
    }

}