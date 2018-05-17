package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

public class AlarmImageInfo implements Serializable {

	private static final long serialVersionUID = 9215240778745954781L;
	
	private Date time;
	private String cameraName;
	private String stationName;
	private String imageData;//小图
	private String uri;//大图

	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getImageData() {
		return imageData;
	}
	public void setImageData(String imageData) {
		this.imageData = imageData;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
