package intellif.lire;

import intellif.consts.GlobalConsts;
import intellif.dao.IndexFaceRecordDao;
import intellif.dao.SearchRecordDao;
import intellif.utils.ApplicationResource;
import intellif.database.entity.SearchRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexRefreshJob extends Thread {

	private static Logger LOG = LogManager.getLogger(SolrIndexRefreshJob.class);
	 
	@Autowired
	private IndexFaceRecordDao recordRepository;

	@Autowired
	private SearchRecordDao searchRecordDao;

	private static String SOLR_PARAMETER = "/select?iff=true&threshold=0.93&fq=type%3A1&feature=vu9lALJfkz2egSk%2BcM%2FcvTsktz27ZqO9rRMCvKnaVT5bCZW7E8JsvcLOiT0ibY09AE7VPTfHK70rA%2Bw8i60QPiFIHb7NZks8ZgaNPaXLpTyR7n88nafgvFEWBr6o6VE9KDd4PUIFvT0D4gi9JchoveIdhLzK4MM8dmDkPSPnaD2998o9OIfgva%2FlEz4pmIs8TjmkPQOdML3dSfG7n3pRvS1pFL3yX%2F69kikYvkGitj1U%2BTM9QxwnPoqhDb5jI4k8rR1KPVuk3T3X4q09TMKhPa6sqbxf2aE8Ok4gvDmP0T02Ynm%2BlqoPvZfG97vpMoA9zm4gPUQXzz1%2BlIG8YdraPEWBdL3SFmA9iCDiPSJQPL6xPoI871v9PZG3Wj055ZS77nXTPSFHeL0M%2BTA9TwwVPSuVqL1QOMC9efPyPekPFD6%2FVTE9ZqRPvYz5jrqAeqq9uA1DvEJAyb0Mna28XmPLvfmV%2Fj3ueRm8T1OLPeGudL1D1hc%2B6GwvvEpY%2BDx8BaE9K1thva9YVr3w%2Fww%2Baeg%2BPs5dfr3eDO46ds%2BNPUqoZL0HYXa9yoecuxIdkT1A8dY88V1ovDg%2Bcb0kt%2BG9Rbghvfci3Lwr0jq86ljrPTAzTT06ams9ej09Pd50Er57dmE%2BY22xvI8fiL3sDo29Vcs8vQw5AL6mKn69GBRWPhShzr1S1KS9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA%3D%3D&rows=2000000000&wt=json&version=2";

//	@Scheduled(fixedRate = 3600000)
	public void run() {
		if(!GlobalConsts.run){
    		return;
    	}
		SearchRecord record = searchRecordDao.getLaskRecord();
		Date now = new Date();
		boolean needFresh = (now.getTime() - record.getCreated().getTime()) / 1000 > 1;
		if (!needFresh) {
			return;
		}
		List<Object> solrServerCameras = recordRepository.getSolrServerWithCameras();
		List<String> solrHosts = new ArrayList<String>();
		for (Object object : solrServerCameras) {
			Object[] arrays = (Object[]) object;
			String solrHost = arrays[1].toString();
			solrHosts.add(solrHost);
		}

		try {
			List<ForkJoinTask<String>> tasks = new ArrayList<ForkJoinTask<String>>();
			for (final String host : solrHosts) {
				ForkJoinTask<String> task = ApplicationResource.THREAD_POOL.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						HttpClient httpClient = HttpClientBuilder.create().build();

						long startTime = System.currentTimeMillis();
						HttpGet httpGet = new HttpGet(host + SOLR_PARAMETER);
						httpClient.execute(httpGet);
						String result = host + " executing time is: " + (System.currentTimeMillis() - startTime) + "ms";

						return result;
					}
				});

				tasks.add(task);
			}

			System.out.println("------------------------------------------------------------");
			for (ForkJoinTask<String> task : tasks) {
				System.out.println(task.join());
			}
		} catch (Throwable e) {
			LOG.error("exception on refreshing solr index", e);
			// e.printStackTrace();
		}

	}

}
