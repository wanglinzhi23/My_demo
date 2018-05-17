package intellif.fk.dto;


public class FindFkPlaceFaceDto {

    private static final long serialVersionUID = -1588902803798110245L;

    // 数据开始时间
    private String startTime;    
    // 数据开始时间
    private String endTime;
    //场所编号
    private long placeId;
    //摄像头列表
    private String cameraIds;

    private String lastId;
    
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
    public long getPlaceId() {
        return placeId;
    }
    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
    public String getCameraIds() {
        return cameraIds;
    }
    public void setCameraIds(String cameraIds) {
        this.cameraIds = cameraIds;
    }
    public String getLastId() {
        return lastId;
    }
    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
    
 

}
