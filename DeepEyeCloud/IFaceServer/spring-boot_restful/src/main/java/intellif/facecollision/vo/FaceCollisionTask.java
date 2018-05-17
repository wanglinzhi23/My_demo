package intellif.facecollision.vo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import intellif.consts.GlobalConsts;
import intellif.database.entity.InfoBase;

/**
 * 人脸碰撞任务
 * @author Zheng Xiaodong
 */
@Entity
@Table(name = GlobalConsts.T_NAME_FACE_COLLISION_TASK, schema = GlobalConsts.INTELLIF_BASE)
public class FaceCollisionTask extends InfoBase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // 任务名
    private String taskName;
    // 完成时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date completeTime;
    // 是否删除
    private Boolean deleted;
    // 用户id
    private Long userId;
    // 任务参数
    private String taskParam;
    // 任务结果
    private String taskResult;
    // 任务进度
    private Double progress;
    
    private Integer status;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(String taskParam) {
        this.taskParam = taskParam;
    }

    public String getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(String taskResult) {
        this.taskResult = taskResult;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
