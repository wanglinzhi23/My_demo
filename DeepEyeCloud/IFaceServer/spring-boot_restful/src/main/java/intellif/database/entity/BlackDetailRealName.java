package intellif.database.entity;

import java.io.Serializable;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
public class BlackDetailRealName extends InfoBase implements Serializable {
	
	private static Logger LOG = LogManager.getLogger(BlackDetailRealName.class);
	
	private static final long serialVersionUID = 6212108229844672936L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 名单库ID
	// @Column(name="bank_id")
	private long bankId;

	// 黑名描述信息
	// @Column(name="black_description")
	private String blackDescription;
	
	// 版本号
	private int version;
	@Transient
	private String json;
	
	private int indexed;
	

	public String getBlackDescription() {
		return blackDescription;
	}

	public void setBlackDescription(String blackDescription) {
		this.blackDescription = blackDescription;
	}

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
	@JsonProperty("faceFeature")
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
	private String imageData;

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	// 所属图片ID
    @JsonSerialize(using=ToStringSerializer.class)
	private long fromImageId;

	public long getFromImageId() {
		return fromImageId;
	}

	public void setFromImageId(long fromImageId) {
		this.fromImageId = fromImageId;
	}

	// 所属人物ID
	private long fromPersonId;

	public long getFromPersonId() {
		return fromPersonId;
	}

	public void setFromPersonId(long fromPersonId) {
		this.fromPersonId = fromPersonId;
	}
	
	@Column
	private String realName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getIndexed() {
		return indexed;
	}

	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}
	
}