package intellif.dto;

import java.io.Serializable;
import java.util.List;

import intellif.database.entity.AreaCameraStatistic;

public class AreaStatisticCameraDataDto implements Serializable {

	private static final long serialVersionUID = 2916019254287159330L;
	private String areaName;
	private long count;
	private List<AreaCameraStatistic> cameraList;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public List<AreaCameraStatistic> getCameraList() {
		return cameraList;
	}
	public void setCameraList(List<AreaCameraStatistic> cameraList) {
		this.cameraList = cameraList;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
}
