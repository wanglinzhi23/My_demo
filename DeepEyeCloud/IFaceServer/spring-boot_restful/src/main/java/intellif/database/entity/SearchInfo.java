package intellif.database.entity;

import java.util.Date;

public class SearchInfo {
	//
	private double threshold;

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	//
	private Date startTime;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	//
	private Date endTime;

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	//
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String toString()
	{
		return "fileName:"+this.getFileName()+
				",startTime:"+this.getStartTime()+
				",endTime:"+this.getEndTime()+
				",threshold:"+this.getThreshold();
	}
}
