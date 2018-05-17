package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.JuZhuDetailDao;
import intellif.settings.SolrCloudSetting;
import intellif.database.entity.JuZhuDetail;

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
public class CloudJuzhuIndexThread extends Thread {

	private static Logger LOG = LogManager.getLogger(CloudJuzhuIndexThread.class);

	private static final CloudSolrClient SOLR_CLOUD = new CloudSolrClient(SolrCloudSetting.getZkServers()); 
	static{								 
		SOLR_CLOUD.setDefaultCollection("juzhuinfo");
	}

	@Autowired
	private JuZhuDetailDao juZhuDetailDao;

//	@Scheduled(cron = "0 0 0 * * ?")
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		Date now = new Date();
		try {
			List<JuZhuDetail> saveList = new ArrayList<JuZhuDetail>();
			Iterable<JuZhuDetail> juzhuList = this.juZhuDetailDao.findUnIndexed();
			LOG.info("Start JuzhuInfo indexing ...");
			int i = 0;
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			for (JuZhuDetail juzhu : juzhuList) {
				if(null == juzhu.getFaceFeature() || juzhu.getFaceFeature().length<700) continue;
				try {
					SolrInputDocument solrDoc = new SolrInputDocument();
					solrDoc.addField("id", juzhu.getId());
                    solrDoc.addField("createTimeNS", System.nanoTime());
					solrDoc.addField("time", juzhu.getCreated());
					solrDoc.addField("file", juzhu.getImageData());
					solrDoc.addField("type", GlobalConsts.JUZHU_INFO_TYPE);
					solrDoc.addField("feature", Base64.encodeBase64String(juzhu.getFaceFeature()));
					solrDoc.addField("camera", juzhu.getFromCidId());
					solrDoc.addField("version", juzhu.getVersion());

					ByteBuffer buffer = ByteBuffer.allocate(8);
					buffer.putLong(0, juzhu.getId());
					solrDoc.addField("docid", Base64.encodeBase64String(buffer.array()));

					docs.add(solrDoc);
					juzhu.setIndexed(1);
					saveList.add(juzhu);
					i++;
					if (i % 5000 == 0) {
						SOLR_CLOUD.add(docs);
						SOLR_CLOUD.commit(true, true, true);
						this.juZhuDetailDao.save(saveList);
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
				this.juZhuDetailDao.save(saveList);
			}

		} catch (Throwable e) {
			LOG.error("Error reading image or indexing it.");
			LOG.error("create solr index error for back", e);
		}
		LOG.info("Finished JuzhuInfo indexing ...");
		LOG.info("JuzhuInfo Index cost time：" + (new Date().getTime() - now.getTime()));
	}

}
