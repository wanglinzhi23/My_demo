package intellif.chd.vo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import intellif.consts.GlobalConsts;
import intellif.database.entity.InfoBase;

@Entity
@Table(name = GlobalConsts.T_NAME_CONTRAST_FACE_INFO,schema=GlobalConsts.INTELLIF_BASE)
public class ContrastFaceInfo  extends InfoBase implements Serializable{
	
	private static final long serialVersionUID = 7670724701156661873L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	private String cjImage;
	
	private String faceTime;
	
	private String bzImage;
	
	private String name;
	
	private float score;
	
	private String identity;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCjImage() {
		return cjImage;
	}

	public void setCjImage(String cjImage) {
		this.cjImage = cjImage;
	}


	public String getBzImage() {
		return bzImage;
	}

	public void setBzImage(String bzImage) {
		this.bzImage = bzImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}


	@Override
	public String toString() {
		return "ContrastFaceInfo [id=" + id + ", cjImage=" + cjImage + ", faceTime=" + faceTime + ", bzImage=" + bzImage
				+ ", name=" + name + ", score=" + score + ", identity=" + identity + "]";
	}

	public String getFaceTime() {
		return faceTime;
	}

	public void setFaceTime(String faceTime) {
		this.faceTime = faceTime;
	}
	
}
