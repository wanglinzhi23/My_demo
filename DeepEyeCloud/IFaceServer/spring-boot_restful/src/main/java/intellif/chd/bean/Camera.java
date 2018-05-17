package intellif.chd.bean;

import java.io.Serializable;

public class Camera implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -995677114089900520L;

	// 编号id
	private Long id;

	// 显示名称
	private String displayName;

	// 地图信息
	private String geoString;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getGeoString() {
		return geoString;
	}

	public void setGeoString(String geoString) {
		this.geoString = geoString;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Camera [id=");
		builder.append(id);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", geoString=");
		builder.append(geoString);
		builder.append("]");
		return builder.toString();
	}
}
