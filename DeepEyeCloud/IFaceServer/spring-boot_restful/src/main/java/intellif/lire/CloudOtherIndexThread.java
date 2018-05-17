package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.OtherDetailDao;
import intellif.settings.SolrCloudSetting;
import intellif.database.entity.OtherDetail;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CloudOtherIndexThread extends Thread {

	private static Logger LOG = LogManager.getLogger(CloudOtherIndexThread.class);

	private static final CloudSolrClient SOLR_CLOUD = new CloudSolrClient(SolrCloudSetting.getZkServers()); 
	static{								 
		SOLR_CLOUD.setDefaultCollection("otherinfo");
	}

	@Autowired
	private OtherDetailDao otherDetailDao;

//	@Scheduled(cron = "0 0 0 * * ?")
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		Date now = new Date();
		try {
			List<OtherDetail> saveList = new ArrayList<OtherDetail>();
			Iterable<OtherDetail> otherList = this.otherDetailDao.findUnIndexed();
			LOG.info("Start OtherInfo indexing ...");
			int i = 0;
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			for (OtherDetail other : otherList) {
				if(null == other.getFaceFeature() || other.getFaceFeature().length<700) continue;
				try {
					SolrInputDocument solrDoc = new SolrInputDocument();
					solrDoc.addField("id", other.getId());
                    solrDoc.addField("createTimeNS", System.nanoTime());
					solrDoc.addField("time", other.getCreated());
					solrDoc.addField("file", other.getImageData());
					solrDoc.addField("type", other.getZplxmc());
					solrDoc.addField("feature", Base64.encodeBase64String(other.getFaceFeature()));
					solrDoc.addField("camera", other.getFromCidId());
					solrDoc.addField("version", other.getVersion());

					ByteBuffer buffer = ByteBuffer.allocate(8);
					buffer.putLong(0, other.getId());
					solrDoc.addField("docid", Base64.encodeBase64String(buffer.array()));

					docs.add(solrDoc);
					other.setIndexed(1);
					saveList.add(other);
					i++;
					if (i % 5000 == 0) {
						SOLR_CLOUD.add(docs);
						SOLR_CLOUD.commit(true, true, true);
						this.otherDetailDao.save(saveList);
						docs.clear();
						saveList.clear();
						LOG.info(System.currentTimeMillis() + "：居住证信息已经成功索引 " + i + " 条数据");
					}
				} catch (Exception e) {
					LOG.error("Error reading image or indexing it.");
					e.printStackTrace();
				}
			}
			if (!docs.isEmpty()) {
				SOLR_CLOUD.add(docs);
				SOLR_CLOUD.commit(true, true, true);
				this.otherDetailDao.save(saveList);
			}

		} catch (Throwable e) {
			LOG.error("Error reading image or indexing it.");
			LOG.error("create solr index error for back", e);
		}
		LOG.info("Finished OtherInfo indexing ...");
		LOG.info("OtherInfo Index cost time：" + (new Date().getTime() - now.getTime()));
	}

}
