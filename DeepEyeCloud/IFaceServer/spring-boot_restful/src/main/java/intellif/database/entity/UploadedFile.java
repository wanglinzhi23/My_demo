package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;

@Entity
@Table(name=GlobalConsts.T_UPLOADED_FILE,schema=GlobalConsts.INTELLIF_BASE)
public class UploadedFile implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonSerialize(using=ToStringSerializer.class)
	private long id;
	
	/**
	 * 创建时间
	 */
	private Date created;
	
	/**
	 * 最后修改时间
	 */
	private Date updated;
	
	/**
	 * 用户Id
	 */
	private long userId;
	
	/**
	 * 文件原名
	 */
	private String fileName;
	
	/**
	 * 文件保存路径
	 */
	private String fileUrl;
	
	/**
	 * 文件类型(0:视频，1：压缩包）
	 */
	private Integer fileType;
	
	/**
	 * 图片集张数
	 */
	private Integer picsCount;
	/**
	 * 文件大小
	 */
	private Long fileSize;
	/**
	 * 进度
	 */
	private Integer progress;
	 /**
     * 上传阶段 0:写入阶段 1:重命名阶段 2:图片打包阶段 3:打包成功原图删除阶段4上传完成5解析任务已创建
     */
    private int status;
    
    private int isDeleted;
    
    // 文件解析任务状态（-1：处理任务失败 0：任务尚未处理   1：任务正在被处理   2：任务处理完成）
    @Transient
    private Integer extractStatus;
    //解析进度
    @Transient
    private Double extractProgress;
    //解析文件总大小/图片总张数
    @Transient
    private Long extractTotal;
    //上传标识
    @Transient
    private String uploadIdentifier;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Integer getFileType() {
		return fileType;
	}

	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}

	public Integer getPicsCount() {
		return picsCount;
	}

	public void setPicsCount(Integer picsCount) {
		this.picsCount = picsCount;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getExtractStatus() {
        return extractStatus;
    }

    public void setExtractStatus(Integer extractStatus) {
        this.extractStatus = extractStatus;
    }

    public Double getExtractProgress() {
        return extractProgress;
    }

    public void setExtractProgress(Double extractProgress) {
        this.extractProgress = extractProgress;
    }

    public Long getExtractTotal() {
        return extractTotal;
    }

    public void setExtractTotal(Long extractTotal) {
        this.extractTotal = extractTotal;
    }

    public String getUploadIdentifier() {
        return uploadIdentifier;
    }

    public void setUploadIdentifier(String uploadIdentifier) {
        this.uploadIdentifier = uploadIdentifier;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
