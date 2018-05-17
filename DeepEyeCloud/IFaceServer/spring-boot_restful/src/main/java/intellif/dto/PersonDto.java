package intellif.dto;


import java.util.List;

import intellif.dto.MonitorAreaInfo;
import intellif.database.entity.PersonDetail;

public class PersonDto extends PersonDetail {

	private static final long serialVersionUID = -1588902803798110245L;

	private List<MonitorAreaInfo> areaList;

	private String cameraIds;
	
	private String imageIds;//存在大小图之分，如果传的是face表小图，则有face##前辍
	
	
	private Integer days;
	
	//兼容警务云情况
	private String stationIds;


	public List<MonitorAreaInfo> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<MonitorAreaInfo> areaList) {
        this.areaList = areaList;
    }


	public String getCameraIds() {
		return cameraIds;
	}

	public void setCameraIds(String cameraIds) {
		this.cameraIds = cameraIds;
	}

	public String getImageIds() {
		return imageIds;
	}

	public void setImageIds(String imageIds) {
		this.imageIds = imageIds;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getStationIds() {
		return stationIds;
	}

	public void setStationIds(String stationIds) {
		this.stationIds = stationIds;
	}
	
	
}
