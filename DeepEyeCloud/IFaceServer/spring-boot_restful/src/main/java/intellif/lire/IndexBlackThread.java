package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.BlackDetailDao;
import intellif.database.entity.BlackDetail;
import intellif.service.SolrDataServiceItf;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class IndexBlackThread extends Thread {
  
    private static Logger LOG = LogManager.getLogger(IndexBlackThread.class);
   
    @Autowired
    private BlackDetailDao blackDetailDao;
    @Autowired
    private SolrDataServiceItf _solrDataServiceItf;

    //@Scheduled(fixedRate = 300000)
    public void run() {
    	
    	if(!GlobalConsts.run){
    		return;
    	}

        Date now = new Date();
        try {
            List<BlackDetail> blackList = (List<BlackDetail>) this.blackDetailDao.findUnIndexed();
            HttpSolrClient solrServer = _solrDataServiceItf.getServer(GlobalConsts.coreMap.get(GlobalConsts.BLACK_INFO_TYPE));
            LOG.info("Start black indexing ...");
            int i = 0;
            for (BlackDetail black : blackList) {
            	if(null == black.getFaceFeature() || black.getFaceFeature().length!=724) continue;
                try {
                    SolrInputDocument solrDoc = new SolrInputDocument();
                    solrDoc.addField("id", black.getId());
                    solrDoc.addField("time", black.getCreated());
                    solrDoc.addField("file", black.getImageData());
                    solrDoc.addField("type", GlobalConsts.BLACK_INFO_TYPE);
                    solrDoc.addField("feature", Base64.encodeBase64String(black.getFaceFeature()));
                    solrDoc.addField("camera", black.getFromPersonId());
                    solrDoc.addField("version", black.getVersion());
                    solrServer.add(solrDoc);
                    solrServer.commit(true, true, true);
                    black.setIndexed(1);
                    blackDetailDao.save(black);
                    i++;
                    if (i % 100 == 0)
                        LOG.info("嫌疑人已经成功索引 " + i + " 条数据");
                } catch (Exception e) {
                    LOG.error("Error reading image or indexing it.",e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("Finished indexing ...");
        LOG.info("Index cost time：" + (new Date().getTime() - now.getTime()));
    }

}