package intellif.share.service;

import intellif.dto.FaceResultByCameraDto;
import intellif.dto.FaceResultDto;
import intellif.dto.FaceSearchStatisticDto;
import intellif.dto.SearchFaceDto;
import intellif.database.entity.BankMatchResultTuple;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public interface ShareSolrServerItf {

	public List<FaceResultDto> searchFaceByIdInBank(SearchFaceDto searchFaceDto) throws Exception;
	
	public List<FaceResultDto> getFaceByCameraId(SearchFaceDto searchFaceDto, long cameraId) throws Exception;
	
	public List<FaceResultByCameraDto> getFaceByFaceId(SearchFaceDto searchFaceDto, long stationId, int size) throws Exception;
	
	public List<FaceResultByCameraDto> getFaceByFaceIdAreas(SearchFaceDto searchFaceDto, Set<Long> areaIds, int size) throws Exception;

	public List<FaceResultDto> searchFaceByBlackId(long id, float scoreThreshold, int type) throws Exception;

	public FaceSearchStatisticDto getFaceStatistic(SearchFaceDto searchFaceDto) throws Exception;

	public List<FaceResultDto> searchFaceByIdInCamera(long faceId, float scoreThreshold, int type, long cameraId,int hours) throws Exception;
	
	public List<FaceResultDto> getFaceByCameraId(SearchFaceDto searchFaceDto, long cameraId, int page, int pageSize) throws Exception;

	public ConcurrentSkipListMap<Long, BankMatchResultTuple> searchFaceByDatasetInBank(long targetbankid, int staticbankid, long key, final int matchnum) throws Exception;
	
	public ConcurrentSkipListMap<Long, BankMatchResultTuple> searchFaceByNewRecordsInBank(long blackid, long targetbankid, int staticbankid, long key, final int matchnum) throws Exception;
}
