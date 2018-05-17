package intellif.database.entity;

import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = GlobalConsts.T_NAME_VIDEO_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class VideoInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 摄像头id
    private long cameraId;

    public long getCameraId() {
        return cameraId;
    }

    public void setCameraId(long cameraId) {
        this.cameraId = cameraId;
    }

    // 视频开始时间
    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    // 视频结束时间
    private Date endTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    // 视频文件地址
    private String uri;//Uniform OauthResource Identifier

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
