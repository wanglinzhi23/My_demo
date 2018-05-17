package intellif.service;

import java.util.List;
import java.util.Map;

import intellif.database.entity.FaceStatisticCount;

public interface FaceQualityCameraCountItf {
	
	Map<Long, List<FaceStatisticCount>> findBySourceIdByPeriod(Long[] ids, String startdate, String enddate,int quality);
	
	Map<Long, List<FaceStatisticCount>> findByPeriod(String startdate, String enddate,int quality);
}
