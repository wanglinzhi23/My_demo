package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import intellif.consts.GlobalConsts;

@Entity
public class FaceInfoDto  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3923743872626773277L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 相似度
	private double similarity;

	// 大图Url
	private String bigImageUrl;

	// 数据源ID
	private long sourceId;

	// 数据源类型
	/**
	 * 0 摄像头
	 * 1 视频
	 * 2 图片
	 */
	private int sourceType;

	// 录入时间
	private Date time;

	// 人脸图像数据
	private String imageData;//URI

	// 所属人物编号
	private Long fromPersonId;

	// 所属图片编号
	private Long fromImageId;

	// 所属视频编号
	private Long fromVideoId;

	@JsonIgnore
	@Lob
	@Column
	private byte[] faceFeature;

	@Transient
	@JsonProperty("faceFeature")
	@JsonIgnore
	private String base64FaceFeature = "";

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public String getBigImageUrl() {
		return bigImageUrl;
	}

	public void setBigImageUrl(String bigImageUrl) {
		this.bigImageUrl = bigImageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public Long getFromPersonId() {
		return fromPersonId;
	}

	public void setFromPersonId(Long fromPersonId) {
		this.fromPersonId = fromPersonId;
	}

	public Long getFromImageId() {
		return fromImageId;
	}

	public void setFromImageId(Long fromImageId) {
		this.fromImageId = fromImageId;
	}

	public Long getFromVideoId() {
		return fromVideoId;
	}

	public void setFromVideoId(Long fromVideoId) {
		this.fromVideoId = fromVideoId;
	}

	public byte[] getFaceFeature() {
		return faceFeature;
	}

	public void setFaceFeature(byte[] faceFeature) {
		this.faceFeature = faceFeature;
	}

	public String getBase64FaceFeature() {
		return base64FaceFeature;
	}

	public void setBase64FaceFeature(String base64FaceFeature) {
		this.base64FaceFeature = base64FaceFeature;
	}

}
