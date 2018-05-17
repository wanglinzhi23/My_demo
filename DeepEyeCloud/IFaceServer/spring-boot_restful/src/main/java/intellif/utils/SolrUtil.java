package intellif.utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolrUtil {
	 private static Logger LOG = LogManager
	            .getLogger(SolrUtil.class);
	/**
	 * 清除某个core下所有索引
	 * @param url
	 * @param coreName
	 */
	public  static void clearSolrCoreIndex(String url,String filter){
		try{
			String urlParam = "/update/?stream.body=%3Cdelete%3E%3Cquery%3E"+filter+"%3C/query%3E%3C/delete%3E&stream.contentType=text/xml;charset=utf-8&commit=true";
			String solrUrl = url+urlParam;
			HttpGet validateGet = new HttpGet(solrUrl);
			HttpClients.createDefault().execute(validateGet);
		}catch(Exception e){
			LOG.error("clear solr core error, url:"+url+" error:",e);
		}
	}
	
	
}
