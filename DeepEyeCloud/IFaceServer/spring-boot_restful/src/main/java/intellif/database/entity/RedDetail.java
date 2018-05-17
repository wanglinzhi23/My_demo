package intellif.database.entity;

import intellif.consts.GlobalConsts;

import java.io.Serializable;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
@Entity
@Table(name = GlobalConsts.T_NAME_RED_DETAIL,schema=GlobalConsts.INTELLIF_BASE)
public class RedDetail extends InfoBase implements Serializable{

	/**
	 * 红名单信息
	 */
	private static final long serialVersionUID = 7193516788002170428L;
	
	private static Logger LOG = LogManager.getLogger(RedDetail.class);


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 人脸特征
	// @see
	// http://stackoverflow.com/questions/14897297/how-to-represent-an-image-from-database-in-json
	// @see:
	// http://stackoverflow.com/questions/6662432/easiest-way-to-convert-a-blob-into-a-byte-array
	@JsonIgnore
	// private Blob face_feature;
	// @see:
	// http://stackoverflow.com/questions/5031585/how-to-write-java-sql-blob-to-jpa-entity
	@Lob
	@Column
	private byte[] faceFeature;

	public byte[] getFaceFeature() {
		return faceFeature;
	}

	public void setFaceFeature(byte[] faceFeature) {
		this.faceFeature = faceFeature;
	}

	@Transient
	//@JsonProperty("faceFeature")
	@JsonIgnore
	private String base64FaceFeature = "";

	public String getBase64FaceFeature() throws SQLException {
		try {
			base64FaceFeature = DatatypeConverter.printBase64Binary(this.getFaceFeature());
		} catch (Exception exp) {
			LOG.error(exp.toString());
		}
		return base64FaceFeature;
	}

	// 人脸图像数据
	// @Transient
	// @JsonIgnore
	// private Blob image_data;
	private String faceUrl;
	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	private long fromPersonId;
	public long getFromPersonId() {
		return fromPersonId;
	}

	public void setFromPersonId(long fromPersonId) {
		this.fromPersonId = fromPersonId;
	}

	// 所属图片ID
    @JsonSerialize(using=ToStringSerializer.class)
	private long fromImageId;
	
	@JsonIgnore
	private String json;

	public long getFromImageId() {
		return fromImageId;
	}

	public void setFromImageId(long fromImageId) {
		this.fromImageId = fromImageId;
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
