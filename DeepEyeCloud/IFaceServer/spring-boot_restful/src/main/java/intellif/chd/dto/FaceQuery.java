package intellif.chd.dto;

import java.io.Serializable;
import java.util.Date;

import intellif.utils.DateUtil;

public class FaceQuery implements Serializable {

	private static final long serialVersionUID = -1030577555412766820L;
	private Date startTime;
	private Date endTime;
	private float threshold = 0.92f;
	private long minTimeInterval = 300000L;
	private int minTimes = 1;

	
	public String getStartTimeString() {
		return DateUtil.getformatDate(getStartTime());
	}
	
	public String getEndTimeString() {
		return DateUtil.getformatDate(getEndTime());
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public long getMinTimeInterval() {
		return minTimeInterval;
	}

	public void setMinTimeInterval(long minTimeInterval) {
		this.minTimeInterval = minTimeInterval;
	}

	public int getMinTimes() {
		return minTimes;
	}

	public void setMinTimes(int minTimes) {
		this.minTimes = minTimes;
	}


}
