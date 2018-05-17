package intellif.dto;

import intellif.validate.NotBlank;
import intellif.validate.SexType;
import intellif.database.entity.InfoBase;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
@Entity
public class RedDto extends InfoBase implements Serializable {

	private static final long serialVersionUID = 7564447239074311989L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotBlank(fieldName="姓名:")
	private String name;
	@SexType(fieldName="性别:")
	private String sex;
	private String remarks;
    private String policePhone;
    private String faceUrl;
	@Transient
	private int page;
	@Transient
	private int pageSize;	
	@Transient
    String imageIds;
	@Transient
    String keywords;

	
   

	public String getPolicePhone() {
		return policePhone;
	}

	public void setPolicePhone(String policePhone) {
		this.policePhone = policePhone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageIds() {
		return imageIds;
	}

	public void setImageIds(String imageIds) {
		this.imageIds = imageIds;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
	
	

}
