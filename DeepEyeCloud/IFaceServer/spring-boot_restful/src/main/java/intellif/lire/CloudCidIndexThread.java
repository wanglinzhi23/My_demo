package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.CidDetailDao;
import intellif.settings.SolrCloudSetting;
import intellif.database.entity.CidDetail;

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
public class CloudCidIndexThread extends Thread {

	private static Logger LOG = LogManager.getLogger(CloudCidIndexThread.class);

	private static final CloudSolrClient SOLR_CLOUD = new CloudSolrClient(SolrCloudSetting.getZkServers()); 
	static{								 
		SOLR_CLOUD.setDefaultCollection("cidinfo");
	}

	@Autowired
	private CidDetailDao cidDetailDao;

//	@Scheduled(cron = "0 0 0 * * ?")
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		Date now = new Date();
		try {
			List<CidDetail> saveList = new ArrayList<CidDetail>();
			Iterable<CidDetail> cidList = this.cidDetailDao.findUnIndexed();
			LOG.info("Start CidInfo indexing ...");
			int i = 0;
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			for (CidDetail cid : cidList) {
				if(null == cid.getFaceFeature() || cid.getFaceFeature().length<700) continue;
				try {
					SolrInputDocument solrDoc = new SolrInputDocument();
					solrDoc.addField("id", cid.getId());
                    solrDoc.addField("createTimeNS", System.nanoTime());
					solrDoc.addField("time", cid.getCreated());
					solrDoc.addField("file", cid.getImageData());
					solrDoc.addField("type", GlobalConsts.CID_INFO_TYPE);
					solrDoc.addField("feature", Base64.encodeBase64String(cid.getFaceFeature()));
					solrDoc.addField("camera", cid.getFromCidId());
					solrDoc.addField("version", cid.getVersion());

					ByteBuffer buffer = ByteBuffer.allocate(8);
					buffer.putLong(0, cid.getId());
					solrDoc.addField("docid", Base64.encodeBase64String(buffer.array()));

					docs.add(solrDoc);
					cid.setIndexed(1);
					saveList.add(cid);
					i++;
					if (i % 5000 == 0) {
						SOLR_CLOUD.add(docs);
						SOLR_CLOUD.commit(true, true, true);
						this.cidDetailDao.save(saveList);
						docs.clear();
						saveList.clear();
						LOG.info(System.currentTimeMillis() + "：户籍信息已经成功索引 " + i + " 条数据");
					}
				} catch (Exception e) {
					LOG.error("Error reading image or indexing it.");
					e.printStackTrace();
				}
			}
			if (!docs.isEmpty()) {
				SOLR_CLOUD.add(docs);
				SOLR_CLOUD.commit(true, true, true);
				this.cidDetailDao.save(saveList);
			}

		} catch (Throwable e) {
			LOG.error("Error reading image or indexing it.");
			LOG.error("create solr index error for back", e);
		}
		LOG.info("Finished CidInfo indexing ...");
		LOG.info("CidInfo Index cost time：" + (new Date().getTime() - now.getTime()));
	}

}
