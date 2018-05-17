package intellif.facecollision.dto;

public class FaceCollisionQueryParam {
 private int faceCount; //比中结果个数限制
 private int targetCount; //目标图片个数过滤
 private long taskId;
 private int page;
 private int pageSize;
 
public int getFaceCount() {
    return faceCount;
}
public void setFaceCount(int faceCount) {
    this.faceCount = faceCount;
}
public int getTargetCount() {
    return targetCount;
}
public void setTargetCount(int targetCount) {
    this.targetCount = targetCount;
}
public long getTaskId() {
    return taskId;
}
public void setTaskId(long taskId) {
    this.taskId = taskId;
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
 
 
}
