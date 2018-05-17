package intellif.facecollision.dto;

import java.util.Date;
import java.util.List;

import intellif.database.entity.FaceInfo;
import intellif.database.entity.UploadedFile;

public class FaceCollisionTaskDto {
    private Long id;
    
    private Date created;
    
    private Date updated;
    // 任务名
    private String taskName;
    // 完成时间
    private Date completeTime;
    // 任务进度
    private Integer progress;
    //任务状态(0:人脸解析，1：人脸碰撞, 2: 已完成)
    private Integer status;
    
    // 源列表
    private List<UploadedFile> sources;
    // 目标列表
    private List<UploadedFile> targets;
    // 任务模式（0手动, 1半自动, 2全自动）
    private Integer mode;
    // 人脸id列表
    private List<FaceInfo> faces;
    // 相似度
    private Double threshold;
    
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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UploadedFile> getSources() {
        return sources;
    }

    public void setSources(List<UploadedFile> sources) {
        this.sources = sources;
    }

    public List<UploadedFile> getTargets() {
        return targets;
    }

    public void setTargets(List<UploadedFile> targets) {
        this.targets = targets;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
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

    public List<FaceInfo> getFaces() {
        return faces;
    }

    public void setFaces(List<FaceInfo> faces) {
        this.faces = faces;
    }
}
