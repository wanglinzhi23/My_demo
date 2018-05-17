package intellif.dto;

import java.io.Serializable;

/**
 * Created by yangboz on 11/17/15.
 */
public class UploadDto implements Serializable {

    private int type;//简单,报警型,举报
    private String longitude;
    private String latitude;
    private int level;//respect to AlarmThreshold.
//    private String device;

    public UploadDto() {
    }

    public UploadDto(int type, int level, String longitude, String latitude) {
        this.type = type;
        this.level = level;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "UploadDto,type:" + this.getType() + ",level:" + this.getLevel() + ",longitude:" + this.getLongitude() + ",latitude:" + this.getLatitude();
    }

//    public String getDevice() {
//        return device;
//    }
//
//    public void setDevice(String device) {
//        this.device = device;
//    }
}
