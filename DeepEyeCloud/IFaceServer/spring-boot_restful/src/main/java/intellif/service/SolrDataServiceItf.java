package intellif.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import intellif.database.entity.BlackDetail;
import intellif.dto.FaceResultDto;
import intellif.dto.SearchFaceDto;
import intellif.database.entity.BankMatchResultTuple;
import intellif.database.entity.BlackDetailRealName;

/**
 * 
 * <p>Title: SolrDataServiceItf.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015-2018 深圳云天励飞技术有限公司
 * <p>Company: XXX信息技术有限公司</p>
 * @author Peng Cheng
 * @version 1.2.0 创建时间：2017年2月23日 下午4:09:14
 */
public interface SolrDataServiceItf {

	/**
	 * 常用检索接口
	 * @param key
	 * @param faceFeature
	 * @param scoreThreshold
	 * @param type
	 * @param startTime
	 * @param endTime
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	public List<FaceResultDto> searchFaceByType(String key,  String faceFeature,SearchFaceDto sfd) throws Exception;

	/**
	 * 相似嫌疑人检索接口
	 * @param key
	 * @param faceFeature
	 * @param scoreThreshold
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<FaceResultDto> searchFaceByType(String key, String faceFeature, float scoreThreshold, int type) throws Exception;

	/**
	 * 单摄像头检索接口
	 * @param key
	 * @param cameraId
	 * @param faceFeature
	 * @param scoreThreshold
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<FaceResultDto> searchFaceByCamera(String key, long cameraId, String faceFeature, float scoreThreshold, int type,int hours) throws Exception;

	/**
	 * 离线布控使用检索接口
	 * @param key
	 * @param faceFeature
	 * @param scoreThreshold
	 * @param type
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<FaceResultDto> searchFaceByTime(String key, String faceFeature, SearchFaceDto sfd) throws Exception;

	/**
	 * 删除指定Core的一批id索引数据
	 * @param core
	 * @param ids
	 * @throws Exception
	 */
	public void deleteById(String core, List<String> ids) throws Exception;

	/**
	 * 删除指定Core的指定id索引数据
	 * @param core
	 * @param id
	 * @throws Exception
	 */
	public void deleteById(String core, String id) throws Exception;

	/**
	 * 对指定Core增加嫌疑人数据
	 * @param core
	 * @param black
	 */
	public void addBlackDetail(String core, BlackDetail black);
	
	/**
	 * 获取指定摄像头索引数据所在Solr服务器
	 * @param sourceId
	 * @return
	 * @throws MalformedURLException
	 */
	public HttpSolrClient getServer(long sourceId) throws MalformedURLException;

	/**
	 * 获取指定Core命的Solr服务器
	 * @param core
	 * @return
	 * @throws MalformedURLException
	 */
	public HttpSolrClient getServer(String core) throws MalformedURLException;
	
	/**
	 * PK双库碰撞
	 * @param blackDetailList, 目标库人物列表
	 * @param targetbankid, 目标库id
	 * @param staticbankid, 静态库id
	 * @param key, 双库碰撞进度条的key
	 * @param matchnum, 双库碰撞最匹配的前多少个
	 * @return
	 * @throws Exception
	 */
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> bankMatch(List<BlackDetailRealName> blackDetailList, long targetbankid, int staticbankid, long key, int matchnum) throws Exception;

	List<FaceResultDto> searchFaceByAttribute(String key, String faceFeature, SearchFaceDto sfd) throws Exception;

	public List<FaceResultDto> chdSearchFaceByType(String digest, String faceFeature, float threshold, int type) throws MalformedURLException, SolrServerException, IOException;
	
}
