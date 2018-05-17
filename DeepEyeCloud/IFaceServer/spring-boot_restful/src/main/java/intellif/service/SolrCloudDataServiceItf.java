package intellif.service;

import intellif.dto.FaceResultDto;
import intellif.database.entity.BlackDetail;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import intellif.database.entity.BankMatchResultTuple;
import intellif.database.entity.BlackDetailRealName;

public interface SolrCloudDataServiceItf {

	public List<FaceResultDto> searchFaceByType(String key, String faceFeature, float scoreThreshold, int type, String startTime, String endTime) throws Exception;

	public List<FaceResultDto> searchFaceByType(String key, String faceFeature, float scoreThreshold, int type) throws Exception;

	public List<FaceResultDto> searchFaceByCamera(String key, long cameraId, String faceFeature, float scoreThreshold, int type) throws Exception;

	public List<FaceResultDto> searchFaceByTime(String key, String faceFeature, float scoreThreshold, int type, String startTime, String endTime) throws Exception;

	public void deleteById(Integer type, List<String> ids) throws Exception;

	public void deleteById(Integer type, String id) throws Exception;

	public void addBlackDetail(Integer type, BlackDetail black);
	
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> bankMatch(List<BlackDetailRealName> blackDetailList, long targetbankid, int staticbankid, long key, final int matchnum) throws Exception;

}
