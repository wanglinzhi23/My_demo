package intellif.service;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import intellif.chd.dto.ChdFaceSearchStatisticDto;
import intellif.dto.CidInfoDto;
import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceSearchStatisticDto;
import intellif.dto.SearchFaceDto;
import intellif.database.entity.BankMatchResultTuple;

public interface SolrServerItf {

	public List<FaceResultDto> searchFaceByIdInBank(SearchFaceDto searchFaceDto) throws Exception;
	
	public List<FaceResultDto> getFaceByCameraId(SearchFaceDto searchFaceDto, long cameraId) throws Exception;
	
	public List<FaceResultByCameraDto> getFaceByFaceId(SearchFaceDto searchFaceDto, long stationId, int size) throws Exception;

	public List<FaceResultDto> searchFaceByBlackId(long id, float scoreThreshold, int type) throws Exception;

	public FaceSearchStatisticDto getFaceStatistic(SearchFaceDto searchFaceDto) throws Exception;

	public List<FaceResultDto> searchFaceByIdInCamera(long faceId, float scoreThreshold, int type, long cameraId,int hours) throws Exception;
	
	public List<FaceResultDto> getFaceByCameraId(SearchFaceDto searchFaceDto, long cameraId, int page, int pageSize) throws Exception;

	public ConcurrentSkipListMap<Long, BankMatchResultTuple> searchFaceByDatasetInBank(long targetbankid, int staticbankid, long key, final int matchnum) throws Exception;
	
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> searchFaceByNewRecordsInBank(long blackid, long targetbankid, int staticbankid, long key, final int matchnum) throws Exception;

	List<FaceResultDto> searchFaceByAttribute(SearchFaceDto searchFaceDto) throws Exception;

	List<FaceResultByCameraDto> getFaceAttributeByFaceId(SearchFaceDto searchFaceDto, long stationId, int size)
			throws Exception;

	FaceSearchStatisticDto getFaceAttributeStatistic(SearchFaceDto searchFaceDto) throws Exception;

	List<FaceResultDto> chdSearchFaceByIdInBank(int type, float threshold, long faceId) throws Exception;
	List<CidInfoDto> getChdFaceStatistic(long faceId, float thresHold) throws Exception;
}
