package intellif.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


public class FaceSearchStatisticDto implements Serializable {
	
	private static final long serialVersionUID = -3744792988975709916L;

	private int total;
	
	private HashMap<Long, Integer> stationStatistic;
	
	private HashMap<Long, Integer> cameraStatistic;
	
	private HashMap<Long, Integer> districtStatistic;
	
	private Date startTime;
	
	private Date endTime;

	public void setTotal(int total) {
		this.total = total;
	}

	public Integer getTotal() {
		return total;
	}

	public HashMap<Long, Integer> getStationStatistic() {
		return stationStatistic;
	}

	public void setStationStatistic(HashMap<Long, Integer> stationStatistic) {
		this.stationStatistic = stationStatistic;
	}

	public HashMap<Long, Integer> getCameraStatistic() {
		return cameraStatistic;
	}

	public void setCameraStatistic(HashMap<Long, Integer> cameraStatistic) {
		this.cameraStatistic = cameraStatistic;
	}

	public HashMap<Long, Integer> getDistrictStatistic() {
		return districtStatistic;
	}

	public void setDistrictStatistic(HashMap<Long, Integer> districtStatistic) {
		this.districtStatistic = districtStatistic;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}
