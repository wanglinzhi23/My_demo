package intellif.database.entity;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;

import com.intellif.core.cluster.vo.Face;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.annotation.MultiTablePrefix;
import intellif.chd.consts.Constant;
import intellif.chd.util.FaceUtil;
import intellif.consts.GlobalConsts;

@Entity
@MultiTablePrefix(shortName = GlobalConsts.T_NAME_FACE_INFO,schema=GlobalConsts.INTELLIF_FACE)
public class FaceInfo implements Face, Serializable {

    private static final long serialVersionUID = -2684645766889341498L;
    private static Logger LOG = LogManager.getLogger(FaceInfo.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
    // 人脸编号
    private long id;

    // 数据源ID
    @JsonSerialize(using=ToStringSerializer.class)
    private long sourceId;
    /**
     * 0 摄像头
     * 1 视频
     * 2 图片
     */
    private int sourceType;
    // 数据源类型
    // 录入时间
    private Date time;
    // 人脸图像数据
    private String imageData;//URI
    // 性别
    @JsonIgnore
    private int gender;
    // 年龄
    @JsonIgnore
    private int age;
    // 配饰
    @JsonIgnore
    private int accessories;
    // 种族
    private int race;
    // 所属人物编号
    @JsonIgnore
    private Long fromPersonId;
    // 所属图片编号
    @JsonSerialize(using=ToStringSerializer.class)
    private Long fromImageId;
    // 所属视频编号
    @JsonIgnore
    private Long fromVideoId;
  //  @JsonIgnore
    private String json;
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
    @Transient
//    @JsonProperty("faceFeature")
    @JsonIgnore
    private String base64FaceFeature = "";
    // 是否已经Lire索引
    @JsonIgnore
    @Column(nullable = true, columnDefinition = "int(1) default '0'")
    private int indexed;
    
    /**
     * 图片质量：0高质量 1低质量
     */
    private int quality = 0;
    
    private long sequence;

    // 版本号
    @JsonIgnore
    private int version = 0;
    
    // 人脸特征向量
    @Transient
    @JsonIgnore
    private float[] feature;
    
    @Transient
    private float score = 0f;

    public synchronized float getScore() {
        return score;
    }

    public synchronized void setScore(float score) {
        this.score = score;
    }

    public float[] getFeature() {
        try {
            if (null == feature && null != faceFeature) {
                feature = FaceUtil.byte2float(faceFeature, 0, Constant.REAL_LENGTH_181 * 4);
                faceFeature = null;
            }
        } catch (Exception e) {
            feature = new float[0];
        }
        return feature;
    }

    public void clearFeature() {
        feature = null;
        faceFeature = null;
    }
    
    @Override
    public String toString() {
        return "FaceInfo{" +
                "id=" + id +
                ", sourceId=" + sourceId +
                ", sourceType=" + sourceType +
                ", time=" + time +
                ", imageData='" + imageData + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", race=" + race +
                ", accessories=" + accessories +
                ", fromPersonId=" + fromPersonId +
                ", fromImageId=" + fromImageId +
                ", fromVideoId=" + fromVideoId +
                ", faceFeature=" + Arrays.toString(faceFeature) +
                ", base64FaceFeature='" + base64FaceFeature + '\'' +
                ", indexed=" + indexed +",quality="+quality+
                '}';
    }

    public String getBase64FaceFeature() throws SQLException {
        try {
            base64FaceFeature = DatatypeConverter.printBase64Binary(this.getFaceFeature());
        } catch (Exception exp) {
            LOG.error(exp.toString());
        }
        return base64FaceFeature;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
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

    public int getIndexed() {
        return indexed;
    }

    public void setIndexed(int indexed) {
        this.indexed = indexed;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAccessories() {
        return accessories;
    }

    public void setAccessories(int accessories) {
        this.accessories = accessories;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public int getRace() {
		return race;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public String version() {
        return "" + getVersion();
    }

    @Override
    public float[] feature() {
        return getFeature();
    }

    @Override
    public synchronized void putScore(float v) {
        this.setScore(v);
    }
}
