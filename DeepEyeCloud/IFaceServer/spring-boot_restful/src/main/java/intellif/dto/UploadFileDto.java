package intellif.dto;

public class UploadFileDto {
    private String fileName;
    private Integer fileType;
    private Integer status;
    private String ids;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Integer getFileType() {
        return fileType;
    }
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getIds() {
        return ids;
    }
    public void setIds(String ids) {
        this.ids = ids;
    }
}
