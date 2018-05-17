package intellif.service;

import intellif.database.entity.EventInfo;
import intellif.database.entity.G20Statistic;

import java.util.List;

public interface G20Service {

	 public List<G20Statistic> findG20Statistic(String startTime, String endTime);
	 

	 public List<EventInfo> findAllPersonEvents(long stationId, int pageSize, String personIds, double threshold, int important);
}
