package intellif.dto;

public class VehiclePlateQueryDto {
	private String crossingId;
	private Long startTime;
	private Long endTime;
	public String getCrossingId() {
		return crossingId;
	}
	public void setCrossingId(String crossingId) {
		this.crossingId = crossingId;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	@Override
	public String toString() {
		return "VehiclePlateQueryDto [crossingId=" + crossingId + ", startTime=" + startTime + ", endTime=" + endTime
				+ "]";
	}
	
}
