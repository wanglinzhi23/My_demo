package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class FaceSearchDto implements Serializable {

	private static final long serialVersionUID = -3224749283036025496L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// 待检索人脸ID
	private long face_id;
	
	// 检索分值阈值
	private float scoreThreshold;
	
	// 检索深度
	private int depth;
	
	// 一组待检索人脸id，逗号分隔
	private String ids;
	
	private String startTime;

	private String endTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFace_id() {
		return face_id;
	}

	public void setFace_id(long face_id) {
		this.face_id = face_id;
	}

	public float getScoreThreshold() {
		return scoreThreshold;
	}

	public void setScoreThreshold(float scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
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

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
}
