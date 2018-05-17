package intellif.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
public class HistorySearchOperationDetailDto implements Serializable {

    private static final long serialVersionUID = -8721847450402807625L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 搜索人
    private String operator;

    // 搜索人账号类型
    private String operatorRole;
    
    //图片地址
    private String faceUrl;
    
    //图片faceid
    @JsonSerialize(using=ToStringSerializer.class)
    private long faceId;
    
    //图片datatype
    private String dataType;
    
    // 搜索时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date opetimestamp;
    
    // 搜索时间
    @Transient
    private String opetime;
    
    // 操作一级详细信息（搜索：搜索事由）
    private String friDetail = "";
    
    //操作二级详细信息 （搜索：搜索原因）
    private String secDetail = "";
    

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperatorRole() {
		return operatorRole;
	}

	public void setOperatorRole(String operatorRole) {
		this.operatorRole = operatorRole;
	}

	public String getOpetime() {
		return opetime;
	}

	public void setOpetime(String opetime) {
		this.opetime = opetime;
	}

	public String getFriDetail() {
		return friDetail;
	}

	public void setFriDetail(String friDetail) {
		this.friDetail = friDetail;
	}

	public String getSecDetail() {
		return secDetail;
	}

	public void setSecDetail(String secDetail) {
		this.secDetail = secDetail;
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

	public Date getOpetimestamp() {
		return opetimestamp;
	}

	public void setOpetimestamp(Date opetimestamp) {
		this.opetimestamp = opetimestamp;
	}

	


 

}