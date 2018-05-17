package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import intellif.zoneauthorize.itf.Zone;

public class FaceResultDto implements Serializable, Zone {

    private static final long serialVersionUID = -6113693483554282287L;

    // faceId
    private String id;

    // 人脸类型（0：重点人员；1：抓拍人员）
    private int type;

    // 摄像头ID
    private long camera;

    // 图片地址_人脸
    private String file;

    // 抓拍时间
    private Date time;

    // 比对分值
    private float score;

    // 图片地址_背景
    private String file_bg;
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCamera() {
        return camera;
    }

    public void setCamera(long camera) {
        this.camera = camera;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

 
	@Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FaceResultDto other = (FaceResultDto) obj;
        if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
   
    
    
    public String getFile_bg() {
        return file_bg;
    }

	public void setFile_bg(String file_bg) {
        this.file_bg = file_bg;
    }
	
    @Autowired
    public Long zoneId() {
        return camera;
    }

    @Override
    public String toString() {
        return "FaceResultDto{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", camera=" + camera +
                ", file='" + file + '\'' +
                ", time=" + time +
                ", score=" + score +
                ", file_bg='" + file_bg + '\'' +
                '}';
    }
}
