package intellif.facecollision.vo;

import intellif.consts.GlobalConsts;
import intellif.database.entity.InfoBase;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 * 人脸解析任务
 * @author Zheng Xiaodong
 */
@Entity
@Table(name = GlobalConsts.T_NAME_FACE_EXTRACT_TASK, schema = GlobalConsts.INTELLIF_BASE)
public class FaceExtractTask extends InfoBase implements Serializable {
    @Id
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;
    // 引擎id
    private Long serverId;
    // 文件id
    private Long fileId;
    // 文件类型
    private Integer fileType;
    // 任务状态
    private Integer status;
    // 任务名
    private String taskName;
    // 文档路径
    private String archiveUrl;
    // 文件总大小/图片总张数
    private Long total;
    // 已处理的文件大小/图片张数
    private Long current;
    // 错误原因
    private String errorReason;
    // 是否已删除
    private Boolean deleted;
    
    public FaceExtractTask(){
        long now =new Date().getTime();
        Random random=new Random();
        long randomNum=random.nextInt(999);
        this.id=576460752303423488L|(now<<15)|randomNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(String archiveUrl) {
        this.archiveUrl = archiveUrl;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "FaceExtractTask{" +
                "id=" + id +
                ", serverId=" + serverId +
                ", fileId=" + fileId +
                ", fileType=" + fileType +
                ", status=" + status +
                ", taskName='" + taskName + '\'' +
                ", archiveUrl='" + archiveUrl + '\'' +
                ", total=" + total +
                ", current=" + current +
                ", errorReason='" + errorReason + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}

