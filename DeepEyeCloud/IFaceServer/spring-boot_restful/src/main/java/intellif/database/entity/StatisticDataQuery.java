package intellif.database.entity;

import java.io.Serializable;

public class StatisticDataQuery implements Serializable {

	private static final long serialVersionUID = -1030577555412766820L;
	private Long[] cameraids;//set to 0 if search for all cameras
	private String starttime;
	private String endtime;
	private int quality;
	private int timeslot;//3:day, 2:hour, 1:min, 0:second
	private float threshold;
	
	public StatisticDataQuery() {}
	
	public StatisticDataQuery(Long[] cameraids, String starttime, String endtime, int timeslot, int quality, float threshold) {
		super();
		this.cameraids = cameraids;
		this.starttime = starttime;
		this.endtime = endtime;
		this.timeslot = timeslot;
		this.threshold = threshold;
		this.quality = quality;
	}
	public Long[] getCameraids() {
		return cameraids;
	}
	public void setCameraids(Long[] cameraids) {
		this.cameraids = cameraids;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public int getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(int timeslot) {
		this.timeslot = timeslot;
	}
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}
	
}
