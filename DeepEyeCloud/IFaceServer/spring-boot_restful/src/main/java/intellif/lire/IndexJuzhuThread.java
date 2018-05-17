package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.JuZhuDetailDao;
import intellif.service.SolrDataServiceItf;
import intellif.service.StaticBankServiceItf;
import intellif.database.entity.JuZhuDetail;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IndexJuzhuThread extends Thread {

    private static Logger LOG = LogManager.getLogger(IndexJuzhuThread.class);
   
    @Autowired
    private JuZhuDetailDao juzhuDetailRepository;
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
                List<JuZhuDetail> saveList = new ArrayList<JuZhuDetail>();
                List<SolrInputDocument> docsList = new ArrayList<SolrInputDocument>();
                Iterable<JuZhuDetail> juzhuList = this.juzhuDetailRepository.findUnIndexed();
                HttpSolrClient solrServer = _solrDataServiceItf.getServer(GlobalConsts.coreMap.get(GlobalConsts.JUZHU_INFO_TYPE));
    			LOG.info("Start indexing ...");
    			int i = 0;
    			for (JuZhuDetail juzhu : juzhuList) {
    				if(null == juzhu.getFaceFeature() || juzhu.getFaceFeature().length!=724) continue;
    				try {
    					SolrInputDocument solrDoc = new SolrInputDocument();
    					solrDoc.addField("id", juzhu.getId());
    					solrDoc.addField("time", juzhu.getCreated());
    					solrDoc.addField("file", juzhu.getImageData());
    					solrDoc.addField("type", GlobalConsts.JUZHU_INFO_TYPE);
    					solrDoc.addField("feature", Base64.encodeBase64String(juzhu.getFaceFeature()));
    					solrDoc.addField("camera", juzhu.getFromCidId());
    
    					solrDoc.addField("version", juzhu.getVersion());
    					docsList.add(solrDoc);
    					juzhu.setIndexed(1);
        				saveList.add(juzhu);
        				i++;
    					if (i % 3000 == 0) {
    						solrServer.add(docsList);
        					solrServer.commit(true, true, true);
    						List<Long> idList = saveList.stream().map(item -> item.getId()).collect(Collectors.toList());
    						staticBankService.updateIndexOfIds(GlobalConsts.T_NAME_JUZHU_DETAIL, idList);
    						saveList.clear();
    						docsList.clear();
    						LOG.info("居住证数据已经成功索引 " + i + " 条数据");
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
					staticBankService.updateIndexOfIds(GlobalConsts.T_NAME_JUZHU_DETAIL, idList);
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