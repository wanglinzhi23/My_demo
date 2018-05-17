package intellif.dto;

import java.io.Serializable;

/**
 * 
 * @author yktangint
 * 数据统计里面，摄像头数量统计的返回结果。
 *
 */
public class CameraStatisticInfoDto implements Serializable {

	private static final long serialVersionUID = 629516299945460643L;

	private long stationid;
	private String stationname;
	private long quantity;
	private String[] cameranamelist;
	
	public long getStationid() {
		return stationid;
	}
	public void setStationid(long stationid) {
		this.stationid = stationid;
	}
	public String getStationname() {
		return stationname;
	}
	public void setStationname(String stationname) {
		this.stationname = stationname;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public String[] getCameranamelist() {
		return cameranamelist;
	}
	public void setCameranamelist(String[] cameranamelist) {
		this.cameranamelist = cameranamelist;
	}
}
