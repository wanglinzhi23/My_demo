package intellif.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import intellif.consts.GlobalConsts;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 非人脸图片过滤类型
 * @author Zheng Xiaodong
 */
@Entity
@Table(name= GlobalConsts.T_NAME_FACE_FILTERED, schema=GlobalConsts.INTELLIF_FACE)
public class FilteredFaceInfo implements Serializable {
    // 人脸编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
    private long id;

    // 数据源ID
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

    private Long filterType;
    @Transient
    private String filterTypeName;

    public long getId() {
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

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
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

    public int getIndexed() {
        return indexed;
    }

    public void setIndexed(int indexed) {
        this.indexed = indexed;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public float[] getFeature() {
        return feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Long getFilterType() {
        return filterType;
    }

    public void setFilterType(Long filterType) {
        this.filterType = filterType;
    }

    public String getFilterTypeName() {
        return filterTypeName;
    }

    public void setFilterTypeName(String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }
}
