package intellif.service;

import java.util.List;
import java.util.Map;

import intellif.database.entity.FaceStatisticCount;

public interface FaceCameraCountItf {
	
	Map<Long, List<FaceStatisticCount>> findBySourceIdByPeriod(Long[] ids, String startdate, String enddate);
	
	Map<Long, List<FaceStatisticCount>> findByPeriod(String startdate, String enddate);
}
