package com.intellif.bankmatch.service;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellif.bankmatch.TopFaceMatch;
import com.intellif.bankmatch.beans.CidInfoDetail;
import com.intellif.bankmatch.dto.FaceResultPKDto;
import com.intellif.bankmatch.executor.ApplicationResource;
import com.intellif.bankmatch.executor.FunctionUtils;
import com.intellif.bankmatch.utils.FileUtil;

public class DataFetch {
	
	public static Logger logger = Logger.getLogger(DataFetch.class);
	public String connectUrl = "jdbc:mysql://172.18.225.182:3306?autoReconnect=true&user=root&password=introcks&useUnicode=true&characterEncoding=UTF8";
	public int matchnum = 20;
	
	public void fetchPersonInfo() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		while(!TopFaceMatch.timeToStop) {
			List<CidInfoDetail> personInfoList = new ArrayList<CidInfoDetail>();
			logger.info("start from next of " + TopFaceMatch.lastPersonId);
//			String sqlString = "select a.id, a.GMSFHM, a.xm, b.face_feature, b.image_data from intellif_static.t_cid_info a, intellif_static.t_cid_detail b where a.id = b.from_cid_id and a.id > " 
//					+ TopFaceMatch.lastPersonId + " order by a.id limit 7200";
			String sqlString = "select c.*, d.face_feature, d.image_data from (select a.id, a.GMSFHM, a.xm, max(b.id) as maxid from intellif_static.t_cid_info a, intellif_static.t_cid_detail b "
					+ "where a.id = b.from_cid_id and a.id > " + TopFaceMatch.lastPersonId + " group by a.id order by a.id limit 7200) c, intellif_static.t_cid_detail d "
							+ "where c.maxid = d.id order by c.id";
			Connection connection = null;
			try {
				connection = DriverManager.getConnection(connectUrl);
				Statement statement = connection.createStatement();
				ResultSet personSet = statement.executeQuery(sqlString);
				while (personSet.next()) {
					CidInfoDetail cidInfoDetail = new CidInfoDetail();
					cidInfoDetail.setId(personSet.getLong("id"));
					cidInfoDetail.setGMSFHM(personSet.getString("GMSFHM"));
					cidInfoDetail.setXm(personSet.getString("xm"));
					cidInfoDetail.setImageData(personSet.getString("image_data"));
					Blob blob = personSet.getBlob("face_feature");
					if (blob == null) {
						continue;
					}
					byte[] faceFeatures = blob.getBytes(1, (int) blob.length());
					String feature = Base64.encodeBase64String(faceFeatures);
					cidInfoDetail.setFaceFeature(feature);
					personInfoList.add(cidInfoDetail);
				}
				personSet.close();
				
				if (personInfoList.size() < 1) {
					logger.info("no more records.");
					TopFaceMatch.taskFinished = true;
					TopFaceMatch.stop();
					break;
				}
				
				List<HttpSolrClient> solrConfigInfos = getSoleConfigs(connection);
				List< ForkJoinTask<?>> tasks = new ArrayList<ForkJoinTask<?>>();
				for (CidInfoDetail personInfo : personInfoList) {
					if (TopFaceMatch.timeToStop) {
						logger.info("progress: finished person " + TopFaceMatch.lastPersonId);
						return;
					}
					List<FaceResultPKDto> resultlist = Collections.synchronizedList(new ArrayList<>());
					for (HttpSolrClient solrClient : solrConfigInfos) {
						tasks.add(ApplicationResource.getThreadPool().submit(() -> {
							SolrQuery query = new SolrQuery();
							query.setRequestHandler("/topsearch");
							query.set("iff","true");
							query.set("rows",matchnum);
							query.set("type", 1);
							query.set("feature", personInfo.getFaceFeature());
				            try {
				            	logger.info("start solr " + solrClient.getBaseURL());
								QueryResponse rsp = solrClient.query(query);
								NamedList<Object> namedlist = rsp.getResponse();
								ObjectMapper objectMapper = new ObjectMapper();
								List<FaceResultPKDto> pojos = objectMapper.convertValue(namedlist.get("docs"), new TypeReference<List<FaceResultPKDto>>() { });
								if(pojos != null) {
									for(FaceResultPKDto pkdto : pojos) {
										pkdto.setFilename(getFileName(pkdto.getFile()));
									}
									resultlist.addAll(pojos);
//									resultmap.get(Long.valueOf(personInfo.getId())).getResultlist().addAll(pojos);
//									saveImgToDir(pojos, personInfo.getGMSFHM());
								}
							} catch (SolrServerException | IOException e) {
								e.printStackTrace();
							} finally {
								logger.info("end solr " + solrClient.getBaseURL());
							}
						}));
					}
					
					tasks.forEach(FunctionUtils::waitTillThreadFinish);
					List<FaceResultPKDto> filteredList = resultlist.stream().filter(x -> x.getScore() >= 0.92).collect(Collectors.toList());
					filteredList.sort((m, n) -> Float.compare(m.getScore(), n.getScore()));
					Collections.reverse(filteredList);
					List<FaceResultPKDto> list = new ArrayList<>();
					if(filteredList.size() >= matchnum) {
						list = filteredList.subList(0, matchnum);
					} else {
						list = filteredList;
					}
					saveImgToDir(personInfo, list, personInfo.getGMSFHM());
					updateLastPersonId(personInfo.getId());
					logger.info("progress: finished person " + TopFaceMatch.lastPersonId);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getFileName(String filePath) {
		int length = filePath.split("/").length;
		String fileName = filePath.split("/")[length - 1];
		return fileName;
	}
	
	private void updateLastPersonId(long id) {
		TopFaceMatch.lastPersonId = id;
	}
	
	private void saveImgToDir(CidInfoDetail personInfo, List<FaceResultPKDto> faceResultList, String IDCardNo) {
		try {
//			logger.info("save image start..");
			String filelink = FileUtil.getZipUrl(true) + "export/image/" + "/碰撞搜索结果/" + IDCardNo +"/";
			File file = new File(filelink);
			int i = 1;
			while (file .exists()  || file .isDirectory()) {
				filelink = FileUtil.getZipUrl(true) + "export/image/" + "/碰撞搜索结果/" + IDCardNo + "_" + i + "/";
				file = new File(filelink);
				i++;
			}
//			logger.info("save image point1...");
			synchronized(this) {
				FileUtil.checkFileExist(file);
			}
			String urlPerson = personInfo.getImageData();
			String fullFileNamePerson = filelink + "身份证图.jpg";
			FileUtil.copyUrl(urlPerson, fullFileNamePerson);
			for (FaceResultPKDto faceResult : faceResultList) {
				String url = faceResult.getFile();
				String fullFileName = filelink + faceResult.getScore() + faceResult.getFilename();
				boolean status = FileUtil.copyUrl(url, fullFileName);
			}
//			logger.info("save image end..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<HttpSolrClient> getSoleConfigs(Connection connection) throws SQLException {
		String solrConfig = "select * from intellif_base.t_solr_config_info group by server_url";
		List<HttpSolrClient> solrConfigInfos = new ArrayList<HttpSolrClient>();
		ResultSet solrRs = connection.createStatement().executeQuery(solrConfig);
		while (solrRs.next()) {
			String solrUrl = solrRs.getString("server_url");
			System.out.println("solr url is :" + solrUrl);
//			if(solrUrl.contains("176")) continue;
//		String solrUrl = "http://192.168.2.15:1986/solr/intellifusion";
			HttpSolrClient server = new HttpSolrClient(solrUrl);
			server.setSoTimeout(120000); // socket read timeout
			server.setConnectionTimeout(120000);
			server.setDefaultMaxConnectionsPerHost(100);
			server.setMaxTotalConnections(100);
			server.setFollowRedirects(false);
			server.setAllowCompression(true);
			solrConfigInfos.add(server);
		}
		solrRs.close();
		return solrConfigInfos;
	}

}
