package intellif.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class AlarmQueryDto {
	
	// 查询的人或库的id
	@JsonSerialize(using=ToStringSerializer.class)
	private long id;
	
	// 查询关键字
	private String text;
	
	// 查询的摄像头范围
	private String cameraIds = "";
	
	// 告警过滤阈值
	private float threshold = 0.92F;
	
	// 查询开始时间
	private String startTime = "";

	// 查询结束时间
	private String endTime = "";
	
	// 分页页数
	private int page;

	// 分页大小
	private int pageSize;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCameraIds() {
		return cameraIds;
	}

	public void setCameraIds(String cameraIds) {
		this.cameraIds = cameraIds;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
