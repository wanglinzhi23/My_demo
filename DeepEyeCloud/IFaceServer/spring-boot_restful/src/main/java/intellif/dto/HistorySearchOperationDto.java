package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class HistorySearchOperationDto implements Serializable {

    private static final long serialVersionUID = -8721847450402807625L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    //图片地址
    private String faceUrl;
    
    //图片id
    private long faceId;
    
    //图片来源
    private String dataType;

    // 搜索时间
    private String opetime;
    
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOpetime() {
		return opetime;
	}

	public void setOpetime(String opetime) {
		this.opetime = opetime;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public long getFaceId() {
		return faceId;
	}

	public void setFaceId(long faceId) {
		this.faceId = faceId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

   

	
   

}