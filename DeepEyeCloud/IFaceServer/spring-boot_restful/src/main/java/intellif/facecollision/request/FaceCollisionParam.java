package intellif.facecollision.request;

import java.util.List;

/**
 * @author Zheng Xiaodong
 */
public class FaceCollisionParam {
    // 源列表
    private List<FaceCollisionParamItem> sources;
    // 目标列表
    private List<FaceCollisionParamItem> targets;
    // 任务名
    private String taskName;
    // 任务模式（0手动, 1半自动, 2全自动）
    private Integer mode;
    // 人脸id列表
    private List<Long> faceIds;
    // 相似度
    private Double threshold;

    public List<FaceCollisionParamItem> getSources() {
        return sources;
    }

    public void setSources(List<FaceCollisionParamItem> sources) {
        this.sources = sources;
    }

    public List<FaceCollisionParamItem> getTargets() {
        return targets;
    }

    public void setTargets(List<FaceCollisionParamItem> targets) {
        this.targets = targets;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public List<Long> getFaceIds() {
        return faceIds;
    }

    public void setFaceIds(List<Long> faceIds) {
        this.faceIds = faceIds;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "FaceCollisionParam{" +
                "sources=" + sources +
                ", targets=" + targets +
                ", taskName='" + taskName + '\'' +
                ", mode=" + mode +
                ", faceIds=" + faceIds +
                ", threshold=" + threshold +
                '}';
    }
}
