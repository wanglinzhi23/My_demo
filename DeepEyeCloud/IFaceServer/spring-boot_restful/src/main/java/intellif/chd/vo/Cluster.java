package intellif.chd.vo;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class Cluster {

	private float[] feature;

	private List<Face> faceList;

	private String faceUrl;
	
	private String imageUrl;
	
	private String time;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private Long faceId;

	public float[] getFeature() {
		return feature;
	}

	public Cluster setFeature(float[] feature) {
		this.feature = feature;
		return this;
	}

	public List<Face> getFaceList() {
		return faceList;
	}

	public Cluster setFaceList(List<Face> faceList) {
		this.faceList = faceList;
		return this;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public Cluster setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cluster [feature=");
		builder.append(Arrays.toString(feature));
		builder.append(", faceList=");
		builder.append(faceList);
		builder.append(", faceUrl=");
		builder.append(faceUrl);
		builder.append(",imageUrl=");
		builder.append(imageUrl);
		builder.append("]");
		return builder.toString();
	}

	public Long getFaceId() {
		return faceId;
	}

	public Cluster setFaceId(Long faceId) {
		this.faceId = faceId;
		return this;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
