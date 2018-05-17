package intellif.database.entity;

import intellif.ifaas.T_IF_FACERECT;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class ImageDetail implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4239840593554702381L;

	// 图片编号

    @JsonSerialize(using=ToStringSerializer.class)
    private long id;

    // 拍摄时间
    private Date time;

    // 图片数据
    private String uri;//Uniform OauthResource Identifier

    private String faceUri;
    
    private String redUri; //比中红名单图片
  
    private int faces = -2;
    
    private List<T_IF_FACERECT> faceList;

    public ImageDetail(ImageInfo ii){
    	this.id = ii.getId();
    	this.time = ii.getTime();
    	this.uri = ii.getUri();
    	this.faceUri = ii.getFaceUri();
    }
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

    public long getId() {
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

	public List<T_IF_FACERECT> getFaceList() {
		return faceList;
	}

	public void setFaceList(List<T_IF_FACERECT> faceList) {
		this.faceList = faceList;
	}
    public String getRedUri() {
        return redUri;
    }
    public void setRedUri(String redUri) {
        this.redUri = redUri;
    }

}
