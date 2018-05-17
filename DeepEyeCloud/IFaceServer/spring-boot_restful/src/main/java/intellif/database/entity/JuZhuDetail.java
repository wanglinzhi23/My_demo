package intellif.database.entity;

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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;
import intellif.utils.FileUtil;

@Entity
@Table(name = GlobalConsts.T_NAME_JUZHU_DETAIL,schema=GlobalConsts.INTELLIF_STATIC)
public class JuZhuDetail extends InfoBase implements Serializable {

	private static final long serialVersionUID = -5324862598524328031L;

	private static Logger LOG = LogManager.getLogger(JuZhuDetail.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
	private long id;

    // 照片类型名称
    private String zplxmc;

    
    public JuZhuDetail(String imageUrl,long id){
		this.id = id;
		this.imageData = imageUrl;
		this.fromCidId = 0;
		this.indexed = -1;
		this.fromImageId = 0;
	}
    
    public JuZhuDetail(){
		
	}
    // 人脸特征值
	@JsonIgnore
	@Lob
	@Column
	private byte[] faceFeature;
	
	// 原图id
    @JsonIgnore
    @JsonSerialize(using=ToStringSerializer.class)
	private long fromImageId;
	
	// 所属证件信息id
    @JsonSerialize(using=ToStringSerializer.class)
	private long fromCidId;
	
	// 图片地址
	private String imageData;
    
    // 图片Base64
	@Transient
    private String imageBase64;
	
	// 版本
    @JsonIgnore
	private int version;
	
	// 是否索引（-1:未识别;0:未索引;1:已索引）
    @JsonIgnore
	private int indexed;
    
    @JsonIgnore
	@Transient
    private String base64FaceFeature = "";
	
	@JsonIgnore
	private String json;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getZplxmc()
	{
		return zplxmc;
	}

	public void setZplxmc(String zplxmc)
	{
		this.zplxmc = zplxmc;
	}

	public byte[] getFaceFeature()
	{
		return faceFeature;
	}

	public void setFaceFeature(byte[] faceFeature)
	{
		this.faceFeature = faceFeature;
	}

	public long getFromImageId()
	{
		return fromImageId;
	}

	public void setFromImageId(long fromImageId)
	{
		this.fromImageId = fromImageId;
	}

	public long getFromCidId()
	{
		return fromCidId;
	}

	public void setFromCidId(long fromCidId)
	{
		this.fromCidId = fromCidId;
	}

	public String getImageData()
	{
		return imageData;
	}

	public void setImageData(String imageData)
	{
		this.imageData = imageData;
	}

	

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getIndexed()
	{
		return indexed;
	}

	public void setIndexed(int indexed)
	{
		this.indexed = indexed;
	}

	public String getImageBase64() {
//		try {
//			imageBase64 = FileUtil.GetImageStr(getImageData());
//		} catch (Exception e) {
//			LOG.error(e.toString());
//		}
		return imageBase64;
	}

    public String getBase64FaceFeature() throws SQLException {
        try {
            base64FaceFeature = DatatypeConverter.printBase64Binary(this.getFaceFeature());
        } catch (Exception exp) {
            LOG.error(exp.toString());
        }
        return base64FaceFeature;
    }

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
}
