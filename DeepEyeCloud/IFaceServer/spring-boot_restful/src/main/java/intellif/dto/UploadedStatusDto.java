package intellif.dto;

public class UploadedStatusDto {
	private int resumableChunkNumber;
	private int resumableChunkSize;
	private long resumableTotalSize;
	private int fileType;
	private String resumableFilename;
	private String resumableRelativePath;
	private String uploadIdentifier;
	private Integer picsCount;
	private String resumableIdentifier;
	private String resumableFilePath;
	private String fileHash;
	private Long picTotalSize;
	
	public int getResumableChunkNumber() {
		return resumableChunkNumber;
	}
	public void setResumableChunkNumber(int resumableChunkNumber) {
		this.resumableChunkNumber = resumableChunkNumber;
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
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public String getResumableFilename() {
		return resumableFilename;
	}
	public void setResumableFilename(String resumableFilename) {
		this.resumableFilename = resumableFilename;
	}
	public String getResumableRelativePath() {
		return resumableRelativePath;
	}
	public void setResumableRelativePath(String resumableRelativePath) {
		this.resumableRelativePath = resumableRelativePath;
	}
	public String getUploadIdentifier() {
		return uploadIdentifier;
	}
	public void setUploadIdentifier(String uploadIdentifier) {
		this.uploadIdentifier = uploadIdentifier;
	}
	public Integer getPicsCount() {
		return picsCount;
	}
	public void setPicsCount(Integer picsCount) {
		this.picsCount = picsCount;
	}
	public String getResumableIdentifier() {
		return resumableIdentifier;
	}
	public void setResumableIdentifier(String resumableIdentifier) {
		this.resumableIdentifier = resumableIdentifier;
	}
	public String getResumableFilePath() {
		return resumableFilePath;
	}
	public void setResumableFilePath(String resumableFilePath) {
		this.resumableFilePath = resumableFilePath;
	}
    public String getFileHash() {
        return fileHash;
    }
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
    public Long getPicTotalSize() {
        return picTotalSize;
    }
    public void setPicTotalSize(Long picTotalSize) {
        this.picTotalSize = picTotalSize;
    }
}
