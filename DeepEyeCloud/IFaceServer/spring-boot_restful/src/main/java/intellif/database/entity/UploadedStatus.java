package intellif.database.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import intellif.consts.GlobalConsts;


/**
 * 大文件上传状态表
 */
@Entity
@Table(name=GlobalConsts.T_UPLOADED_STATUS,schema=GlobalConsts.INTELLIF_BASE)
public class UploadedStatus implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -131382259090652293L;
	
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
	 * 块平均大小
	 */
	private int resumableChunkSize;
    
	/**
	 * 文件总大小
	 */
    private long resumableTotalSize;
    /**
     * 文件标识
     */
    private String resumableIdentifier;
    /**
     * 文件名
     */
    private String resumableFileName;
    /**
     * 文件相对路径
     */
    private String resumableRelativePath;
    /**
     * 文件存储路径
     */
    private String resumableFilePath;
    /**
     * 已上传的块
     */
 
    private String uploadedChunks;//Chunks uploaded 
    
    /**
     * 文件类型(0:视频，1：压缩包）
     */
    private Integer fileType;
    
    @JsonSerialize(using=ToStringSerializer.class)
    private Long fileId;
    
    /**
     * 是否已上传成功 0上传中 1已上传
     */
    private int isFinished;
    
    private int progress;
    
    /**
     * 上传标识
     */
    private String uploadIdentifier;
   
    private int isDeleted;
    
    @Transient
    private HashSet<String> uploadChunkSet=new HashSet<String>();
    
    @Transient
    private Integer resumableChunkNumber;
    
    @Transient
    private UploadedFile uploadFile=new UploadedFile();
    
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

	public int getResumableChunkSize() {
		return resumableChunkSize;
	}

	public void setResumableChunkSize(int resumableChunkSize) {
		this.resumableChunkSize = resumableChunkSize;
	}

	public long getResumableTotalSize() {
		return resumableTotalSize;
	}

	public void setResumableTotalSize(long resumableTotalSize) {
		this.resumableTotalSize = resumableTotalSize;
	}

	public String getResumableIdentifier() {
		return resumableIdentifier;
	}

	public void setResumableIdentifier(String resumableIdentifier) {
		this.resumableIdentifier = resumableIdentifier;
	}

	public String getResumableFileName() {
		return resumableFileName;
	}

	public void setResumableFileName(String resumableFileName) {
		this.resumableFileName = resumableFileName;
	}

	public String getResumableRelativePath() {
		return resumableRelativePath;
	}

	public void setResumableRelativePath(String resumableRelativePath) {
		this.resumableRelativePath = resumableRelativePath;
	}

	public String getResumableFilePath() {
		return resumableFilePath;
	}

	public void setResumableFilePath(String resumableFilePath) {
		this.resumableFilePath = resumableFilePath;
	}
	
	public String getUploadedChunks() {
		return uploadedChunks;
	}

	public void setUploadedChunks(String uploadedChunks) {
		this.uploadedChunks = uploadedChunks;
	}

	public synchronized HashSet<String> getUploadChunkSet() {
		return uploadChunkSet;
	}

	public synchronized void  setUploadChunkSet(String resumableChunkNumber) {
		this.uploadChunkSet.add(resumableChunkNumber);
		this.uploadedChunks=this.hashsetToString(uploadChunkSet);
	}
	
	public String hashsetToString(HashSet<String> hashSet){
		StringBuffer buffer=new StringBuffer();
		for(Iterator<String> it=hashSet.iterator();it.hasNext();){
			buffer.append(it.next()+",");
		}
		return buffer.toString();
	}

	public Integer getFileType() {
		return fileType;
	}

	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}

	public Integer getResumableChunkNumber() {
		return resumableChunkNumber;
	}

	public void setResumableChunkNumber(Integer resumableChunkNumber) {
		this.resumableChunkNumber = resumableChunkNumber;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public int getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(int isFinished) {
		this.isFinished = isFinished;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getUploadIdentifier() {
		return uploadIdentifier;
	}

	public void setUploadIdentifier(String uploadIdentifier) {
		this.uploadIdentifier = uploadIdentifier;
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

}
