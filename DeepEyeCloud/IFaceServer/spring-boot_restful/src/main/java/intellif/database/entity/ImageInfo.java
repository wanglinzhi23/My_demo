package intellif.database.entity;

import intellif.annotation.MultiTablePrefix;
import intellif.consts.GlobalConsts;
import org.springframework.data.annotation.Transient;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
@MultiTablePrefix(shortName = GlobalConsts.T_NAME_IMAGE_INFO,schema=GlobalConsts.INTELLIF_FACE)
public class ImageInfo  {

    // 图片编号
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using=ToStringSerializer.class)
    private long id;

    // 拍摄时间
    private Date time;

    // 图片数据
    private String uri;//Uniform OauthResource Identifier

    private String faceUri;
    
    @javax.persistence.Transient
    private String json;

    @Transient
    private int faces = -1;

    public String getFaceUri() {
        return faceUri;
    }

    public void setFaceUri(String faceUri) {
        this.faceUri = faceUri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public int getFaces() {
        return faces;
    }

    public void setFaces(int faces) {
        this.faces = faces;
    }

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
