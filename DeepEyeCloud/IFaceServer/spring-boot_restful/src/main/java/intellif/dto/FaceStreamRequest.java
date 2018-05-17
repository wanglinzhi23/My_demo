package intellif.dto;


/**
 * @author Zheng Xiaodong
 */
public class FaceStreamRequest {
    private Long venueId;
    private String startTime;

    private String endTime;
    private String cameraIds;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(String cameraIds) {
        this.cameraIds = cameraIds;
    }

    @Override
    public String toString() {
        return "FaceStreamRequest [venueId=" + venueId + ", startTime=" + startTime + ", endTime=" + endTime + ", cameraIds=" + cameraIds + "]";
    }

}
